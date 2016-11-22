package com.wangjingxi.outsourcing.gvsinglecolor.core;

import android.content.Context;

import com.wangjingxi.outsourcing.gvsinglecolor.entity.MusicItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.MusicFavorTab;
import com.wangjingxi.outsourcing.gvsinglecolor.utils.MediaUtil;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 音乐管理
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MusicManager {
    private static MyLog myLog = new MyLog("[MusicManager] ");
    private static List<List<MusicItem>> musicItemList = new ArrayList<>();
    public static boolean isStart = false;
    public static int currentMenuId = 1;
    public static int currentIndex = 0;

    /**
     * 获取指定列表索引的音乐列表
     * @param context
     * @param menuId    列表索引
     * @return
     */
    public static List<MusicItem> getMusicListById(Context context, int menuId) {
        //如果没有歌曲，则获取全部音乐并添加到索引0
        if (musicItemList.size() == 0){
            musicItemList.add(0, MediaUtil.getMusicInfos(context));
        }

        //如果页数+1大于歌曲List元素数量
        if (musicItemList.size() < menuId + 1) {
            List<MusicItem> musicItems = new ArrayList<>();
            List<MusicFavorTab> musicFavorTab = MusicFavorTab.getByMenuId(menuId);  //获取数据库中指定页数记录

            //如果没有则返回空
            if (musicFavorTab == null)
                return null;

            //如果有，则遍历所有的，添加到musicItems
            for (MusicFavorTab favorTab : musicFavorTab) {
                musicItems.add(new MusicItem(favorTab.getMusicPath(), favorTab.getMusicName(), favorTab.getArtist(), favorTab.getMusicDuration(), true));
            }

            //再将musicItem添加到指定页的musicItemList
            musicItemList.add(menuId, musicItems);
        }

        return musicItemList.get(menuId);
    }

    /**
     * 获取默认的音乐列表
     * @param context
     * @return
     */
    public static List<MusicItem> getDefaultMusicList(Context context) {
        //移除原有记录，重新获取并添加到musicItemList的索引0，并返回
        musicItemList.remove(0);
        musicItemList.add(0, MediaUtil.getMusicInfos(context));

        return musicItemList.get(0);
    }

    /**
     * 获取下一首音乐
     * @return
     */
    public static MusicItem getNextMusic() {
        myLog.d("currentMenuId: " + currentMenuId);
        currentIndex++;
        if (currentIndex > musicItemList.get(currentMenuId).size()-1)
            currentIndex =0;
        return musicItemList.get(currentMenuId).get(currentIndex);
    }

    /**
     * 获取上一首音乐
     * @return
     */
    public static MusicItem getPreMusic() {
        currentIndex--;
        if (currentIndex == -1)
            currentIndex = musicItemList.get(currentMenuId).size() - 1;
        return musicItemList.get(currentMenuId).get(currentIndex);
    }

    /**
     * 添加音乐到menu
     *
     * @param musicItem
     * @param menuId
     */
    public static void addMusicToMenu(MusicItem musicItem, int menuId) {
        if (MusicFavorTab.getByMusicPath(musicItem.getMusicPath()) != null) {
            return;
        }
        MusicFavorTab musicFavorTab = new MusicFavorTab(menuId, musicItem.getMusicPath(), musicItem.getMusicName(), musicItem.getArtist(), musicItem.getMusicDuration(), System.currentTimeMillis());
        musicFavorTab.save();
        musicItemList.get(menuId).add(musicItem);
    }

    /**
     * 从menu删除音乐
     *
     * @param musicItem
     * @param menuId
     */
    public static void deleteMusicFromMenu(MusicItem musicItem, int menuId) {
        if (MusicFavorTab.getByMusicPath(musicItem.getMusicPath()) == null) {
            return;
        }

        MusicFavorTab.deleteByMusicPath(musicItem.getMusicPath());
        for (MusicItem item : musicItemList.get(menuId)) {
            myLog.d("deleteMusicFromMenu before: " + item.getMusicName());
        }
        musicItemList.get(menuId).remove(musicItem);
        for (MusicItem item : musicItemList.get(0)) {
            if (item.getMusicPath().equals(musicItem.getMusicPath()))
                item.setIsFavor(false);
        }
        for (MusicItem item : musicItemList.get(menuId)) {
            myLog.d("deleteMusicFromMenu after: " + item.getMusicName());
        }
    }

    /**
     * 获取music位置
     * @return
     */
    public static String getMusicPath() {
        return musicItemList.get(currentMenuId).get(currentIndex).getMusicPath();
    }

    /**
     * 获取musicItem
     * @return
     */
    public static MusicItem getMusicItem() {
        return musicItemList.get(currentMenuId).get(currentIndex);
    }

    /**
     * 请出列表
     */
    public static void clear() {
        musicItemList.clear();
    }

    /**
     * 获取当前的音乐索引
     * @return
     */
    public static int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 获取当前的菜单索引
     * @return
     */
    public static int getCurrentMenuId() {
        return currentMenuId;
    }

    public static boolean isStart() {
        return isStart;
    }

    public static void setIsStart(boolean isStart) {
        MusicManager.isStart = isStart;
    }

}
