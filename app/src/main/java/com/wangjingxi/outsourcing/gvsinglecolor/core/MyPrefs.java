package com.wangjingxi.outsourcing.gvsinglecolor.core;

import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 *
 * 蓝牙偏好存储
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@SharedPref(value= SharedPref.Scope.UNIQUE)
public interface MyPrefs {


    //忽略版本？
    @DefaultString("null")
    String ignoreVersion();

    //蓝牙名称
    @DefaultString("null")
    String bleName0();


    //蓝牙地址
    @DefaultString("null")
    String bleAddr0();


    //蓝牙所在的组
    @DefaultLong(0)
    long groupAddedTime();


}
