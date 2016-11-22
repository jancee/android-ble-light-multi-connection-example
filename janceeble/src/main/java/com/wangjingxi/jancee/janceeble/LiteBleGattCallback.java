package com.wangjingxi.jancee.janceeble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import com.wangjingxi.jancee.janceeble.exception.BleException;

/**
 *
 */
public abstract class LiteBleGattCallback extends BluetoothGattCallback {

    public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

    public abstract void onConnectFailure(BleException exception);
}
