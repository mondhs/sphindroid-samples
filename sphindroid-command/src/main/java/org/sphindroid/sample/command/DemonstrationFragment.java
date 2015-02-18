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

import edu.cmu.pocketsphinx.Hypothesis;

public class DemonstrationFragment extends ShowcaseFragment {


    private static final String TAG = DemonstrationFragment.class.getSimpleName();

//    private ToggleButton toggleButton;
    private CommandExecutor commandExecutor;
//    private ProgressBar progressBar;


    public DemonstrationFragment(){
        this.commandExecutor = new CommandExecutor();
    }




    /**
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        commandExecutor.init(context);
        Log.w(TAG, "[onStart]");
    }

    /**
     *
     * @param hypothesis
     */
    @Override
    public boolean processRecognitionPartialResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        Log.w(TAG, "[processPartialGrammarResult]>>> part result: "+ command);
        updateRecognitionResults(hypothesis.getHypstr());
        boolean executed = executeIfCan(command);
        return executed;
    }

    /**
     *
     * @param hypothesis
     */
    public void processRecognitionResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        Log.d(TAG, "[processGrammarResult] final result" + command);
//        executeIfCan(command);
        updateRecognitionResults(command);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        super.onPartialResult(hypothesis);
    }

    /**
     *
     * @param command
     */
    public boolean executeIfCan(   String command){
        Log.w(TAG, "[executeIfCan]: "  + command);
        GeneralCommand generalCommand = commandExecutor.findCommand(command);
        if(generalCommand != null){
            return commandExecutor.execute(generalCommand, command, getActivity().getApplicationContext());
        }
        return false;
    }

    /**
     *
     */



    private void notify(int resId, Object... args) {
        String text = context.getString(resId, args);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }



}
