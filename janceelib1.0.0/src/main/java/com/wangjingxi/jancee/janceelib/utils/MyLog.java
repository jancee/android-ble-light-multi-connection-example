package com.wangjingxi.jancee.janceelib.utils;

import android.content.Context;
import android.util.Log;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MyLog
{
    private  boolean isDebug = true;
    private  final String TAG = "OS";
    private  String myClassTag = "";
    private  StringBuffer classTag = new StringBuffer();

    public MyLog(String classTag) {
        this.classTag.append(classTag);
    }

    public MyLog(String classTag, boolean isDebug) {
        this.isDebug = isDebug;
        this.classTag.append(classTag);
    }

    public MyLog(Context context) {
        this.classTag.append("[").append(context.getClass().getSimpleName()).append("] ");
    }

    public void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, classTag + myClassTag + msg);
    }

    public void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, classTag + myClassTag + msg);
    }

    public void w(String msg)
    {
        if (isDebug)
            Log.w(TAG, classTag + myClassTag + msg);
    }

    public void setMyClassTag(String myClassTag) {
        this.myClassTag = myClassTag;
    }

    public String getTag() {
        return classTag + myClassTag;
    }

}
