package org.sphindroid.sample.kws;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class KeywordSpottingActivity extends Activity implements
        RecognitionListener {

    private static final String TAG = KeywordSpottingActivity.class.getSimpleName();

    private static final String KWS_SEARCH_NAME = "wakeup";
    private static final String KEYPHRASE = "gerai berže";

    private SpeechRecognizer recognizer;
    private final Map<String, Integer> captions = new HashMap<String, Integer>();

    public KeywordSpottingActivity() {
        captions.put(KWS_SEARCH_NAME, R.string.kws_caption);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        File appDir;

        try {
            Assets assets = new Assets(KeywordSpottingActivity.this);
            appDir = assets.syncAssets();
        } catch (IOException e) {
            Log.e(TAG,"syncAssets failed", e);
                throw new RuntimeException("failed to synchronize assets", e);
        }

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(appDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(appDir, "acoustic_model/lt_lt/dict/robotas.dict"))
                .setRawLogDir(appDir)
                .setKeywordThreshold(1e-20f)
                .getRecognizer();

        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH_NAME, KEYPHRASE);

        setContentView(R.layout.main);
        switchSearch(KWS_SEARCH_NAME);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        String text = hypothesis.getHypstr();
        Log.d(TAG, "on partial: " + text);
        ((TextView) findViewById(R.id.result_text)).setText(text);

        if (text.equals(KEYPHRASE)) {
            switchSearch(KWS_SEARCH_NAME);
        }

        ((TextView) findViewById(R.id.result_text)).setText(text);

    }

    private void switchSearch(String searchName) {
        Log.d(TAG, "switchSearch" + searchName);
        recognizer.stop();
        recognizer.startListening(searchName);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) findViewById(R.id.caption_text)).setText(caption);
        Toast.makeText(getApplicationContext(), searchName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String text = "Neatpažinta";
        if (hypothesis != null){
            text = hypothesis.getHypstr();
        }
        ((TextView) findViewById(R.id.result_text)).setText(text);
        Log.d(TAG, "on partial: " + text);
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
       Log.d(TAG, "onEndOfSpeech");
       switchSearch(KWS_SEARCH_NAME);
    }
}