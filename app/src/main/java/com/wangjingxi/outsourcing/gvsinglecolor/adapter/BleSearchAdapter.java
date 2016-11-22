package com.wangjingxi.outsourcing.gvsinglecolor.adapter;

import android.content.Context;

import com.wangjingxi.outsourcing.gvsinglecolor.R;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.BleScanItem;
import com.wangjingxi.outsourcing.gvsinglecolor.entity.sqltab.BleAddedTab;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.CommonAdapter;
import com.wangjingxi.jancee.janceelib.ui.recyclerview.ViewHolder;

import java.util.List;

/**
 *
 * 灯Adapter
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public class BleSearchAdapter extends CommonAdapter<BleScanItem> {

    public BleSearchAdapter(Context context, int layoutId, List<BleScanItem> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, BleScanItem bleScanItem, int position) {
        BleAddedTab bleAddedTab = BleAddedTab.getByAddr(bleScanItem.getBleAddr());
        holder.setText(R.id.tv_search_addr, bleScanItem.getBleAddr());
        if (bleAddedTab == null) {
            holder.setText(R.id.tv_search_name, bleScanItem.getBleName());
            holder.setVisible(R.id.img_search_item, false);
//            holder.setImageResource(R.id.img_search_item, R.mipmap.ble_led_unadded);
            holder.setVisible(R.id.tv_add_device, true);
        } else {
            bleScanItem.setType(BleScanItem.TYPE_ADDED);
            bleScanItem.setBleName(bleScanItem.getBleName());
            holder.setText(R.id.tv_search_name, bleAddedTab.bleName);
            holder.setVisible(R.id.img_search_item, true);
            holder.setImageResource(R.id.img_search_item, R.mipmap.ble_led);
            holder.setVisible(R.id.tv_add_device, false);
        }
    }

}
