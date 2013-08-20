package org.sphindroid.sample.call.service.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;

public class LightOffCommand extends AbstractAsrCommand{

	private static final String KEY_COMMAND = "TURN_LIGHT_OFF";
	public static final String COMMAND_TRANSCRIPTION = "IŠJUNK ŠVIESĄ";
	
	public LightOffCommand(Context context) {
		super(context);
	}


	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		return COMMAND_TRANSCRIPTION.equals(commandDto.getCommandName());
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dict = new HashSet<String>();
		String[] cmds = COMMAND_TRANSCRIPTION.split(" ");
		for (String word : cmds) {
			dict.add(word);			
		}
		return dict;
	}

	@Override
	public Map<String,String> getCommandMap() {
		Map<String,String> cmdMap= new HashMap<String,String>();
		cmdMap.put(KEY_COMMAND, COMMAND_TRANSCRIPTION);
		return cmdMap;
	}


	@Override
	public boolean execute(AsrCommandParcelable commandDto) {
		return true;
	}
	

}
