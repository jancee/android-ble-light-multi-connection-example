package com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode;

import android.media.audiofx.Visualizer;
import android.os.Handler;

import com.wangjingxi.jancee.janceeble.Utils.HexUtil;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.core.ble.BleControl;
import com.wangjingxi.outsourcing.gvsinglecolor.service.MusicPlayerService;
import com.wangjingxi.jancee.janceelib.base.MvpPresenter;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import org.androidannotations.annotations.EBean;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EBean
public class MusicModePresenter extends MvpPresenter<MusicModeContract.View> implements MusicModeContract.Presenter {
    private MyLog myLog = new MyLog("[MusicModePresenter] ");

    private Visualizer mVisualizer;
    private boolean isLinked = false;
    private boolean isStart = false;

    /**
     * 连接音乐服务逻辑
     */
    @Override
    public void linkMediaPlayer() {
        if (!isLinked)
            linkPlayer();
        else {
            setLinkEnable();
        }
    }

    /**
     * 连接音乐服务
     */
    private void linkPlayer() {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                if (MusicPlayerService.getMediaPlayer() == null)
                    return;

                mVisualizer = new Visualizer(MusicPlayerService.getMediaPlayer().getAudioSessionId());
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

                // Pass through Visualizer data to VisualizerView
                Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                      int samplingRate) {
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                                 int samplingRate) {
                        myLog.d("onFftDataCapture: " + HexUtil.encodeHexStr(bytes));
                        BleControl.setMusicLight(bytes[20]);
                    }
                };

                mVisualizer.setDataCaptureListener(captureListener,
                        Visualizer.getMaxCaptureRate() / 2, true, true);

                // Enabled Visualizer and disable when we're done with the stream
                mVisualizer.setEnabled(true);
                isLinked = true;
            }
        }, 200);
    }

    /**
     * 释放连接
     */
    @Override
    public void releaseLink() {
        if (mVisualizer != null) {
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    /**
     * 使能连接
     */
    private void setLinkEnable() {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                if (MusicManager.isStart){
                    myLog.d("setLinkEnable(true)");
                    mVisualizer.setEnabled(true);
                }
                else{
                    myLog.d("setLinkEnable(false)");
                    mVisualizer.setEnabled(false);
                }
            }
        }, 50);
    }

}
