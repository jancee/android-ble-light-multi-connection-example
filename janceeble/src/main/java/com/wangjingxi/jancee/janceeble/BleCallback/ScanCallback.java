package com.wangjingxi.jancee.janceeble.BleCallback;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;
import android.os.Looper;

import com.wangjingxi.jancee.janceeble.LiteBle;

/**
 *
 */
public abstract class ScanCallback implements BluetoothAdapter.LeScanCallback {
    protected Handler handler = new Handler(Looper.getMainLooper());
    protected LiteBle liteBle;

    public ScanCallback() {
    }

    public void notifyScanStarted() {
        removeHandlerMsg();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                liteBle.stopBleScan(ScanCallback.this);
            }
        }, LiteBle.DEFAULT_SCAN_TIME);
    }

    public void removeHandlerMsg() {
        handler.removeCallbacksAndMessages(null);
    }

    public ScanCallback setLiteBle(LiteBle liteBle) {
        this.liteBle = liteBle;
        return this;
    }
}
