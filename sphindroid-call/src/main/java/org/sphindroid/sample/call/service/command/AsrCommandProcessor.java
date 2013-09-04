package org.sphindroid.sample.call.service.command;

import java.util.Map;
import java.util.Set;

import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

public interface AsrCommandProcessor {
	boolean isSupports(AsrCommandParcelable cmd);
	Set<String> getDictionary();
	Map<String,String> getCommandMap();
	Map<String,String> getAditionalGrammarMap();
	AsrCommandResult execute(AsrCommandParcelable cmd);
}
