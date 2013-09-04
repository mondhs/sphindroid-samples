package org.sphindroid.sample.call.service.aidl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.AsrAssert;
import org.sphindroid.core.service.SphindroidResrouceHelper;
import org.sphindroid.core.service.impl.GraphemeToPhonemeMapperLithuanianImpl;
import org.sphindroid.lib.async.SphindroidRecognizer;
import org.sphindroid.lib.service.SphindroidFactory;
import org.sphindroid.sample.call.CallActivity;
import org.sphindroid.sample.call.R;
import org.sphindroid.sample.call.dto.AsrContact;
import org.sphindroid.sample.call.service.AsrContantDaoImpl;
import org.sphindroid.sample.call.service.CallGrammarBuilderLithuanianImpl;
import org.sphindroid.sample.call.service.CallRecognitionListener;
import org.sphindroid.sample.call.service.command.AsrCommandProcessor;
import org.sphindroid.sample.call.service.command.AsrCommandResolver;
import org.sphindroid.sample.call.service.command.AsrCommandResult;
import org.sphindroid.sample.call.service.command.ContactsCommand;
import org.sphindroid.sample.call.service.command.HiAsrCommand;
import org.sphindroid.sample.call.service.command.HowdyAsrCommand;
import org.sphindroid.sample.call.service.command.LightOffCommand;
import org.sphindroid.sample.call.service.command.LightOnCommand;
import org.sphindroid.sample.call.service.command.NewsCommand;
import org.sphindroid.sample.call.service.command.TimeAsrCommand;
import org.sphindroid.sample.call.service.command.WeatherAsrCommand;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class SphindroidService extends Service {
	private static final Logger LOG = LoggerFactory.getLogger(SphindroidService.class);
	
	static {
		System.loadLibrary("pocketsphinx_jni");
	}
	
	private NotificationManager nm;
	private SphindroidRecognizer sphindroidRecognizer;
	private SphindroidResrouceHelper sphindroidResrouceHelper;
	private CallRecognitionListener callRecognitionListener;
	private Notification notification;
	private AsrCommandResolver commandResolver;
	
	
	private CallGrammarBuilderLithuanianImpl grammarBuilder = null;
	private final Calendar time = Calendar.getInstance();
	private final RemoteCallbackList<ISphndroidRecognitionCallback> callbacks = new RemoteCallbackList<ISphndroidRecognitionCallback>();
	private ISphndroidRemoteService.Stub sphndroidRemoteService = new ISphndroidRemoteService.Stub() {
		@Override
		public String executeCommand(AsrCommandParcelable commandDto) throws RemoteException {
			return processExecuteCommand(commandDto);
		}

		@Override
		public void startListening() throws RemoteException {
			if(sphindroidRecognizer != null){
				LOG.debug("[startListening]");
				sphindroidRecognizer.start();
				nm.cancel(R.string.service_label);
				notification.tickerText=getText(R.string.service_listen);
				notification.icon = R.drawable.ic_launcher_listen;
				nm.notify(R.string.service_label, notification);
			}else{
				LOG.error("sphindroidRecognizer is not initialized");
			}
		}

		@Override
		public boolean isReady() throws RemoteException {
			return callRecognitionListener !=null && callRecognitionListener.isReady();
		}
		

		@Override
		public void stopListening() throws RemoteException {
			if(sphindroidRecognizer != null){
				sphindroidRecognizer.stop();
				LOG.debug("[stopListening]");
				
				nm.cancel(R.string.service_label);
				notification.tickerText=getText(R.string.service_waiting);
				notification.icon = R.drawable.ic_launcher;
				nm.notify(R.string.service_label, notification);
			}else{
				LOG.error("sphindroidRecognizer is not initialized");
			}
		}

		@Override
		public void registerCallBack(ISphndroidRecognitionCallback cb)
				throws RemoteException {
			if (cb != null){
				callbacks.register(cb);
			}
			
		}

		@Override
		public void unregisterCallBack(ISphndroidRecognitionCallback cb)
				throws RemoteException {
			if (cb != null){
				callbacks.unregister(cb);
			}			
		}

		@Override
		public List<AsrContactParcelable> findContacts() throws RemoteException {
			AsrAssert.isNotNull(grammarBuilder, "GrammarBuilder cannot be null");
			AsrAssert.isNotNull(grammarBuilder.getAsrContantService(), "AsrContantService cannot be null");
			List<AsrContact> asrContacts = grammarBuilder.getAsrContantService().findContacts();
			List<AsrContactParcelable> rtn = new ArrayList<AsrContactParcelable>();
			for (AsrContact asrContact : asrContacts) {
				AsrContactParcelable parcelable = new AsrContactParcelable();
				parcelable.setId(asrContact.getId());
				parcelable.setDisplayName(asrContact.getDisplayName());
				if(asrContact.getAvatar()!=null){
					parcelable.setAvatar(asrContact.getAvatar().toString());
				}
				rtn.add(parcelable);
			}
			return rtn;
		}

		@Override
		public boolean isListening() throws RemoteException {
			return sphindroidRecognizer.isListening();
		}

	};


	
	@Override
	public IBinder onBind(Intent intent) {
		LOG.debug("[onBind] {}", sphndroidRemoteService);
		return sphndroidRemoteService;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
			PhoneStateListener phoneListener = new PhoneStateListenerImpl();
			telephonyManager.listen(phoneListener ,PhoneStateListener.LISTEN_CALL_STATE);
		nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Toast.makeText(this,"Service created at " + time.getTime(), Toast.LENGTH_LONG).show();
		showNotification();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		initSphindroid(callbacks);
		LOG.debug("onStart({})", getClass().getSimpleName());
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        // Cancel the persistent notification.
        nm.cancel(R.string.service_label);
        sphindroidRecognizer.shutdown();
        sphindroidResrouceHelper.cleanUp(sphindroidRecognizer.getCtx());
		Toast.makeText(this, "Service destroyed at " + time.getTime(), Toast.LENGTH_LONG).show();
	}
	
	
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.service_starting);

        // Set the icon, scrolling text and timestamp
        notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());
        

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, CallActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.service_label),
                       text, contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        nm.notify(R.string.service_label, notification);
    }


	/**
	 * @param aCallbacks 
     * 
     */
	private void initSphindroid(RemoteCallbackList<ISphndroidRecognitionCallback> aCallbacks) {
		LOG.debug("initSphindroid({})", getClass().getSimpleName());
		AsrContantDaoImpl aContantService = new AsrContantDaoImpl();
		aContantService.setContentResolver(getContentResolver());
		
		Set<AsrCommandProcessor> commands = createCommandCollection(aContantService);
		CallGrammarBuilderLithuanianImpl aGrammarBuilder = createGrammarBuilder(commands, aContantService);
		AsrCommandResolver aCommandResolver = new AsrCommandResolver(commands);
		CallRecognitionListener aCallRecognitionListener = new CallRecognitionListener(aCallbacks, aCommandResolver);
		SphindroidFactory factory = SphindroidFactory.getInstance();
		factory.registryBuilder(new Locale("lt", "LT"), aGrammarBuilder);
		
		this.callRecognitionListener = aCallRecognitionListener;
		this.commandResolver = aCommandResolver;
		this.grammarBuilder = aGrammarBuilder;
		this.sphindroidResrouceHelper = factory
				.createSphindroidResrouceHelper(this);
		this.sphindroidRecognizer = factory.createDefaultSphindroidRecognizer(
				this, callRecognitionListener);
		
		this.sphindroidRecognizer.execute();
		
	}


	
	private Set<AsrCommandProcessor> createCommandCollection(AsrContantDaoImpl aContantService) {
		Set<AsrCommandProcessor> commands = new LinkedHashSet<AsrCommandProcessor>();
		commands.add(new HiAsrCommand(getApplicationContext()));
		commands.add(new HowdyAsrCommand(getApplicationContext()));
		commands.add(new TimeAsrCommand(getApplicationContext()));
		commands.add(new WeatherAsrCommand(getApplicationContext()));
		commands.add(new LightOnCommand(getApplicationContext()));
		commands.add(new LightOffCommand(getApplicationContext()));
		commands.add(new NewsCommand(getApplicationContext()));
//		commands.add(new ExampleAsrCommand(getApplicationContext()));
		commands.add(new ContactsCommand(getApplicationContext(), aContantService));
		return commands;
	}

	/**
	 * 
	 * @param commands
	 * @param aContantService
	 * @return
	 */
	private CallGrammarBuilderLithuanianImpl createGrammarBuilder(Set<AsrCommandProcessor> commands, AsrContantDaoImpl aContantService) {
		CallGrammarBuilderLithuanianImpl theGrammarBuilder = new CallGrammarBuilderLithuanianImpl(commands);
		theGrammarBuilder
				.setGraphemeToPhonemeMapper(new GraphemeToPhonemeMapperLithuanianImpl());
		theGrammarBuilder.setAsrContantService(aContantService);
		return theGrammarBuilder;
	}
	
	private String processExecuteCommand(AsrCommandParcelable commandDto) {
		String cmd = commandDto.getCommandName();
		LOG.debug("[processExecuteCommand]: {}", cmd);
		AsrCommandResult result = commandResolver.execute(commandDto);
		if(!Boolean.TRUE.equals(result.getConsumed())){
			Toast.makeText(getApplicationContext(),
					"Kas yra " +  cmd + "?", Toast.LENGTH_SHORT)
					.show();
		}
		return cmd;			
	}
	
	public class PhoneStateListenerImpl extends PhoneStateListener {
		private boolean isPhoneCalling = false;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				LOG.debug("RINGING, number: " + incomingNumber);
			}

			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				LOG.debug("OFFHOOK");
				isPhoneCalling = true;
			}

			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended,
				// need detect flag from CALL_STATE_OFFHOOK
				LOG.debug("IDLE");
				if (isPhoneCalling) {
					LOG.debug("restart app");
					// restart app
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage(
									getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);

					isPhoneCalling = false;
				}

			}
		}
	}

}
