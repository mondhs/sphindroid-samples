package org.sphindroid.sample;

import java.text.MessageFormat;

import org.sphindroid.core.dto.AsrStatistics;
import org.sphindroid.core.dto.RecognitionContext;
import org.sphindroid.core.service.AsrMessagePublisher;
import org.sphindroid.core.service.AsrRecognitionListener;
import org.sphindroid.core.service.SphindroidResrouceHelper;
import org.sphindroid.lib.async.AsrRecordAudioTaskImpl;
import org.sphindroid.lib.async.AsrWavAudioTaskImpl;
import org.sphindroid.lib.async.SphindroidRecognizer;
import org.sphindroid.lib.service.SphindroidFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class SimpleActivity extends Activity {

	static {
		System.loadLibrary("pocketsphinx_jni");
	}

	private SphindroidResrouceHelper sphindroidResrouceHelper;
	private SphindroidRecognizer sphindroidRecognizer;
	private SimpleAsrMessagePublisher messagePublisher; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		messagePublisher = new SimpleAsrMessagePublisher();
		SphindroidFactory factory = SphindroidFactory.getInstance();
		sphindroidResrouceHelper = factory
				.createSphindroidResrouceHelper(this);
		SphindroidFactory.getInstance().changeAsrMessagePublisher(messagePublisher);
		//add action handlers for button
		initUI();
		//Start Sphindrod thread.
		initSphinxdroid();

	}

	@Override
	protected void onDestroy() {
		sphindroidResrouceHelper.cleanUp(sphindroidRecognizer.getCtx());
		super.onDestroy();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_self_test:
			selfTest();
			break;
		case R.id.menu_reinit:
			sphindroidRecognizer.reinit();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void selfTest() {
		SphindroidFactory.getInstance().changeAsrAudioRunnable(
				new AsrWavAudioTaskImpl(getCtx().getSelfTestDir()));
		sphindroidRecognizer.start();
		sphindroidRecognizer.stop();

	}

	private void initUI() {
		final Button listenButton = (Button) findViewById(R.id.btnListen);
		listenButton.setEnabled(false);
		listenButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					SphindroidFactory.getInstance().changeAsrAudioRunnable(
							new AsrRecordAudioTaskImpl());
					sphindroidRecognizer.start();
					break;
				case MotionEvent.ACTION_UP:
					sphindroidRecognizer.stop();
					break;
				default:
					;
				}
				return false;
			}
		});

	}

	/**
	 * @param acousticModelCode
	 * 
	 */
	private void initSphinxdroid() {
		SphindroidFactory factory = SphindroidFactory.getInstance();
		this.sphindroidRecognizer = factory.createDefaultSphindroidRecognizer(
				this, new MainActivityRecognitionListener());
		sphindroidRecognizer.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}





	public class MainActivityRecognitionListener implements
			AsrRecognitionListener {


		@Override
		public void onPartialResults(AsrStatistics stats) {
			//Do nothing
		}

		@Override
		public void onResults(AsrStatistics stats) {
			messagePublisher.publishMessage(MessageFormat.format(
					"Result {0}[{1}]", stats.getHypothesis(),
					stats.getBestScore()));
		}

		@Override
		public void onError(int err) {
			messagePublisher.publishMessage("" + err);
		}

		@Override
		public void onEndOfSpeech() {
			//Do nothing
		}

		@Override
		public void ready() {

			messagePublisher.publishMessage("Ready: ");
			StringBuilder content = sphindroidResrouceHelper.readFromFile(sphindroidRecognizer.getCtx()
					.getFileJsgf());
			messagePublisher.publishMessage(content.toString());

			runOnUiThread(new Runnable() {
				public void run() {
					((Button) findViewById(R.id.btnListen)).setEnabled(true);
				}
			});

		}

	}
	
	class SimpleAsrMessagePublisher implements AsrMessagePublisher{

		@Override
		public void publishMessage(final String format, Object... args) {
			final String message = MessageFormat.format(format, args);
			runOnUiThread(new Runnable() {
				public void run() {
					TextView out = ((TextView) findViewById(R.id.outputText));
					out.append(message + "\n");
				}
			});
		}
		
	}

	public RecognitionContext getCtx() {
		return sphindroidRecognizer.getCtx();
	}

}
