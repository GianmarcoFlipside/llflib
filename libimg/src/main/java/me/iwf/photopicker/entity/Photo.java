package me.iwf.photopicker.entity;

import me.iwf.photopicker.adapter.PhotoGridAdapter;

/**
 * Created by llf on 2015/10/22.
 *
 * @email llfer2006@gmail.com
 */
public class Photo {
    private int id;
    private String path;
    public boolean checked;
    public int type;

    public Photo() {
        this(0,null,PhotoGridAdapter.ITEM_TYPE_PHOTO);
    }

    public Photo(int id, String path) {
        this(id,path, PhotoGridAdapter.ITEM_TYPE_PHOTO);
    }
    public Photo(int id,String path,int type) {
        this.id = id;
        this.path = path;
        this.type = type;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    @Override public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
