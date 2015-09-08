package com.llflib.cm.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by llf on 2015/4/22.
 */
public abstract class BaseAdapter extends android.widget.BaseAdapter{

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = createItemView(position,parent);
        }
        bindItemView(position,convertView);
        return convertView;
    }

    protected abstract View createItemView(int pos,ViewGroup parent);

    protected abstract void bindItemView(int pos,View convertView);
}
