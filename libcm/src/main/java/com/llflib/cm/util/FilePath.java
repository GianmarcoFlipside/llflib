package com.llflib.cm.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * @author llfer 2015/3/25
 */
public class FilePath {
    private final static String FILE_NAME_DATA = "cache";
    private final static String FILE_NAME_IMG = "img";
    private final static String FILE_NAME_DOWNLOAD = "download";
    private static FilePath ourInstance = new FilePath();
    public static FilePath get() {
        return ourInstance;
    }

    private File mBase,mInternalBase;
    private FilePath() {
    }

    public void init(Context ctx,String baseName){
        if(baseName == null ||baseName.length() == 0)
            throw new NullPointerException("the arg baseName can't be Null");
        mBase = new File(Environment.getExternalStorageDirectory(),baseName);
        if(mBase.exists())
            mBase.mkdirs();
        mInternalBase = ctx.getFilesDir();
    }

    private void ensureInit(){
        if(mBase == null)
            throw new IllegalStateException("FilePath must be init first !!");
    }


    public File getSubPath(String subName){
        ensureInit();
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) )
            return null;
         if(subName == null||subName.length() == 0)
             return mBase;
        File f = new File(mBase,subName);
        f.mkdirs();
        return f;
    }

    public File getDownload(){
        return getSubPath(FILE_NAME_DOWNLOAD);
    }

    public File getExistSubPath(String subName){
        File file = getSubPath(subName);
        if(file != null){
            return file;
        }
        if(subName == null || subName.length() == 0)
            return mInternalBase;
        File f = new File(mInternalBase,subName);
        f.mkdir();
        return f;
    }

    public File getExistDataPath(){
        return getExistSubPath(FILE_NAME_DATA);
    }

    public File getExistImgPath(){
        return getExistSubPath(FILE_NAME_IMG);
    }

    public File getInternalSubPath(String folder){
        File file = new File(mInternalBase,folder);
        if(!file.exists()) file.mkdirs();
        return file;
    }

}
