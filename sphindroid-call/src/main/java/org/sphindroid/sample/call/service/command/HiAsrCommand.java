package org.sphindroid.sample.call.service.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;

public class HiAsrCommand extends AbstractTtsAsrCommand{
	private static final Logger LOG = LoggerFactory.getLogger(HiAsrCommand.class); 
	
	private static final String KEY_COMMAND_HI = "HI";
	public static final String COMMAND_TRANSCRIPTION = "LABAS";


	public HiAsrCommand(Context context) {
		super(context);
	}


	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		return COMMAND_TRANSCRIPTION.equals(commandDto.getCommandName());
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dict = new HashSet<String>();
		dict.add(COMMAND_TRANSCRIPTION);
		return dict;
	}

	@Override
	public Map<String, String> getCommandMap() {
		Map<String, String> cmdMap = new HashMap<String, String>();
		cmdMap.put(KEY_COMMAND_HI, COMMAND_TRANSCRIPTION);
		return cmdMap;
	}

	@Override
	public boolean execute(AsrCommandParcelable commandDto) {
		LOG.debug("[execute] {}", COMMAND_TRANSCRIPTION);
		return speak(COMMAND_TRANSCRIPTION);
	}

}
