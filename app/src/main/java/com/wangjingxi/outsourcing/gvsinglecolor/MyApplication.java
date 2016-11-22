package com.wangjingxi.outsourcing.gvsinglecolor;

import android.content.Context;

import com.pgyersdk.crash.PgyCrashManager;
import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

/**
 *
 * Application
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MyApplication extends com.activeandroid.app.Application {
    private static Context context;
    private MyLog myLog = new MyLog("[MyApplication] ");

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        myLog.d("MyApplication onCreate");

        //友盟初始化
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        //蒲公英
        PgyCrashManager.register(this);

//        Recovery.getInstance()
//                .debug(false)
//                .recoverInBackground(false)
//                .recoverStack(true)
//                .mainPage(FlashActivity_.class)
//                .silent(true, Recovery.SilentMode.RESTART)
//                .init(this);


    }

    public static Context getContext() {
        return context;
    }

}
