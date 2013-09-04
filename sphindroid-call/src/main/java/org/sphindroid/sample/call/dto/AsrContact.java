package org.sphindroid.sample.call.dto;

import android.net.Uri;

public class AsrContact {
	private String displayName;
	private String number;
	private String givenName;
	private String familyName;
	private Long id;
	private Uri avatar;

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getNumber() {
		return number;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getGivenName() {
		return givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}


	public void setAvatar(Uri avatar) {
		this.avatar = avatar;
	}

	public Uri getAvatar() {
		return avatar;
	}

	@Override
	public String toString() {
		return "AsrContact [displayName=" + displayName + ", number=" + number
				+ "]";
	}
	
	
}
