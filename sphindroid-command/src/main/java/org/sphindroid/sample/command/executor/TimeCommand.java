package org.sphindroid.sample.command.executor;

import android.content.Context;

import org.sphindroid.core.service.SfdCoreFactory;
import org.sphindroid.core.service.grammar.GenusEnum;
import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;

import java.text.MessageFormat;
import java.util.Calendar;

/**
 * Created by mgreibus on 14.3.23.
 */
public class TimeCommand implements GeneralCommand {

    private static final String WHAT_TIME_IS_IT= "kiek valandų";



    @Override
    public String execute(String command, Context context) {
        Calendar cal = Calendar.getInstance();
        return createTimeForSpeech(cal);
    }

    @Override
    public boolean isSupport(String command) {
        return WHAT_TIME_IS_IT.equals(command);
    }

    @Override
    public String retrieveCommandSample() {
        return WHAT_TIME_IS_IT;
    }


    private String createTimeForSpeech(Calendar cal) {
        LithuanianGrammarHelperImpl grammarHelper = SfdCoreFactory.getInstance().createLithuanianGrammarHelper();
        int currentHours = cal.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = cal.get(Calendar.MINUTE);
        String timeForSpeech = MessageFormat.format("{0} {1} {2} {3}", grammarHelper.resolveNumber(currentHours, GenusEnum.feminine),
                grammarHelper.matchNounToNumerales(currentHours, "valanda"),//resolveHourName(currentHours),
                grammarHelper.resolveNumber(currentMinutes, GenusEnum.feminine),
                grammarHelper.matchNounToNumerales(currentMinutes, "minutė")//resolveMinuteName(currentMinutes)
        );
        return timeForSpeech;
    }

}
