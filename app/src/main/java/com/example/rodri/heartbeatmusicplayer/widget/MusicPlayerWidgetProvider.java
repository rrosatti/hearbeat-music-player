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
import com.example.rodri.heartbeatmusicplayer.service.MusicService;
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

            // Intent related with Play button
            Intent playIntent = new Intent(context, MusicService.class);
            playIntent.setAction(MusicPlayerWidgetService.PLAYSONG);
            playIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent playPendingIntent = PendingIntent.getService(context, 0, playIntent, 0);

            // Intent related with Next button
            Intent nextIntent = new Intent(context, MusicPlayerWidgetService.class);
            nextIntent.setAction(MusicPlayerWidgetService.NEXTSONG);
            nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent nextPendingIntent = PendingIntent.getService(context, 0, nextIntent, 0);

            // Intent when user click in the song title
            Intent songTitleIntent = new Intent(context, MusicPlayerActivity.class);
            songTitleIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            PendingIntent songTitlePendingIntent = PendingIntent.getActivity(context, 0, songTitleIntent, 0);


            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            remoteViews.setOnClickPendingIntent(R.id.imgWidgetPlay, playPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.imgWidgetNext, nextPendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.songTitleWidgetLinearLayout, songTitlePendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }
}
