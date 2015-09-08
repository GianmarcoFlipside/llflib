/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;

import java.io.File;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "com/volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        Network network = createDefaultNet(context,stack);
        RequestQueue queue;
        if (maxDiskCacheBytes <= -1)
        {
        	// No maximum size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        }
        else
        {
        	// Disk cache size specified
        	queue = new RequestQueue(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }

        queue.start();

        return queue;
    }

    private static Network  createDefaultNet(Context ctx,HttpStack stack){
        String userAgent = "com/volley/0";
        try {
            String packageName = ctx.getPackageName();
            PackageInfo info = ctx.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }
        return new BasicNetwork(stack);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, HttpStack stack)
    {
    	return newRequestQueue(context, stack, -1);
    }
    
    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null,-1);
    }

    public static RequestQueue newRequestQueue(Context context,Cache cache) {
        RequestQueue queue = new RequestQueue(cache,createDefaultNet(context,null));
        queue.start();
        return queue;
    }

}

