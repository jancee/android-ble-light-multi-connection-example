package com.wangjingxi.jancee.janceeble.Utils;

import android.util.Log;

/**
 *
 * Log
 *
 */
public class BleLog {
    private  boolean isDebug = true;
    private  final String TAG = "JanceeBle";
    private  String classTag = "";
    private  String bleTag = "";

    public BleLog(String classTag) {
        this.classTag = this.classTag + classTag;
    }

    public void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, classTag + bleTag + msg);
    }

    public void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, classTag + bleTag + msg);
    }

    public void w(String msg)
    {
        if (isDebug)
            Log.w(TAG, classTag + bleTag + msg);
    }

    public void setBleTag(String bleTag) {
        this.bleTag = bleTag;
    }
}
