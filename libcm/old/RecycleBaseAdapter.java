/*
 * Copyright 2015 llfer2006@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.llflib.cm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by llf on 2015/7/21.
 */
public abstract class RecycleBaseAdapter<T, VH extends ViewHolder> extends RecyclerView.Adapter<VH> implements
        OnClickListener {
    public interface OnItemClickListener {
        void onItemClick(View v, Object obj);
    }

    protected List<T> mList;
    private OnItemClickListener mItemClickListener;

    public RecycleBaseAdapter(List<T> l) {
        mList = l;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mItemClickListener = l;
    }

    public void addItem(T item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override public void onClick(View v) {
        if (mItemClickListener != null)
            mItemClickListener.onItemClick(v, v.getTag());
    }

    @Override public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
