package com.example.rodri.heartbeatmusicplayer.song;

import android.net.Uri;

/**
 * Created by rodri on 5/20/2016.
 */
public class Song {

    private long id;
    private String title;
    //private String path;
    private String artist;
    private String albumUri;

    public Song() {}

    public Song(long id, String title, String artist, String albumUri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.albumUri = albumUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumUri() {
        return albumUri;
    }

    public void setAlbumUri(String albumUri) {
        this.albumUri = albumUri;
    }

    /**
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    } */

}
