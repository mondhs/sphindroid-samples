package org.sphindroid.sample.command.executor;

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


    public final List<GeneralCommand> commandList = new ArrayList<GeneralCommand>();
    private static final String TAG = CommandExecutor.class.getSimpleName();



    public void init(Context context) {
        if(commandList.size()==0) {
            tts = new TextToSpeech(context, this);
            commandList.add(new HiCommand());
            commandList.add(new HowdyCommand());
            commandList.add(new ViewNewsCommand());
            commandList.add(new TimeCommand());
            commandList.add(new WeatherCommand());
//            commandList.add(new FlashlightCommand());
            commandList.add(new CalculatorCommand());
            commandList.add(new CurrencyConverterCommand());
            commandList.add(new HelpCommand(commandList));
        }


    }
    public GeneralCommand findCommand(String command){
        for (GeneralCommand commandExecutor: commandList){
            if (commandExecutor.isSupport(command)){
                Log.w(TAG, "[findCommand] found: " + commandExecutor.getClass().getSimpleName());
                return commandExecutor;
            }
        }
        return null;
    }

    public boolean execute(GeneralCommand generalCommand, String command, Context context){
            if (generalCommand.isSupport(command)){
                String textForSpeak = generalCommand.execute(command, context);
//                try{ Thread.sleep(300); }catch(InterruptedException e){ Log.e(TAG,"bad",e);}
                speak(textForSpeak);
                return true;
            }
        return false;
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
            Log.w(TAG, "[speak]" + message);
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
        return true;
    }
    ////////////////////// TTS /////////////////////////////////////


}
