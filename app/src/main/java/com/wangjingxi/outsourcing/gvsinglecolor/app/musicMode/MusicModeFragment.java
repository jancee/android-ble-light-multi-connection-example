package com.wangjingxi.outsourcing.gvsinglecolor.app.musicMode;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.app.fragment.DialogMusicFragment;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyMusic_;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MyPrefs_;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.MusicFavorTab;
import com.wangjingxi.outsourcing.gvsinglecolor.service.MusicPlayerService;
import com.wangjingxi.outsourcing.gvsinglecolor.utils.Utils;
import com.wangjingxi.jancee.janceelib.base.MvpFragment;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.List;

/**
 *
 * 音乐模式
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
@EFragment(R.layout.fragment_music)
public class MusicModeFragment extends MvpFragment<MusicModeContract.Presenter> implements MusicModeContract.View {

    private MyLog myLog = new MyLog("[MusicModeFragment] ");
    private DialogMusicFragment dialogMusicFragment;
    private boolean isEnableControl = false;
    private boolean isMusicReSelected = false;

    @Receiver(actions = MusicPlayerService.ACTION_REFRESH_MUSIC)
    public void onActionMusicRefresh() {
        refreshMusicDetail();
        initMusicDetail();
    }

    @Receiver(actions = DialogMusicFragment.ACTION_SELECT_MUSIC)
    public void onActionMusicSelected() {
        myLog.d("onActionMusicSelected");
        isMusicReSelected = true;
        refreshMusicDetail();
        setControlStatue();
        initMusicDetail();
    }

    @Receiver(actions = Utils.ACTION_STOP_MUSIC)
    public void onActionStopMusic() {
        rl_music_toggle();
    }

    @Pref
    MyPrefs_ myPrefs;

    @Pref
    MyMusic_ myMusic;

    @ViewById
    RelativeLayout rl_music_toggle;

    @ViewById
    ImageView img_music_toggle;

    @ViewById
    ImageView img_next;

    @ViewById
    ImageView img_previous;

    @ViewById
    Button btn_music;

    @ViewById
    Button btn_music_rock;

    @ViewById
    Button btn_music_default;

    @ViewById
    Button btn_music_jazz;

    @ViewById
    Button btn_music_classical;

    @Click
    void rl_music_toggle() {
        if (isEnableControl) {
            Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_MUSIC_TOGGLE);
            mPresenter.linkMediaPlayer();
        }
    }

    @Click
    void img_next() {
        if (isEnableControl)
            Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_MUSIC_NEXT);
    }

    @Click
    void img_previous() {
        if (isEnableControl)
            Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_MUSIC_PREVIOUS);
    }

    @Click
    void btn_music() {
        showGroupDialog();
    }

    @Click
    void btn_music_rock() {
        setMusicModeSelected(0);
    }

    @Click
    void btn_music_default() {
        setMusicModeSelected(1);
    }

    @Click
    void btn_music_jazz() {
        setMusicModeSelected(2);
    }

    @Click
    void btn_music_classical() {
        setMusicModeSelected(3);
    }


    @AfterViews
    void init() {
        MusicManager.isStart = false;
        MusicManager.getMusicListById(getActivity(), 0);
        MusicManager.getMusicListById(getActivity(), 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageEnd("MusicModeFragment");
        myLog.d("onResume");
        refreshMusicDetail();
        setControlStatue();
        initMusicDetail();
        setMusicModeSelected(myMusic.musicMode().get());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.releaseLink();
    }

    private void initMusicDetail() {
        if (isEnableControl) {
            if (!isMusicReSelected) {
                if (!myMusic.lastMusicName().get().equals("null")) {
                    btn_music.setText(myMusic.lastMusicName().get());
                    MusicManager.currentIndex = myMusic.lastMusicIndex().get();
                } else {
                    btn_music.setText(MusicManager.getMusicListById(getActivity(), 1).get(0).getMusicName());
                }
            } else {
                isMusicReSelected = false;
                btn_music.setText(MusicManager.getMusicListById(getActivity(), 1).get(0).getMusicName());
            }
        }
    }

    /**
     * 更新音乐暂停还是播放View
     */
    private void refreshMusicDetail() {
        if (MusicManager.isStart) {
            img_music_toggle.setImageResource(R.mipmap.music_pause_colorful);
        } else {
            img_music_toggle.setImageResource(R.mipmap.music_play_colorful);
        }
    }

    /**
     * 设置音乐状态
     */
    private void setControlStatue() {
        MusicManager.getMusicListById(getActivity(), 0);                    //获取音乐列表的第一首音乐
        List<MusicFavorTab> musicFavorTab = MusicFavorTab.getByMenuId(1);   //获取选择的第一首音乐

        if (musicFavorTab.size() == 0) {    //没有选择音乐，则控制按键都是灰色，并且显示"请选择音乐"
            img_music_toggle.setImageResource(R.mipmap.music_play_gray);
            img_next.setImageResource(R.mipmap.music_next_gray);
            img_previous.setImageResource(R.mipmap.music_before_gray);
            btn_music.setText(R.string.music_select);
            isEnableControl = false;   //标记为不允许控制
        } else {    //有音乐
            isEnableControl = true;     //标记为允许控制
            img_next.setImageResource(R.mipmap.music_next_colorful);
            img_previous.setImageResource(R.mipmap.music_before_colorful);
        }
    }

    /**
     * 显示音乐选择弹窗
     */
    public void showGroupDialog() {
        dialogMusicFragment = new DialogMusicFragment();
        dialogMusicFragment.show(getFragmentManager(), "dialogMusicFragment");
    }

    /**
     * 音效选择
     * @param id
     */
    private void setMusicModeSelected(int id) {
        //复位四个按键
        resetMusicModeButton();
        switch (id) {
            case 0:
                btn_music_rock.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_selected_button));
                btn_music_rock.setTextColor(getResources().getColor(R.color.white));
                myMusic.musicMode().put(0);
                Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_SELECT_MUSIC_MODE, MusicPlayerService.CONTROL_ID, MusicPlayerService.MUSIC_ROCK);
                break;
            case 1:
                btn_music_default.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_selected_button));
                btn_music_default.setTextColor(getResources().getColor(R.color.white));
                myMusic.musicMode().put(1);
                Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_SELECT_MUSIC_MODE, MusicPlayerService.CONTROL_ID, MusicPlayerService.MUSIC_DEFAULT);
                break;
            case 2:
                btn_music_jazz.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_selected_button));
                btn_music_jazz.setTextColor(getResources().getColor(R.color.white));
                myMusic.musicMode().put(2);
                Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_SELECT_MUSIC_MODE, MusicPlayerService.CONTROL_ID, MusicPlayerService.MUSIC_JAZZ);
                break;
            case 3:
                btn_music_classical.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_selected_button));
                btn_music_classical.setTextColor(getResources().getColor(R.color.white));
                myMusic.musicMode().put(3);
                Tools.broadUpdate(getActivity(), MusicPlayerService.ACTION_SELECT_MUSIC_MODE, MusicPlayerService.CONTROL_ID, MusicPlayerService.MUSIC_CLASSICAL);
                break;
            default:
                break;
        }
    }

    /**
     * 复位  四个音效 按键
     */
    private void resetMusicModeButton() {
        btn_music_rock.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_unselected_button));
        btn_music_default.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_unselected_button));
        btn_music_jazz.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_unselected_button));
        btn_music_classical.setBackground(getResources().getDrawable(R.drawable.shape_music_mode_unselected_button));

        btn_music_rock.setTextColor(getResources().getColor(R.color.button_blue));
        btn_music_default.setTextColor(getResources().getColor(R.color.button_blue));
        btn_music_jazz.setTextColor(getResources().getColor(R.color.button_blue));
        btn_music_classical.setTextColor(getResources().getColor(R.color.button_blue));
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("MusicModeFragment");
    }

}
