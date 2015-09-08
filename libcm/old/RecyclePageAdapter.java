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

import com.llflib.cm.ui.view.LoadMoreView;

import java.util.List;

/**
 * Created by llf on 2015/7/22.
 */
public abstract class RecyclePageAdapter<T, VH extends RecyclerView.ViewHolder> extends RecycleBaseAdapter<T, VH> {
    public int mPage;
    protected int mTotalPage;
    private int mLoadFlag;

    private LoadMoreView mLoadMore;
    private LoadMoreView.OnLoadingListener mLoadingListener;

    public RecyclePageAdapter(List<T> l, int tp, int cp) {
        super(l);
        mTotalPage = tp;
        setNewPage(cp);
    }

    public void setLoadMoreEnable(boolean enable) {
        mLoadFlag = enable ? 1 : 0;
    }

    public void setLoadingListener(LoadMoreView.OnLoadingListener l) {
        mLoadingListener = l;
        if (mLoadMore != null)
            mLoadMore.setOnLoadingListener(l);
    }

    public void finishLoaded() {
        if (mLoadMore != null)
            mLoadMore.resetState();
    }

    public void addPage(List<T> list, int page) {
        if (list == null || list.isEmpty()) {
            setNewPage(Math.max(mPage, page));
            return;
        }
        if (mPage < page) {
            setNewPage(page);
            if (mList == null)
                mList = list;
            else
                mList.addAll(list);
        } else {
            if (mList == null || mList.isEmpty()) {
                mList = list;
                setNewPage(page);
            } else {
                int ns = mList.size() / Math.max(1, mPage);
                int ps = Math.min(Math.max(1, ns), list.size());
                int startId = ps * (page - 1);
                mList.addAll(startId, list);
                int i = 0;
                startId += list.size();
                while (i++ < ps) {
                    mList.remove(startId);
                }
            }
        }
        notifyDataSetChanged();
    }

    protected void setNewPage(int page) {
        mPage = page;
        if (mPage >= mTotalPage)
            setLoadMoreEnable(false);
        else
            setLoadMoreEnable(true);
    }

    @Override public int getItemViewType(int position) {
        if (mLoadFlag == 0)
            return 0;
        else
            return position != getItemCount() - 1 ? 0 : 1;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return createLoad(parent);
        } else {
            VH vh = onCreateHolder(parent, viewType);
            vh.itemView.setOnClickListener(this);
            return vh;
        }
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        if (getItemViewType(position) != 1)
            onBindHolder(holder, position);
    }

    protected VH createLoad(ViewGroup parent) {
        LoadMoreView flv = new LoadMoreView(parent.getContext());
        flv.setOnLoadingListener(mLoadingListener);
        mLoadMore = flv;
        return (VH) new FooterHolder(flv);
    }

    protected abstract VH onCreateHolder(ViewGroup parent, int viewType);

    protected abstract void onBindHolder(VH holder, int position);

    @Override public int getItemCount() {
        return mList == null ? 0 : mList.size() + mLoadFlag;
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }
}
