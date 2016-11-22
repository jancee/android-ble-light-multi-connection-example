package com.wangjingxi.outsourcing.gvsinglecolor.entity;

/**
 *
 * 音乐Item
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MusicItem {
    private String musicPath;
    private String musicName;
    private String artist;
    private long musicDuration;
    private boolean isFavor = false;

    public MusicItem(String musicPath, String musicName, String artist, long musicDuration) {
        this.musicPath = musicPath;
        this.musicName = musicName;
        this.artist = artist;
        this.musicDuration = musicDuration;
    }

    public MusicItem(String musicPath, String musicName, String artist, long musicDuration, boolean isFavor) {
        this.musicPath = musicPath;
        this.musicName = musicName;
        this.artist = artist;
        this.musicDuration = musicDuration;
        this.isFavor = isFavor;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public long getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(long musicDuration) {
        this.musicDuration = musicDuration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isFavor() {
        return isFavor;
    }

    public void setIsFavor(boolean isFavor) {
        this.isFavor = isFavor;
    }

    @Override
    public String toString() {
        return "MusicInfo [musicName=" + musicName + ", musicPath=" + musicPath + ",musicDuration: " + musicDuration + "]";
    }

}
