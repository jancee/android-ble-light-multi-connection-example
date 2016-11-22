package com.wangjingxi.outsourcing.gvsinglecolor.app.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.umeng.analytics.MobclickAgent;
import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.adapter.MusicAdapter;
import com.wangjingxi.outsourcing.gvsinglecolor.core.MusicManager;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.MusicItem;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.DividerItemDecoration;
import com.wangjingxi.jancee.janceelib.utils.MyLog;
import com.wangjingxi.jancee.janceelib.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 音乐选择会话框
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class DialogMusicFragment extends DialogFragment implements View.OnClickListener, MusicAdapter.onFavorClickListener {
    private MyLog myLog = new MyLog("[DialogMusicFragment] ");

    public static final String ACTION_SELECT_MUSIC = "com.BleService.ACTION_SELECT_MUSIC";
    private RecyclerView mRecyclerView;
    private MusicAdapter musicAdapter;
    private Button btn_ok;
    private Button btn_cancel;
    List<MusicItem> musicItemList = new ArrayList<>();
    protected RecyclerView.LayoutManager mLayoutManager;
    List<MusicItem> musicSelectedList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉默认有的一个标题
        View view = inflater.inflate(R.layout.fragement_dialog_music, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicSelectedList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        myLog.d("onStart");
        intiView();
    }

    void intiView() {
        mRecyclerView   = (RecyclerView) getView().findViewById(R.id.mRecyclerView);
        musicItemList   = MusicManager.getDefaultMusicList(getActivity());
        btn_cancel      = (Button) getView().findViewById(R.id.btn_cancel);
        btn_ok          = (Button) getView().findViewById(R.id.btn_ok);
        btn_cancel.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        initRecyclerView();
        setUpRecyclerView(musicItemList);
    }

    /**
     * 初始化Recycler
     */
    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    /**
     * 设置recycler，监听，以及其adapter
     * @param musicItems
     */
    private void setUpRecyclerView(List<MusicItem> musicItems) {
        musicAdapter = new MusicAdapter(getActivity(), R.layout.list_music, musicItems);
        musicAdapter.setOnFavorClickListener(this);
        mRecyclerView.setAdapter(musicAdapter);
    }

    /**
     * 按钮点击
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                Tools.broadUpdate(getActivity(), ACTION_SELECT_MUSIC);
                musicSelectedList.clear();
                this.dismiss();
                break;
            case R.id.btn_ok:
                Tools.broadUpdate(getActivity(), ACTION_SELECT_MUSIC);
                saveSelectedMusic();
                this.dismiss();
                break;
        }
    }

    /**
     * favor选择
     *
     * @param musicItem
     * @param isAdd
     */
    @Override
    public void onFavor(MusicItem musicItem, boolean isAdd) {
        if (!musicSelectedList.contains(musicItem))
            musicSelectedList.add(musicItem);
    }

    /**
     * 保存选的音乐
     */
    private void saveSelectedMusic() {
        for (MusicItem musicItem : musicSelectedList) {
            if (musicItem.isFavor())
                MusicManager.addMusicToMenu(musicItem, 1);
            else
                MusicManager.deleteMusicFromMenu(musicItem, 1);
        }
        MusicManager.getDefaultMusicList(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("DialogMusicFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageEnd("DialogMusicFragment");
    }
}
