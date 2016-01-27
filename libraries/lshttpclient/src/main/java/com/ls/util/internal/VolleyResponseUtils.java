/*
 * The MIT License (MIT)
 *  Copyright (c) 2014 Lemberg Solutions Limited
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.ls.util.internal;

import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;
import java.net.HttpURLConnection;

/**
 * Created by Lemberg-i5 on 07.10.2014.
 */
public class VolleyResponseUtils {

    public static boolean isNetworkingError(VolleyError volleyError)
    {
        if (volleyError.networkResponse == null) {
            if (volleyError instanceof TimeoutError) {
               return true;
            }

            if (volleyError instanceof NoConnectionError) {
                return true;
            }

            if (volleyError instanceof NetworkError) {
                return true;
            }

        }
        return false;
    }

    public static boolean isAuthError(VolleyError volleyError){
        return volleyError != null && volleyError.networkResponse != null
                && volleyError.networkResponse.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED;
    }

    public static RequestQueue defaultRequestQueue(Context context)
    {
        return newRequestQueue(context,null,-1);
    }

    public static RequestQueue newRequestQueue(Context context, HttpStack stack,int maxDiskCacheSizeBytes) {

        final VolleyHelperFactory.IVolleyHelper helper = VolleyHelperFactory.newHelper();
        final File cacheDir = helper.getBestCacheDir(context);

        if (stack == null) {
            stack = helper.createHttpStack(context);
        }

        final Network network = new BasicNetwork(stack);

        final DiskBasedCache diskCache;
        if (maxDiskCacheSizeBytes < 0) {
            diskCache = new DiskBasedCache(cacheDir);
        } else {
            diskCache = new DiskBasedCache(cacheDir, maxDiskCacheSizeBytes);
        }

        final RequestQueue queue = new RequestQueue(diskCache, network,1);
        queue.start();
        return queue;
    }
}
