package com.wangjingxi.jancee.janceeble.exception.hanlder;

import android.content.Context;
import android.widget.Toast;

import com.wangjingxi.jancee.janceeble.Utils.BleToast;
import com.wangjingxi.jancee.janceeble.exception.ConnectException;
import com.wangjingxi.jancee.janceeble.exception.GattException;
import com.wangjingxi.jancee.janceeble.exception.InitiatedException;
import com.wangjingxi.jancee.janceeble.exception.OtherException;
import com.wangjingxi.jancee.janceeble.exception.TimeoutException;

/**
 *
 */
public class DefaultBleExceptionHandler extends BleExceptionHandler {
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {
        BleToast.showShort(context, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        BleToast.showShort(context, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        BleToast.showShort(context, e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        BleToast.showShort(context, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        BleToast.showShort(context, e.getDescription());
    }
}
