package org.sphindroid.sample.call;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.AsrAssert;
import org.sphindroid.sample.call.service.SphindroidClientImpl;
import org.sphindroid.sample.call.service.aidl.AsrContactParcelable;
import org.sphindroid.sample.call.service.aidl.AsrStatisticsParcelable;
import org.sphindroid.sample.call.service.aidl.ISphndroidRecognitionCallback;
import org.sphindroid.sample.call.service.command.HiAsrCommand;
import org.sphindroid.sample.call.service.command.HowdyAsrCommand;
import org.sphindroid.sample.call.service.command.LightOffCommand;
import org.sphindroid.sample.call.service.command.LightOnCommand;
import org.sphindroid.sample.call.service.command.TimeAsrCommand;
import org.sphindroid.sample.call.service.command.WeatherAsrCommand;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallActivity extends Activity {

	private SphindroidClientImpl sphindroidClient = null;

	private static final Logger LOG = LoggerFactory
			.getLogger(CallActivity.class);

	private ISphndroidRecognitionCallback callback = new ISphndroidRecognitionCallback.Stub() {
		@Override
		public void onResults(final AsrStatisticsParcelable asrStatistics)
				throws RemoteException {
			LOG.debug("[onResults] callback {}[{}]",
					asrStatistics.getHypothesis(),
					asrStatistics.getBestScore());
			runOnUiThread(new Runnable() {
				public void run() {
					updateUIOnResult(asrStatistics.getHypothesis());
				}
			});
		}

		@Override
		public void ready() throws RemoteException {
			LOG.debug("[ready] callback");
			runOnUiThread(new Runnable() {
				public void run() {
					findViewById(R.id.btn_start).setEnabled(false);
					findViewById(R.id.btn_stop).setEnabled(true);
					findViewById(R.id.btn_listen).setEnabled(true);
					((TextView)findViewById(R.id.txt_output)).setText(
							getResources().getString(
							R.string.msg_ready));
				}
			});
		}

	};

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LOG.info("onCreate");
		setContentView(R.layout.call_main);
		sphindroidClient = new SphindroidClientImpl(getApplicationContext());
		initUI();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            case R.id.menu_command_hi: sphindroidClient.executeCommand(HiAsrCommand.COMMAND_TRANSCRIPTION); break;
            case R.id.menu_command_howdy: sphindroidClient.executeCommand(HowdyAsrCommand.COMMAND_TRANSCRIPTION); break;
            case R.id.menu_command_time: sphindroidClient.executeCommand(TimeAsrCommand.COMMAND_TRANSCRIPTION); break;
            case R.id.menu_command_weather: sphindroidClient.executeCommand(WeatherAsrCommand.COMMAND_TRANSCRIPTION); break;
//            case R.id.menu_test: startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"))); break;
            default: return super.onOptionsItemSelected(item);
            }
            return true;
    }


	@Override
	protected void onDestroy() {
		super.onDestroy();
		sphindroidClient.releaseService(callback);
		LOG.debug("[onDestroy]");
	}

	/**
	 * 
	 */
	private void initUI() {
		LOG.debug("[initUI]+++");
		final Button btn_listen = (Button) findViewById(R.id.btn_listen);
		AsrAssert.isNotNull(btn_listen, "Button not found?");
		btn_listen.setEnabled(false);
		btn_listen.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sphindroidClient.startListening();
					break;
				case MotionEvent.ACTION_UP:
					sphindroidClient.stopListening();
					break;
				default:
					;
				}
				return false;
			}

		});

		Button startButton = (Button) findViewById(R.id.btn_start);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (!sphindroidClient.isServiceRunning()) {
					sphindroidClient.startService();
					bindService(callback);
				}
				updateServiceStatus();
			}
		});
		Button stopButton = (Button) findViewById(R.id.btn_stop);
		stopButton.setEnabled(false);
		stopButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sphindroidClient.releaseService(callback);
				sphindroidClient.stopService();
				updateServiceStatus();
			}
		});

		if (!sphindroidClient.isBound() && sphindroidClient.isServiceRunning()) {
			LOG.debug("[initUI] Is not bound but service is running");
			bindService(callback);

		}
		LOG.debug("[initUI] Bound {}; Service {}", sphindroidClient.isBound(),
				sphindroidClient.isServiceRunning());
		LOG.debug("[initUI]---");
	}

	private void bindService(ISphndroidRecognitionCallback callback) {
		sphindroidClient.bindService(callback);
		new ServiceBoundAsyncTask().execute();
	}

	private void onBound() {
		updateContacts(sphindroidClient.findContacts());
		if (sphindroidClient.isReady()) {
			findViewById(R.id.btn_start).setEnabled(false);
			findViewById(R.id.btn_stop).setEnabled(true);
			findViewById(R.id.btn_listen).setEnabled(true);
			((TextView)findViewById(R.id.txt_output)).setText(
					getResources().getString(
					R.string.msg_ready));
		}
		updateServiceStatus();
	}

	/**
	 * @param collection
	 * 
	 */
	private void updateContacts(List<AsrContactParcelable> list) {
		ListView contactList = (ListView) findViewById(R.id.contactList);
		contactList
				.setAdapter(new ContactAdapter(this, list, sphindroidClient));
	}

	private void updateUIOnResult(String hypothesis) {
		TextView txtOutput = (TextView) findViewById(R.id.txt_output);
		txtOutput.setText(hypothesis);
		//command should be executed by service
//		sphindroidClient.executeCommand(hypothesis);
		// hack as it is no way in service to access or change the layout
		// attributes like background
		if (LightOnCommand.COMMAND_TRANSCRIPTION.equals(hypothesis)) {
			View layout_call_main = findViewById(R.id.layout_call_main);
			layout_call_main.setBackgroundColor(Color.parseColor("#FFFFFF"));
		} else if (LightOffCommand.COMMAND_TRANSCRIPTION.equals(hypothesis)) {
			View layout_call_main = findViewById(R.id.layout_call_main);
			layout_call_main.setBackgroundColor(Color.parseColor("#000000"));
		}

	}

	private void updateServiceStatus() {
		String bindStatus = sphindroidClient.isBound() ? "bound" : "unbound";
		String startStatus = sphindroidClient.isServiceRunning() ? "started"
				: "not started";
		String statusText = "Service status: " + bindStatus + "," + startStatus;
		// TextView t = (TextView) findViewById(R.id.serviceStatus);
		Toast.makeText(getApplicationContext(), statusText, Toast.LENGTH_SHORT)
				.show();
	}

	public class ServiceBoundAsyncTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			while (!sphindroidClient.isBound()) {
				LOG.debug("[ServiceBoundTimerTask] bound {}",
						sphindroidClient.isBound());
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					LOG.error("Error while sleeping", e);
				}
			}
			runOnUiThread(new Runnable() {
				public void run() {
					onBound();
				}
			});

			return null;
		}

	}

}
