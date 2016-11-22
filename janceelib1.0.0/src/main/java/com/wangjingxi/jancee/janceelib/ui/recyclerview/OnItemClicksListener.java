package com.wangjingxi.jancee.janceelib.ui.recyclerview;

import android.view.View;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public interface OnItemClicksListener<T> {
    void onItemClick(ViewHolder viewHolder, View view, T t, int position);
    boolean onItemLongClick(ViewHolder viewHolder, View view, T t, int position);
}
