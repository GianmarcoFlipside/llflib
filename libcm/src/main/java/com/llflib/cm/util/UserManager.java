//package com.juntuo163.finance.user;
//
//import com.llflib.cm.util.FilePath;
//
//import java.io.File;
//
//public class UserManager{
//    final static String UPATH = "um";
//    final static String UNAME = "un";
//    private static UserManager INSTANCE;
//    public static synchronized UserManager get(){
//        if(INSTANCE == null){
//            INSTANCE = new UserManager();
//        }
//        return INSTANCE;
//    }
//
//    T mUser;
//    UserManager(){
//        initFromFile();
//    }
//
//    void initFromFile(){
//        mUser = (T) Files.objectFromFile(getSaveFile());
//    }
//
//    public T getUser(){
//        return mUser;
//    }
//
//    public boolean isUserLogin(){
//        return mUser != null;
//    }
//
//    public void exit(){
//        mUser = null;
//        Files.deleteFile(getSaveFile());
//    }
//
//    File getSaveFile(){
//        File parent = FilePath.get().getInternalSubPath(UPATH);
//        return new File(parent,UNAME);
//    }
//
//}
