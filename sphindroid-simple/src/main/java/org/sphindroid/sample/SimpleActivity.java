package org.sphindroid.sample;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

public class SimpleActivity extends Activity implements
        RecognitionListener,CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SimpleActivity.class.getSimpleName();

    private SpeechRecognizer recognizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        File appDir;
        try {
            Assets assets = new Assets(SimpleActivity.this);
            appDir = assets.syncAssets();
        } catch (IOException e) {
            Log.e(TAG, "syncAssets failed", e);
            throw new RuntimeException("failed to synchronize assets", e);
        }

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(appDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(appDir, "acoustic_model/lt_lt/dict/numeriai.dict"))
                .setRawLogDir(appDir)
                .setKeywordThreshold(1e-20f)
                .getRecognizer();

        recognizer.addListener(this);

        File demoGrammar = new File(appDir, "acoustic_model/lt_lt/lm/numeriai.gram");

        recognizer.addGrammarSearch("demoGrammar", demoGrammar);

        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.start_button);
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(this);


    }




    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        Log.w(TAG, "[onCheckedChanged]: checked " + checked);
        recognizer.startListening("demoGrammar");
    }

    @Override
    protected void onDestroy() {
        recognizer.stop();
        super.onDestroy();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
        String uttid = hypothesis.getUttid();
        Log.d(TAG, "[onResult]>>>  result: [" + uttid + "]: "+ message);
		TextView out = ((TextView) findViewById(R.id.outputText));
		out.append(message + "\n");
    }

}
