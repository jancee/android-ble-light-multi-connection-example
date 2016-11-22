package com.wangjingxi.jancee.janceeble.BleCallback;

import android.bluetooth.BluetoothGattCallback;

import com.wangjingxi.jancee.janceeble.exception.BleException;

/**
 *
 */
public abstract class BleCallback {
    private BluetoothGattCallback bluetoothGattCallback;

    public BleCallback setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
        this.bluetoothGattCallback = bluetoothGattCallback;
        return this;
    }

    public BluetoothGattCallback getBluetoothGattCallback() {
        return bluetoothGattCallback;
    }

    public void onInitiatedSuccess() {
    }

    public abstract void onFailure(BleException exception);
}
