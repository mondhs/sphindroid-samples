package org.sphindroid.sample.call.service.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class AsrStatisticsParcelable implements Parcelable{

	private String hypothesis;
	private int bestScore;
	
	
	public AsrStatisticsParcelable() {}
	
	public AsrStatisticsParcelable(Parcel pc){
		hypothesis = pc.readString();
		bestScore = pc.readInt();
	}
	
	public static final Parcelable.Creator<AsrStatisticsParcelable> CREATOR = new Parcelable.Creator<AsrStatisticsParcelable>() {
		@Override
		public AsrStatisticsParcelable createFromParcel(Parcel pc) {
			return new AsrStatisticsParcelable(pc);
		}

		@Override
		public AsrStatisticsParcelable[] newArray(int size) {
			return new AsrStatisticsParcelable[size];
		}

	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel pc, int flag) {
		pc.writeString(hypothesis);
		pc.writeInt(bestScore);
	}



	public String getHypothesis() {
		return hypothesis;
	}



	public void setHypothesis(String hypothesis) {
		this.hypothesis = hypothesis;
	}



	public int getBestScore() {
		return bestScore;
	}



	public void setBestScore(int bestScore) {
		this.bestScore = bestScore;
	}

}
