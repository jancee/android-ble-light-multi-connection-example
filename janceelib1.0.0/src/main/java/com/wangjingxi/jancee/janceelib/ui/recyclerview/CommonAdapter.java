package com.wangjingxi.jancee.janceelib.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 *
 * 通用的RecyclerView Adapter
 *
 * 王静茜 Jancee.Wang
 * 2016-10-23
 */
public abstract class CommonAdapter<T> extends RecyclerView.Adapter<ViewHolder>
{
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemClicksListener<T> mOnItemClicksListener;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener)
    {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemClicksListener(OnItemClicksListener<T> onItemClicksListener)
    {
        this.mOnItemClicksListener = onItemClicksListener;
    }

    public void add(T item) {
        mDatas.add(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public CommonAdapter(Context context, int layoutId, List<T> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        ViewHolder viewHolder = ViewHolder.get(mContext, null, parent, mLayoutId, -1);
        setListener(parent, viewHolder, viewType);
        return viewHolder;
    }

    protected int getPosition(RecyclerView.ViewHolder viewHolder)
    {
        return viewHolder.getPosition();
    }

    protected boolean isEnabled(int viewType)
    {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType)
    {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnItemClickListener != null)
                {
                    int position = getPosition(viewHolder);
                    mOnItemClickListener.onItemClick(viewHolder, mDatas.get(position), position);
                }
                if (mOnItemClicksListener != null)
                {
                    int position = getPosition(viewHolder);
                    mOnItemClicksListener.onItemClick(viewHolder, v, mDatas.get(position), position);
                }
            }
        });
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                if (mOnItemClicksListener != null)
                {
                    int position = getPosition(viewHolder);
                    return mOnItemClicksListener.onItemLongClick(viewHolder, v, mDatas.get(position), position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.updatePosition(position);
        convert(holder, mDatas.get(position), position);
    }

    public abstract void convert(ViewHolder holder, T t, int position);

    @Override
    public int getItemCount()
    {
        if (mDatas == null)
            return 0;
        return mDatas.size();
    }

}
