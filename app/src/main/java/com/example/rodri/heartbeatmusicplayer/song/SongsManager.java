package com.example.rodri.heartbeatmusicplayer.song;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by rodri on 5/25/2016.
 */
public class SongsManager {

    private Context activity;
    private ContentResolver musicResolver;
    private Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Cursor musicCursor;
    private ArrayList<Song> songsList;

    private Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");

    public SongsManager(Context activity) {
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

            int album_id = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);


            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                long albumId = musicCursor.getLong(album_id);
                String uri = null;

                Uri temp = ContentUris.withAppendedId(albumArtUri, albumId);

                if (temp != null) {
                    uri = temp.toString();
                }


                songsList.add(new Song(id, title, artist, uri));
            } while (musicCursor.moveToNext());

        }

        return songsList;

    }

}
