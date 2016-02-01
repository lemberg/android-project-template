package com.ls.http;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;

/**
 * {@link ContextWrapper} that uses returns the {@link ContentResolver} specified in constructor in
 * {@link #getContentResolver()}
 */
final class MockContentResolverContextWrapper extends ContextWrapper {

    @NonNull
    private final ContentResolver mContentResolver;

    public MockContentResolverContextWrapper(@NonNull final Context base,
            @NonNull final ContentResolver contentResolver) {
        super(base);
        mContentResolver = contentResolver;
    }

    @Override
    public Context getApplicationContext() {
        return this;
    }

    @Override
    @NonNull
    public ContentResolver getContentResolver() {
        return mContentResolver;
    }
}
