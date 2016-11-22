package com.wangjingxi.outsourcing.gvsinglecolor.core.ble;

import android.text.format.Time;

import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.service.BleService;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;

/**
 *
 * 蓝牙控制  发送逻辑处理，主要用于处理三个设备的发送标记
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleControl {
    private static MyLog myLog = new MyLog("[BleControl] ");

    //命令类型标识
    private static final int COMMAND_MODE   = 21;
    private static final int COMMAND_SWITCH = 22;

    private static int lastCommand      = -1;            //最后一次指令
    private static int lastMode         = -1;               //最后一次模式
    private static boolean lastSwitch   = false;      //最后一次开关状态

    /**
     * 设置灯的变换模式
     * @param mode
     */
    public static void setBleMode(int mode) {
        //记录本次作为最后一次的命令
        lastCommand = COMMAND_MODE;
        lastMode    = mode;

        myLog.d("setBleMode");

        //判断连上了哪几个灯
        if (BleService.bleConnected0)
        {
            BleService.myBle0.setMode(mode);
            Tools.mSleep(20);
        }
        if (BleService.bleConnected1){
            BleService.myBle1.setMode(mode);
            Tools.mSleep(20);
        }
        if (BleService.bleConnected2){
            BleService.myBle2.setMode(mode);
            Tools.mSleep(20);
        }
    }

    /**
     * 设置灯的开关
     * @param isOpen
     */
    public static void switchBle(boolean isOpen) {
        lastCommand = COMMAND_SWITCH;
        lastSwitch = isOpen;
        myLog.d("switchBle");

        if (BleService.bleConnected0){
            BleService.myBle0.switchLed(isOpen);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected1){
            BleService.myBle1.switchLed(isOpen);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected2){
            BleService.myBle2.switchLed(isOpen);
            Tools.mSleep(20);
        }
    }

    /**
     * 对时
     */
    public static void hackBleTime(Time timeT) {
        Time t;
        if(timeT != null) {
            t = timeT;
        } else {
            //获取当前时间
            t = new Time();
            t.setToNow(); // 取得系统时间。
        }
        int year    = t.year;
        int month   = t.month + 1;
        int day     = t.monthDay;
        int hour    = t.hour;
        int minute  = t.minute;
        int second  = t.second;

        if (BleService.bleConnected0) {
            BleService.myBle0.hackBleTime((byte) (year / 255),
                    (byte) (year % 255),
                    (byte) (month),
                    (byte) (day),
                    (byte) (hour),
                    (byte) (minute),
                    (byte) (second));
            Tools.mSleep(20);
        }

        if (BleService.bleConnected1){
            BleService.myBle1.hackBleTime((byte) (year / 255),
                    (byte) (year % 255),
                    (byte) (month),
                    (byte) (day),
                    (byte) (hour),
                    (byte) (minute),
                    (byte) (second));
            Tools.mSleep(20);
        }

        if (BleService.bleConnected2){
            BleService.myBle2.hackBleTime((byte) (year / 255),
                    (byte) (year % 255),
                    (byte) (month),
                    (byte) (day),
                    (byte) (hour),
                    (byte) (minute),
                    (byte) (second));
            Tools.mSleep(20);
        }
    }

    /**
     * 设置闹钟数量
     * （设置闹钟步骤1）
     *
     * @param count
     */
    public static void setAlertCount(int count) {
        if (BleService.bleConnected0) {
            BleService.myBle0.setAlertCount(count);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected1) {
            BleService.myBle1.setAlertCount(count);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected2) {
            BleService.myBle2.setAlertCount(count);
            Tools.mSleep(20);
        }
    }

    /**
     * 设置闹钟（重复）
     * （设置闹钟步骤2）
     * @param index
     * @param hour
     * @param minute
     * @param second
     * @param toOn
     */
    public static void setAlertEvenRepeat(int index, int hour, int minute, int second, boolean toOn) {
        setAlertEvenNoRepeat(index,0xff,0xff,0xff,hour,minute,second,toOn);
    }

    /**
     * 设置闹钟（不重复）
     * （设置闹钟步骤2）
     * @param index
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @param toOn
     */
    public static void setAlertEvenNoRepeat(int index, int year, int month, int day,
                                            int hour, int minute, int second, boolean toOn) {
        if (BleService.bleConnected0) {
            BleService.myBle0.setAlertEven(index, year, month, day, hour, minute, second, toOn);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected1) {
            BleService.myBle1.setAlertEven(index, year, month, day, hour, minute, second, toOn);
            Tools.mSleep(20);
        }

        if (BleService.bleConnected2) {
            BleService.myBle2.setAlertEven(index, year, month, day, hour, minute, second, toOn);
            Tools.mSleep(20);
        }
    }

    /**
     * 设置闹钟完成
     * （设置闹钟步骤3）
     */
    public static void setAlertSettingFinish() {
        if (BleService.bleConnected0) {
            BleService.myBle0.setAlertSettingFinish();
            Tools.mSleep(20);
        }

        if (BleService.bleConnected1) {
            BleService.myBle1.setAlertSettingFinish();
            Tools.mSleep(20);
        }

        if (BleService.bleConnected2) {
            BleService.myBle2.setAlertSettingFinish();
            Tools.mSleep(20);
        }
    }

    /**
     * 设置灯的亮度（音乐用）
     * @param light
     */
    public static void setMusicLight(byte light) {
        if (!MusicManager.isStart)
            return;
        myLog.d("setMusicLight");
        if (BleService.bleConnected0){
            BleService.myBle0.setMusicLight(light);
            Tools.mSleep(20);
        }
        if (BleService.bleConnected1){
            BleService.myBle1.setMusicLight(light);
            Tools.mSleep(20);
        }
        if (BleService.bleConnected2){
            BleService.myBle2.setMusicLight(light);
            Tools.mSleep(20);
        }
    }

    /**
     * 发送上一个命令
     */
    public static void sendLastCommand() {
        switch (lastCommand) {
            case COMMAND_MODE:
                setBleMode(lastMode);
                break;
            case COMMAND_SWITCH:
                switchBle(lastSwitch);
                break;
            default:
                break;
        }
    }


}
