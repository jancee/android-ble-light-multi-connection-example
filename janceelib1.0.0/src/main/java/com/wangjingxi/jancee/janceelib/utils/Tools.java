package com.wangjingxi.jancee.janceelib.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import com.wangjingxi.jancee.janceelib.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * 工具类
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class Tools {
    private static MyLog myLog = new MyLog("[Tools] ");

    private static final double EARTH_RADIUS = 6378137.0;
    private static long firstTime = 0;

    /**
     * 获取两条线的夹角
     *
     * @param centerX
     * @param centerY
     * @param xInView
     * @param yInView
     * @return
     */
    public static int getRotationBetweenLines(float centerX, float centerY, float xInView, float yInView) {
        double rotation = 0;

        double k1 = (double) (centerY - centerY) / (centerX * 2 - centerX);
        double k2 = (double) (yInView - centerY) / (xInView - centerX);
        double tmpDegree = Math.atan((Math.abs(k1 - k2)) / (1 + k1 * k2)) / Math.PI * 180;

        if (xInView > centerX && yInView < centerY) {  //第一象限
            rotation = 90 - tmpDegree;
        } else if (xInView > centerX && yInView > centerY) //第二象限
        {
            rotation = 90 + tmpDegree;
        } else if (xInView < centerX && yInView > centerY) { //第三象限
            rotation = 270 - tmpDegree;
        } else if (xInView < centerX && yInView < centerY) { //第四象限
            rotation = 270 + tmpDegree;
        } else if (xInView == centerX && yInView < centerY) {
            rotation = 0;
        } else if (xInView == centerX && yInView > centerY) {
            rotation = 180;
        }

        return (int) rotation;
    }

    public static String secToTime(int time, int style) {
//        myLog.d("secToTime style: " + style);

        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            if (style == 0)
                return "00:00:00";
            else
                return "00h 00m 00s";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                if (style == 0) {
                    timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    timeStr = "00 h" + unitFormat(minute) + " m" + unitFormat(second) + " s";
                }
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                if (style == 0) {
                    timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    timeStr = unitFormat(hour) + " h" + unitFormat(minute) + "m" + unitFormat(second) + " s";
                }
            }
        }
        return timeStr;
    }

    public static String formatMusicDuration(int duration) {
        duration /= 1000; // milliseconds into seconds
        int minute = duration / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }

    public static String getDayOfWeek(Context context, long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String today = null;
        if (day == 2) {
            today = context.getString(R.string.monday);
        } else if (day == 3) {
            today = context.getString(R.string.tuesday);
        } else if (day == 4) {
            today = context.getString(R.string.wednesday);
        } else if (day == 5) {
            today = context.getString(R.string.thursday);
        } else if (day == 6) {
            today = context.getString(R.string.friday);
        } else if (day == 7) {
            today = context.getString(R.string.saturday);
        } else if (day == 1) {
            today = context.getString(R.string.sunday);
        }
        return today;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static double decimalTwo(double data) {
        BigDecimal bigDecimal = new BigDecimal(data);
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 根据经纬度计算距离
     *
     * @param locationA
     * @param locationB
     * @return
     */
    public static float gps2m(Location locationA, Location locationB) {
        double radLat1 = (locationA.getLatitude() * Math.PI / 180.0);
        double radLat2 = (locationB.getLatitude() * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (locationA.getLongitude() - locationB.getLongitude()) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return (float) s;
    }

    public static boolean doubleClick() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) //如果两次按键时间间隔大于2秒,重置
        {
            firstTime = secondTime;//更新firstTime
            return false;
        } else  //两次按键小于2秒时，退出应用
        {
            return true;
        }
    }

    /**
     * 广播
     *
     * @param mContext
     * @param action
     */
    public static void broadUpdate(Context mContext, final String action) {
        final Intent intent = new Intent(action);
        mContext.sendBroadcast(intent);
    }

    /**
     * 广播
     *
     * @param mContext
     * @param action
     * @param key
     * @param value
     */
    public static void broadUpdate(Context mContext, final String action, String key, String value) {
        final Intent intent = new Intent(action);
        intent.putExtra(key, value);
        mContext.sendBroadcast(intent);
    }

    /**
     * 广播
     *
     * @param mContext
     * @param action
     * @param key
     * @param value
     */
    public static void broadUpdate(Context mContext, final String action, String key, int value) {
        final Intent intent = new Intent(action);
        intent.putExtra(key, value);
        mContext.sendBroadcast(intent);
    }

    /**
     * 广播
     *
     * @param mContext
     * @param action
     * @param key
     * @param value
     */
    public static void broadUpdate(Context mContext, final String action, String key, long value) {
        final Intent intent = new Intent(action);
        intent.putExtra(key, value);
        mContext.sendBroadcast(intent);
    }

    /**
     * 延迟ms
     * @param time ms
     */
    public static void mSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * MD5 32位小写加密
     * 16位小写加密只需getMd5Value("xxx").substring(8, 24);即可
     *
     * @param sSecret
     * @return
     */
    public static String getMd5Value(String sSecret) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance("MD5");
            bmd5.update(sSecret.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String edittextTostring(EditText editText) {
        return editText.getText().toString();
    }

    public static String getDateFromMs(long date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(date));
    }

    public static String getDateAndHourFromMs(long date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(date));
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static PowerManager.WakeLock wakeLock = null;

    public static void acquireWakeLock(Context context) {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "WakeLock");
            if (null != wakeLock) {
                //   Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    // 释放设备电源锁
    public static void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            //   Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 保存图片
     * @param bm
     * @param dirName
     * @param fileName
     * @return
     */
    public static Uri saveBitmap(Bitmap bm, String dirName, String fileName) {
        FileOutputStream fos = null;

        File tmpDir;//图片文件夹
        File picture;//图片文件
        tmpDir = new File(Environment.getExternalStorageDirectory() + "/com.dxy.blecar/" + dirName);
        if (!tmpDir.exists()) {
            tmpDir.mkdir();
        }
        picture = new File(tmpDir.getAbsolutePath() + File.separator + fileName + ".jpg");
        try {
            fos = new FileOutputStream(picture);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            myLog.d(Uri.fromFile(picture) + "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null){
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(picture);
    }

    //zh：汉语  en：英语
    public static boolean isZh(Context mContext) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    public static void openFile(Context mContext, File file) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 计算0的个数
     * @param data
     */
    public static byte checkSum(byte[] data) {
        byte sum = 0;
        for (int i=1; i<data.length-1; i++){
            for (int j=0; j<8; j++){
                if (( (data[i]>>j) & 0x01 ) == 0) {
                    sum++;
                }
            }
        }
        return sum;
    }

}
