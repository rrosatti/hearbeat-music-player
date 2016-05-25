package com.example.rodri.heartbeatmusicplayer.song;

/**
 * Created by rodri on 5/20/2016.
 */
public class Song {

    private long id;
    private String title;
    //private String path;
    private String artist;

    public Song(long id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
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

    /**
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    } */

}
