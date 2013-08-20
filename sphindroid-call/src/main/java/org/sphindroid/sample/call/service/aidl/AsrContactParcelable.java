package org.sphindroid.sample.call.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class AsrContactParcelable implements Parcelable{

	private String displayName;
	private long id;
	private String avatar;
	
	
	public AsrContactParcelable() {}
	
	public AsrContactParcelable(Parcel pc){
		displayName = pc.readString();
		id = pc.readLong();
		avatar = pc.readString();
	}
	
	public static final Parcelable.Creator<AsrContactParcelable> CREATOR = new Parcelable.Creator<AsrContactParcelable>() {
		@Override
		public AsrContactParcelable createFromParcel(Parcel pc) {
			return new AsrContactParcelable(pc);
		}

		@Override
		public AsrContactParcelable[] newArray(int size) {
			return new AsrContactParcelable[size];
		}

	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flag) {
		pc.writeString(displayName);
		pc.writeLong(id);
		pc.writeString(avatar);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setAvatar(String avatar) {
		this.avatar=avatar;
	}

	public String getAvatar() {
		return avatar;
	}

}
