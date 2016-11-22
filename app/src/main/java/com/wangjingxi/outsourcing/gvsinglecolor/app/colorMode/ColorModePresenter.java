package com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode;

import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.BleControl;
import com.wangjingxi.jancee.janceelib.base.MvpPresenter;

import org.androidannotations.annotations.EBean;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EBean
public class ColorModePresenter extends MvpPresenter<ColorModeContract.View> implements ColorModeContract.Presenter {

    @Override
    public void setLedMode(int mode) {
        BleControl.setBleMode(mode);
    }

}
