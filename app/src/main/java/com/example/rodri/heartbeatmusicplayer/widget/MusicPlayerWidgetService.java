package com.example.rodri.heartbeatmusicplayer.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by rodri on 6/3/2016.
 */
public class MusicPlayerWidgetService extends Service {

    public static final String PLAYSONG = "PlaySong";

    public MusicPlayerWidgetService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);

        playSong(intent);

        return START_STICKY;
    }

    public void playSong(Intent intent) {
        if (intent != null) {
            String requestedAction = intent.getAction();

            if (requestedAction != null && requestedAction.equals(PLAYSONG)) {
                Toast.makeText(getApplicationContext(), "The song will be played!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
