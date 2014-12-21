package org.sphindroid.sample.command;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainDemoActivity extends Activity {

    protected static String KWS_SEARCH_NAME = "wakeup_search";
    protected static String KEYPHRASE = "gerai berže";
    private static final String TAG = MainDemoActivity.class.getName();


    private ActionBar tabBar;
    private SpeechRecognizer recognizer;
    private CommandAppContext commandAppContext;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        Log.w(TAG, "[onCreate]");
        loadPreferences();

        File appDir;
        try {
            Assets assets = new Assets(MainDemoActivity.this);
            appDir = assets.syncAssets();
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            throw new RuntimeException(e);
        }
        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(appDir, "acoustic_model/lt_lt/hmm"))
                .setDictionary(new File(appDir, "acoustic_model/lt_lt/dict/demo.dict"))
                //.setRawLogDir(appDir)
                .setKeywordThreshold(1e-40f)
                .getRecognizer();

//        recognizer.addListener(this);

        recognizer.addKeyphraseSearch(KWS_SEARCH_NAME, KEYPHRASE);

        File demoGrammar = new File(appDir, "acoustic_model/lt_lt/lm/demo.gram");

        recognizer.addGrammarSearch(GeneralFragment.class.getSimpleName(), demoGrammar);


        tabBar = getActionBar();
        tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab t = tabBar.newTab();
        t.setText("Pokalbis");
        t.setTabListener(newTabListener(GeneralFragment.class, state));
        tabBar.addTab(t);


        if (null != state)
            tabBar.setSelectedNavigationItem(state.getInt("tab", 0));
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
        Log.w(TAG, "[loadPreferences]");
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
        outState.putInt("tab", tabBar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    <T extends Fragment> TabListener newTabListener(Class<T> c, Bundle state) {
        return new TabFragmentListener<T>(this, c.getSimpleName(), c, state);
    }

    public CommandAppContext getCommandAppContext() {
        return commandAppContext;
    }
}
