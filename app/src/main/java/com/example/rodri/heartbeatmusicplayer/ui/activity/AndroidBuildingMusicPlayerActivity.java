package com.example.rodri.heartbeatmusicplayer.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.util.Utilities;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rodri on 5/22/2016.
 */
public class AndroidBuildingMusicPlayerActivity extends Activity
        implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btPlay;
    private ImageButton btForward;
    private ImageButton btBackward;
    private ImageButton btNext;
    private ImageButton btPrevious;
    private ImageButton btPlalist;
    private ImageButton btRepeat;
    private ImageButton btShuffle;
    private SeekBar songProgressBas;
    private TextView txtSongTittle;
    private TextView txtCurrentDuration;
    private TextView txtTotalDuration;

    private MediaPlayer mediaPlayer;

    private Handler handler = new Handler();
    private SongsManager songsManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // milliseconds
    private int seekBackwardTime = 5000;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<Song> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        initialize();

        /**
         *  Open Playlist activity
         */
        btPlalist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openPlayList = new Intent(getApplicationContext(), PlaylistActivity.class);
                startActivityForResult(openPlayList, 100);
            }
        });

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
        });




    }

    public void initialize() {
        btPlay = (ImageButton) findViewById(R.id.imgPlayStop);
        btForward = (ImageButton) findViewById(R.id.imgForward);
        btBackward = (ImageButton) findViewById(R.id.imgBackwards);
        btNext = (ImageButton) findViewById(R.id.imgNext);
        btPrevious = (ImageButton) findViewById(R.id.imgBack);
        btPlalist = (ImageButton) findViewById(R.id.btPlaylist);
        btRepeat = (ImageButton) findViewById(R.id.imgRepeat);
        btShuffle = (ImageButton) findViewById(R.id.imgShuffle);
        songProgressBas = (SeekBar) findViewById(R.id.progressBar);
        txtSongTittle = (TextView) findViewById(R.id.txtSongTitle);
        txtCurrentDuration = (TextView) findViewById(R.id.txtCurrentTime);
        txtTotalDuration = (TextView) findViewById(R.id.txtTotalTime);

        mediaPlayer = new MediaPlayer();
        songsManager = new SongsManager();
        utils = new Utilities();

        // Listeners
        songProgressBas.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        songsList = songsManager.getPlaylist();
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
            currentSongIndex = data.getExtras().getInt("songIndex");
            playSong(currentSongIndex);
        }
    }

    public void playSong(int songIndex) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songsList.get(songIndex).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            // Display song title
            String songTitle = songsList.get(songIndex).getTitle();
            txtSongTittle.setText(songTitle);

            // Change start button to stop button
            btPlay.setImageResource(R.drawable.stop_button_states);

            // Set values to progress bar
            songProgressBas.setProgress(0);
            songProgressBas.setMax(100);

            // Update progress bar
            updateProgressBar();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateProgressBar() {
        handler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Set the Current and Total duration to TextView
            txtTotalDuration.setText(""+utils.millisecondsToTimer(totalDuration));
            txtCurrentDuration.setText(""+utils.millisecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
            songProgressBas.setProgress(progress);

            // Repeat this thread each 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    /**
     * When user starts moving the progress bar
     * @param seekBar
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
    }

    /**
     * When user stops moving the progress bar
     * @param seekBar
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(updateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPos = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mediaPlayer.seekTo(currentPos);

        updateProgressBar();
    }
}
