package org.sphindroid.sample.call.service.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NewsCommand extends AbstractAsrCommand {

	public static final String COMMAND_TRANSCRIPTION = "RODYK NAUJIENAS";
	
	private static final String KEY_COMMAND = "VIEW_NEWS";
	
	public NewsCommand(Context context) {
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
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.delfi.lt"));
		browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getContext().startActivity(browserIntent);
		return true;
	}
	

}
