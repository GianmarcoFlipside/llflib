package com.net;

import com.android.volley.Cache;
import com.android.volley.VolleyLog;
import com.bean.Page;
import com.util.FilePath;
import com.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 908397 on 2015/1/13.
 * 请求数据本地缓存
 *
 * 缓存策略根据key中包含的{@link com.bean.Page#POLICY_KEY}的个数不同而不同
 * 0: 直接读取缓存数据，若无缓存则连网请求
 * 1: 无视缓存数据，直接连网请求
 * 2: 先读取缓存数据，再连网请求（一般界面会有两次数据更新回调）
 * 3: 无缓存
 */
public class CustomCache implements Cache {
    private File mBase;

    public CustomCache() {
        mBase = FilePath.get().getExistSubPath("Cache");
    }

    @Override
    public Entry get(String key) {
        if (key == null)
            return null;
        String spilt = Page.POLICY_KEY;
        String[] arrays = key.split(spilt);
        int cachePolicy = arrays.length -1;
        key = arrays[0];

        File f = new File(mBase, FileUtil.getNumName(key));
        if (!f.exists())
            return null;
        byte[] data = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            data = streamToBytes(fis, fis.available());
        } catch (IOException e) {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e1) {

                }
            return null;
        }
        Cache.Entry ce = new Entry();
        ce.softTtl = System.currentTimeMillis() +(cachePolicy == 2?-10l:0l);
        ce.ttl = System.currentTimeMillis() + (cachePolicy == 1?-10l:9999999l);
        ce.data = data;
        return ce;
    }

    @Override
    public void put(String key, Entry entry) {
        if (key == null)
            return;
        key = key.split(Page.POLICY_KEY)[0];
        File f = new File(mBase, FileUtil.getNumName(key));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(entry.data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            VolleyLog.e("CustomCache save cache %s error,%s", f.getAbsolutePath(), e.getLocalizedMessage());
        }
    }

    @Override
    public void initialize() {
        if (!mBase.exists())
            if (!mBase.mkdirs()) {
                VolleyLog.e("CustomCache can't create dir %s ", mBase.getAbsolutePath());
                return;
            }

    }

    @Override
    public void invalidate(String key, boolean fullExpire) {

    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void clear() {

    }

    /**
     * Reads the contents of an InputStream into a byte[].
     */
    private static byte[] streamToBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        int count;
        int pos = 0;
        while (pos < length && ((count = in.read(bytes, pos, length - pos)) != -1)) {
            pos += count;
        }
        if (pos != length) {
            throw new IOException("Expected " + length + " bytes, read " + pos + " bytes");
        }
        return bytes;
    }
}
