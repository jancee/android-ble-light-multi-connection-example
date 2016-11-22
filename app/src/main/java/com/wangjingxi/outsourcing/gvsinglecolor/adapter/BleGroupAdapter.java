package com.wangjingxi.outsourcing.gvsinglecolor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleGroupItem;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.CommonAdapter;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.ViewHolder;

import java.util.List;

/**
 *
 * 分组Adapter
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleGroupAdapter extends CommonAdapter<BleGroupItem> {

    public BleGroupAdapter(Context context, int layoutId, List<BleGroupItem> datas) {
        super(context, layoutId, datas);
    }

    public GroupConnectClick groupConnectClick;


    public interface GroupConnectClick {
        void onGroupClick(BleGroupItem bleGroupItem, int position);
    }

    public void setGroupConnectClick(GroupConnectClick groupConnectClick) {
        this.groupConnectClick = groupConnectClick;
    }

    @Override
    public void convert(ViewHolder holder, final BleGroupItem bleGroupItem, final int position) {
        holder.setVisible(R.id.tv_add_device, false);
        holder.setText(R.id.tv_search_name, bleGroupItem.getName());
        holder.setText(R.id.tv_search_addr, bleGroupItem.getDetail());

        if (bleGroupItem.getType() == BleGroupItem.TYPE_GROUP) {
            holder.setImageResource(R.id.img_search_item, R.mipmap.ble_group);
            holder.getView(R.id.view_bottom).setBackgroundColor(Color.parseColor("#0178FF"));
            holder.getView(R.id.rl_all).setBackground(null);
            holder.getView(R.id.img_search_item).setClickable(true);
            holder.getView(R.id.img_search_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupConnectClick != null)
                        groupConnectClick.onGroupClick(bleGroupItem, position);
                }
            });
        } else {
            holder.getView(R.id.view_bottom).setBackgroundColor(Color.parseColor("#BEBEBF"));
            if (bleGroupItem.getType() == BleGroupItem.TYPE_LED)
                holder.setImageResource(R.id.img_search_item, R.mipmap.ble_led_unadded);
            else
                holder.setImageResource(R.id.img_search_item, R.mipmap.ble_led);
        }
    }

    /**
     * 更新状态:如果在组中找到指定地址的灯，则将该灯设置类型为已扫描到
     * @param addr
     */
    public void updateStatue(String addr) {
        for (BleGroupItem mData : mDatas) {
            if (mData.getDetail().equals(addr)) {
                mData.setType(BleGroupItem.TYPE_LED_SCANED);
                notifyDataSetChanged();
            }
        }
    }

}
