package com.example.rodri.heartbeatmusicplayer.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.rodri.heartbeatmusicplayer.R;
import com.example.rodri.heartbeatmusicplayer.song.Song;

import java.util.ArrayList;

/**
 * Created by rodri on 5/21/2016.
 */
public class SongAdapter extends ArrayAdapter<Song> {

    private Activity activity;
    private LayoutInflater inflater = null;
    private ArrayList<Song> songs;

    public SongAdapter(Activity activity, int textViewResourceId, ArrayList<Song> songs) {
        super (activity, textViewResourceId, songs);
        try {
            this.activity = activity;
            this.songs = songs;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    public class ViewHolder {
        public TextView displaySongTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            v = inflater.inflate(R.layout.playlist_item, null);

            holder.displaySongTitle = (TextView) v.findViewById(R.id.txtPlaylistSongTitle);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.displaySongTitle.setText(songs.get(position).getTitle());

        return v;
    }




}
