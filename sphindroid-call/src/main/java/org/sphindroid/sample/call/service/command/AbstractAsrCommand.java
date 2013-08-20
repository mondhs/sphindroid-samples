package org.sphindroid.sample.call.service.command;

import java.util.Collections;
import java.util.Map;

import android.content.Context;

public abstract class AbstractAsrCommand implements AsrCommandProcessor{
	private Context context;
	public AbstractAsrCommand(Context context) {
		this.context = context;
	}
	public Context getContext() {
		return context;
	}
	@Override
	public Map<String, String> getAditionalGrammarMap() {
		return Collections.emptyMap();
	}
}
