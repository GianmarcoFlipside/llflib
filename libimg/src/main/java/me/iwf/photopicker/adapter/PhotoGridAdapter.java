package me.iwf.photopicker.adapter;

import java.util.List;

import me.iwf.photopicker.entity.PhotoDirectory;
/**
 * Created by llf on 2015/10/22.
 *
 * @email llfer2006@gmail.com
 */
public class PhotoGridAdapter extends PhotoAdapter {
    private List<PhotoDirectory> mDirectoryList;

    public PhotoGridAdapter(List<PhotoDirectory> list) {
        super(null);
        refreshDirectory(list);
    }

    public void refreshDirectory(List<PhotoDirectory> list) {
        mDirectoryList = list;
        if (list != null && !list.isEmpty())
            setList(list.get(0).getPhotos());
    }

    public void setCurrentDirectory(int idx) {
        setList(mDirectoryList.get(idx).getPhotos());
    }


}
