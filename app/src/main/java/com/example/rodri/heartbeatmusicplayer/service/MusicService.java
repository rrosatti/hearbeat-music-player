package com.example.rodri.heartbeatmusicplayer.service;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.ui.activity.MusicPlayerActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rodri on 5/26/2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songsList;
    private int songPos;

    private final IBinder musicBind = new MusicBinder();

    private boolean isRepeat = false;
    private boolean isShuffle = false;

    public void onCreate() {
        super.onCreate();
        songPos = 0;
        mediaPlayer = new MediaPlayer();

        initMusicPlayer();
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

        long currSong = song.getId();

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);

        try {
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    public void pauseSong() {
        mediaPlayer.pause();
    }

    public void continueSong(int currentTimePos) {
        mediaPlayer.seekTo(currentTimePos);
        mediaPlayer.start();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getSongDuration() {
        return mediaPlayer.getDuration();
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
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

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

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}
