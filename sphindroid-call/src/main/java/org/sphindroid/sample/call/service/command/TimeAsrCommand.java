package org.sphindroid.sample.call.service.command;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.service.CoreFactory;
import org.sphindroid.core.service.grammar.GenusEnum;
import org.sphindroid.core.service.grammar.LithuanianGrammarHelperImpl;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;

public class TimeAsrCommand extends AbstractTtsAsrCommand {
	private static final Logger LOG = LoggerFactory
			.getLogger(TimeAsrCommand.class);

	private static final String KEY_COMMAND = "WHAT_TIME_IS_IT";
	public static final String COMMAND_TRANSCRIPTION = "KIEK VALANDŲ";

	

	public TimeAsrCommand(Context context) {
		super(context);
	}

	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		return COMMAND_TRANSCRIPTION.equals(commandDto.getCommandName());
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dict = new HashSet<String>();
		for (String string : COMMAND_TRANSCRIPTION.split(" ")) {
			dict.add(string);
		}
		return dict;
	}

	@Override
	public Map<String, String> getCommandMap() {
		Map<String, String> cmdMap = new HashMap<String, String>();
		cmdMap.put(KEY_COMMAND, COMMAND_TRANSCRIPTION);
		return cmdMap;
	}

	@Override
	public boolean execute(AsrCommandParcelable commandDto) {
		Calendar cal = Calendar.getInstance();
		speak(createTimeForSpeech(cal));
		return true;
	}

	private String createTimeForSpeech(Calendar cal) {
		LithuanianGrammarHelperImpl grammarHelper = CoreFactory.getInstance().createLithuanianGrammarHelper();
		int currentHours = cal.get(Calendar.HOUR_OF_DAY);
		int currentMinutes = cal.get(Calendar.MINUTE);
		String timeForSpeech = MessageFormat.format("{0} {1} {2} {3}", grammarHelper.resolveNumber(currentHours, GenusEnum.feminine),
				grammarHelper.matchNounToNumerales(currentHours, "valanda"),//resolveHourName(currentHours), 
				grammarHelper.resolveNumber(currentMinutes, GenusEnum.feminine),
				grammarHelper.matchNounToNumerales(currentMinutes, "minutė")//resolveMinuteName(currentMinutes)
				);
		LOG.debug("createTimeForSpeech: {}", timeForSpeech);
		return timeForSpeech;
	}

	

}
