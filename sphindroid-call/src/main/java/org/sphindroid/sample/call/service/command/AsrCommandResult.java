package org.sphindroid.sample.call.service.command;

public class AsrCommandResult {
	Boolean consumed;

	public AsrCommandResult() {
	}
	
	public AsrCommandResult(Boolean consumed) {
		super();
		this.consumed = consumed;
	}

	public Boolean getConsumed() {
		return consumed;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}
}
