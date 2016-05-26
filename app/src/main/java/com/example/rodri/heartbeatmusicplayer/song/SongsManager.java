package com.example.rodri.heartbeatmusicplayer.song;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by rodri on 5/25/2016.
 */
public class SongsManager {

    private Activity activity;
    private ContentResolver musicResolver;
    private Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Cursor musicCursor;
    private ArrayList<Song> songsList;

    public SongsManager(Activity activity) {
        this.activity = activity;
        this.musicResolver = activity.getContentResolver();
        this.musicCursor = musicResolver.query(musicUri, null, null, null, null);
    }

    public ArrayList<Song> getPlaylist() {
        songsList = new ArrayList<>();
        if (musicCursor != null && musicCursor.moveToFirst()) {
            // get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                songsList.add(new Song(id, title, artist));
            } while (musicCursor.moveToNext());

        }

        return songsList;

    }

}
