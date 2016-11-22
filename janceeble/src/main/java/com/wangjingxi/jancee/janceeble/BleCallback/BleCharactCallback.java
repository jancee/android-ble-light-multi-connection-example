package com.wangjingxi.jancee.janceeble.BleCallback;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 *
 */
public abstract class BleCharactCallback extends BleCallback {
    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);
}
