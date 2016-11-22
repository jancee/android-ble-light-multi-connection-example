package com.wangjingxi.outsourcing.gvsinglecolor.app;

import android.app.Fragment;
import android.content.Context;

import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch.BleSearchContract;
import com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch.BleSearchFragment_;
import com.wangjingxi.outsourcing.gvsinglecolor.app.bleSearch.BleSearchPresenter;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 *
 * FragmentFactory管理
 * 这里基本只有一个作用，就是获取frag_ble_search的layout的对应fragment
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EBean
public class FragmentFactory {


    @Pref
    MyPrefs_ myPrefs;

    @Bean
    BleSearchPresenter bleSearchPresenter;


    /**
     * 根据资源id返回不同的fragment
     *
     * 但是为什么要用title的id作为id？
     */
    public Fragment createById(Context context, int resId) {
        Fragment fragment = null;
        switch (resId) {

            //蓝牙搜索Fragment
            case R.id.frag_ble_search:
                //创建fragment
                fragment = new BleSearchFragment_();

                //给blesearch的Presenter 设置fragment
                bleSearchPresenter.setView(context, (BleSearchContract.View) fragment);
                break;

        }

        return fragment;
    }


}
