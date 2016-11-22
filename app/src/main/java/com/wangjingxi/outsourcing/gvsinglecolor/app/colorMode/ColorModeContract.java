package com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode;

import com.wangjingxi.jancee.janceelib.base.BasePresenter;
import com.wangjingxi.jancee.janceelib.base.BaseView;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class ColorModeContract {

    public interface View extends BaseView<ColorModeContract.Presenter> {
    }

    public interface Presenter extends BasePresenter {
        void setLedMode(int mode);
    }

}
