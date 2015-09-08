package com.llf.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.llf.libtest.R;

/**
 * Created by llf on 2015/9/6.
 */
public class ImageRecycleAdapter extends RecyclerView.Adapter<ImageRecycleAdapter.ViewHolder> {

    private int mCount;
    public ImageRecycleAdapter(){
        this(20);
    }

    public ImageRecycleAdapter(int size){
        mCount = size;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycle_image,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    }

    @Override public int getItemCount() {
        return mCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv;
        TextView title,summary;
        public ViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            summary = (TextView) itemView.findViewById(R.id.summary);
        }
    }
}
