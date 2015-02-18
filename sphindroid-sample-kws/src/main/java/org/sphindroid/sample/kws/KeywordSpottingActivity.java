package org.sphindroid.sample.kws;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
        setContentView(R.layout.main);

//        try {
//            Assets assets = new Assets(KeywordSpottingActivity.this);
//            appDir = assets.syncAssets();
//        } catch (IOException e) {
//            Log.e(TAG,"syncAssets failed", e);
//                throw new RuntimeException("failed to synchronize assets", e);
//        }

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(KeywordSpottingActivity.this);
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
                    switchSearch(KWS_SEARCH_NAME);
                }
            }
        }.execute();



    }

    private void setupRecognizer(File assetDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(assetDir, "acoustic_model/lt_lt/dict/robotas.dict"))
                //.setRawLogDir(appDir)
                .setKeywordThreshold(1e-40f)
                .getRecognizer();
        recognizer.addListener(this);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH_NAME, KEYPHRASE);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if(hypothesis == null){
            return;
        }
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
    @Override
    public void onError(Exception e) {
        Log.e(TAG, "[onError]>>> ", e);
    }

    @Override
    public void onTimeout() {
        Log.e(TAG, "[onTimeout]>>> ");
    }
}