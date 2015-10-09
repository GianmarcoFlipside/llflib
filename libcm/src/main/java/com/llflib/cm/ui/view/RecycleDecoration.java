package com.llflib.cm.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleDecoration extends RecyclerView.ItemDecoration {
    static final int[] ATTRS = {android.R.attr.listDivider,android.R.attr.dividerHeight};

    private Drawable mDivider;
    private int mDividerHeight;

    public RecycleDecoration(Context ctx){
        this(ctx,true,-1);
    }

    public RecycleDecoration(Context ctx,int margin){
        this(ctx,false,margin);
    }

    public RecycleDecoration(Context ctx, boolean hasDivider, int itemMargins) {
        if (hasDivider) {
            TypedArray a = ctx.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            mDividerHeight = a.getDimensionPixelOffset(1,0);
            a.recycle();
        }
        mDividerHeight = Math.max(itemMargins,mDividerHeight);
    }

    private boolean isVertical(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager == null)
            return false;
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL;
        }
        return false;
    }

    @Override public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mDivider == null)
            return;
        int childCount = parent.getChildCount();
        if (childCount == 0)
            return;
        if (isVertical(parent))
            drawVertical(c, parent, childCount);
        else
            drawHorizontal(c, parent, childCount);
    }

    private void drawVertical(Canvas c, RecyclerView parent, int childCount) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = lp.bottomMargin + child.getBottom();
            int bottom = top + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent, int childCount) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getPaddingBottom();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = lp.rightMargin + child.getRight();
            int right = left + mDividerHeight;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mDividerHeight == 0)
            return;
        if(isVertical(parent)){
            outRect.left = mDividerHeight;
            outRect.right = mDividerHeight;
            if(mDivider == null){
                outRect.bottom = mDividerHeight;
                if(parent.indexOfChild(view) == 0)
                    outRect.top = mDividerHeight;
            }else{
                outRect.bottom = mDividerHeight;
                outRect.top = 0;
            }
        }else{
            outRect.top = mDividerHeight;
            outRect.bottom = mDividerHeight;
            if(mDivider == null){
                outRect.right = mDividerHeight;
                if(parent.indexOfChild(view) == 0)
                    outRect.left = mDividerHeight;
            }else{
                outRect.right = mDividerHeight;
                outRect.left = 0;
            }
        }
    }
}