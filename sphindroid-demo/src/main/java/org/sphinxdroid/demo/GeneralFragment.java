package org.sphinxdroid.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.sphinxdroid.demo.command.CommandExecutor;

import edu.cmu.pocketsphinx.Hypothesis;

public class GeneralFragment extends ShowcaseFragment {

    private TextView resultText;
    private static final String TAG = GeneralFragment.class.getSimpleName();

    private ToggleButton toggleButton;
    private CommandExecutor commandExecutor;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.robot, container, false);
        toggleButton = (ToggleButton) v.findViewById(R.id.start_button);
        resultText = (TextView) v.findViewById(R.id.result_text);
        commandExecutor = new CommandExecutor();
        commandExecutor.init(getActivity().getApplicationContext());
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        toggleButton.setChecked(false);
        toggleButton.setOnCheckedChangeListener(this);
    }



    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        Log.i(TAG, ">>> part result: " + hypothesis.getHypstr());
        super.onPartialResult(hypothesis);
        if (hypothesis.getHypstr().equals(MainDemoActivity.KEYPHRASE))
            return;
        resultText.setText(hypothesis.getHypstr());
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        Log.i(TAG, ">>> final result: " + command);
        if (command.equals(MainDemoActivity.KEYPHRASE))
            return;
        commandExecutor.execute(command);
        resultText.setText(command);
    }
    
    @Override
    protected void setButtonPressed() {
        toggleButton.setChecked(true);
    }

    private void notify(int resId, Object... args) {
        String text = context.getString(resId, args);
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVadStateChanged(boolean state) {
	if (!state && recognizer.getSearchName().equals(GeneralFragment.class.getSimpleName())) {
	    //speech -> silence transition,
	    //utterance ended
	    toggleButton.setChecked(false);
	}
    }
}
