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

    private boolean musicBound = false;


    public MusicPlayerWidgetService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(LASTSONG, Context.MODE_PRIVATE);
        manager = new SongsManager(MusicPlayerWidgetService.this);

        songs = manager.getPlaylist();
        setList(songs);

        /**if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, widgetConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Toast.makeText(getApplicationContext(), "I've been here 1!", Toast.LENGTH_SHORT).show();
            System.out.println("I've been here 1!");
        }*/
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStart(intent, startId);

        Toast.makeText(getApplicationContext(), "I've been here 2!", Toast.LENGTH_SHORT).show();
        System.out.println("I've been here 2!");

        checkAction(intent);

        stopSelf(startId);

        return START_NOT_STICKY;
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
                    System.out.println("I've been here 5!");
                    if (isPlaying()) {
                        Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                        pauseSong(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "playing", Toast.LENGTH_SHORT).show();
                        playSong(intent);
                    }


                } else if (requestedAction.equals(NEXTSONG)) {
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

    public void pauseSong(Intent intent) {
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
        remoteViews.setImageViewResource(R.id.imgWidgetPlay, R.drawable.play_button);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        pauseSong();
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

            songs = manager.getPlaylist();

            musicService.setList(songs);
            musicBound = true;
            musicService.setCallbacks(MusicPlayerWidgetService.this);
            System.out.println("I've been here 3!");

            int temp = musicService.getSongPos();
            if (temp != -1) {
                songPos = temp;
                System.out.println("I've been here 4!");
                //displaySongInfoWhenPlayingMusic();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void updateMusicInfo() {

    }

    @Override
    public void onDestroy() {
        /**if (musicBound) {
            musicService.setCallbacks(null);
            unbindService(widgetConnection);
            musicBound = false;
        }
        stopService(playIntent);
        musicService = null;*/
        super.onDestroy();
    }
}
