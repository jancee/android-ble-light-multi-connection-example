package com.wangjingxi.outsourcing.gvsinglecolor.app.activity;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import com.wangjingxi.outsourcing.gvsinglecolor.utils.Utils;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 *
 * 启动的Activity，Style设置了其背景图片为启动图片
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EActivity(R.layout.activity_flash)
public class FlashActivity extends Activity {
    private MyLog mylog = new MyLog("[FlashActivity] ");

    @Pref
    MyPrefs_ myPrefs;

    @AfterViews
    void init() {

        //将指定id传递给ClickActivity_.class并启动
        Utils.startClickActivityById(this, R.id.frag_ble_search);

        //结束当前这个activity
        finish();


//        checkLocationPermission();
    }

//    private void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//            MyToast.showLong(this, getString(R.string.please_opne_gps_permission));
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}


