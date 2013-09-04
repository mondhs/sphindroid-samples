package org.sphindroid.sample.call.service;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.AsrAssert;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;
import org.sphindroid.sample.call.service.aidl.AsrContactParcelable;
import org.sphindroid.sample.call.service.aidl.ISphndroidRecognitionCallback;
import org.sphindroid.sample.call.service.aidl.SphindroidService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.widget.Toast;

public class SphindroidClientImpl {

	private CallRemoteServiceConnection conn;
	private Context context;

	public SphindroidClientImpl(Context context) {
		super();
		this.context = context;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(SphindroidClientImpl.class);

	public void startService() {
		if (isServiceRunning()) {
			inform("Service already started");
		} else {
			ComponentName componentName = context.startService(createIntent());
			AsrAssert.isNotNull(componentName, "Component not found");
			LOG.debug("startService()");
		}

	}

	public void stopService() {
		if (!isServiceRunning()) {
			inform("Service not yet started");
		} else {
			context.stopService(createIntent());
			LOG.debug("stopService()");
		}
	}
	/**
	 * 
	 * @param callback
	 */
	public void bindService(ISphndroidRecognitionCallback callback) {
		if (connectionActive(conn, "Cannot bind - service already bound", null)) {
			return;
		}
		conn = new CallRemoteServiceConnection(callback);
		context.bindService(createIntent(), conn, Context.BIND_AUTO_CREATE);
	}
	/**
	 * 
	 */
	public void bindService() {
		if (connectionActive(conn, "Cannot bind - service already bound", null)) {
			return;
		}
		conn = new CallRemoteServiceConnection();
		context.bindService(createIntent(), conn, Context.BIND_AUTO_CREATE);

	}
	

	public void releaseService(ISphndroidRecognitionCallback callback) {
		if (!connectionActive(conn, null, "Cannot unbind - service not bound")) {
			return;
		}
		try {
			conn.getRemoteService().unregisterCallBack(callback);
		} catch (RemoteException e) {
			LOG.error("problem with unregister callback",e);
		}
		context.unbindService(conn);
		conn = null;
	}


	public boolean isListening() {
		if (!connectionActive(conn, null,
				"Cannot invoke isReady - service not bound")) {
			return false;
		}
		try {
			LOG.debug("isListening()");
			return conn.getRemoteService().isListening();
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
		return false;
	}
	
	public boolean isReady() {
		if (!connectionActive(conn, null,
				"Cannot invoke isReady - service not bound") || conn.getRemoteService()==null) {
			return false;
		}
		try {
			LOG.debug("isReady()");
			return conn.getRemoteService().isReady();
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
		return false;
	}

	public List<AsrContactParcelable> findContacts() {
		if (!connectionActive(conn, null,
				"Cannot invoke findContacts - service not bound")) {
			return null;
		}
		try {
			return conn.getRemoteService().findContacts();
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
		return null;
	}

	// public void callContact(long contactId) {
	// if (!connectionActive(conn, null, "Cannot invoke - service not bound")) {
	// return;
	// }
	// try {
	// // conn.getRemoteService().callContact(contactId);
	// AsrCommandParcelable cmd = new AsrCommandParcelable();
	// cmd.setCommandName(ContactsCommand.KEY_COMMAND);
	// cmd.setId(contactId);
	// conn.getRemoteService().executeCommand(cmd);
	// LOG.debug("callContact({})", contactId);
	// } catch (RemoteException re) {
	// LOG.error("RemoteException", re);
	// }
	// }

	public void startListening() {
		if (!connectionActive(conn, null,
				"Cannot invoke startListening - service not bound")) {
			return;
		}
		try {
			conn.getRemoteService().startListening();
			LOG.debug("startListening()");
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
	}

	public void stopListening() {
		if (!connectionActive(conn, null,
				"Cannot invoke stopListening - service not bound")) {
			return;
		}

		try {
			conn.getRemoteService().stopListening();
			LOG.debug("startListening()");
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
	}

	public void executeCommand(String commandName) {
		LOG.debug("[executeCommand]{}", commandName);
		AsrCommandParcelable command = new AsrCommandParcelable();
		command.setCommandName(commandName);
		executeCommand(command);
	}

	public void executeCommand(AsrCommandParcelable command) {
		if (!connectionActive(conn, null, MessageFormat.format(
				"Cannot invoke executeCommand {0}- service not bound",
				command.getCommandName()))) {
			return;
		}
		try {
			conn.getRemoteService().executeCommand(command);
			LOG.debug("invokeService()");
		} catch (RemoteException re) {
			LOG.error("RemoteException", re);
		}
	}

	public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (SphindroidService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public boolean isBound() {
		return conn != null && conn.getRemoteService() != null;
	}

	protected Intent createIntent() {
		Intent i = new Intent();
		String clazzName = SphindroidService.class.getCanonicalName();
		String packageName = context.getApplicationInfo().packageName;
		i.setClassName(packageName, clazzName);
		return i;
	}

	protected void inform(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		LOG.debug(message);
	}

	protected boolean connectionActive(CallRemoteServiceConnection connection,
			String readyMsg, String notReadyMsg) {
		LOG.debug("connectionActive() {}", connection);
		if (connection == null) {
			if (notReadyMsg != null) {
				inform(notReadyMsg);
			}
			LOG.debug("connectionActive() false");
			return false;
		} else {
			LOG.debug("connectionActive() {}", connection.getRemoteService());
			if (readyMsg != null) {
				inform(readyMsg);
			}
			LOG.debug("connectionActive() true");
			return true;
		}
	}




}
