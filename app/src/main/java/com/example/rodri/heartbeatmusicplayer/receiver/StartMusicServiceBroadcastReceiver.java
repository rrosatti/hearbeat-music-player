package com.example.rodri.heartbeatmusicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.rodri.heartbeatmusicplayer.service.MusicService;

/**
 * Created by rodri on 6/11/2016.
 */
public class StartMusicServiceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startService = new Intent(context, MusicService.class);
        context.startService(startService);
    }
}
