package org.sphindroid.sample.call.widget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sphindroid.sample.call.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class SphindroidWidgetProvider extends AppWidgetProvider {

	private static final Logger LOG = LoggerFactory
			.getLogger(SphindroidWidgetIntentReceiver.class);

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		// initializing widget layout
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.sphindroid_widget_layout);
		// register for button event
		remoteViews.setOnClickPendingIntent(R.id.btn_widget_listen,
				buildButtonPendingIntent(context));
		pushWidgetUpdate(context, remoteViews);
		
		LOG.debug("onReceive");
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		LOG.debug("onUpdate");
		
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

	

}
