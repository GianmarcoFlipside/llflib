package me.iwf.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPickerActivity;
import me.iwf.photopicker.R;

/**
 * Created by donglua on 15/6/21.
 */
public class PhotoPagerAdapter extends PagerAdapter {

    private List<String> paths = new ArrayList<>();
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener mClick;
    public PhotoPagerAdapter(Context mContext, List<String> paths) {
        this.mContext = mContext;
        this.paths = paths;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void setOnClickListener(View.OnClickListener l){
        mClick = l;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.item_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.iv_pager);

        final String path = paths.get(position);
        final Uri uri;
        if (path.startsWith("http")) {
            uri = Uri.parse(path);
        } else {
            uri = Uri.fromFile(new File(path));
        }
        Glide.with(mContext).load(uri).thumbnail(0.4f).dontAnimate().dontTransform().placeholder(
                R.drawable.ic_photo_black_48dp).error(R.drawable.ic_broken_image_black_48dp).into(imageView);
        imageView.setOnClickListener(mClick);
        container.addView(itemView);
        return itemView;
    }


    @Override public int getCount() {
        return paths.size();
    }


    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
