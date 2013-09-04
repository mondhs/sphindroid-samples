package org.sphindroid.sample.call.service.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

public class AsrCommandResolver implements AsrCommandProcessor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AsrCommandResolver.class);
	
	private Collection<AsrCommandProcessor>  commands = null;

	
	public AsrCommandResolver(Collection<AsrCommandProcessor> commands) {
		super();
		this.commands = commands;
	}

	@Override
	public boolean isSupports(AsrCommandParcelable cmd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> dictionary = new HashSet<String>();
		for (AsrCommandProcessor cmd : commands) {
			dictionary.addAll(cmd.getDictionary());
		}
		return dictionary;
	}

	@Override
	public Map<String, String> getCommandMap() {
		return null;
	}

	@Override
	public Map<String, String> getAditionalGrammarMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsrCommandResult execute(AsrCommandParcelable commandDto) {
		boolean isProcessed = false;
		for (AsrCommandProcessor command : commands) {
			if(command.isSupports(commandDto)){
				LOG.debug("Prepared to executed {}", commandDto.getCommandName());
				AsrCommandResult result =command.execute(commandDto); 
				if(Boolean.TRUE.equals(result.getConsumed())){
					isProcessed = true;
					LOG.debug("Command executed successfully {}", commandDto.getCommandName());
				}
			}
		}
		return new AsrCommandResult(isProcessed);	
	}

}
