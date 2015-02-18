package org.sphindroid.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SimpleActivity extends Activity
        implements RecognitionListener,CompoundButton.OnCheckedChangeListener
{
    private static final String TAG = SimpleActivity.class.getCanonicalName();

    private SpeechRecognizer recognizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(SimpleActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    throw new IllegalArgumentException("Something bad happen");
                } else {
                    ToggleButton toggleButton = (ToggleButton) findViewById(R.id.start_button);
                    toggleButton.setChecked(false);
                    toggleButton.setOnCheckedChangeListener(SimpleActivity.this);
                }
            }
        }.execute();
    }

    private void setupRecognizer(File assetDir) throws IOException {

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(assetDir, "acoustic_model/lt_lt/dict/numeriai.dict"))
                .getRecognizer();

        recognizer.addListener(this);

        File grammar = new File(assetDir, "acoustic_model/lt_lt/lm/numeriai.gram");

        recognizer.addGrammarSearch("grammar", grammar);

    }



    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        Log.w(TAG, "[onCheckedChanged]: checked " + checked);
        recognizer.startListening("grammar");
    }

    @Override
    protected void onDestroy() {
        recognizer.stop();
        super.onDestroy();
    }


    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        ((ToggleButton) findViewById(R.id.start_button)).setChecked(false);
        recognizer.stop();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String message = hypothesis.getHypstr();
        Log.d(TAG, "[onResult]>>>  result: "+ message);
		TextView out = ((TextView) findViewById(R.id.outputText));
		out.append(message + "\n");
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "[onError]>>> ", e);
    }

    @Override
    public void onTimeout() {
        Log.e(TAG, "[onTimeout]>>> ");
    }

}
