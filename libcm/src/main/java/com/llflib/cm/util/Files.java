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
import java.util.Objects;

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
            ILog.w(TAG,"Object To File faild, msg "+e.getMessage());
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
            ILog.w(TAG,"Object read from file faild,msg "+e.getMessage());
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
            ILog.w(TAG, "stringToFile faild," + e.getMessage());
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
            ILog.w(TAG,"stringFromFile notFound File error ",e);
            return null;
        }
        try {
            return isToStr(fis,encoding);
        } catch (IOException e) {
            ILog.w(TAG,"stringFromFile stream to String error ",e);
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

}
