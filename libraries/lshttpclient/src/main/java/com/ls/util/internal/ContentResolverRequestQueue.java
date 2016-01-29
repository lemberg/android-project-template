package com.ls.util.internal;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.ls.util.L;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yaroslav Mytkalyk on 29/01/16.
 */
public class ContentResolverRequestQueue extends RequestQueue {

    public ContentResolverRequestQueue(@NonNull final Context context) {
        super(null, new ContentResolverNetwork(context.getContentResolver()));
    }

    private static final class ContentResolverNetwork implements Network {

        @NonNull
        private final ContentResolver mContentResolver;

        public ContentResolverNetwork(@NonNull final ContentResolver contentResolver) {
            mContentResolver = contentResolver;
        }

        @Override
        public NetworkResponse performRequest(final Request<?> request) throws VolleyError {
            final Uri uri = Uri.parse(request.getUrl());
            final String scheme = uri.getScheme();
            if (!scheme.equals(ContentResolver.SCHEME_CONTENT)
                    && !scheme.equals(ContentResolver.SCHEME_FILE)
                    && !scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                throw new IllegalArgumentException(
                        "Request scheme must be 'content', 'file', or 'android.resource'");
            }

            InputStream is = null;
            ByteArrayOutputStream os = null;

            try {
                is = mContentResolver.openInputStream(uri);
                if (is == null) {
                    throw new IOException(
                            "Failed to open InputStream from ContentResolver, uri = " + uri);
                }

                final int size = is.available();
                os = new ByteArrayOutputStream(size > 0 ? size : 0);

                final byte[] buff = new byte[1024];
                int read;
                while ((read = is.read(buff, 0, buff.length)) != -1) {
                    os.write(buff, 0, read);
                }

                return new NetworkResponse(os.toByteArray());
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
    }
}
