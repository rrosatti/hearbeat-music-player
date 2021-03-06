package com.example.rodri.heartbeatmusicplayer.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.service.MusicService;
import com.example.rodri.heartbeatmusicplayer.service.MusicService.MusicBinder;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.song.SongsManager;
import com.example.rodri.heartbeatmusicplayer.util.Utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by rodri on 5/26/2016.
 */
public class MusicPlayerActivity extends Activity implements MusicService.ServiceCallbacks{

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
    private ImageView albumImg;

    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isPaused = true;
    private boolean isStarted = false;

    private int currentSongIndex = 0;
    private int currentTimePos = 0;
    private int seekForwardTime = 5000; // milliseconds
    private int seekBackwardTime = 5000; // milliseconds

    private Handler handler = new Handler();
    private Utilities utils = new Utilities();
    public  boolean killThread = false;

    public static final String LASTSONG = "LastSong";
    private SharedPreferences sharedPreferences;


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


        btForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isStarted) {
                    currentTimePos = musicService.getCurrentPosition();

                    if (currentTimePos + seekForwardTime <= musicService.getSongDuration()) {
                        // forward song
                        musicService.seekTo(currentTimePos + seekForwardTime);
                    } else {
                        // forward to end
                        musicService.seekTo(musicService.getSongDuration());
                    }

                    updateProgressBar();

                }

            }

        });

        btBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStarted) {
                    currentTimePos = musicService.getCurrentPosition();

                    if (currentTimePos - seekBackwardTime >= 0) {
                        musicService.seekTo(currentTimePos - seekBackwardTime);
                    } else {
                        musicService.seekTo(0);
                    }

                    updateProgressBar();

                }

            }
        });


        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isShuffle) {
                    if (currentSongIndex < (songList.size() - 1)) {
                        musicService.setSong(currentSongIndex + 1);
                        musicService.playSong();
                        currentSongIndex += 1;
                    } else {
                        musicService.setSong(0);
                        musicService.playSong();
                        currentSongIndex = 0;
                    }


                } else {
                    Random random = new Random();
                    currentSongIndex  = random.nextInt((songList.size() - 1) + 1);
                    musicService.setSong(currentSongIndex);
                    musicService.playSong();
                }


                if (!isStarted) {
                    isStarted = true;
                    isPaused = false;
                    btPlay.setImageResource(R.drawable.stop_button_states);
                }

                displaySongInfoWhenPlayingMusic();
                updateProgressBar();


            }
        });

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSongIndex > 0) {
                    musicService.setSong(currentSongIndex - 1);
                    musicService.playSong();
                    currentSongIndex -= 1;
                } else {
                    musicService.setSong(songList.size() - 1);
                    musicService.playSong();
                    currentSongIndex = songList.size() - 1;
                }

                isStarted = true;
                isPaused = false;
                displaySongInfoWhenPlayingMusic();
                updateProgressBar();
                btPlay.setImageResource(R.drawable.stop_button_states);

            }
        });

        btRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRepeatButton();
                musicService.updateVariables(isRepeat, isShuffle);
            }
        });

        btShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateShuffleButton();
                musicService.updateVariables(isRepeat, isShuffle);
            }
        });

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    killThread = false;
                    if (isStarted) {
                        musicService.continueSong(currentTimePos);
                        System.out.println("I've been here! in position: " + currentTimePos);
                    } else {
                        if (currentSongIndex == 0) {
                            musicService.setSong(0);
                            musicService.playSong();
                        } else {
                            musicService.setSong(currentSongIndex);
                            musicService.playSong();
                        }
                        displaySongInfoWhenPlayingMusic();
                    }

                    btPlay.setImageResource(R.drawable.stop_button_states);
                    isPaused = false;
                    isStarted = true;

                } else {
                    currentTimePos = musicService.getCurrentPosition();
                    musicService.pauseSong();
                    btPlay.setImageResource(R.drawable.play_button_states);
                    isPaused = true;
                    killThread = true;
                }

            }
        });

        songProgressBas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateTimeTask);
                int totalDuration = musicService.getSongDuration();
                currentTimePos = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                musicService.seekTo(currentTimePos);

                updateProgressBar();
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
        albumImg = (ImageView) findViewById(R.id.imgThumbnail);

        manager = new SongsManager(this);

        sharedPreferences = getSharedPreferences(LASTSONG, Context.MODE_PRIVATE);

        isRepeat = sharedPreferences.getBoolean("isRepeat", false);
        isShuffle = sharedPreferences.getBoolean("isShuffle", false);
        int tempSongPos = sharedPreferences.getInt("songPos", -1);

        if (tempSongPos != -1) {
            currentSongIndex = tempSongPos;
        }

        //Toast.makeText(getApplicationContext(), "isRepeat: " + isRepeat, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), "isShuffle: " + isShuffle, Toast.LENGTH_SHORT).show();

        if (isShuffle) {
            // need to set as false, because of the previous implementation for btShuffle.onClick()
            isShuffle = false;
            updateShuffleButton();
        }
        if (isRepeat) {
            // need to set as false, because of the previous implementation for btRepeat.onClick()
            isRepeat = false;
            updateRepeatButton();
        }

    }

    public void updateShuffleButton() {
        if(isShuffle) {
            isShuffle = false;
            //Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
            btShuffle.setImageResource(R.drawable.shuffle_button);
        } else {
            isShuffle = true;
            //Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
            btShuffle.setImageResource(R.drawable.shuffle_button_pressed);

            isRepeat = false;
            btRepeat.setImageResource(R.drawable.repeat_button);
        }
    }

    public void updateRepeatButton() {
        if(isRepeat) {
            isRepeat = false;
            //Toast.makeText(getApplicationContext(), "Reapeat is OFF", Toast.LENGTH_SHORT).show();
            btRepeat.setImageResource(R.drawable.repeat_button);
        } else {
            isRepeat = true;
            //Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
            btRepeat.setImageResource(R.drawable.repeat_button_pressed);

            isShuffle = false;
            btShuffle.setImageResource(R.drawable.shuffle_button);
        }
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
        if (requestCode == 100 && data != null) {
            currentSongIndex = data.getExtras().getInt("songIndex");
            musicService.setSong(currentSongIndex);
            musicService.playSong();
            isPaused = false;
            isStarted = true;
            btPlay.setImageResource(R.drawable.stop_button_pressed);
            displaySongInfoWhenPlayingMusic();
        }
    }

    public void displaySongInfoWhenPlayingMusic() {
        String songTitle = songList.get(currentSongIndex).getTitle();
        txtSongTittle.setText(songTitle);

        songProgressBas.setProgress(0);
        songProgressBas.setMax(100);

        String albumUri = songList.get(currentSongIndex).getAlbumUri();

        if (albumUri != null) {
            ContentResolver res = getContentResolver();
            try {
                // need to find a better way to verify it
                InputStream in = res.openInputStream(Uri.parse(albumUri));
                Bitmap temp = BitmapFactory.decodeStream(in);
                albumImg.setImageURI(Uri.parse(albumUri));
            } catch (FileNotFoundException e) {
                albumImg.setImageResource(R.drawable.thumbnail);
                e.printStackTrace();
            }
        }

        updateProgressBar();
    }


    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;

            musicService = binder.getService();

            musicService.setList(songList);
            musicBound = true;
            musicService.setCallbacks(MusicPlayerActivity.this);

            int temp = musicService.getSongPos();
            if (temp != -1) {
                currentSongIndex = temp;
                displaySongInfoWhenPlayingMusic();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("onStart() was called ------------------");
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            System.out.println("playIntent is null ------------------");
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume() was called ------------------");
        if (playIntent == null) {
            System.out.println("playIntent is null ------------------");
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    protected void onDestroy() {
        System.out.println("onDestroy() was called ------------------");


        killThread = true;
        if (isPaused) {
            stopService(playIntent);
            musicService = null;

            if (musicBound) {
                musicService.setCallbacks(null);
                unbindService(musicConnection);
                musicBound = false;
            }

        }

        super.onDestroy();
    }

    /**
     * Seek bar events/methods
     * */

    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (!killThread && isStarted) {
                long totalDuration = musicService.getSongDuration();
                currentTimePos = musicService.getCurrentPosition();

                // Set the Current and Total duration to TextView
                txtTotalDuration.setText(""+utils.millisecondsToTimer(totalDuration));
                txtCurrentDuration.setText(""+utils.millisecondsToTimer(currentTimePos));

                // Updating progress bar
                int progress = utils.getProgressPercentage(currentTimePos, totalDuration);
                songProgressBas.setProgress(progress);

                // Repeat this thread each 100 milliseconds
                handler.postDelayed(this, 100);
            }

        }


    };


    @Override
    public void updateMusicInfo() {
        currentSongIndex = musicService.getSongPos();
        displaySongInfoWhenPlayingMusic();
    }
}
