package org.sphindroid.sample.command;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.sphindroid.sample.command.executor.CommandExecutor;
import org.sphindroid.sample.command.executor.GeneralCommand;

import java.util.HashSet;
import java.util.Set;

import edu.cmu.pocketsphinx.Hypothesis;

public class GeneralFragment extends ShowcaseFragment {

    private TextView resultText;
    private static final String TAG = GeneralFragment.class.getName();

    private ToggleButton toggleButton;
    private CommandExecutor commandExecutor;
    private ProgressBar progressBar;
    private Set<String> executedCommandUttId = new HashSet<String>();


    public GeneralFragment(){
        this.commandExecutor = new CommandExecutor();
    }


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
        commandExecutor.init(context);
        return v;
    }

    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.w(TAG, "[onStart]");
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(this);
    }

    /**
     *
     * @param hypothesis
     */
    @Override
    public void processPartialGrammarResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        String uttid = hypothesis.getUttid();
        Log.w(TAG, "[processPartialGrammarResult]>>> part result: [" + uttid + "]: "+ command);
        executeIfCan(uttid, command);
        resultText.setText(hypothesis.getHypstr());
    }

    /**
     *
     * @param hypothesis
     */
    public void processGrammarResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        String uttid = hypothesis.getUttid();
        Log.w(TAG, "[processGrammarResult] final result: [" + uttid + "]: " + command);
        executeIfCan(uttid, command);
        resultText.setText(command);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        super.onPartialResult(hypothesis);
    }

    /**
     *
     * @param uttid
     * @param command
     */
    public void executeIfCan(String uttid, String command){
        Log.w(TAG, "[executeIfCan]: command [" + uttid + "]: "  + command);
        GeneralCommand generalCommand = commandExecutor.findCommand(command);
        if(generalCommand != null && !executedCommandUttId.contains(uttid)){
            executedCommandUttId.add(uttid);
            switchToPause();
            commandExecutor.execute(generalCommand, command, getActivity().getApplicationContext());
            switchToSpotting();
        }
    }

    /**
     *
     */
    @Override
    protected void prepareForRecognition() {
       Log.w(TAG, "[prepareForRecognition]");
       progressBar.setVisibility(View.VISIBLE);
       toggleButton.setChecked(true);
    }

    @Override
    protected void finalizeRecognition() {
        Log.w(TAG, "[finalizeRecognition]");
        progressBar.setVisibility(View.GONE);
        toggleButton.setChecked(false);
    }


    private void notify(int resId, Object... args) {
        String text = context.getString(resId, args);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



}
