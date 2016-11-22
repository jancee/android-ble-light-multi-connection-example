package com.wangjingxi.outsourcing.gvsinglecolor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.wangjingxi.outsourcing.gvsinglecolor.app.ClickActivity_;

/**
 *
 * 通用工具类
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class Utils {
    public final static boolean DEBUG = false;
    public final static boolean TOAST_DEBUG = false;
    public static final String ACTION_STOP_MUSIC    = "com.BleService.ACTION_STOP_MUSIC";
    public static final String ACTION_TOGGLE_MUSIC  = "com.BleService.ACTION_TOGGLE_MUSIC";


//    public static String appDowanloadUrl = "http://d.dxycloud.com/statistics/getDownLoadAddr/passivityDownAndroidApp?file_name=";
//    public static String postInfo = "BleCar";

    public static String appId ="";

    static int versionCode = 1;
    private static String getVersion = "com.wangjingxi.outsourcing.zaplitesbasic";

    /**
     * 实现ClickActivity的调度
     *
     * @param context
     * @param resId
     */
    public static void startClickActivityById(Context context, int resId){
        Intent intent = new Intent(context, ClickActivity_.class);
        intent.putExtra("resId",resId);
        context.startActivity(intent);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(Utils.getVersion, 0).versionName;
            System.out.println(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

}
