package org.sphindroid.sample.call.widget;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.service.SphindroidClientImpl;


public class ServiceBoundTimerTask extends TimerTask {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(SphindroidWidgetIntentReceiver.class);
	
	private SphindroidClientImpl sphindroidClient;

	public ServiceBoundTimerTask(SphindroidClientImpl sphindroidClient) {
		this.sphindroidClient = sphindroidClient;
	}

	public void run() {
		if (!sphindroidClient.isBound()) {
			LOG.debug("[ServiceBoundTimerTask] is bound {}",
					sphindroidClient.isBound());
			return;
		}
		if (sphindroidClient.isServiceRunning()) {
			LOG.debug("InvokeService$buildUpdate runing");
			if (sphindroidClient.isListening()) {
				LOG.debug("InvokeService$buildUpdate stop listening");
				sphindroidClient.stopListening();
			} else {
				LOG.debug("InvokeService$buildUpdate start listening");
				sphindroidClient.startListening();
			}
		} else {
			LOG.debug("InvokeService$buildUpdate not runing");
		}
		this.cancel();
	}

}