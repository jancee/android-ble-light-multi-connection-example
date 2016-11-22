package com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.List;

/**
 *
 * 音乐
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@Table(name = "MusicFavorTabs")
public class MusicFavorTab extends Model {
    private static MyLog myLog = new MyLog("[MusicFavorTab] ");

    @Column(name = "menuId")
    int menuId = 1;

    @Column(name = "musicPath")
    String musicPath = "null";

    @Column(name = "musicName")
    String musicName = "null";

    @Column(name = "artist")
    String artist = "null";

    @Column(name = "album")
    String album = "null";

    @Column(name = "musicDuration")
    long musicDuration = 0;

    @Column(name = "addTime")
    long addTime = 0;

    public MusicFavorTab() {
        super();
    }

    public MusicFavorTab(int menuId, String musicPath, String musicName, String artist, long musicDuration, long addTime) {
        super();
        this.menuId = menuId;
        this.musicPath = musicPath;
        this.musicName = musicName;
        this.artist = artist;
        this.musicDuration = musicDuration;
        this.addTime = addTime;
    }

    public static List<MusicFavorTab> getByMenuId(int myId) {
        return new Select().from(MusicFavorTab.class).where("menuId = ?", myId).orderBy("addTime ASC").execute();
    }

    public static MusicFavorTab getByMusicPath(String musicPath) {
        return new Select().from(MusicFavorTab.class).where("musicPath = ?", musicPath).executeSingle();
    }

    public static void deleteByMusicPath(String musicPath) {
        new Delete().from(MusicFavorTab.class).where("musicPath = ?", musicPath).execute();
    }

    public static List<MusicFavorTab> getAll() {
        return new Select().from(MusicFavorTab.class).orderBy("addTime ASC").execute();
    }

    public static void deleteAll() {
        new Delete().from(MusicFavorTab.class).execute();
    }

    public String getMusicPath() {
        return musicPath;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public long getMusicDuration() {
        return musicDuration;
    }
}
