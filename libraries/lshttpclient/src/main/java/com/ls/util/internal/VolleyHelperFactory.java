package com.ls.util.internal;

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

/**
 * Created by Yaroslav Mytkalyk on 18/12/15.
 */
public final class VolleyHelperFactory {

    @NonNull
    public static IVolleyHelper newHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return new VolleyHelperGingerbread();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return new VolleyHelperFroyo();
        } else {
            return new VolleyHelperDonut();
        }
    }

    @SuppressWarnings("deprecation")
    public interface IVolleyHelper {
        File getBestCacheDir(@NonNull Context context);
        HttpStack createHttpStack(@NonNull Context context);
        HttpClient createHttpClient(@NonNull Context context);
    }

    private static class VolleyHelperDonut implements IVolleyHelper {

        protected static final String CACHE_DIR_NAME = "volley";

        @Override
        public File getBestCacheDir(@NonNull final Context context) {
            return new File(context.getCacheDir(), CACHE_DIR_NAME);
        }

        @Override
        public HttpStack createHttpStack(@NonNull final Context context) {
            return new HttpClientStack(createHttpClient(context));
        }

        @SuppressWarnings("deprecation")
        @Override
        public HttpClient createHttpClient(@NonNull final Context context) {
            return new DefaultHttpClient();
        }
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private static class VolleyHelperFroyo extends VolleyHelperDonut {

        @Override
        public File getBestCacheDir(@NonNull final Context context) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return new File(context.getExternalCacheDir(), CACHE_DIR_NAME);
            } else {
                return new File(context.getCacheDir(), CACHE_DIR_NAME);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        public HttpClient createHttpClient(@NonNull final Context context) {
            String userAgent;
            try {
                final String packageName = context.getPackageName();
                final PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
                userAgent = packageName + '/' + info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                userAgent = "volley/0";
            }
            return AndroidHttpClient.newInstance(userAgent);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static class VolleyHelperGingerbread extends VolleyHelperFroyo {

        @Override
        public HttpStack createHttpStack(@NonNull final Context context) {
            return new HurlStack();
        }
    }
}
