package com.wangjingxi.jancee.janceelib.ui.recyclerview;

/**
 *
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public interface MultiItemTypeSupport<T>
{
    int getLayoutId(int itemType);

    int getItemViewType(int position, T t);
}
