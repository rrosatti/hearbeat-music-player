package com.example.rodri.heartbeatmusicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.ui.activity.MusicPlayerActivity;
import com.example.rodri.heartbeatmusicplayer.widget.MusicPlayerWidgetProvider;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rodri on 5/26/2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    public static boolean isServiceStarted = false;
    private static final int NOTIFY_ID = 1;

    private String songTitle = "";

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songsList;
    private int songPos;

    private final IBinder musicBind = new MusicBinder();

    private boolean isRepeat = false;
    private boolean isShuffle = false;

    private ServiceCallbacks serviceCallbacks;

    public static final String LASTSONG = "LastSong";
    SharedPreferences sharedPreferences;

    public void onCreate() {
        super.onCreate();
        songPos = 0;
        mediaPlayer = new MediaPlayer();
        isServiceStarted = true;
        Toast.makeText(getApplicationContext(), "Service is started -> ?" + isServiceStarted, Toast.LENGTH_SHORT).show();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Toast.makeText(getApplicationContext(), "Could not get audio focus!", Toast.LENGTH_SHORT).show();
        }

        sharedPreferences = getSharedPreferences(LASTSONG, Context.MODE_PRIVATE);

        int tempSongPos = sharedPreferences.getInt("songPos", -1);
        isRepeat = sharedPreferences.getBoolean("isRepeat", false);
        isShuffle = sharedPreferences.getBoolean("isShuffle", false);
        if (tempSongPos != -1) {
            songPos = sharedPreferences.getInt("songPos", -1);
        }

        initMusicPlayer();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public interface ServiceCallbacks {
        void updateMusicInfo();
    }

    public void initMusicPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> songs) {
        this.songsList = songs;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        mediaPlayer.reset();
        Song song = songsList.get(songPos);
        songTitle = song.getTitle();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("songPos", songPos);
        editor.commit();

        long currSong = song.getId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();

        // Set song to the widget
        ComponentName name = new ComponentName(getApplicationContext(), MusicPlayerWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] ids = appWidgetManager.getAppWidgetIds(name);
        System.out.println("ids count: " + ids.length);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.txtWidgetSongTitle, songTitle);
        appWidgetManager.updateAppWidget(ids[0], remoteViews);
    }

    public void pauseSong() {
        mediaPlayer.pause();
    }

    public void continueSong(int currentTimePos) {
        mediaPlayer.seekTo(currentTimePos);
        mediaPlayer.start();
    }

    public int getSongPos() {
        return songPos;
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getSongDuration() {
        return mediaPlayer.getDuration();
    }

    // Add after the Controller implementation
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void go() {
        mediaPlayer.start();
    }

    public void setSong(int songIndex) {
        songPos = songIndex;
    }

    public void seekTo(int time) {
        mediaPlayer.seekTo(time);
    }

    public void updateVariables(boolean isRepeat, boolean isShuffle) {
        this.isRepeat = isRepeat;
        this.isShuffle = isShuffle;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isRepeat", isRepeat);
        editor.putBoolean("isShuffle", isShuffle);
        editor.commit();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        mediaPlayer.reset();
        // if repeat is on, then the same song must be repeated
        if (isRepeat) {
            playSong();
        } else if (isShuffle) { // is shuffle is on, then we need to random a new song
            Random random = new Random();
            songPos = random.nextInt((songsList.size() - 1) + 1);
            playSong();
        } else {

            // play the next song, unless it is the last one.
            if (songPos < (songsList.size() - 1)) {
                setSong(songPos + 1);
                playSong();
                songPos += 1;
            } else {
                setSong(0);
                playSong();
                songPos = 0;
            }

        }

        if (serviceCallbacks != null) {
            serviceCallbacks.updateMusicInfo();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        Intent notIntent = new Intent(this, MusicPlayerActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.playlist_button)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);

        Notification notification = builder.build();

        startForeground(NOTIFY_ID, notification);

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

}
