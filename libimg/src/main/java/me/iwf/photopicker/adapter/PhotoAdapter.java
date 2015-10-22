package me.iwf.photopicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.R;
import me.iwf.photopicker.entity.Photo;
import me.iwf.photopicker.event.OnItemCheckListener;
import me.iwf.photopicker.event.OnPhotoClickListener;
import timber.log.Timber;

/**
 * Created by llf on 2015/10/22.
 *
 * @email llfer2006@gmail.com
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    public final static int ITEM_TYPE_CAMERA = 100;
    public final static int ITEM_TYPE_PHOTO = 101;

    private OnItemCheckListener onItemCheckListener = null;
    private OnPhotoClickListener onPhotoClickListener = null;
    private View.OnClickListener onCameraClickListener = null;

    private boolean hasCamera = false;
    private List<Photo> mList;
    private int mSelectedCount = 0;

    public PhotoAdapter(List<Photo> photos) {
        this(photos, false);
    }

    public PhotoAdapter(List<Photo> photos, boolean hasCamera) {
        setList(photos);
        setHasCamera(hasCamera);
    }

    protected void setList(List<Photo> photos) {
        if (mList == null)
            mList = new ArrayList<>();
        mList.clear();
        if (photos != null) {
            mList.addAll(photos);
        }
        if (hasCamera)
            addCamera();
    }

    public void setHasCamera(boolean enable) {
        if (hasCamera != enable) {
            hasCamera = enable;
            if (hasCamera)
                addCamera();
            else
                removeCamera();
        }
    }

    private void removeCamera() {
        if (mList == null || mList.isEmpty())
            return;
        Photo photo = mList.get(0);
        if (photo.type == ITEM_TYPE_CAMERA)
            mList.remove(0);
    }

    private void addCamera() {
        if (mList != null && !mList.isEmpty()) {
            Photo photo = mList.get(0);
            if (photo.type == ITEM_TYPE_CAMERA)
                return;
        }
        if (mList == null)
            mList = new ArrayList<>();
        mList.add(0, new Photo(0, null, ITEM_TYPE_CAMERA));
    }

    @Override public int getItemViewType(int position) {
        return mList.get(position).type;
    }

    @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_photo, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(itemView);
        if (viewType == ITEM_TYPE_CAMERA) {
            holder.vSelected.setVisibility(View.GONE);
            holder.ivPhoto.setImageResource(R.drawable.camera);
            holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    if (onCameraClickListener != null) {
                        onCameraClickListener.onClick(view);
                    }
                }
            });
        }
        return holder;
    }

    @Override public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        if (getItemViewType(position) == ITEM_TYPE_CAMERA)
            return;
        final Photo photo = mList.get(position);
        final Context ctx = holder.itemView.getContext();
        Glide.with(ctx).load(new File(photo.getPath())).centerCrop().thumbnail(0.1f).placeholder(
                R.drawable.ic_photo_black_48dp).error(R.drawable.ic_broken_image_black_48dp).into(holder.ivPhoto);
        final boolean checked = photo.checked;
        holder.vSelected.setChecked(checked);
        holder.ivPhoto.setSelected(checked);
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (onPhotoClickListener != null) {
                    onPhotoClickListener.onClick(view, position, hasCamera);
                }
            }
        });
        holder.vSelected.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                boolean enable = false;
                if (onItemCheckListener != null) {
                    enable = onItemCheckListener.onItemCheck(position, photo, !photo.checked, mSelectedCount);
                }
                notifyItemChanged(position);
                if(!enable) return;
                photo.checked = !photo.checked;
                if (photo.checked)
                    mSelectedCount++;
                else
                    mSelectedCount--;
            }
        });

    }


    @Override public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public List<Photo> getPhotos() {
        return mList;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        this.onItemCheckListener = onItemCheckListener;
    }

    public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
        this.onPhotoClickListener = onPhotoClickListener;
    }

    public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
        this.onCameraClickListener = onCameraClickListener;
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPhoto;
        private CheckBox vSelected;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
            vSelected = (CheckBox) itemView.findViewById(R.id.v_selected);
        }
    }
}
