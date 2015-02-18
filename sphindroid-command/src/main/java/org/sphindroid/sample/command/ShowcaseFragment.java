package org.sphindroid.sample.command;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.sphindroid.sample.command.dto.CommandAppContext;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

public abstract class ShowcaseFragment extends Fragment implements
        RecognitionListener {

    private static final String TAG = ShowcaseFragment.class.getSimpleName();

    protected Context context;
    protected SpeechRecognizer recognizer;
    
    private Vibrator vibrator;
    private ToggleButton toggleButton;

    private CommandAppContext commandAppContext;
    private TextView resultText;
    private ProgressBar progressBar;
//    private boolean sleeping;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w(TAG, "[onCreateView]");
        View v = inflater.inflate(R.layout.general_fragment, container, false);
        this.toggleButton = (ToggleButton) v.findViewById(R.id.start_button);
        this.resultText = (TextView) v.findViewById(R.id.result_text);
        this.progressBar = (ProgressBar) v.findViewById(R.id.recognitionProgressBar);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "[onCreate] ");
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
            switchSearch(MainDemoActivity.KWS_SEARCH);
        }
        toggleButton.setChecked(false);
        if(getCommandAppContext().getAutoVad()) {
            toggleButton.setOnCheckedChangeListener(new AutoVadCheckBox());
        }else{
            toggleButton.setOnCheckedChangeListener(new ManualVadCheckBox());
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.w(TAG, "[onStop] ");
        getCommandAppContext().setListening(false);
        recognizer.stop();
        finalizeRecognition();
        recognizer.removeListener(this);
        recognizer = null;
    }
    
//    private void switchToRecognition_() {
//        Log.w(TAG, "[switchToRecognition]");
//        recognizer.stop();
//        vibrator.vibrate(300);
//        Log.w(TAG, "[switchToRecognition] pre sleep");
//        try{ Thread.sleep(400); }catch(InterruptedException e){ }
//        Log.w(TAG, "[switchToRecognition] post sleep");
//        recognizer.startListening(((Object)this).getClass().getSimpleName());
//        prepareForRecognition();
//    }
    private void switchSearch(String searchName) {
        Log.w(TAG, "[switchSearch] searchName: " + searchName);
        if(getCommandAppContext().getListening()) {
            recognizer.stop();
            finalizeRecognition();
            getCommandAppContext().setListening(false);
        }

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(MainDemoActivity.KWS_SEARCH)) {
            recognizer.startListening(searchName);
            getCommandAppContext().setListening(true);
        }else {
            getCommandAppContext().setListening(true);
            prepareForRecognition();
            recognizer.startListening(searchName, 10000);
        }

    }

    private void switchToPause() {
        Log.w(TAG, "[switchToPause] is listening: " +  getCommandAppContext().getListening());
        if(getCommandAppContext().getListening()){
            recognizer.stop();
            finalizeRecognition();
        }
    }


//    protected void switchToSpotting_() {
//        Log.w(TAG, "[switchToSpotting]");
//        recognizer.stop();
//        if(this.commandAppContext.getAutoVad()) {
//            Log.w(TAG, "[switchToSpotting] pre sleep");
//            try{ Thread.sleep(300); }catch(InterruptedException e){ }
//            Log.w(TAG, "[switchToSpotting] post sleep");
//            recognizer.
//            recognizer.startListening(MainDemoActivity.KWS_SEARCH_NAME);
//        }
//        finalizeRecognition();
//    }
    
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
//        if (sleeping && hypothesis.getHypstr().equals(MainDemoActivity.KEYPHRASE))
//            //keyphrase detected. equivalent to toggle button pressed
//            setButtonPressed();
//        Log.w(TAG, "[onPartialResult]: " + recognizer.getSearchName());
        if(hypothesis == null){
            return;
        }
        String text = hypothesis.getHypstr();
        Log.w(TAG, "[onPartialResult]: getHypstr " + hypothesis.getHypstr());
        if (text.equals(MainDemoActivity.KEYPHRASE)){
//            switchToRecognition();
            switchSearch(DemonstrationFragment.class.getSimpleName());
        }else{
            if(processRecognitionPartialResult(hypothesis)){
                Log.d(TAG, "[onPartialResult]: successfully executed ");
                if(getCommandAppContext().getAutoVad()){
                    switchSearch(MainDemoActivity.KWS_SEARCH);
                }else{
                    Log.d(TAG, "[onPartialResult]: getAutoVad " + getCommandAppContext().getAutoVad());
                    switchToPause();
                }

            }
        }
//        if (MainDemoActivity.KWS_SEARCH_NAME.equals(recognizer.getSearchName()) ){
//            String text = hypothesis.getHypstr();
//            if(MainDemoActivity.KEYPHRASE.equals(text)){
//                switchToRecognition();
//            }
//        }else{
//            //grammar recognition
//            processPartialGrammarResult(hypothesis);
//        }
    }

    public void onResult(Hypothesis hypothesis) {
        Log.w(TAG, "[onResult]: " + recognizer.getSearchName());
        if(hypothesis == null){
            Log.w(TAG, "[onResult]: Hypothesis is null. will not process");
            return;
        }
        processRecognitionResult(hypothesis);
//        if (MainDemoActivity.KWS_SEARCH_NAME.equals(recognizer.getSearchName()) ){
//            String text = hypothesis.getHypstr();
//            if(MainDemoActivity.KEYPHRASE.equals(text)){
//                switchToRecognition();
//            }
//        } else{
//            //grammar recognition
//            processGrammarResult(hypothesis);
//        }
    }




    public abstract boolean processRecognitionPartialResult(Hypothesis hypothesis);
    public abstract void processRecognitionResult(Hypothesis hypothesis);

//    protected abstract void prepareForRecognition();
//    protected abstract void finalizeRecognition();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBeginningOfSpeech() {
//        toggleButton.setChecked(true);
        Log.w(TAG, "[onBeginningOfSpeech]" );
    }


    @Override
    public void onEndOfSpeech() {
        Log.w(TAG, "[onEndOfSpeech]" );
        if(!getCommandAppContext().getAutoVad()){
            finalizeRecognition();
        }else {
            if (!recognizer.getSearchName().equals(MainDemoActivity.KWS_SEARCH)) {
                switchSearch(MainDemoActivity.KWS_SEARCH);
            }
        }
    }

    @Override
    public void onError(Exception e) {
        Log.e(TAG, "[onError]", e);
    }

    @Override
    public void onTimeout() {
        Log.e(TAG, "[onTimeout]");
        switchSearch(MainDemoActivity.KWS_SEARCH);
    }

    //    protected abstract void setButtonPressed();

    public CommandAppContext getCommandAppContext() {
        return commandAppContext;
    }

    protected void prepareForRecognition() {
        Log.w(TAG, "[prepareForRecognition]");
        progressBar.setVisibility(View.VISIBLE);
        toggleButton.setChecked(true);
        vibrator.vibrate(100);
        try{ Thread.sleep(150); }catch(InterruptedException e){ }
    }

    protected void finalizeRecognition() {
        Log.w(TAG, "[finalizeRecognition]");
        progressBar.setVisibility(View.GONE);
        toggleButton.setChecked(false);
        try{ Thread.sleep(150); }catch(InterruptedException e){ }
        vibrator.vibrate(100);
        try{ Thread.sleep(150); }catch(InterruptedException e){ }
    }
    protected void updateRecognitionResults(String text) {
        resultText.setText(text);
    }



    class AutoVadCheckBox implements OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton button, boolean checked) {
            Log.w(TAG, "[onCheckedChanged]: checked " + checked);
        }
    }

    class ManualVadCheckBox implements OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton button, boolean checked) {
            Log.w(TAG, "[onCheckedChanged]: checked " + checked);
            if (checked) {
                prepareForRecognition();
                switchSearch(DemonstrationFragment.class.getSimpleName());
            }else{
                finalizeRecognition();
                switchToPause();
            }

        }
    }
}