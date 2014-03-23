package org.sphinxdroid.demo.command;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mgreibus on 14.3.23.
 */
public class CommandExecutor implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private Context context;

    public final List<GeneralCommand> commandList = new ArrayList<GeneralCommand>();
    private static final String TAG = CommandExecutor.class.getSimpleName();



    public void init(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, this);
        commandList.add(new HiCommand());
        commandList.add(new HowdyCommand());
        commandList.add(new ViewNewsCommand(context));
        commandList.add(new TimeCommand(context));
        commandList.add(new WeatherCommand(context));
        commandList.add(new FlashlightCommand(context));
        commandList.add(new CalculatorCommand());
        commandList.add(new HelpCommand(commandList));


    }


    public void execute(String command){
        for (GeneralCommand commandExecutor: commandList){
            if (commandExecutor.isSupport(command)){
                String textForSpeak = commandExecutor.execute(command);
                speak(textForSpeak);
                break;
            }
        }
    }




    ////////////////////// TTS /////////////////////////////////////
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("LT","lt"));
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "This Language is not supported");
            }
        } else {
            Log.e(TAG,"TTS Initilization Failed!");
        }
    }

    public boolean speak(String message) {
        if(message != null) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
        return true;
    }
    ////////////////////// TTS /////////////////////////////////////

    public Context getContext() {
        return context;
    }
}
