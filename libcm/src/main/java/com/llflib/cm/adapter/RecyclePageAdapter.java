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

import com.llflib.cm.ui.pullview.PullRecycleView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by llf on 2015/7/22.
 */
public abstract class RecyclePageAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public int mPage;
    protected int mTotalPage;
    protected List<T> mList;
    private WeakReference<PullRecycleView> mPullViewReference;

    /**
     * @param l  数据集合
     * @param tp 总页数
     * @param cp 当前页
     */
    public RecyclePageAdapter(List<T> l, int tp, int cp) {
        mList = l;
        mTotalPage = tp;
        setNewPage(cp);
    }

    @Override public int getItemCount() {
        return mList == null ? 0:mList.size();
    }

    public void addItem(T item){
        if(mList == null){
            mList = new ArrayList<>();
        }
        mList.add(item);
        notifyItemInserted(mList.size()-1);
    }

    public void remove(T item){
        int idx = mList.indexOf(item);
        if(idx >= 0){
            mList.remove(idx);
            notifyItemRemoved(idx);
        }
    }

    public void addPage(List<T> list, int page) {
        if (list == null || list.isEmpty()) {
            setNewPage(Math.max(mPage, page));
            return;
        }
        if (mPage < page) {
            setNewPage(page);
            if (mList == null) {
                mList = list;
                notifyDataSetChanged();
            }else {
                mList.addAll(list);
                notifyItemRangeChanged(mList.size() - list.size(),list.size());
            }
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
            notifyDataSetChanged();
        }
    }

    protected void setNewPage(int page) {
        mPage = page;
        PullRecycleView view = getPullView();
        if (view != null && mTotalPage >= 0 && page >= mTotalPage) {
            view.setPullMode(PullRecycleView.MODE_PULL_DOWN);
        }
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getParent() instanceof PullRecycleView) {
            mPullViewReference = new WeakReference<PullRecycleView>((PullRecycleView) recyclerView.getParent());
        }
    }

    @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mPullViewReference.clear();
        mPullViewReference = null;
    }

    protected PullRecycleView getPullView() {
        if (mPullViewReference == null)
            return null;
        return mPullViewReference.get();
    }
}
