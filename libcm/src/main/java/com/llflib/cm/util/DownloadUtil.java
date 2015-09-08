/*
 * Copyright 2015 llfer2006@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.llflib.cm.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by llf on 2015/7/27.
 */
public class DownloadUtil {
    public static boolean loadAttache(Context ctx,String url,String cookie) {
        //开始下载
        Uri resource = Uri.parse(encode(url));
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        if(!TextUtils.isEmpty(cookie)){
            request.addRequestHeader("Cookie",cookie);
        }
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        //在通知栏中显示
        request.setShowRunningNotification(true);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹
        request.setDestinationInExternalPublicDir("/download/", "G3.mp4");
        request.setTitle("移动G3广告");
        DownloadManager downloadManager = (DownloadManager) ctx.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request) > 0l;
    }

    static String encode(String url) {
        //转换中文编码
        String split[] = url.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "utf-8");
            } catch (UnsupportedEncodingException e) {
                split[i] = URLEncoder.encode(split[i]);
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");//处理空格
        return split[0];
    }
}
