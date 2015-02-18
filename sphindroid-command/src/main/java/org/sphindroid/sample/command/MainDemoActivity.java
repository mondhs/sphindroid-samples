package org.sphindroid.sample.command;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.sphindroid.sample.command.dto.CommandAppContext;
import org.sphindroid.sample.command.ui.SetPreferenceActivity;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import android.support.v7.app.ActionBarActivity;

public class MainDemoActivity extends ActionBarActivity {

    protected static String KWS_SEARCH = "wakeup";
    protected static String KEYPHRASE = "gerai berže";
    private static final String TAG = MainDemoActivity.class.getSimpleName();


//    private ActionBar tabBar;
    private SpeechRecognizer recognizer;
    private CommandAppContext commandAppContext;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "[onCreate]");
        loadPreferences();
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(MainDemoActivity.this);
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
                    recognitionReady(savedInstanceState);
                }
            }
        }.execute();


    }

    /**
     *
     * @param savedInstanceState
     */
    private void recognitionReady(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DemonstrationFragment())
                    .commit();
        }

    }

    /**
     *
     * @param assetDir
     * @throws IOException
     */
    private void setupRecognizer(File assetDir) throws IOException {
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(assetDir, "acoustic_model/lt_lt/dict/lt_lt.dict"))
                        //.setRawLogDir(appDir)
                .setKeywordThreshold(1e-45f)
                .getRecognizer();

        File demoGrammar = new File(assetDir, "acoustic_model/lt_lt/lm/demo_lt.gram");
        recognizer.addGrammarSearch(DemonstrationFragment.class.getSimpleName(), demoGrammar);
        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        getCommandAppContext().setListening(false);
    }

   /**
   * Because it's onlt ONE option in the menu.
   * In order to make it simple, We always start SetPreferenceActivity
   * without checking.
   */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();
        intent.setClass(this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //To make it simple, always re-load Preference setting.
        loadPreferences();
    }

    private void loadPreferences() {
        Log.d(TAG, "[loadPreferences]");
        if(this.commandAppContext  == null){
            this.commandAppContext = new CommandAppContext();
        }
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.commandAppContext.setAutoVad(mySharedPreferences.getBoolean("autoVad", false));

    }

    public SpeechRecognizer getRecognizer() {
        return recognizer;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt("tab", tabBar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }


    public CommandAppContext getCommandAppContext() {
        return commandAppContext;
    }
}
