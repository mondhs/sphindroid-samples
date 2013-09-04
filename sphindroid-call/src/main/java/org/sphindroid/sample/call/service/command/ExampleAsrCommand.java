package org.sphindroid.sample.call.service.command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

import android.content.Context;

public class ExampleAsrCommand extends AbstractAsrCommand  {
	private static final Logger LOG = LoggerFactory.getLogger(ExampleAsrCommand.class); 
	
	public ExampleAsrCommand(Context context) {
		super(context);
	}

	@Override
	public boolean isSupports(AsrCommandParcelable commandDto) {
		String cmd = commandDto.getCommandName().toUpperCase();
		return cmd.startsWith("SUK");
	}

	@Override
	public Set<String> getDictionary() {
		Set<String> words = new HashSet<String>();
        words.add("EIK");
        words.add("VIENĄ");
        words.add("DU");
        words.add("TRIS");
        words.add("KETURIS");
        words.add("PENKIS");
        words.add("METRUS");
        words.add("PIRMYN");
        words.add("ATGAL");
        words.add("SUK");
        words.add("GRĘŽKIS");
        words.add("KAIRĖN");
        words.add("DEŠINĖN");
        words.add("VARYK");
		return words;
	}

	@Override
	public Map<String, String> getCommandMap() {
		Map<String, String> cmdMap = new HashMap<String, String>();
		cmdMap.put("EIK", "(EIK | VARYK ) [ ( VIENĄ | DU | TRIS | KETURIS | PENKIS ) METRUS ] (PIRMYN | ATGAL)");
		cmdMap.put("SUK", " (SUK | GRĘŽKIS ) ( KAIRĖN | DEŠINĖN )");
		return cmdMap;
	}

	@Override
	public AsrCommandResult execute(AsrCommandParcelable commandDto) {
		LOG.debug("[execute] {}", commandDto.getCommandName());
		return new AsrCommandResult(true);
	}

}
