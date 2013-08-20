package org.sphindroid.sample.call.service.aidl;

import org.sphindroid.sample.call.service.aidl.AsrStatisticsParcelable;

oneway interface ISphndroidRecognitionCallback {
	void onResults(in AsrStatisticsParcelable asrStatistics);
	void ready();
}
