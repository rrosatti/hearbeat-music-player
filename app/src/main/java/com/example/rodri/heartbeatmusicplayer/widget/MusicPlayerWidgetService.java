package com.example.rodri.heartbeatmusicplayer.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.service.MusicService;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.song.SongsManager;

import java.util.ArrayList;

/**
 * Created by rodri on 6/3/2016.
 */
public class MusicPlayerWidgetService extends MusicService implements MusicService.ServiceCallbacks {


    public static final String PLAYSONG = "PlaySong";
    public static final String NEXTSONG = "NextSong";

    public static final String LASTSONG = "LastSong";

    SharedPreferences sharedPreferences;
    private int songPos = 0;
    private MusicService musicService;
    private SongsManager manager;
    private ArrayList<Song> songs = new ArrayList<>();;
    private Intent playIntent = null;

    private boolean isStarted = false;


    public MusicPlayerWidgetService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(LASTSONG, Context.MODE_PRIVATE);
        manager = new SongsManager(MusicPlayerWidgetService.this);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, widgetConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);

        checkAction(intent);

        stopSelf(startId);

        return START_STICKY;
    }



    public void checkAction(Intent intent) {
        if (intent != null) {
            String requestedAction = intent.getAction();

            if (requestedAction != null) {

                if (requestedAction.equals(PLAYSONG)) {

                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.widget_layout, null);

                    ImageView pause = (ImageView) v.findViewById(R.id.imgWidgetPlay);
                    pause.setImageResource(R.drawable.stop_button);

                    if (isStarted) {



                        pauseSong();
                    } else {
                        playSong(intent);
                    }


                } else if (requestedAction.equals(NEXTSONG)) {
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View v = inflater.inflate(R.layout.widget_layout, null);

                    ImageView pause = (ImageView) v.findViewById(R.id.imgWidgetPlay);
                    pause.setImageResource(R.drawable.stop_button);
                }
            }
        }
    }

    public void playSong(Intent intent) {
        int temp = sharedPreferences.getInt("songPos", -1);
        if (temp != -1) {
            songPos = temp;
        } else {
            songPos = 0;
        }
        isStarted = true;

        songs = manager.getPlaylist();
        String title = songs.get(temp).getTitle();

        Toast.makeText(getApplicationContext(), "Title: " + title, Toast.LENGTH_SHORT).show();

        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.txtWidgetSongTitle, title);
        remoteViews.setImageViewResource(R.id.imgWidgetPlay, R.drawable.stop_button);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        setList(songs);
        playSong();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ServiceConnection widgetConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;

            musicService = binder.getService();
            musicService.setCallbacks(MusicPlayerWidgetService.this);


            int temp = musicService.getSongPos();
            if (temp != -1) {
                songPos = temp;
                //displaySongInfoWhenPlayingMusic();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void updateMusicInfo() {

    }
}
