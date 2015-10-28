package com.llflib.cm.util;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

import timber.log.Timber;

/**
 * @author llfer 2015/3/25
 */
public class Files {

    static final String TAG = "Files";

    private Files(){
        throw new UnsupportedOperationException("Files unsupport init");
    }

    public static String getNumName(String str){
        if(str == null || str.length() == 0)
            return str;
        return Integer.toHexString(str.hashCode());
    }

    public static boolean objectToFile(Object o,File file){
        try {
            ObjectOutputStream  oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(o);
            oos.flush();
            oos.close();
            return true;
        } catch (IOException e) {
            Timber.w(TAG, "Object To File faild, msg " + e.getMessage());
        }
        return false;
    }

    public static Object objectFromFile(File file){
        if(file ==null || !file.exists())
            return null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object o = ois.readObject();
            ois.close();
            return o;
        } catch (Exception e) {
            Timber.w(TAG,"Object read from file faild,msg "+e.getMessage());
        }
        return null;
    }

    public static boolean stringToFile(File file,String str,String encoding){
        if(file == null|| TextUtils.isEmpty(str))
            return false;
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs())
            return false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes;
            try {
                bytes = str.getBytes(encoding);
            } catch (UnsupportedEncodingException e) {
                bytes = str.getBytes();
            }
            fos.write(bytes);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            Timber.w(TAG, "stringToFile faild," + e.getMessage());
        }
        return false;
    }

    public static String stringFromFile(File file, String encoding){
        if(file == null || !file.exists())
            return null;
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Timber.w(TAG,"stringFromFile notFound File error ",e);
            return null;
        }
        try {
            return isToStr(fis,encoding);
        } catch (IOException e) {
            Timber.w(TAG,"stringFromFile stream to String error ",e);
            return null;
        }
    }

    public static String isToStr(InputStream is,String encoding) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[]buffer = new byte[1024*4];
        int idx;
        while((idx = is.read(buffer)) >0){
            bos.write(buffer,0,idx);
        }
        is.close();
        bos.flush();
        buffer = bos.toByteArray();
        bos.close();

        return new String(buffer,encoding);
    }

    public static String getFileName(String path){
        int idx = path.lastIndexOf(File.separatorChar);
        if(idx < 0 || idx == path.length()-1)
            idx = Math.max(0,path.length() - 8);
        return path.substring(idx+1);
    }

    public static boolean deleteFile(File f){
        if(f == null || !f.exists())
            return false;
        if(f.isFile())
            return f.delete();
        File[] listFiles = f.listFiles();
        if(listFiles != null && listFiles.length > 0){
            for(File file:listFiles){
                deleteFile(file);
            }
        }
        return f.delete();
    }

    public static String getFormatSize(long size){
        Timber.i("Format size "+size);
        if(size == 0)
            return "";
        final int limit = 512;
        if(size < limit){
            return String.format("%dB",size);
        }
        if(size < limit*1024){
            return String.format("%.2fKB",size/1024.0);
        }
        if(size < limit*1024*1024){
            return String.format("%.2fMB",size/(1024*1024.0));
        }
        return String.format("%.2fGB", size / (1024 * 1024 * 1024.0));
    }

}
