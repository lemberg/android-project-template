package com.ls.util.internal;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ResponseDelivery;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NoCache;
import com.ls.util.L;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by Yaroslav Mytkalyk on 29/01/16.
 */
public class ContentResolverRequestQueue extends RequestQueue {

    public static final String SCHEME_ASSETS = "assets";

    public ContentResolverRequestQueue(@NonNull final Context context) {
        super(new NoCache(), new ContentResolverNetwork(context.getApplicationContext()));
    }

    public ContentResolverRequestQueue(@NonNull Cache cache, @NonNull ContentResolverNetwork network) {
        super(cache, network);
    }

    public ContentResolverRequestQueue(@NonNull Cache cache, @NonNull ContentResolverNetwork network, int threadPoolSize) {
        super(cache, network, threadPoolSize);
    }

    public ContentResolverRequestQueue(@NonNull Cache cache, @NonNull ContentResolverNetwork network, int threadPoolSize, ResponseDelivery delivery) {
        super(cache, network, threadPoolSize, delivery);
    }

    public static final class ContentResolverNetwork implements Network {

        @NonNull
        private final ContentResolver mContentResolver;

        @NonNull
        private final AssetManager mAssetManager;

        public ContentResolverNetwork(@NonNull final Context context) {
            mContentResolver = context.getContentResolver();
            mAssetManager = context.getAssets();
        }

        @Override
        public NetworkResponse performRequest(final Request<?> request) throws VolleyError {
            final Uri uri = Uri.parse(request.getUrl());
            final String scheme = uri.getScheme();
            if (!scheme.equals(ContentResolver.SCHEME_CONTENT)
                    && !scheme.equals(ContentResolver.SCHEME_FILE)
                    && !scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    && !scheme.equals(SCHEME_ASSETS)) {
                throw new IllegalArgumentException(
                        "Request scheme must be 'content', 'file', or 'android.resource'");
            }

            InputStream is = null;
            ByteArrayOutputStream os = null;

            try {
                if (isAndroidAssetUri(uri)) {
                    is = mAssetManager.open(androidAssetUriToPath(uri));
                } else {
                    is = mContentResolver.openInputStream(uri);
                    if (is == null) {
                        throw new IOException(
                                "Failed to open InputStream from ContentResolver, uri = " + uri);
                    }
                }

                final int size = resolveStreamSize(mContentResolver, uri, is);
                final byte[] data;

                if (size != -1) {
                    data = new byte[size];
                    final int read = is.read(data);
                    if (read != size) {
                        throw new IOException(String.format(Locale.US, "Failed to read data fully. Expected '%d' bytes, read '%d'", size, read));
                    }
                } else {
                    final byte[] buff = new byte[4096];
                    final int available = is.available();
                    os = new ByteArrayOutputStream(available > 0 ? available : buff.length);

                    int read;
                    while ((read = is.read(buff, 0, buff.length)) != -1) {
                        os.write(buff, 0, read);
                    }

                    data = os.toByteArray();
                }
                return new NetworkResponse(data);
            } catch (IOException e) {
                if (e instanceof FileNotFoundException) {
                    throw new ServerError();
                }
                throw new NoConnectionError(e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        L.w("Failed to close InputStream, uri = " + uri, e);
                    }
                }
                if (os != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        L.w("Failed to close OutputStream, uri = " + uri, e);
                    }
                }
            }
        }

        private boolean isAndroidAssetUri(@NonNull final Uri uri) {
            return SCHEME_ASSETS.equals(uri.getScheme());
        }

        private String androidAssetUriToPath(@NonNull final Uri uri) {
            if (!SCHEME_ASSETS.equals(uri.getScheme())) {
                throw new IllegalArgumentException("Invalid scheme for android_asset Uri: " + uri);
            }
            final String path = uri.getPath();
            return !TextUtils.isEmpty(path) && path.charAt(0) == '/' ? path.substring(1) : path;
        }

        private int resolveStreamSize(@NonNull final ContentResolver resolver,
                @NonNull final Uri uri,
                @NonNull final InputStream openedInputStream) throws IOException {
            switch (uri.getScheme()) {
                case ContentResolver.SCHEME_CONTENT:
                    return resolveSizeByContentUri(resolver, uri);

                case ContentResolver.SCHEME_FILE:
                    return resolveSizeByFileUri(uri);

                case ContentResolver.SCHEME_ANDROID_RESOURCE:
                    return resolveSizeByAndroidResourceUri(openedInputStream);

                case SCHEME_ASSETS:
                    return resolveSizeByAndroidAssetUri(openedInputStream);

                default:
                    return -1;
            }
        }

        private int resolveSizeByContentUri(@NonNull final ContentResolver resolver,
                @NonNull final Uri uri) {
            final Cursor cursor = resolver.query(uri, new String[]{OpenableColumns.SIZE}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        final long length = cursor.getLong(0);
                        if (length > Integer.MAX_VALUE) {
                            throw new IllegalArgumentException("File is too large. Uri = " + uri);
                        }
                        return (int) length;
                    }
                } finally {
                    cursor.close();
                }
            }
            return -1;
        }

        private int resolveSizeByFileUri(@NonNull final Uri uri) {
            final long length = new File(uri.getPath()).length();
            if (length > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("File is too large. Uri = " + uri);
            }
            return (int) length;
        }

        private int resolveSizeByAndroidResourceUri(@NonNull final InputStream openedInputStream) throws IOException {
            if (openedInputStream instanceof AssetManager.AssetInputStream) {
                return openedInputStream.available();
            }
            return -1;
        }

        private int resolveSizeByAndroidAssetUri(@NonNull final InputStream openedInputStream) throws IOException {
            if (openedInputStream instanceof AssetManager.AssetInputStream) {
                return openedInputStream.available();
            }
            return -1;
        }
    }
}
