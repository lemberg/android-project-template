package com.ls.util;

import com.ls.http.base.BaseRequestBuilder;
import com.ls.http.base.client.LSClient;
import com.ls.util.image.LSImageView;
import com.ls.util.internal.ContentResolverRequestQueue;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.XmlRes;

/**
 * Created by Yaroslav Mytkalyk on 01/02/16.
 */
public final class UriFactory {

    /**
     * Makes an Android asset Uri for passing it to {@link LSImageView#setImageWithURL(String)} or using it as {@link BaseRequestBuilder#setRequestURL(String)} for {@link LSClient} requests
     */
    public static Uri makeAssetUri(@NonNull final String path) {
        final String[] pathSegments = path.split("/");
        final Uri.Builder builder = new Uri.Builder()
                .scheme(ContentResolverRequestQueue.SCHEME_ASSETS);
        // Note we can't just pass "path" since the assets names are not URI-encoded while "/" separator is
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < pathSegments.length; i++) {
            builder.appendPath(pathSegments[i]);
        }
        return builder.build();
    }

    /**
     * Makes an Android drawable Uri for passing it to {@link LSImageView#setImageWithURL(String)} or using it as {@link BaseRequestBuilder#setRequestURL(String)} for {@link LSClient} requests
     */
    public static Uri makeDrawableResourceUri(@NonNull final Resources res,
            @DrawableRes final int resId) {
        return makeResourceUri(res, resId);
    }

    /**
     * Makes an Android xml Uri for passing it to {@link LSImageView#setImageWithURL(String)} or using it as {@link BaseRequestBuilder#setRequestURL(String)} for {@link LSClient} requests
     */
    public static Uri makeXmlResourceUri(@NonNull final Resources res,
            @XmlRes final int resId) {
        return makeResourceUri(res, resId);
    }

    /**
     * Makes an Android raw Uri for passing it to {@link LSImageView#setImageWithURL(String)} or using it as {@link BaseRequestBuilder#setRequestURL(String)} for {@link LSClient} requests
     */
    public static Uri makeRawResourceUri(@NonNull final Resources res,
            @RawRes final int resId) {
        return makeResourceUri(res, resId);
    }

    /**
     * Makes an Android resource Uri for passing it to {@link LSImageView#setImageWithURL(String)} or using it as {@link BaseRequestBuilder#setRequestURL(String)} for {@link LSClient} requests
     */
    private static Uri makeResourceUri(@NonNull final Resources res, final int resId) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(res.getResourcePackageName(resId))
                .appendPath(res.getResourceTypeName(resId))
                .appendPath(res.getResourceEntryName(resId))
                .build();
    }

}
