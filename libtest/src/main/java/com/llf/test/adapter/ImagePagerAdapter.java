package com.llf.test.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.llf.libtest.R;

/**
 * Created by llf on 2015/9/6.
 */
public class ImagePagerAdapter extends PagerAdapter {
    private int mCount;

    public ImagePagerAdapter() {
        this(6);
    }

    public ImagePagerAdapter(int size) {
        mCount = size;
    }

    @Override public int getCount() {
        return mCount;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        ImageView iv = (ImageView) LayoutInflater.from(container.getContext()).inflate(R.layout.item_pager_image,
                container, false);
        container.addView(iv);
        return iv;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
