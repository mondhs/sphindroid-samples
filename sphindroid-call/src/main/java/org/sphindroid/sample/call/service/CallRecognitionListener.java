package org.sphindroid.sample.call.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.core.dto.AsrStatistics;
import org.sphindroid.core.service.AsrRecognitionListener;
import org.sphindroid.sample.call.service.aidl.AsrCommandParcelable;
import org.sphindroid.sample.call.service.aidl.AsrStatisticsParcelable;
import org.sphindroid.sample.call.service.aidl.ISphndroidRecognitionCallback;
import org.sphindroid.sample.call.service.command.AsrCommandProcessor;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

public class CallRecognitionListener implements AsrRecognitionListener {
	private static final Logger LOG = LoggerFactory
			.getLogger(CallRecognitionListener.class);
	private RemoteCallbackList<ISphndroidRecognitionCallback> callbacks;
	private boolean ready = false;
	private AsrCommandProcessor commandProcessor;

	public CallRecognitionListener(
			RemoteCallbackList<ISphndroidRecognitionCallback> callbacks, AsrCommandProcessor commandProcessor) {
		this.callbacks = callbacks;
		this.commandProcessor = commandProcessor;
	}

	@Override
	public void onEndOfSpeech() {
		LOG.debug("onEndOfSpeech");
	}

	@Override
	public void onError(int arg0) {
		LOG.debug("onError");
	}

	@Override
	public void onPartialResults(AsrStatistics asrStatistics) {
		LOG.debug("         onPartialResults: {}",
				asrStatistics.getHypothesis());
	}

	@Override
	public void onResults(AsrStatistics asrStatistics) {
		LOG.debug(">onResults: {}", asrStatistics.getHypothesis());
		AsrStatisticsParcelable asrStatisticsMsg = new AsrStatisticsParcelable();
		asrStatisticsMsg.setHypothesis(asrStatistics.getHypothesis());
		asrStatisticsMsg.setBestScore(asrStatistics.getBestScore());
		AsrCommandParcelable command = new AsrCommandParcelable();
		command.setCommandName(asrStatistics.getHypothesis());
		commandProcessor.execute(command);
		if(callbacks == null){
			return;
		}
		final int N = callbacks.beginBroadcast();
		LOG.debug("Notifying onResults {} callbacks", N);
		for (int i = 0; i < N; i++) {
			try {
				callbacks.getBroadcastItem(i).onResults(asrStatisticsMsg);
			} catch (RemoteException e) {
				LOG.error("[onResults] cannot send message", e);
			}
		}
		callbacks.finishBroadcast();
		
	}

	@Override
	public void ready() {
		LOG.debug("[ready]");
		if(callbacks == null){
			return;
		}
		final int N = callbacks.beginBroadcast();
		LOG.debug("Notifying ready {} callbacks", N);
		for (int i = 0; i < N; i++) {
			try {
				callbacks.getBroadcastItem(i).ready();
			} catch (RemoteException e) {
				LOG.error("[onResults] cannot send message", e);
			}
		}
		callbacks.finishBroadcast();
		ready = true;

	}
	/**
	 * Track state if ready for service clients
	 * @return
	 */
	public boolean isReady() {
		return ready;
	}



}
