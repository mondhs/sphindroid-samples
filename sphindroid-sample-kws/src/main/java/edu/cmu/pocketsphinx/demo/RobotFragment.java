package edu.cmu.pocketsphinx.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import edu.cmu.pocketsphinx.Hypothesis;

public class RobotFragment extends ShowcaseFragment {

    private TextView resultText;
    private static final String TAG = RobotFragment.class.getSimpleName();

    private ToggleButton toggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.robot, container, false);
        toggleButton = (ToggleButton) v.findViewById(R.id.start_button);
        resultText = (TextView) v.findViewById(R.id.result_text);

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
        Log.i(TAG, ">>> part result from bank: " + hypothesis.getHypstr());
        super.onPartialResult(hypothesis);
        if (hypothesis.getHypstr().equals(PocketSphinxActivity.KEYPHRASE))
            return;
        resultText.setText(hypothesis.getHypstr());
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String command = hypothesis.getHypstr();
        Log.i("BankAccountFragment", ">>> final result from bank: " + command);
        if (command.equals(PocketSphinxActivity.KEYPHRASE))
            return;
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
	if (!state && recognizer.getSearchName().equals(RobotFragment.class.getSimpleName())) {
	    //speech -> silence transition,
	    //utterance ended
	    toggleButton.setChecked(false);
	}
    }
}
