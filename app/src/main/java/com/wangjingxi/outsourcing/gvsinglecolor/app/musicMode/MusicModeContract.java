package com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode;


import com.wangjingxi.jancee.janceelib.base.BasePresenter;
import com.wangjingxi.jancee.janceelib.base.BaseView;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MusicModeContract {
    public interface View extends BaseView<MusicModeContract.Presenter> {
    }

    public interface Presenter extends BasePresenter {
        void linkMediaPlayer();
        void releaseLink();
    }
}
