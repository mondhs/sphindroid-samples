package org.sphindroid.sample.call.widget;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.R;
import org.sphindroid.sample.call.service.SphindroidClientImpl;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

public class SphindroidWidgetIntentReceiver extends BroadcastReceiver {
	public static int clickCount = 0;

	private static final Logger LOG = LoggerFactory
			.getLogger(SphindroidWidgetIntentReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		LOG.debug("[onReceive] {}", intent.getAction());

		if (intent.getAction().equals(WidgetUtils.WIDGET_SWITCH_ACTION)) {
			context.startService(new Intent(context, InvokeService.class));
			updateWidgetPictureAndButtonListener(context);

		}
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.sphindroid_widget_layout);
		// updating view

		// re-registering for click listener
		remoteViews.setOnClickPendingIntent(R.id.btn_widget_listen,
				buildButtonPendingIntent(context));

		pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}

	private PendingIntent buildButtonPendingIntent(Context context) {
		// initiate widget update request
		Intent intent = new Intent();
		intent.setAction(WidgetUtils.WIDGET_SWITCH_ACTION);
		return PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
		ComponentName myWidget = new ComponentName(context,
				SphindroidWidgetProvider.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(myWidget, remoteViews);
	}

	public static class InvokeService extends Service {

		// private MovieSeeker movieSeeker = new MovieSeeker();

		@Override
		public void onStart(Intent intent, int startId) {
			LOG.debug("InvokeService$onStart");
			// Build the widget update for today
			RemoteViews updateViews = buildUpdate(this);

			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(this,
					SphindroidWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(this);
			manager.updateAppWidget(thisWidget, updateViews);
		}

		public RemoteViews buildUpdate(Context context) {
			LOG.debug("InvokeService$buildUpdate");
			RemoteViews updateViews = new RemoteViews(context.getPackageName(),
					R.layout.sphindroid_widget_layout);

			SphindroidClientImpl sphindroidClient = new SphindroidClientImpl(
					context);
			sphindroidClient.bindService();
			Timer timer = new Timer();
			timer.schedule(new ServiceBoundTimerTask(sphindroidClient), 100,
					100);
			return updateViews;

		}

		@Override
		public IBinder onBind(Intent intent) {
			LOG.debug("InvokeService$onBind");
			// We don't need to bind to this service
			return null;
		}
	}

}
