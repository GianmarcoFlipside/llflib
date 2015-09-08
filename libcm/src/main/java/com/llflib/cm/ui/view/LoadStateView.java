package com.llflib.cm.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.util.ArrayList;

/**
 * @author llfer 2015/3/23
 */
public class LoadStateView extends RelativeLayout{
    final static int LAYOUT_INX_EMPTY = 0;
    final static int LAYOUT_INX_LOAD = 1;
    final static int LAYOUT_INX_ERROR = 2;

    private int[] mLayoutIds;
    private ArrayList<View>[] mLayoutViews;

    private int mCurShowIdx;

    public LoadStateView(Context context) {
        this(context, null);
    }

    public LoadStateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadStateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWithAttr(context,attrs,defStyleAttr);
    }

    private void initWithAttr(Context ctx,AttributeSet attrs, int defStyleAttr){

        mCurShowIdx = -1;
        mLayoutViews = new ArrayList[3];
        mLayoutIds = new int[3];
//        mLayoutIds[LAYOUT_INX_EMPTY] = R.layout.;
//        mLayoutIds[LAYOUT_INX_ERROR] = R.layout.sv_layout_error;
//        mLayoutIds[LAYOUT_INX_LOAD] = R.layout.sv_layout_load;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadStateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void hide(){
        if(mCurShowIdx >= 0){
            setViewsState(mLayoutViews[mCurShowIdx],GONE);
            mCurShowIdx = -1;
        }
    }
    /**
     * 设置加载提示的布局界面
     * 必须在加载界面初始化之前调用才有效
     *
     * @param rid :layout id
     * */
    public void setLoadLayout(int rid){
        mLayoutIds[LAYOUT_INX_LOAD] = rid;
    }
    /**
     * 设置无内容提示的布局界面
     * 必须在加载界面初始化之前调用才有效
     *
     * @param rid :layout id
     * */
    public void setEmptyLayout(int rid){
        mLayoutIds[LAYOUT_INX_EMPTY] = rid;
    }
    /**
     * 设置错误提示的布局界面
     * 必须在加载界面初始化之前调用才有效
     *
     * @param rid :layout id
     * */
    public void setErrorLayout(int rid){
        mLayoutIds[LAYOUT_INX_ERROR] = rid;
    }
    /**
     * 显示加载界面
     * */
    public void showLoading(){
        hide();
        mCurShowIdx = LAYOUT_INX_LOAD;
        if(mLayoutViews[LAYOUT_INX_LOAD] == null)
            initLayout();
        setViewsState(mLayoutViews[LAYOUT_INX_LOAD],VISIBLE);
    }

    public void showEmpty(){
        hide();
        mCurShowIdx = LAYOUT_INX_EMPTY;
        if(mLayoutViews[LAYOUT_INX_EMPTY] == null)
            initLayout();
        setViewsState(mLayoutViews[LAYOUT_INX_EMPTY],VISIBLE);
    }

    public void showError(){
        hide();
        mCurShowIdx = LAYOUT_INX_ERROR;
        if(mLayoutViews[LAYOUT_INX_ERROR] == null)
            initLayout();
        setViewsState(mLayoutViews[LAYOUT_INX_ERROR],VISIBLE);
    }

    private void setViewsState(ArrayList<View> list,int state){
        if(list == null ||list.isEmpty())
            return;
        int len = list.size();
        for(int i =0;i<len;i++){
            list.get(i).setVisibility(state);
        }
    }

    private void initLayout(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup vg = (ViewGroup) inflater.inflate(mLayoutIds[mCurShowIdx],this,true);
        int childCount = vg.getChildCount();
        if(mLayoutViews[mCurShowIdx] == null)
            mLayoutViews[mCurShowIdx] = new ArrayList<View>(childCount);
        View v;
        for(int i = 0;i <childCount;i++){
            v = vg.getChildAt(i);
            if(v.getVisibility() != VISIBLE)
                continue;
            mLayoutViews[mCurShowIdx].add(vg.getChildAt(i));
        }
    }

}
