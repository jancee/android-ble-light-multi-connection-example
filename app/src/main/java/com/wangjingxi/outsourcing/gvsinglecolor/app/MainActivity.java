package com.wangjingxi.outsourcing.gvsinglecolor.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.jancee.janceeble.LiteBle;
import com.wangjingxi.jancee.janceelib.utils.MyToast;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode.ColorModeFragment;
import com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode.ColorModeFragment_;
import com.wangjingxi.outsourcing.gvsinglecolor.app.colorMode.ColorModePresenter;
import com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode.MusicModeFragment;
import com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode.MusicModeFragment_;
import com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode.MusicModePresenter;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.BleControl;
import com.wangjingxi.outsourcing.gvsinglecolor.service.BleService;
import com.wangjingxi.outsourcing.gvsinglecolor.service.MusicPlayerService_;
import com.wangjingxi.outsourcing.gvsinglecolor.utils.Utils;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 *
 * 用于灯的控制页面(不包括fragment部分)
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private MyLog myLog = new MyLog("[MainActivity] ");
    private static final String[] FRAGMENT_TAG = {"color", "music"};
    private Fragment mFragmentNow;  //当前显示的fragment
    private FragmentManager fm;
    private boolean isMusicNeedRestart = false;


    //生成变换控制和音乐控制两个frag
    private ColorModeFragment colorModeFragment = new ColorModeFragment_();
    private MusicModeFragment musicModeFragment = new MusicModeFragment_();
    private Intent musicPlayServiceIntent = null;

    private boolean isOpen = true;  //灯开关的当前状态


    @Receiver(actions = LiteBle.ACTION_BLE_DISCONNECT)
    protected void onActionDisconneted() {
        myLog.d("ACTION_BLE_DISCONNECT");
        MyToast.showShort(MainActivity.this, getString(R.string.disconnected));
        if (!BleService.isConnectGroup) {
            stopMusicAfterDisconnected();
        } else {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    if (BleService.connectedNum <= 0) {
                        stopMusicAfterDisconnected();
                    }
                }
            }, 100);
        }
    }

    @Receiver(actions = BleService.ACTION_BLE_RECONNECT)
    protected void onActionBleReconnected() {
        myLog.d("ACTION_BLE_RECONNECT");
        MyToast.showShort(MainActivity.this, getString(R.string.ble_reconnected));
        if (isMusicNeedRestart) {
            if (!BleService.isConnectGroup){
                restartMusiciAfterReconnected();
            } else {
                if (BleService.connectedNum >= BleService.groupToConnectedNum) {
                    restartMusiciAfterReconnected();
                }
            }
        }
    }

    @Pref
    MyPrefs_ myPrefs;

    @Extra
    String mTitle;

    @Bean
    ColorModePresenter colorModePresenter;

    @Bean
    MusicModePresenter musicModePresenter;

    @ViewById
    RelativeLayout rl_color;

    @ViewById
    RelativeLayout rl_music;

    @ViewById
    RelativeLayout rl_back;

    @ViewById
    RelativeLayout rl_switch;

    @ViewById
    ImageView img_color;

    @ViewById
    ImageView img_music;

    @ViewById
    ImageView img_switch;

    @ViewById
    ImageView img_alarm;

    @ViewById
    TextView tv_color;

    @ViewById
    TextView tv_music;

    @ViewById
    TextView tv_title;

    /**
     * 导航栏的color Item
     */
    @Click
    void rl_color() {
        //关闭音乐
        if (MusicManager.isStart)
            Tools.broadUpdate(this, MusicPlayerService_.ACTION_MUSIC_TOGGLE);

        switchContent(mFragmentNow, colorModeFragment);
        setCurrentButton(0);
    }

    /**
     * 导航栏的music Item
     */
    @Click
    void rl_music() {
        switchContent(mFragmentNow, musicModeFragment);
        setCurrentButton(1);
    }

    /**
     * 返回到搜索页面
     */
    @Click
    void rl_back() {
        finish();   //结束当前的activity
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out); //设置动画
    }

    /**
     * 灯开关
     */
    @Click
    void rl_switch() {
        isOpen = !isOpen;
        BleControl.switchBle(isOpen);
        if (isOpen)
            img_switch.setImageResource(R.mipmap.switch_on);
        else
            img_switch.setImageResource(R.mipmap.switch_off);
    }

    /**
     * 点击闹钟
     */
    @Click
    void rl_alarm() {
        Intent intent = new Intent(this, AlertViewActivity.class);
        startActivity(intent);
    }

    @AfterViews
    void init() {
        myLog.d("AfterViews");
        tv_title.setText(mTitle);
        setDefaultFragment();

        //设置Presenter和Fragment的绑定
        colorModePresenter.setView(this, colorModeFragment);
        musicModePresenter.setView(this, musicModeFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myLog.d("onCreate");
//        MusicManager.clear();
        fm = getFragmentManager();

        //如果之前已经创建过，即该activity不是这次创建的，即两个frame目前都已加载
        if (savedInstanceState != null) {
            //将两个frame记录存储
            colorModeFragment = (ColorModeFragment_) fm.findFragmentByTag(FRAGMENT_TAG[0]);
            musicModeFragment = (MusicModeFragment_) fm.findFragmentByTag(FRAGMENT_TAG[1]);
            FragmentTransaction transaction = fm.beginTransaction();

            //隐藏music的frame
            transaction.hide(musicModeFragment).commit();
        }

        //初始化music服务
        musicPlayServiceIntent = new Intent(this, MusicPlayerService_.class);
        startService(musicPlayServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    /**
     * 即退出控制页面
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        myLog.d("onDestroy");

        //停掉music服务
        if (musicPlayServiceIntent != null) {
            stopService(musicPlayServiceIntent);
        }

        //通知命令：蓝牙关闭
        Tools.broadUpdate(this, BleService.ACTION_BLE_CLOSE);

        //标记蓝牙连接状态为未连接
        //BleService.isConnected = false;
    }

    /**
     * 设置初始化时默认显示的frame
     */
    private void setDefaultFragment() {
        FragmentTransaction transaction = fm.beginTransaction();

        if (!colorModeFragment.isAdded()) {    // 先判断是否被add过
            transaction.add(R.id.fragcontent, colorModeFragment, FRAGMENT_TAG[0]).commit();
        } else {
            transaction.show(colorModeFragment).commit();
        }
        mFragmentNow = colorModeFragment;
    }

    /**
     * 导航栏切换fragment
     *
     * @param from
     * @param to
     */
    private void switchContent(Fragment from, Fragment to) {
        //先判断当前的fragment是不是和目的fragment一样（即导航栏点选的蓝色的），如果不是才处理下面的
        if (mFragmentNow != to) {
            mFragmentNow = to;
            FragmentTransaction transaction = fm.beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过，若没有add过，隐藏当前的并开一个新的
                transaction.hide(from).add(R.id.fragcontent, to, getFragmentTag(to)).commit();
            } else {    // 若有add过，隐藏当前的即可
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    /**
     * 获取fragment的tag名称
     * @param fragment
     * @return
     */
    private String getFragmentTag(Fragment fragment) {
        if (fragment instanceof ColorModeFragment_)
            return FRAGMENT_TAG[0];
        if (fragment instanceof MusicModeFragment_)
            return FRAGMENT_TAG[1];
        else
            return "null";
    }

    /**
     * 导航栏图标view设置
     * @param position
     */
    private void setCurrentButton(int position) {
        resetButton();  //图标都设置为灰色
        switch (position) {
            case 0: //选中颜色
                img_color.setImageResource(R.mipmap.tab_color_colorful);
                tv_color.setTextColor(getResources().getColor(R.color.navigation_blue));
                break;
            case 1: //选中音乐
                img_music.setImageResource(R.mipmap.tab_music_colorful);
                tv_music.setTextColor(getResources().getColor(R.color.navigation_blue));
                break;
        }
    }

    /**
     * 复位导航栏图标为灰色
     */
    private void resetButton() {
        img_color.setImageResource(R.mipmap.tab_color_gray);
        img_music.setImageResource(R.mipmap.tab_music_gray);

        tv_color.setTextColor(getResources().getColor(R.color.navigation_gray));
        tv_music.setTextColor(getResources().getColor(R.color.navigation_gray));
    }

    /**
     * 按键按下处理
     *
     * 返回按键处理：用于修复，播放音乐时，返回后有的机型还存在声音的问题
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //如果音乐服务是开着的，就通知关掉
                if (MusicManager.isStart)
                    Tools.broadUpdate(this, Utils.ACTION_STOP_MUSIC);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void stopMusicAfterDisconnected() {
        if (MusicManager.isStart) {
            isMusicNeedRestart = true;
            Tools.broadUpdate(MainActivity.this, MusicPlayerService_.ACTION_MUSIC_TOGGLE);
        }
    }

    private void restartMusiciAfterReconnected() {
        if (!MusicManager.isStart){
            isMusicNeedRestart = false;
            Tools.broadUpdate(MainActivity.this, MusicPlayerService_.ACTION_MUSIC_TOGGLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}


