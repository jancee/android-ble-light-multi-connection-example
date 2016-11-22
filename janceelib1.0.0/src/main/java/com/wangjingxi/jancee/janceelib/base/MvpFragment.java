package com.wangjingxi.jancee.janceelib.base;

import android.app.Fragment;

/**
 *
 * 用于Fragment实现
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MvpFragment <P extends BasePresenter> extends Fragment implements BaseView<P>{
    public P mPresenter;

    @Override
    public void setPresenter(BasePresenter presenter) {
        if (presenter != null)
            mPresenter = (P) presenter;
    }
}
