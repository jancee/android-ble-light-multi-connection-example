package com.wangjingxi.outsourcing.gvsinglecolor.core.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.wangjingxi.jancee.janceeble.BleCallback.BleCharactCallback;
import com.wangjingxi.jancee.janceeble.Utils.HexUtil;
import com.wangjingxi.jancee.janceeble.listener.BleConnectListener;
import com.wangjingxi.jancee.janceeble.listener.BleDisconnectListener;
import com.wangjingxi.jancee.janceeble.listener.BleOnReceiveListener;
import com.wangjingxi.jancee.janceeble.listener.BleReadCallback;
import com.wangjingxi.jancee.janceeble.listener.BleServicesListener;
import com.wangjingxi.jancee.janceeble.listener.BleWriteCallback;
import com.wangjingxi.jancee.janceeble.LiteBle;
import com.wangjingxi.jancee.janceeble.LiteBleConnector;
import com.wangjingxi.jancee.janceeble.Utils.BleLog;
import com.wangjingxi.jancee.janceeble.exception.BleException;

import java.util.List;

/**
 *
 * 蓝牙管理
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleManager {
    private BleLog myLog = new BleLog("[BleManager] ");
    private Context context;
    private LiteBle liteBle;
    private LiteBleConnector liteBleConnector;
    private boolean isDiscoveredService = false;
    private int id = 255;

    //存储的特征
    public BluetoothGattCharacteristic switchGattCharacteristic;
    public BluetoothGattCharacteristic modeGattCharacteristic;
    public BluetoothGattCharacteristic musicGattCharacteristic;
    public BluetoothGattCharacteristic timeHackGattCharacteristic;
    public BluetoothGattCharacteristic setAlertCountGattCharacteristic;
    public BluetoothGattCharacteristic setAlertEvenGattCharacteristic;
    public BluetoothGattCharacteristic setAlertFinishGattCharacteristic;

    //所需要的特征值
    private final String UUID_LED_SWITCH        = "0000edb1-0000-1000-8000-00805f9b34fb";
    private final String UUID_LED_MODE          = "0000eda2-0000-1000-8000-00805f9b34fb";
    private final String UUID_MUSIC_MODE        = "0000eda3-0000-1000-8000-00805f9b34fb";
    private final String UUID_TIME_HACK         = "0000ed01-0000-1000-8000-00805f9b34fb";
    private final String UUID_SET_ALERT_COUNT   = "0000ed04-0000-1000-8000-00805f9b34fb";
    private final String UUID_SET_ALERT_EVEN    = "0000ed03-0000-1000-8000-00805f9b34fb";
    private final String UUID_SET_ALERT_FINISH  = "0000ed03-0000-1000-8000-00805f9b34fb";

    private BleDisconnectListener bleDisconnectListener = null; //断开连接处理
    private BleOnReceiveListener bleOnReceiveListener = null;

    public BleManager(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    private void init() {
        liteBle = new LiteBle(context, id);
        if (bleDisconnectListener != null)
            liteBle.setBleDisconnectListener(bleDisconnectListener);
    }

    public void setBleDisconnectListener(BleDisconnectListener bleDisconnectListener) {
        this.bleDisconnectListener = bleDisconnectListener;
    }

    public void closeBle() {
        if (liteBle != null)
            liteBle.closeBluetoothGatt();
    }

    /**
     *
     * 连接设备
     *
     * @param bleName
     * @param bleAddr
     * @param bleConnectCallback    需要回调的结果
     */
    public void connect(String bleName, String bleAddr, final BleConnectListener bleConnectCallback) {
        isDiscoveredService = false;
        init();
        liteBle.connect(bleName, bleAddr, new BleServicesListener() {
            @Override
            public void discoverServices(List<BluetoothGattService> gattServices) {
                if (gattServices == null) return;

                //寻找特定特征并存储到变量
                for (BluetoothGattService gattService : gattServices) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                    for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        myLog.d("gattCharacteristics: " + gattCharacteristic.getUuid().toString());

                        //寻找特征值并存储
                        if (gattCharacteristic.getUuid().toString().equals(UUID_LED_SWITCH)) {
                            switchGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_LED_MODE)) {
                            modeGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_MUSIC_MODE)) {
                            musicGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_TIME_HACK)) {
                            timeHackGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_SET_ALERT_COUNT)) {
                            setAlertCountGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_SET_ALERT_EVEN)) {
                            setAlertEvenGattCharacteristic = gattCharacteristic;
                        }
                        if (gattCharacteristic.getUuid().toString().equals(UUID_SET_ALERT_FINISH)) {
                            setAlertFinishGattCharacteristic = gattCharacteristic;
                        }
                    }
                }

                //如果已发现服务？
                if (isDiscoveredService) {
                    myLog.e("connect: 第二次进入服务！");
                }


                //检查是否找到所有特征
                //特证全
                if ((switchGattCharacteristic               != null)  &&
                        (modeGattCharacteristic             != null) &&
                        (musicGattCharacteristic            != null) &&
                        (timeHackGattCharacteristic         != null) &&
                        (setAlertCountGattCharacteristic != null) &&
                        (setAlertEvenGattCharacteristic != null) &&
                        (setAlertFinishGattCharacteristic != null)) {
                    //已发现服务标记
                    isDiscoveredService = true;

                    //新建一个连接器？
                    liteBleConnector = liteBle.newBleConnector();

//                    setOnReceiveListener(settingGattCharacteristic);
//                    liteBleConnector.setNotificationForCharacteristic(settingGattCharacteristic, true);

                    //回调蓝牙设备已连接
                    bleConnectCallback.onBleConnect();
                }

                //特征不全
                else {
                    if (!isDiscoveredService) {
                        myLog.e("connect: 发现的服务不全");

                        //回调蓝牙断开连接
                        if (bleDisconnectListener != null)
                            bleDisconnectListener.onBleDisconnect(true);
                    }
                }
            }

        });
    }

    /**
     * 回调为null则为无应答写，否则为有应答写
     *
     * @param data
     * @param characteristic
     * @param bleWriteCallback
     */
    public void writeCharacteristic(byte[] data, BluetoothGattCharacteristic characteristic, final BleWriteCallback bleWriteCallback) {
        if (bleWriteCallback != null) {
            liteBleConnector.writeCharacteristic(data, characteristic, new BleCharactCallback() {
                @Override
                public void onSuccess(BluetoothGattCharacteristic characteristic) {
                    bleWriteCallback.onWriteSuccess();
                }

                @Override
                public void onFailure(BleException exception) {
                    myLog.e("writeCharacteristic exception: " + exception);
                }

            });
        } else {
            liteBleConnector.writeCharacteristic(data, characteristic, null);
        }
    }

    /**
     * 写入特征，并监听收到数据
     *
     * @param data
     * @param characteristic
     * @param bleWriteCallback
     * @param bleOnReceiveListener
     */
    public void writeCharacteristic(byte[] data, BluetoothGattCharacteristic characteristic, final BleWriteCallback bleWriteCallback, BleOnReceiveListener bleOnReceiveListener) {
        this.bleOnReceiveListener = bleOnReceiveListener;
        if (bleWriteCallback != null) {
            liteBleConnector.writeCharacteristic(data, characteristic, new BleCharactCallback() {
                @Override
                public void onSuccess(BluetoothGattCharacteristic characteristic) {
                    bleWriteCallback.onWriteSuccess();
                }

                @Override
                public void onFailure(BleException exception) {
                    myLog.e("writeCharacteristic exception: " + exception);
                }

            });
        } else {
            liteBleConnector.writeCharacteristic(data, characteristic, null);
        }
    }

    /**
     * 从特征读
     *
     * @param characteristic
     * @param bleReadCallback
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic, final BleReadCallback bleReadCallback) {
        liteBleConnector.readCharacteristic(characteristic, new BleCharactCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                bleReadCallback.onReadSuccess(characteristic.getValue());
            }

            @Override
            public void onFailure(BleException exception) {
                myLog.e("readCharacteristic exception: " + exception);
            }
        });
    }

    /**
     * 设置接收到数据的listener(本程序工程没用上读取数据)
     *
     * @param bluetoothGattCharacteristic
     */
    public void setOnReceiveListener(final BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        liteBleConnector.enableCharacteristicNotification(bluetoothGattCharacteristic, new BleCharactCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                if (characteristic == bluetoothGattCharacteristic) {
                    myLog.d("bleOnReceiveListener: " + HexUtil.encodeHexStr(characteristic.getValue()));
                    if (bleOnReceiveListener != null) {
                        bleOnReceiveListener.onReceiveListener(characteristic.getValue());
                        bleOnReceiveListener = null;
                    }
                }
            }

            @Override
            public void onFailure(BleException exception) {
                myLog.e("setOnReceiveListener onFailure");
            }
        });
    }

//    public void enableNotify() {
//        liteBleConnector.enableNotify(notifyGattCharacteristic);
//        liteBleConnector.setNotificationForCharacteristic(notifyGattCharacteristic, true);
//    }

}
