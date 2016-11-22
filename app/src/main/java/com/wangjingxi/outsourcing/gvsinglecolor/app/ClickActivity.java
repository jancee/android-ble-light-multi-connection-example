package com.wangjingxi.outsourcing.gvsinglecolor.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.MyToast;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * 调度Activity
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EActivity(R.layout.activity_click)
public class ClickActivity extends Activity {
    private MyLog myLog = new MyLog("[ClickActivity] ");
    private FragmentManager fm;
    Fragment fragment;

    @Extra
    int resId;

    @ViewById
    TextView tv_top_title;

    @Bean
    FragmentFactory fragmentFactory;

    @ViewById
    RelativeLayout rl_top_all;

    @ViewById
    RelativeLayout rl_top_back;

    @Click
    void rl_top_back() {
        finish();
    }

    @AfterViews
    void init() {
        setTitleBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLog.d("resId: " + resId);

        //按照id获取对应代表的
        fragment = fragmentFactory.createById(this, resId);

        //显示fragment
        setDefaultFragment();
    }

    /**
     * 显示fragment
     */
    private void setDefaultFragment() {
        fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fragcontent, fragment);
        transaction.commit();
    }

    private void setTitleBar() {
        switch (resId){
            default:
//                rl_top_all.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * 退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (Tools.doubleClick()) {
                    finish();
                } else {
                    MyToast.showShort(this, getString(R.string.press_again_exit));
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
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
