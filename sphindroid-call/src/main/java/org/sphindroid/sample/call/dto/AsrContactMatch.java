package org.sphindroid.sample.call.dto;

public class AsrContactMatch {
	private AsrContact asrContact;
	private String detailedName;
	
	public AsrContactMatch(AsrContact asrContact, String detailedName) {
		this.asrContact = asrContact;
		this.detailedName = detailedName;
	}
	public AsrContact getAsrContact() {
		return asrContact;
	}
	public void setAsrContact(AsrContact asrContact) {
		this.asrContact = asrContact;
	}
	public String getDetailedName() {
		return detailedName;
	}
	public void setDetailedName(String detailedName) {
		this.detailedName = detailedName;
	}
	
}
