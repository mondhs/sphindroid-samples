package org.sphindroid.sample.call.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class AsrCommandParcelable implements Parcelable{

	private String commandName;
	private long id;
	
	
	public AsrCommandParcelable() {}
	
	public AsrCommandParcelable(Parcel pc){
		commandName = pc.readString();
		id = pc.readLong();
	}
	
	public static final Parcelable.Creator<AsrCommandParcelable> CREATOR = new Parcelable.Creator<AsrCommandParcelable>() {
		@Override
		public AsrCommandParcelable createFromParcel(Parcel pc) {
			return new AsrCommandParcelable(pc);
		}

		@Override
		public AsrCommandParcelable[] newArray(int size) {
			return new AsrCommandParcelable[size];
		}

	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flag) {
		pc.writeString(commandName);
		pc.writeLong(id);
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

}
