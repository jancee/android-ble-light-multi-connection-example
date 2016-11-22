package com.wangjingxi.jancee.janceeble.listener;

import android.bluetooth.BluetoothDevice;

/**
 *
 */
public interface BleScanListener {
    void onScanTimeout();
    void onBleScanResult(BluetoothDevice device, int rssi, byte[] scanRecord);
}
