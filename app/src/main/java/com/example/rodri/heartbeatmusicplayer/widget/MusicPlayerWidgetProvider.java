package com.example.rodri.heartbeatmusicplayer.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.ui.activity.MusicPlayerActivity;

/**
 * Created by rodri on 6/3/2016.
 */
public class MusicPlayerWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // get all ids
        ComponentName thisWidget = new ComponentName(context, MusicPlayerWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int  widgetId : allWidgetIds) {

            Toast.makeText(context.getApplicationContext(), "onUpdate() was called!", Toast.LENGTH_SHORT).show();

            // Register an onClickListener

            Intent intent = new Intent(context, MusicPlayerWidgetService.class);
            intent.setAction(MusicPlayerWidgetService.PLAYSONG);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            //intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            //intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            remoteViews.setOnClickPendingIntent(R.id.imgWidgetPlay, pendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }
}
