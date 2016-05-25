package com.example.rodri.heartbeatmusicplayer.ui.activity;

import com.example.rodri.heartbeatmusicplayer.song.Song;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rodri on 5/19/2016.
 */
public class old_SongsManager {

    final String MEDIA_PATH = new String("/sdcard/");
    private ArrayList<Song> songList = new ArrayList<>();

    public old_SongsManager() {

    }

    /**
     *
     * get all the .mp3 files from sdcard
     *
     * @return
     */
    public ArrayList<Song> getPlaylist() {
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                Song song = new Song();
                song.setTitle(file.getName().substring(0, (file.getName().length() - 4)));
                song.setPath(file.getPath());

                songList.add(song);
            }
        }

        return songList;
    }

    /**
     *
     * Filter only .mp3 files
     *
     */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }

}
