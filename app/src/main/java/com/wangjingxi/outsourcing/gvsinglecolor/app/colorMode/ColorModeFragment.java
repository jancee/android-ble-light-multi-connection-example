package com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode;

import android.os.Handler;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.ui.WheelView;
import com.wangjingxi.jancee.janceelib.base.MvpFragment;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 *
 * 变换模式
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EFragment(R.layout.fragment_color)
public class ColorModeFragment extends MvpFragment<ColorModeContract.Presenter> implements ColorModeContract.View, WheelView.WheelSelectedListener {
    private MyLog mylog = new MyLog("[ColorModeFragment] ");

    @ViewById
    WheelView wheelView;

    @AfterViews
    void init() {
        mylog.d("AfterViews");
        setUpWheelData();
    }

    private boolean setUpWheelData() {
        String[] wheelStrings = new String[]{
                getString(R.string.color_mode1),
                getString(R.string.color_mode2),
                getString(R.string.color_mode3),
                getString(R.string.color_mode4),
                getString(R.string.color_mode5),
                getString(R.string.color_mode6),
                getString(R.string.color_mode7),
                getString(R.string.color_mode8),
                getString(R.string.color_mode9),
                getString(R.string.color_mode10),
                getString(R.string.color_mode11),
                getString(R.string.color_mode12),
                getString(R.string.color_mode13),
                getString(R.string.color_mode14),
                getString(R.string.color_mode15),
                getString(R.string.color_mode16)};

        wheelView.clearData();
        wheelView.setWheelSelectedListener(this);
        for (int i = 0; i < wheelStrings.length; i++) {
            wheelView.addData(wheelStrings[i], i);
        }
        wheelView.setCenterItem(15);
        return true;
    }

    @Override
    public void onWheelSelected() {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                int mode = (int) wheelView.getCenterItem();
                mylog.d("onWheelSelected : " + mode);
                mPresenter.setLedMode(mode);
            }
        }, 500);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("ColorModeFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageEnd("ColorModeFragment");
    }
}
