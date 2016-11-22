package com.wangjingxi.jancee.janceeble.Utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.wangjingxi.jancee.janceeble.BleCallback.PeriodScanCallback;
import com.wangjingxi.jancee.janceeble.LiteBle;
import com.wangjingxi.jancee.janceeble.Utils.BleLog;
import com.wangjingxi.jancee.janceeble.listener.BleScanListener;

import java.util.ArrayList;

/**
 *
 * 蓝牙搜索
 *
 */
public class BleScan {
    private BleLog myLog = new BleLog("[BleScan] ");
    private Context context;
    private LiteBle liteBle;
    private BleScanListener bleScanListener;
    private ArrayList<String> addrList = new ArrayList<>();
    private int period = 30000;

    public BleScan(Context context) {
        this.context = context;
        liteBle = new LiteBle(context);
    }

    public BleScan(Context context, int period) {
        this.context = context;
        this.period = period;
        liteBle = new LiteBle(context);
    }

    public void setBleScanListener(BleScanListener l) {
        bleScanListener = l;
    }

    public void startScan() {
        myLog.d("startScan");
        if (!liteBle.isInScanning()){
            liteBle.startBleScan(periodScanCallback);
            addrList.clear();
        }
    }

    public void stopScan() {
        myLog.d("stopScan");
        if (liteBle.isInScanning())
            liteBle.stopBleScan(periodScanCallback);
    }

    public boolean isInScanning() {
        return liteBle.isInScanning();
    }

    private final PeriodScanCallback periodScanCallback = new PeriodScanCallback(period) {

        @Override
        public void onScanTimeout() {
            myLog.d("periodScanCallback onScanTimeout");
            bleScanListener.onScanTimeout();
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            myLog.d("periodScanCallback onLeScan: device.getName(): " + device.getName() + ",device.getAddress(): " + device.getAddress());
            if (!addrList.contains(device.getAddress())){
                addrList.add(device.getAddress());
                bleScanListener.onBleScanResult(device, rssi, scanRecord);
            }
        }
    };

    public LiteBle getLiteBle() {
        return liteBle;
    }

}
