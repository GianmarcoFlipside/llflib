package com.llflib.cm.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import timber.log.Timber;

/**
 * Created by llf on 2015/9/18.
 */
public class AddressHelper extends SQLiteOpenHelper{
    static final String DB_NAME = "China.db";
    static final int DB_VERSION = 1;
    static final String TB_PROVINCE = "T_Province";
    static final String TB_CITY = "T_City";
    static final String TB_ZONE = "T_Zone";
    public static void initAddress(Context ctx) {
        final File addressFile = ctx.getDatabasePath(DB_NAME);
        if (addressFile.exists()){
            Timber.e("AddressHelper found the address databases");
            return;
        }
        addressFile.getParentFile().mkdirs();
        final Context c = ctx.getApplicationContext();
        new Thread(){
            @Override public void run() {
                InputStream fis;
                try {
                    fis = c.getAssets().open("address/china");
                } catch (IOException e) {
                    Timber.e("AddressHelper can't open the copy src databse file from path asset/address/china");
                    return;
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(addressFile);
                } catch (FileNotFoundException e) {
                    //XXX ignore?
                    e.printStackTrace();
                }
                int idx;
                byte[] buffer =new byte[4096];
                try {
                    while((idx = fis.read(buffer)) > 0){
                        fos.write(buffer,0,idx);
                    }
                } catch (IOException e) {
                    Timber.e("Address copy data File IO exception ");
                    addressFile.delete();
                    return;
                }finally {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                    }
                    try {
                        fis.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }.start();
    }

    public AddressHelper(Context ctx){
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        //nothing
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.i("AddressHelper oldVersion:"+oldVersion+",newVersion:"+newVersion);
    }

    public List<Bean> getProvinces(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TB_PROVINCE,null,null,null,null,null,null);
        ArrayList<Bean> list = new ArrayList<>(c.getCount());
        while(c.moveToNext()){
            list.add(new Bean(c.getString(1),c.getString(0)));
        }
        c.close();
        return list;
    }

    public List<Bean> getCitysByProvince(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TB_CITY,null,"ProID=?",new String[]{id},null,null,null);
        ArrayList<Bean> list = new ArrayList<>(c.getCount());
        while(c.moveToNext()){
            list.add(new Bean(c.getString(2),c.getString(0)));
        }
        c.close();
        return list;
    }

    public List<Bean> getZonesByCity(String id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TB_ZONE,null,"CityID=?",new String[]{id},null,null,null);
        ArrayList<Bean> list = new ArrayList<>(c.getCount());
        while(c.moveToNext()){
            list.add(new Bean(c.getString(2),c.getString(1)));
        }
        c.close();
        return list;
    }

    public static class Bean{
        public String id;
        public String name;
        public Bean(String id,String name){
            this.id = id;
            this.name = name;
        }
    }
}
