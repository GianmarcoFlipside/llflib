package com.llflib.cm.ui.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecycleDecoration extends RecyclerView.ItemDecoration{
        private int mSpace;
        public RecycleDecoration(int space){
            mSpace = space;
        }
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
            if(parent.indexOfChild(view) == 0)
                outRect.top = mSpace;
        }
    }