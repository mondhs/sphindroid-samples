package org.sphindroid.sample.call.service.aidl;

import org.sphindroid.sample.call.service.aidl.ISphndroidRecognitionCallback;
import org.sphindroid.sample.call.service.aidl.AsrContactParcelable;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;

interface ISphndroidRemoteService {
	
	boolean isReady();
	boolean isListening();
	void startListening();
	void stopListening();
	String executeCommand(in AsrCommandParcelable cmd);
	
	
	void registerCallBack(ISphndroidRecognitionCallback callback);
    void unregisterCallBack(ISphndroidRecognitionCallback callback);

	List<AsrContactParcelable> findContacts();

}
