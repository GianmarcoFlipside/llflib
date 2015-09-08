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

import java.util.List;

/**
 * Created by llf on 2015/7/22.
 */
public abstract class RecyclePageAdapter<T, VH extends RecyclerView.ViewHolder> extends RecycleBaseAdapter<T, VH> {
    public int mPage;
    protected int mTotalPage;
    /**
     * @param l  数据集合
     * @param tp 总页数
     * @param cp 当前页
     */
    public RecyclePageAdapter(List<T> l, int tp, int cp) {
        super(l);
        mTotalPage = tp;
        setNewPage(cp);
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
    }
}
