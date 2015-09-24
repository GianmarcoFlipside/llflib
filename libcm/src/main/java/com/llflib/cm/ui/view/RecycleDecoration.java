package com.llflib.cm.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleDecoration extends RecyclerView.ItemDecoration {
    static final int[] ATTRS = {android.R.attr.listDivider};

    private int mItemMargins;
    private Drawable mDivider;

    public RecycleDecoration(Context ctx){
        this(ctx,true,0);
    }

    public RecycleDecoration(Context ctx,int margin){
        this(ctx,false,margin);
    }

    public RecycleDecoration(Context ctx, boolean hasDivider, int itemMargins) {
        if (hasDivider) {
            TypedArray a = ctx.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
        }
        mItemMargins = itemMargins;
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
            int bottom = top + mDivider.getIntrinsicHeight();
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
            int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(isVertical(parent)){
            outRect.left = mItemMargins;
            outRect.right = mItemMargins;
            if(mDivider == null){
                outRect.bottom = mItemMargins;
                if(parent.indexOfChild(view) == 0)
                    outRect.top = mItemMargins;
            }else{
                outRect.bottom = mDivider.getIntrinsicHeight();
                outRect.top = 0;
            }
        }else{
            outRect.top = mItemMargins;
            outRect.bottom = mItemMargins;
            if(mDivider == null){
                outRect.right = mItemMargins;
                if(parent.indexOfChild(view) == 0)
                    outRect.left = mItemMargins;
            }else{
                outRect.right = mDivider.getIntrinsicWidth();
                outRect.left = 0;
            }
        }
    }
}