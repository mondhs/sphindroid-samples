package org.sphindroid.sample.call.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.service.aidl.ISphndroidRecognitionCallback;
import org.sphindroid.sample.call.service.aidl.ISphndroidRemoteService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class CallRemoteServiceConnection implements ServiceConnection {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CallRemoteServiceConnection.class);
	private ISphndroidRemoteService remoteService;
	private ISphndroidRecognitionCallback callback;

	
	public CallRemoteServiceConnection(ISphndroidRecognitionCallback callback) {
		this();
		this.callback = callback;
	}

	public CallRemoteServiceConnection() {
		super();
	}

	public void onServiceConnected(ComponentName className,
			IBinder boundService) {
		remoteService = ISphndroidRemoteService.Stub
				.asInterface((IBinder) boundService);
		if(callback != null){
			try {
				remoteService.registerCallBack(callback);
			} catch (RemoteException e1) {
				LOG.error("Cannot register call back", e1);
			}
		}
		LOG.debug("onServiceConnected()");
	}

	public void onServiceDisconnected(ComponentName className) {
		if(callback!=null){
			try {
				remoteService.unregisterCallBack(callback);
			} catch (RemoteException e1) {
				LOG.error("service cannod be disconected", e1);
			}
		}
		remoteService = null;
		LOG.debug("onServiceDisconnected");
	}

	public ISphndroidRemoteService getRemoteService() {
		return remoteService;
	}
};
