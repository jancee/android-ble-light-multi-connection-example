package com.wangjingxi.jancee.janceeble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.wangjingxi.jancee.janceeble.Utils.BleToast;
import com.wangjingxi.jancee.janceeble.Utils.HexUtil;
import com.wangjingxi.jancee.janceeble.listener.BleDisconnectListener;
import com.wangjingxi.jancee.janceeble.listener.BleServicesListener;
import com.wangjingxi.jancee.janceeble.BleCallback.PeriodScanCallback;
import com.wangjingxi.jancee.janceeble.BleCallback.ScanCallback;
import com.wangjingxi.jancee.janceeble.Utils.BleLog;
import com.wangjingxi.jancee.janceeble.exception.BleException;
import com.wangjingxi.jancee.janceeble.exception.ConnectException;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class LiteBle {

    private Context mContext;
    public static final String ACTION_BLE_DISCONNECT    = "com.wangjingxi.ACTION_BLE_DISCONNECT";
    public static final String BLE_DISCONNECT_ID        = "com.wangjingxi.BLE_DISCONNECT_ID";
    public static final int DEFAULT_SCAN_TIME   = 10000;
    public static final int STATE_DISCONNECTED  = 0;
    public static final int STATE_SCANNING      = 1;
    public static final int STATE_CONNECTING    = 2;
    public static final int STATE_CONNECTED     = 3;
    public static final int STATE_SERVICES_DISCOVERED = 4;
    private final int retryPeriod           = 3000;
    private int retryDiscoverServiceCount   = 0;

    private int connectionState = STATE_DISCONNECTED;
    private BluetoothManager    mBluetoothManager;
    private BluetoothAdapter    mBluetoothAdapter;
    private BluetoothGatt       mBluetoothGatt;
    private Set<BluetoothGattCallback> callbackList = new LinkedHashSet<BluetoothGattCallback>();
    public  BleServicesListener mBleServicesListener;

    public  String mDeviceAddress = "null";
    public  String mDeviceName = "null";
    private BleLog bleLog = new BleLog("[LiteBle] ");
    private Handler handler;
    public  BleDisconnectListener bleDisconnectListener;
    private int id;

    public LiteBle(Context context) {
        this.mContext = context;

        initialize();
        handler = new Handler();
    }

    public LiteBle(Context context, int id) {
        this.mContext = context;
        this.id = id;

        initialize();
        handler = new Handler();
    }

//    private boolean isDateOk() {
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH) + 1;
//
//        bleLog.d("year: " + year + ",month: " + month);
//        return !(year >= 2017 && month >= 6);
//    }

    public LiteBleConnector newBleConnector() {
        return new LiteBleConnector(this);
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
//        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            BleToast.showShort(mContext, "ble not supported");
//            return false;
//        }
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                bleLog.e("Unable to initialize BluetoothManager!");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            bleLog.e("Unable to obtain a BluetoothAdapte!");
            return false;
        }

        enableBluetoothIfDisabled();
        bleLog.d("Initialize Successful!");
        return true;
    }

    public void enableBluetoothIfDisabled() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    //-------------SCAN FOR DEVICE--------------------------------------

    public void startBleScan(PeriodScanCallback callback) {
        callback.setLiteBle(this).notifyScanStarted();   //设置超时停止扫描
        boolean suc = mBluetoothAdapter.startLeScan(callback);
        connectionState = STATE_SCANNING;
        if (suc) {
            connectionState = STATE_SCANNING;
        } else {
            bleLog.e("StarScan Fail!");
        }
    }

    public void startBleScan(ScanCallback callback) {
        callback.setLiteBle(this).notifyScanStarted();   //设置超时停止扫描
        mBluetoothAdapter.startLeScan(callback);
    }

    public void stopBleScan(BluetoothAdapter.LeScanCallback callback) {
        if (callback instanceof PeriodScanCallback) {
            ((PeriodScanCallback) callback).removeHandlerMsg();
        } else if (callback instanceof ScanCallback) {
            ((ScanCallback) callback).removeHandlerMsg();
        }
        mBluetoothAdapter.stopLeScan(callback);
        if (connectionState == STATE_SCANNING) {
            connectionState = STATE_DISCONNECTED;
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     * <p/>
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(String bleName, String bleAddr, BleServicesListener l) {
        mDeviceName = bleName;
        mDeviceAddress = bleAddr;
        if (mBluetoothAdapter == null || mDeviceAddress == null) {
            bleLog.w(mDeviceName + ": BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        if (device == null) {
            bleLog.w(mDeviceName + ": Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bleLog.d("Trying to create a new connection.");
        mBluetoothGatt = device.connectGatt(mContext, false, coreGattCallback);
        bleLog.setBleTag(mDeviceName + ": " + mDeviceAddress + "--->");
        mBleServicesListener = l;
        return true;
    }

    private LiteBleGattCallback coreGattCallback = new LiteBleGattCallback() {
        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            bleLog.d("onConnectSuccess.");
//            mBluetoothGatt = gatt;
//            retryDiscoverServiceCount = 0;
            mBluetoothGatt.discoverServices();
//            handler.postDelayed(retryDiscoverServices, retryPeriod);
            bleLog.d("Attempting to start service discovery");
            for (BluetoothGattCallback call : callbackList) {
                if (call instanceof LiteBleGattCallback) {
                    ((LiteBleGattCallback) call).onConnectSuccess(gatt, status);
                }
            }
        }

        @Override
        public void onConnectFailure(BleException exception) {
            bleLog.e("onConnectFailure.");
//            mBluetoothGatt = null;
            braodUpdate(mContext, ACTION_BLE_DISCONNECT, id);
            if (bleDisconnectListener != null){
                bleDisconnectListener.onBleDisconnect(false);
            }
            for (BluetoothGattCallback call : callbackList) {
                if (call instanceof LiteBleGattCallback) {
                    ((LiteBleGattCallback) call).onConnectFailure(exception);
                }
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            bleLog.d("onConnectionStateChange  status: " + status
                    + " ,newState: " + newState + "  ,thread: " + Thread.currentThread().getId());
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                onConnectSuccess(gatt, status);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                onConnectFailure(new ConnectException(gatt, status));
            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                connectionState = STATE_CONNECTING;
            }
            for (BluetoothGattCallback call : callbackList) {
                call.onConnectionStateChange(gatt, status, newState);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            connectionState = STATE_SERVICES_DISCOVERED;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                bleLog.d("onServicesDiscovered success.");
                mBleServicesListener.discoverServices(gatt.getServices());
                for (BluetoothGattCallback call : callbackList) {
                    call.onServicesDiscovered(gatt, status);
                }
            } else {
                bleLog.e("onServicesDiscovered fail.");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            bleLog.d("onCharacteristicRead.");
            for (BluetoothGattCallback call : callbackList) {
                call.onCharacteristicRead(gatt, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            bleLog.d("onCharacteristicWrite,callbackList.size(): " + callbackList.size());
            for (BluetoothGattCallback call : callbackList) {
                call.onCharacteristicWrite(gatt, characteristic, status);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            bleLog.d("onCharacteristicChanged: " + HexUtil.encodeHexStr(characteristic.getValue()));
            for (BluetoothGattCallback call : callbackList) {
                call.onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            bleLog.d("onReliableWriteCompleted.");
            for (BluetoothGattCallback call : callbackList) {
                call.onReliableWriteCompleted(gatt, status);
            }
        }
    };

    Runnable retryDiscoverServices = new Runnable() {
        @Override
        public void run() {
            if (!isConnectingOrConnected() && retryDiscoverServiceCount<3){
                bleLog.d("retryDiscoverServices: " + retryDiscoverServiceCount);
                retryDiscoverServiceCount++;
                handler.postDelayed(retryDiscoverServices, retryPeriod);
            }
        }
    };

    /**
     * Clears the device cache. After uploading new hello4 the DFU target will have other services than before.
     */
    public boolean refreshDeviceCache() {
        /*
         * There is a refresh() method in BluetoothGatt class but for now it's hidden. We will call it using reflections.
		 */
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(getBluetoothGatt());
                bleLog.d("Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            bleLog.e("An exception occured while refreshing device: " +  e);
        }
        return false;
    }

    /**
     * disconnect, refresh and close bluetooth gatt.
     */
    public void closeBluetoothGatt() {
        if (mBluetoothGatt != null) {
//            mBluetoothGatt.disconnect();
//            refreshDeviceCache();
            connectionState = STATE_DISCONNECTED;
//            handler.removeCallbacks(retryDiscoverServices);
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            bleLog.d("closed BluetoothGatt");
        }
    }

    public boolean isInScanning() {
        return connectionState == STATE_SCANNING;
    }

    public boolean isConnectingOrConnected() {
        return connectionState >= STATE_CONNECTING;
    }

    public boolean isConnected() {
        return connectionState >= STATE_CONNECTED;
    }

    public boolean isServiceDiscoered() {
        return connectionState == STATE_SERVICES_DISCOVERED;
    }

    public boolean addGattCallback(BluetoothGattCallback callback) {
        return callbackList.add(callback);
    }

    public boolean addGattCallback(LiteBleGattCallback callback) {
        return callbackList.add(callback);
    }

    public boolean removeGattCallback(BluetoothGattCallback callback) {
        return callbackList.remove(callback);
    }

    public void braodUpdate(Context mContext, final String action, int id) {
        final Intent intent = new Intent(action);
        intent.putExtra(BLE_DISCONNECT_ID, id);
        mContext.sendBroadcast(intent);
    }

    public void setBleDisconnectListener(BleDisconnectListener bleDisconnectListener) {
        this.bleDisconnectListener = bleDisconnectListener;
    }
    /**
     * return
     * {@link #STATE_DISCONNECTED}
     * {@link #STATE_SCANNING}
     * {@link #STATE_CONNECTING}
     * {@link #STATE_CONNECTED}
     * {@link #STATE_SERVICES_DISCOVERED}
     */
    public int getConnectionState() {
        return connectionState;
    }

    public BluetoothManager getBluetoothManager() {
        return mBluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }

    public String getDeviceAddress() {
        return mDeviceAddress;
    }

    public String getDeviceName() {
        return mDeviceName;
    }
}
