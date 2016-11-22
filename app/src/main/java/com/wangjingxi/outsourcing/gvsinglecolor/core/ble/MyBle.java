package com.wangjingxi.outsourcing.gvsinglecolor.core.ble;

import android.content.Context;

import com.wangjingxi.jancee.janceeble.Utils.BleLog;
import com.wangjingxi.jancee.janceeble.listener.BleConnectListener;

/**
 *
 * 蓝牙控制  底层发送数据和控制处理
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MyBle {
    private BleLog myLog = new BleLog("[MyBle] ");
    private BleManager bleManager;

    private int lastAlertIndex; //最后一个闹钟索引值

    public MyBle(Context context, int id) {
        bleManager = new BleManager(context, id);
    }

    /**
     * 断开ble
     */
    public void closeBle() {
        myLog.d("closeBle");
        bleManager.closeBle();
    }

    /**
     * 连接ble
     * @param name
     * @param addr
     * @param bleConnectListener
     */
    public void connect(String name, String addr, final BleConnectListener bleConnectListener) {
        final String finalAddr = addr;
        bleManager.connect(name, addr, new BleConnectListener() {
            @Override
            public void onBleConnect() {
                myLog.d(finalAddr + "连接成功!");
                bleConnectListener.onBleConnect();
            }
        });
    }

    /**
     * 灯开关
     * @param isOpen
     */
    public void switchLed(boolean isOpen) {
        byte[] bytes = new byte[1];
        if (isOpen)
            bytes[0] = 1;
        else
            bytes[0] = 0;
        bleManager.writeCharacteristic(bytes, bleManager.switchGattCharacteristic, null);
    }

    /**
     * 设置模式
     * @param mode
     */
    public void setMode(int mode) {
        byte[] bytes = new byte[] {(byte) 0xBB, 0x25, 0x02, 0x44};
        bytes[1] = (byte) (0x40 + mode);
        bleManager.writeCharacteristic(bytes, bleManager.modeGattCharacteristic, null);
    }

    /**
     * 设置亮度（音乐灯）
     * @param light
     */
    public void setMusicLight(byte light) {
        byte[] bytes = new byte[] {(byte) 0x78, 0x25, 0x00, (byte) 0xee};
        bytes[2] = light;
        bleManager.writeCharacteristic(bytes, bleManager.musicGattCharacteristic, null);
    }


    /**
     * 对时
     */
    public void hackBleTime(byte yearHigh, byte yearLow,
                                   byte month, byte day, byte hour, byte minute, byte second) {
        byte[] bytes = new byte[] {yearHigh, yearLow, month, day, hour, minute, second};

        bleManager.writeCharacteristic(bytes, bleManager.timeHackGattCharacteristic, null);
    }

    /**
     * 设置闹钟数量
     * （设置闹钟步骤1）
     *
     * @param count
     */
    public void setAlertCount(int count) {
        byte[] bytes = new byte[] {0x00, 0x00, 0x00, 0x00};

        if(count != 0) {
            for (int i = 0; i < count; i++) {
                bytes[i / 8] = (byte) ((bytes[i / 8] << 1) + 1);
            }
        }

        bleManager.writeCharacteristic(bytes, bleManager.setAlertCountGattCharacteristic, null);
    }


    /**
     * 设置闹钟
     * （设置闹钟步骤2）
     * @param index     索引
     * @param year      重复为0xFF
     * @param month     重复为0xFF
     * @param day       重复为0xFF
     * @param hour      时
     * @param minute    分
     * @param second    秒
     * @param toOn      开还是关
     */
    public void setAlertEven(int index, int year, int month, int day,
                                            int hour, int minute, int second, boolean toOn) {
        lastAlertIndex = index;
        byte[] bytes = new byte[] {
                (byte) index,
                (byte) (year / 255),
                (byte) (year % 255),
                (byte) month, (byte) day,
                (byte) hour, (byte) minute,
                (byte) second,
                (byte) (toOn ? 0x31 : 0x30)};
        bleManager.writeCharacteristic(bytes, bleManager.setAlertEvenGattCharacteristic, null);
    }


    /**
     * 设置闹钟完成
     * （设置闹钟步骤3）
     */
    public void setAlertSettingFinish() {
        byte[] bytes = new byte[] {(byte) (lastAlertIndex + 1), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        bleManager.writeCharacteristic(bytes, bleManager.setAlertFinishGattCharacteristic, null);
    }


}
