package com.example.rodri.heartbeatmusicplayer.ui.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.song.Song;
import com.example.rodri.heartbeatmusicplayer.ui.adapter.SongAdapter;

import java.util.ArrayList;

/**
 * Created by rodri on 5/21/2016.
 */
public class PlaylistActivity extends ListActivity {

    public ArrayList<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        SongsManager sManager = new SongsManager();
        this.songList = sManager.getPlaylist();

        // Create custom adapter
        SongAdapter adapter = new SongAdapter(this, 0, songList);
        setListAdapter(adapter);

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
