package com.llf.test.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by llf on 2015/9/6.
 */
public class TextPagerAdapter extends PagerAdapter {
    private int mCount;

    public TextPagerAdapter() {
        this(20);
    }

    public TextPagerAdapter(int size) {
        mCount = size;
    }

    @Override public int getCount() {
        return mCount;
    }

    @Override public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        TextView tv = new TextView(container.getContext(),null,android.R.attr.textAppearanceMedium);
        tv.setText("Item value "+String.valueOf(position));
        container.addView(container);
        return tv;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
