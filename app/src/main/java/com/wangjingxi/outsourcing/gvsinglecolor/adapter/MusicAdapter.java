package com.wangjingxi.outsourcing.gvsinglecolor.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;

import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.MusicItem;
import com.wangjingxi.outsourcing.gvsinglecolor.utils.MediaUtil;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.CommonAdapter;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.ViewHolder;
import com.wangjingxi.jancee.janceelib.utils.MyLog;

import java.util.List;

/**
 *
 * Music列表 Adapter
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class MusicAdapter extends CommonAdapter<MusicItem> {
    private MyLog myLog = new MyLog("[MusicAdapter] ");
    List<MusicItem> datas;
    public onFavorClickListener mOnFavorClickListener;

    public interface onFavorClickListener {
        void onFavor(MusicItem musicItem, boolean isAdd);
    }

    public void setOnFavorClickListener(onFavorClickListener mOnFavorClickListener) {
        this.mOnFavorClickListener = mOnFavorClickListener;
    }

    public MusicAdapter(Context context, int layoutId, List<MusicItem> datas) {
        super(context, layoutId, datas);
        this.datas = datas;
    }

    @Override
    public void convert(final ViewHolder holder, final MusicItem musicItem, final int position) {
        final CheckBox checkBox = holder.getView(R.id.ck_music);

        holder.setText(R.id.tv_music_name, musicItem.getMusicName());
        holder.setText(R.id.tv_artist_name, musicItem.getArtist());
        holder.setText(R.id.tv_music_time, MediaUtil.formatTime(musicItem.getMusicDuration()));

        if (musicItem.isFavor())
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        //获取item的视图
        holder.getView(R.id.rl_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicItem.setIsFavor(!musicItem.isFavor());
                if (musicItem.isFavor()) {
                    checkBox.setChecked(true);
                    if (mOnFavorClickListener != null)
                        mOnFavorClickListener.onFavor(musicItem, true);
                } else {
                    checkBox.setChecked(false);
                    if (mOnFavorClickListener != null)
                        mOnFavorClickListener.onFavor(musicItem, false);
                }
            }
        });
    }

}
