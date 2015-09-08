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

package com.llflib.cm.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.llflib.cm.R;

/**
 * Created by llf on 2015/7/21.
 * 列表加载更多项
 */
public class LoadMoreView extends RelativeLayout implements View.OnClickListener{
    final static int STATE_READY = 0x0;
    final static int STATE_LOADING = 0x1;
    final static int STATE_FAILDED = 0x2;

    public interface OnLoadingListener{
        void onLoading();
    }

    private int mState;
    private int mPullCount;
    private boolean isLoadAuto;
    private OnLoadingListener mLoadingListener;
    private TextView mHitTv;
    private ProgressBar mBar;
    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        isLoadAuto = true;
        setClickable(true);

        TextView tv = new TextView(context,null,android.R.attr.textAppearanceSmall);
        tv.setText(R.string.llf_state_loading);
        tv.setId(android.R.id.text2);
        LayoutParams lp = new LayoutParams(-2,-2);
        lp.addRule(CENTER_IN_PARENT);
        addView(tv,lp);
        mHitTv = tv;

        ProgressBar progress = new ProgressBar(context,null,android.R.attr.progressBarStyle);
        progress.setIndeterminate(true);
        lp = new LayoutParams(-2,-2);
        lp.addRule(LEFT_OF,android.R.id.text2);
        addView(progress,lp);
        ViewGroup.LayoutParams pp = new ViewGroup.LayoutParams(-1,-2);
        setLayoutParams(pp);
        mBar = progress;

        resetState();
    }

    public void setOnLoadingListener(OnLoadingListener l){
        mLoadingListener = l;
    }

    public void resetState(){
        mState = STATE_READY;
        mHitTv.setText(isLoadAuto?R.string.llf_hit_pull :R.string.llf_hit_more);
    }

    public void setFailMode(){
        mState = STATE_FAILDED;
        mBar.setVisibility(View.GONE);
        mHitTv.setText(R.string.llf_hit_failed);
    }

    public void setAuotLoading(boolean enable){
        isLoadAuto = enable;
        if(mState == STATE_READY)
            resetState();
    }

    @Override public void offsetTopAndBottom(int offset) {
        super.offsetTopAndBottom(offset);
        if(offset != 0) mPullCount = 0;
        else mPullCount++;
        if(isLoadAuto && mState == STATE_READY && mPullCount > 3) {
            changeState(STATE_LOADING);
        }
    }

    void notifyLoadingListener(){
        if(mLoadingListener != null)
            mLoadingListener.onLoading();
    }

    void changeState(int newState){
        if(mState != newState){
            mState = newState;
            if(newState == STATE_LOADING){
                mBar.setVisibility(View.VISIBLE);
                mHitTv.setText(R.string.llf_state_loading);
                notifyLoadingListener();
            }
        }
    }

    @Override public void onClick(View v) {
        if(mState != STATE_LOADING){
            changeState(STATE_LOADING);
        }
    }
}
