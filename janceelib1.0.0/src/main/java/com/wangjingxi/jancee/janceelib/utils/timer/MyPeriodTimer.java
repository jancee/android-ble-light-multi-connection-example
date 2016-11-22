package com.wangjingxi.jancee.janceelib.utils.timer;

import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MyPeriodTimer {
    private MyLog myLog = new MyLog("[MyTimer] ");
    private Timer timer = null;
    private TimerTask task = null;
    private MyPeriodTimerListener myTimerListener;
    private long period;

    public MyPeriodTimer(long period, MyPeriodTimerListener l) {
        this.period = period;
        this.myTimerListener = l;
    }

    public void startTimer() {
        myLog.d("startTimer");
        if (timer == null) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
//                    myLog.w("enter");
                    myTimerListener.enterTimer();
                }
            };
            timer.schedule(task, 0, period);
        }
    }

    public void stopTimer() {
        myLog.d("stopTimer");
        if (timer != null) {
            task.cancel();
            timer.cancel();

            task = null;
            timer = null;
        }
    }

}
