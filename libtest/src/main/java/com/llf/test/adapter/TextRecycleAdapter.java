package com.llf.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by llf on 2015/9/6.
 */
public class TextRecycleAdapter extends RecyclerView.Adapter<TextRecycleAdapter.ViewHolder>{
    private int mCount;
    public TextRecycleAdapter(){
        this(20);
    }

    public TextRecycleAdapter(int size){
        mCount = size;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        TextView tv = new TextView(viewGroup.getContext(),null,android.R.attr.textAppearanceMedium);
        return new ViewHolder(tv);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tv.setText("Item value "+String.valueOf(i));
    }

    @Override public int getItemCount() {
        return mCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }
}
