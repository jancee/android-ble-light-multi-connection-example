package com.wangjingxi.jancee.janceeble.listener;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 *
 */
public interface BleServicesListener {
    void discoverServices(List<BluetoothGattService> gattServices);
}
