package com.wangjingxi.outsourcing.gvsinglecolor.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.wangjingxi.outsourcing.gvsinglecolor.entity.MusicItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.MusicFavorTab;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 媒体工具
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MediaUtil {
    private static MyLog myLog = new MyLog("[MediaUtil] ");

    /**
     * 用于从数据库中查询歌曲的信息，保存在List当中
     *
     * @return
     */
    public static List<MusicItem> getMusicInfos(Context context) {
        List<MusicItem> musicItemList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));    // 音乐标题
            long duration = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));  // 时长
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));    // 艺术家
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));      // 文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));  // 是否为音乐
            if (isMusic != 0 && duration > 30) {
                musicItemList.add(new MusicItem(path, title, artist, duration));
            }
//            myLog.d("MusicItem: duration: " + duration);
        }

        for (MusicFavorTab musicFavorTab : MusicFavorTab.getAll()) {
            myLog.d("musicFavorTab: " + musicFavorTab.getMusicPath());
            for (MusicItem musicItem : musicItemList) {
                myLog.d("musicItem: " + musicItem.getMusicPath());
                if (musicFavorTab.getMusicPath().equals(musicItem.getMusicPath()))
                    musicItem.setIsFavor(true);
            }
        }

        return musicItemList;
    }

    /**
     * 格式化时间，将毫秒转换为分:秒格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        String min = time / (1000 * 60) + "";
        String sec = time % (1000 * 60) + "";
        if (min.length() < 2) {
            min = "0" + time / (1000 * 60) + "";
        } else {
            min = time / (1000 * 60) + "";
        }
        if (sec.length() == 4) {
            sec = "0" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 3) {
            sec = "00" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 2) {
            sec = "000" + (time % (1000 * 60)) + "";
        } else if (sec.length() == 1) {
            sec = "0000" + (time % (1000 * 60)) + "";
        }
        return min + ":" + sec.trim().substring(0, 2);
    }

}
