package org.sphindroid.sample.command;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.sphindroid.sample.command.dto.CommandAppContext;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public abstract class ShowcaseFragment extends Fragment implements
        OnCheckedChangeListener, RecognitionListener {

    private static final String TAG = ShowcaseFragment.class.getName();

    protected Context context;
    protected SpeechRecognizer recognizer;
    
    private Vibrator vibrator;
    private CommandAppContext commandAppContext;
//    private boolean sleeping;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "[onCreate] ");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        this.commandAppContext = ((MainDemoActivity) context).getCommandAppContext();
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "[onStart] ");
        recognizer = ((MainDemoActivity) context).getRecognizer();
        recognizer.addListener(this);
        if(this.commandAppContext.getAutoVad()) {
            recognizer.startListening(MainDemoActivity.KWS_SEARCH_NAME);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w(TAG, "[onStop] ");
        recognizer.stop();
        recognizer.removeListener(this);
        recognizer = null;
    }
    
    private void switchToRecognition() {
        Log.w(TAG, "[switchToRecognition]");
        recognizer.stop();
        vibrator.vibrate(300);
        try{ Thread.sleep(300); }catch(InterruptedException e){ }
        recognizer.startListening(((Object)this).getClass().getSimpleName());
        prepareForRecognition();
    }

    protected void switchToPause() {
        Log.w(TAG, "[switchToPause]" );
        recognizer.stop();
    }


    protected void switchToSpotting() {
        Log.w(TAG, "[switchToSpotting]");
        recognizer.stop();
        if(this.commandAppContext.getAutoVad()) {
            try{ Thread.sleep(300); }catch(InterruptedException e){ }
            recognizer.startListening(MainDemoActivity.KWS_SEARCH_NAME);
        }
        finalizeRecognition();
    }
    
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
//        if (sleeping && hypothesis.getHypstr().equals(MainDemoActivity.KEYPHRASE))
//            //keyphrase detected. equivalent to toggle button pressed
//            setButtonPressed();
//        Log.w(TAG, "[onPartialResult]: " + recognizer.getSearchName());
        Log.w(TAG, "[onPartialResult]: getHypstr " + hypothesis.getHypstr());
        if (MainDemoActivity.KWS_SEARCH_NAME.equals(recognizer.getSearchName()) ){
            String text = hypothesis.getHypstr();
            if(MainDemoActivity.KEYPHRASE.equals(text)){
                switchToRecognition();
            }
        }else{
            //grammar recognition
            //processPartialGrammarResult(hypothesis);
        }
    }

    public void onResult(Hypothesis hypothesis) {
        Log.w(TAG, "[onResult]: " + recognizer.getSearchName());
        if(hypothesis == null){
            Log.w(TAG, "[onResult]: Hypothesis is null. will not process");
            return;
        }
        if (MainDemoActivity.KWS_SEARCH_NAME.equals(recognizer.getSearchName()) ){
            String text = hypothesis.getHypstr();
            if(MainDemoActivity.KEYPHRASE.equals(text)){
                switchToRecognition();
            }
        } else{
            //grammar recognition
            processGrammarResult(hypothesis);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        Log.w(TAG, "[onCheckedChanged]: checked " + checked);

        if (checked)
            switchToRecognition();
        else
            switchToSpotting();
    }

    public abstract void processPartialGrammarResult(Hypothesis hypothesis);
    public abstract void processGrammarResult(Hypothesis hypothesis);
    protected abstract void prepareForRecognition();
    protected abstract void finalizeRecognition();

    @Override
    public void onBeginningOfSpeech() {
//        toggleButton.setChecked(true);
        Log.w(TAG, "[onBeginningOfSpeech]" );
    }


    @Override
    public void onEndOfSpeech() {
//        toggleButton.setChecked(false);
        Log.w(TAG, "[onEndOfSpeech]" );
        switchToSpotting();
    }

//    protected abstract void setButtonPressed();
}