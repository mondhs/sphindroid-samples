package edu.cmu.pocketsphinx.demo;

import java.io.File;
import java.io.IOException;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.FsgModel;
import edu.cmu.pocketsphinx.Jsgf;
import edu.cmu.pocketsphinx.JsgfRule;
import edu.cmu.pocketsphinx.NGramModel;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SphinxUtil;

public class PocketSphinxActivity extends Activity {

    protected static String KWS_SRCH_NAME = "wakeup_search";
    protected static String KEYPHRASE = "GERAI LIEPA";
    private static final String TAG = PocketSphinxActivity.class.getSimpleName();

    static {
        System.loadLibrary("pocketsphinx_jni");
    }
    
    private static String joinPath(File dir, String path) {
        return new File(dir, path).getPath();
    }
    
    private ActionBar tabBar;
    private SpeechRecognizer recognizer;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        
        File appDir;
        try {
            appDir = SphinxUtil.syncAssets(getApplicationContext());
        } catch (IOException e) {
            Log.e(TAG, "IO Exception", e);
            throw new RuntimeException(e);
        }
        Config config = Decoder.defaultConfig();
        config.setString("-dict", joinPath(appDir, "acoustic_model/lt_lt/lm/robotas.dict"));
        config.setString("-hmm", joinPath(appDir, "acoustic_model/lt_lt/hmm"));
        config.setString("-rawlogdir", appDir.getPath());
        config.setInt("-maxhmmpf", 10000);
        config.setBoolean("-fwdflat", false);
        config.setBoolean("-bestpath", false);
        config.setFloat("-kws_threshold", 1e-300);
        recognizer = new SpeechRecognizer(config);
        
        recognizer.setKws(KWS_SRCH_NAME, KEYPHRASE);
        Jsgf jsgf = new Jsgf(joinPath(appDir, "acoustic_model/lt_lt/lm/robotas.gram"));
        JsgfRule rule = jsgf.getRule("<robotas.COMMAND>");
        int lw = config.getInt("-lw");
        FsgModel fsg = jsgf.buildFsg(rule, recognizer.getLogmath(), lw);
        recognizer.setFsg(RobotFragment.class.getSimpleName(), fsg);
        recognizer.setSearch(RobotFragment.class.getSimpleName());

        tabBar = getActionBar();
        tabBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        Tab t = tabBar.newTab();
        t.setText("Robot Control");
        t.setTabListener(newTabListener(RobotFragment.class, state));
        tabBar.addTab(t);


        if (null != state)
            tabBar.setSelectedNavigationItem(state.getInt("tab", 0));
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
}
