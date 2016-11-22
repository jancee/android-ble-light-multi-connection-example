package com.wangjingxi.jancee.janceelib.ui.recyclerview;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public interface OnItemClickListener<T>
{
    void onItemClick(ViewHolder viewHolder, T t, int position);
}