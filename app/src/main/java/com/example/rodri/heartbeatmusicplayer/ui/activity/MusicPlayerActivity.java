package com.example.rodri.heartbeatmusicplayer.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.service.MusicService;
import com.example.rodri.heartbeatmusicplayer.service.MusicService.MusicBinder;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.song.SongsManager;

import java.util.ArrayList;

/**
 * Created by rodri on 5/26/2016.
 */
public class MusicPlayerActivity extends Activity {

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private ArrayList<Song> songList = new ArrayList<>();
    private SongsManager manager;


    private ImageButton btPlay;
    private ImageButton btForward;
    private ImageButton btBackward;
    private ImageButton btNext;
    private ImageButton btPrevious;
    private ImageButton btPlaylist;
    private ImageButton btRepeat;
    private ImageButton btShuffle;
    private SeekBar songProgressBas;
    private TextView txtSongTittle;
    private TextView txtCurrentDuration;
    private TextView txtTotalDuration;

    private boolean isShuffle = false;
    private boolean isRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        initialize();

        songList = manager.getPlaylist();

        btPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openPlayList = new Intent(getApplicationContext(), PlaylistActivity.class);
                startActivityForResult(openPlayList, 100);
            }
        });
        /**
        btForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = mediaPlayer.getCurrentPosition();

                if (currentPos + seekForwardTime <= mediaPlayer.getDuration()) {
                    // forward song
                    mediaPlayer.seekTo(currentPos + seekForwardTime);
                } else {
                    // forward to end
                    mediaPlayer.seekTo(mediaPlayer.getDuration());
                }
            }
        });

        btBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = mediaPlayer.getCurrentPosition();

                if (currentPos - seekBackwardTime >= 0) {
                    mediaPlayer.seekTo(currentPos - seekBackwardTime);
                } else {
                    mediaPlayer.seekTo(0);
                }
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSongIndex < (songsList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex += 1;
                } else {
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSongIndex > 0) {
                    playSong(currentSongIndex - 1);
                    currentSongIndex -= 1;
                } else {
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });*/

        btRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Reapeat is OFF", Toast.LENGTH_SHORT).show();
                    btRepeat.setImageResource(R.drawable.repeat_button);
                } else {
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    btRepeat.setImageResource(R.drawable.repeat_button_pressed);

                    isShuffle = false;
                    btShuffle.setImageResource(R.drawable.shuffle_button);
                }
            }
        });

        btShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btShuffle.setImageResource(R.drawable.shuffle_button);
                } else {
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    btShuffle.setImageResource(R.drawable.shuffle_button_pressed);

                    isRepeat = false;
                    btRepeat.setImageResource(R.drawable.repeat_button);
                }
            }
        });

    }

    public void initialize() {
        btPlay = (ImageButton) findViewById(R.id.imgPlayStop);
        btForward = (ImageButton) findViewById(R.id.imgForward);
        btBackward = (ImageButton) findViewById(R.id.imgBackwards);
        btNext = (ImageButton) findViewById(R.id.imgNext);
        btPrevious = (ImageButton) findViewById(R.id.imgBack);
        btPlaylist = (ImageButton) findViewById(R.id.btPlaylist);
        btRepeat = (ImageButton) findViewById(R.id.imgRepeat);
        btShuffle = (ImageButton) findViewById(R.id.imgShuffle);
        songProgressBas = (SeekBar) findViewById(R.id.progressBar);
        txtSongTittle = (TextView) findViewById(R.id.txtSongTitle);
        txtCurrentDuration = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalDuration = (TextView) findViewById(R.id.txtTotalTime);

        manager = new SongsManager(this);
    }

    /**
     * Will receive the song index from playlist and play it
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            int currentSongIndex = data.getExtras().getInt("songIndex");
            musicService.setSong(currentSongIndex);
            musicService.playSong();
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;

            musicService = binder.getService();

            musicService.setList(songList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    public void songPicked(View view) {
        musicService.setSong(Integer.parseInt(view.getTag().toString()));
        musicService.playSong();
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }
}
