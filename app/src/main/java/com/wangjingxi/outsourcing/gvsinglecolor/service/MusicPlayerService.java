package com.wangjingxi.outsourcing.gvsinglecolor.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyMusic_;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.sharedpreferences.Pref;

/**
 *
 * MusicPlayerService音乐服务
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EService
public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {
    private MyLog myLog = new MyLog("[MusicPlayerService] ");

    public static final int MUSIC_ROCK = 30;
    public static final int MUSIC_DEFAULT = 31;
    public static final int MUSIC_JAZZ = 32;
    public static final int MUSIC_CLASSICAL = 33;


    public static final String ACTION_SELECT_MUSIC = "com.wangjingxi.ACTION_SELECT_MUSIC";
    public static final String ACTION_REFRESH_MUSIC = "com.wangjingxi.ACTION_REFRESH_MUSIC";
    public static final String ACTION_SAVE_MUSIC_STATUE = "com.wangjingxi.ACTION_SAVE_MUSIC_STATUE";
    public static final String ACTION_SELECT_MUSIC_MODE = "com.wangjingxi.ACTION_SELECT_MUSIC_MODE";

    public static final String ACTION_MUSIC_SEEK = "com.wangjingxi.ACTION_MUSIC_SEEK";
    public static final String ACTION_MUSIC_PREVIOUS = "com.wangjingxi.ACTION_MUSIC_PREVIOUS";
    public static final String ACTION_MUSIC_NEXT = "com.wangjingxi.ACTION_MUSIC_NEXT";
    public static final String ACTION_MUSIC_TOGGLE = "com.wangjingxi.ACTION_MUSIC_TOGGLE";
    public static final String CONTROL_ID = "controlId";

    private static final int NOTIFICATION_ID = 101;
    private static MediaPlayer mediaPlayer; // 媒体播放器对象
    private boolean isPause = false;
    private boolean isPauseByPhone = false;
    private RemoteViews mContentViewSmall;
    private Equalizer mEqualizer;
    private int[] frequenceAvaliable;
    private Short[] rockMode = new Short[]{0, 6, 4, 0, -2, -6, 1, 4, 6, 7, 9};
    private Short[] defaultMode;
    private Short[] jazzMode = new Short[]{0, 6, 4, -5, 2, 3, 4, 4, 5, 5, 6};
    private Short[] classicMode = new Short[]{0, 4, 0, 1, 2, 3, 4, 5, 3, 3};

    @Pref
    MyMusic_ myMusic;   //用于保存最后一首

    /**
     * 广播监听：保存音乐状态
     */
    @Receiver(actions = ACTION_SAVE_MUSIC_STATUE)
    void saveMusicStatues() {
        myLog.d("ACTION_SAVE_MUSIC_STATUE");
        saveMusicDetail();
    }

    /**
     * 广播监听：切换模式
     *
     * @param controlId 音效模式
     */
    @Receiver(actions = ACTION_SELECT_MUSIC_MODE)
    void selectMusicMode(@Receiver.Extra int controlId) {
        myLog.d("ACTION_SELECT_MUSIC_MODE: " + controlId);
        setMusicMode(controlId);
    }

    /**
     * 广播监听：选择音乐
     * @param musicPath 音乐路径
     */
    @Receiver(actions = ACTION_SELECT_MUSIC)
    void selectMusic(@Receiver.Extra String musicPath) {
        myLog.d("ACTION_SELECT_MUSIC, musicPath: " + musicPath);
        saveMusicDetail();
        play();
        refreshMusicUi();
    }

    /**
     * 广播监听：搜索音乐
     *
     * @param controlId
     */
    @Receiver(actions = ACTION_MUSIC_SEEK)
    void seekMusic(@Receiver.Extra int controlId) {
        myLog.d("ACTION_MUSIC_SEEK");
        if (MusicManager.getMusicItem().getMusicDuration() <= controlId) {
            nextMusic();
        } else {
            mediaPlayer.seekTo(controlId);
        }
    }

    /**
     * 广播监听：音乐上一首
     */
    @Receiver(actions = ACTION_MUSIC_PREVIOUS)
    void previousMusic() {
        myLog.d("ACTION_MUSIC_PREVIOUS");
        MusicManager.getPreMusic();
        saveMusicDetail();
        previous();
        refreshMusicUi();
    }

    /**
     * 广播监听：音乐下一首
     */
    @Receiver(actions = ACTION_MUSIC_NEXT)
    void nextMusic() {
        myLog.d("ACTION_MUSIC_NEXT");
        MusicManager.getNextMusic();
        saveMusicDetail();
        next();
        refreshMusicUi();
    }

    /**
     * 广播监听：音乐切换
     */
    @Receiver(actions = ACTION_MUSIC_TOGGLE)
    void toggleMusic() {
        myLog.d("ACTION_MUSIC_TOGGLE");
        saveMusicDetail();
        if (MusicManager.isStart)
            pause();
        else
            resume();
        refreshMusicUi();
    }

    /**
     * 广播监听：来电状态改变（系统）
     */
    @Receiver(actions = TelephonyManager.ACTION_PHONE_STATE_CHANGED)
    void phoneStateChanged() {
        myLog.d("ACTION_PHONE_STATE_CHANGED");
        TelephonyManager telephony = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                myLog.d("等待接电话");
                if (!isPause) {
                    pause();
                    isPauseByPhone = true;
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                myLog.d("电话挂断");
                if (isPauseByPhone) {
                    resume();
                    isPauseByPhone = false;
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                myLog.d("通话中");
                break;
        }
    }

    private void pause() {
        myLog.d("pause");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
            MusicManager.isStart = false;
        }
    }

    private void resume() {
        myLog.d("resume");
        if (isPause) {
            isPause = false;
            mediaPlayer.start();
            MusicManager.isStart = true;
        } else
            play();
    }

    private void next() {
        myLog.d("next");
        play();
    }

    private void previous() {
        myLog.d("previous");
        play();
    }

    private void play() {
        isPause = false;
        MusicManager.isStart = true;
        prepare();
        mediaPlayer.start();
    }

    private void prepare() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            setupEqualizer();
            mediaPlayer.setOnCompletionListener(this);
        }
        try {
            mediaPlayer.reset();// 把各项参数恢复到初始状态
            mediaPlayer.setDataSource(MusicManager.getMusicPath());
            mediaPlayer.prepare(); // 进行缓冲
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void releaseEqualizer() {
        if (mEqualizer != null) {
            mEqualizer.release();
            mEqualizer = null;
        }
    }

    /**
     * 保存最后一首音乐的详情
     */
    private void saveMusicDetail() {
        myLog.d("MusicManager.getCurrentIndex()" + MusicManager.getCurrentIndex());
        myMusic.lastMusicMenuId().put(MusicManager.getCurrentMenuId());
        myMusic.lastMusicIndex().put(MusicManager.getCurrentIndex());
        myMusic.lastMusicName().put(MusicManager.getMusicItem().getMusicName());
        myMusic.lastArtistName().put(MusicManager.getMusicItem().getArtist());
        myMusic.lastPath().put(MusicManager.getMusicItem().getMusicPath());
    }

    private void refreshMusicUi() {
        Tools.broadUpdate(this, ACTION_REFRESH_MUSIC);
        showNotification();
    }

    /**
     * 设置音效模式
     *
     * @param id
     */
    private void setMusicMode(int id) {
        if (mEqualizer == null)
            return;
        myLog.d("setMusicMode id: " + id);
        switch (id) {
            case MUSIC_ROCK:
                for (short i = 0; i < frequenceAvaliable.length; i++) {
                    if (rockMode.length - 1 >= i)
                        mEqualizer.setBandLevel(i, (short) (rockMode[i]));
                }
                break;
            case MUSIC_DEFAULT:
                for (short i = 0; i < frequenceAvaliable.length; i++) {
                    mEqualizer.setBandLevel(i, (short) 0);
//                    mEqualizer.setBandLevel(i, defaultMode[i]);
                }
                break;
            case MUSIC_JAZZ:
                for (short i = 0; i < frequenceAvaliable.length; i++) {
                    if (jazzMode.length - 1 >= i)
                        mEqualizer.setBandLevel(i, (short) (jazzMode[i]));
                }
                break;
            case MUSIC_CLASSICAL:
                for (short i = 0; i < frequenceAvaliable.length; i++) {
                    if (classicMode.length - 1 >= i)
                        mEqualizer.setBandLevel(i, (short) (classicMode[i]));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化均衡控制器
     */
    private void setupEqualizer() {
        // 以MediaPlayer的AudioSessionId创建Equalizer
        // 相当于设置Equalizer负责控制该MediaPlayer
        mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        // 启用均衡控制效果
        mEqualizer.setEnabled(true);
        // 获取均衡控制器支持最小值和最大值
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];//第一个下标为最低的限度范围
        short maxEQLevel = mEqualizer.getBandLevelRange()[1];  // 第二个下标为最高的限度范围
        // 获取均衡控制器支持的所有频率
        short brands = mEqualizer.getNumberOfBands();
        frequenceAvaliable = new int[brands];
        defaultMode = new Short[brands];
        for (short i = 0; i < brands; i++) {
            myLog.d((mEqualizer.getCenterFreq(i) / 1000) + " Hz" +
                    ",min: " + (minEQLevel / 100) + " dB" +
                    ",max: " + (maxEQLevel / 100) + " dB" +
                    ",default:" +  mEqualizer.getBandLevel(i));
            frequenceAvaliable[i] = mEqualizer.getCenterFreq(i) / 1000;
//            defaultMode[i] = mEqualizer.getBandLevel(i);
        }

        setMusicMode(myMusic.musicMode().get());
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setWhen(System.currentTimeMillis())  // the time stamp
//                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
//                .setCustomBigContentView(getSmallContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build();
        // Send the notification.
        startForeground(NOTIFICATION_ID, notification);
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
//        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last);
//        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next);
//
//        remoteView.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_MUSIC_PREVIOUS));
//        remoteView.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_MUSIC_NEXT));
//        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_MUSIC_TOGGLE));
    }

    /**
     * 更新View 歌名、艺术家
     * @param remoteView
     */
    private void updateRemoteViews(RemoteViews remoteView) {
        myLog.d("updateRemoteViews");
        if (!myMusic.lastMusicName().get().equals("null")) { //如果不是没有歌
            //更新歌名、艺术家
            remoteView.setTextViewText(R.id.text_view_name, myMusic.lastMusicName().get());
            remoteView.setTextViewText(R.id.text_view_artist, myMusic.lastArtistName().get());
        } else {//如果没有歌
            //如果获取不到歌列表则不处理
            if (MusicManager.getMusicListById(this, 0).size() == 0)
                return;
            //更新歌名、艺术家
            remoteView.setTextViewText(R.id.text_view_name, MusicManager.getMusicListById(this, 0).get(0).getMusicName());
            remoteView.setTextViewText(R.id.text_view_artist, MusicManager.getMusicListById(this, 0).get(0).getArtist());
        }
    }

    private PendingIntent getPendingIntent(String action) {
        return PendingIntent.getBroadcast(this, 0, new Intent(action), 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myLog.d("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        myLog.d("onStartCommand");

        showNotification();
        Tools.acquireWakeLock(this);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();//释放player
        releaseEqualizer();  //释放均衡器
        Tools.releaseWakeLock();    //释放设备电源锁
        myLog.d("onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        myLog.d("onCompletion");

        MusicManager.getNextMusic();
        saveMusicDetail();
        next();
        refreshMusicUi();
    }

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

}
