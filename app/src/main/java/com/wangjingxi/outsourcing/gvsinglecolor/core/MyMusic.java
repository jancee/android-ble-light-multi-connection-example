package com.wangjingxi.outsourcing.gvsinglecolor.core;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 *
 * 音乐偏好存储  最后一次的选择记录
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface MyMusic {

    @DefaultString("null")
    String lastPath();

    @DefaultString("null")
    String lastMusicName();

    @DefaultString("null")
    String lastArtistName();

    @DefaultInt(0)
    int lastMusicIndex();

    @DefaultInt(0)
    int lastMusicMenuId();

    @DefaultInt(1)
    int musicMode();

}


