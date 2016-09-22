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

package com.ls.util.image;

import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;
import com.ls.httpclient.R;
import com.ls.util.UriFactory;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created on 22.04.2015.
 */
public class LSImageView extends ImageView {

    private static volatile LSClient sSharedClient;

    private static LSClient getSharedClient(@NonNull final Context context) {
        if (sSharedClient == null) {
            synchronized (LSImageView.class) {
                if (sSharedClient == null) {
                    sSharedClient = new LSClient.Builder(context).build();
                }
            }
        }
        return sSharedClient;
    }

    /**
     * Use this method to provide default drupal client, used by all image views.
     *
     * @param client to be used in order to load images.
     */
    public static void setupSharedClient(final LSClient client) {
        synchronized (LSImageView.class) {
            LSImageView.sSharedClient = client;
        }
    }

    private LSClient localClient;

    private ImageContainer mImageContainer;

    private Drawable noImageDrawable;

    private ImageLoadingListener imageLoadingListener;

    private boolean fixedBounds;

    public LSImageView(Context context) {
        super(context);
        initView(context, null);
    }

    public LSImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LSImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs) {
        if (this.isInEditMode()) {
            return;
        }

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LSImageView);
        final Drawable noImageDrawable = array.getDrawable(R.styleable.LSImageView_noImageResource);
        if (noImageDrawable != null) {
            this.setNoImageDrawable(noImageDrawable);
        }

        final String imagePath = array.getString(R.styleable.LSImageView_srcPath);
        if (!TextUtils.isEmpty(imagePath)) {
            this.setImageWithURL(imagePath);
        }

        this.fixedBounds = array.getBoolean(R.styleable.LSImageView_fixedBounds, false);

        array.recycle();
    }

    /**
     * @deprecated use {@link #setImageUri(String)} instead
     */
    @Deprecated
    public void setImageWithURL(@Nullable final String imageUri) {
        setImageUri(imageUri);
    }

    /**
     * Sets image Uri. This method is overridden
     *
     * Unlike {@link ImageView's} implementation, the request will not be performed in UI thread.
     * See {@link UriFactory} for special uris.
     *
     * @param uri The Uri to set
     */
    @Override
    public void setImageURI(@Nullable final Uri uri) {
        setImageUri(uri != null ? uri.toString() : null);
    }

    /**
     * Sets image Uri. This method is overridden
     *
     * The request will not be performed in UI thread.
     * See {@link UriFactory} for special uris.
     *
     * @param imageUri The Uri to set
     */
    public void setImageUri(@Nullable final String imageUri) {
        if (this.isInEditMode()) {
            return;
        }

        if (this.mImageContainer != null && this.mImageContainer.uri.equals(imageUri)) {
            return;
        }

        this.setImageDrawable(null);
        this.applyNoImageDrawableIfNeeded();


        // null URL means no image
        if (TextUtils.isEmpty(imageUri)) {
            return;
        }

        final LSClient client = this.getClient();
        if (client == null) {
            throw new IllegalStateException(
                    "No DrupalClient set. Please provide local or shared DrupalClient to perform loading");
        }

        this.mImageContainer = new ImageContainer(imageUri, client);
        this.startLoading();
    }

    /**
     * @deprecated use {@link #getImageUri()} instead
     */
    @Nullable
    public String getImageURL() {
        return getImageUri();
    }

    @Nullable
    public String getImageUri() {
        return mImageContainer != null ? mImageContainer.uri : null;
    }

    @Override
    public void setImageDrawable(@Nullable final Drawable drawable) {
        cancelLoading();
        this.mImageContainer = null;
        superSetImageDrawable(drawable);
    }


    /**
     * Layout update skipping workaround
     */
    private boolean skipLayoutUpdate = false;

    /**
     * Layout update skipping workaround
     */
    protected void superSetDrawableSkippingLayoutUpdate(Drawable drawable) {
        if (fixedBounds) {
            skipLayoutUpdate = true;
            superSetImageDrawable(drawable);
            skipLayoutUpdate = false;
        } else {
            superSetImageDrawable(drawable);
        }
    }

    /**
     * Layout update skipping workaround
     */
    @Override
    public void requestLayout() {
        if (!skipLayoutUpdate) {
            super.requestLayout();
        }
    }

    /**
     * Method is calling original ImageView setDrawable method directly
     */
    protected void superSetImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public Drawable getNoImageDrawable() {
        return noImageDrawable;
    }

    public void setNoImageDrawableResource(int resource) {
        this.setNoImageDrawable(this.getContext().getResources().getDrawable(resource));
    }

    public void setNoImageDrawable(Drawable noImageDrawable) {
        if (this.noImageDrawable != noImageDrawable) {
            if (this.getDrawable() == this.noImageDrawable) {
                superSetImageDrawable(noImageDrawable);
            }
            this.noImageDrawable = noImageDrawable;
        }
    }

    public ImageLoadingListener getImageLoadingListener() {
        return imageLoadingListener;
    }

    public void setImageLoadingListener(ImageLoadingListener imageLoadingListener) {
        this.imageLoadingListener = imageLoadingListener;
    }


    public LSClient getLocalClient() {
        return localClient;
    }

    public void setLocalClient(LSClient localClient) {
        this.localClient = localClient;
    }

    public void cancelLoading() {
        if (this.mImageContainer != null) {
            this.mImageContainer.cancelLoad();
        }
    }

    public void startLoading() {
        if (this.mImageContainer != null) {
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingStarted(LSImageView.this,
                        this.mImageContainer.uri);
            }
            this.mImageContainer.loadImage(
                    getInternalImageLoadingListenerForContainer(this.mImageContainer));
        }
    }

    /**
     * @return true if drawable bounds are predefined and there is no need in onLayout call after
     * drawable loading is complete
     */
    public boolean isFixedBounds() {
        return fixedBounds;
    }

    /**
     * @param fixedBounds if true drawable bounds are predefined and there is no need in onLayout
     *                    call after drawable loading is complete
     */
    public void setFixedBounds(boolean fixedBounds) {
        this.fixedBounds = fixedBounds;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.cancelLoading();
    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        this.startLoading();
//    }

    protected void applyNoImageDrawableIfNeeded() {
        if (this.getDrawable() == null) {
            superSetDrawableSkippingLayoutUpdate(noImageDrawable);
        }
    }

    private LSClient getClient() {
        if (this.localClient != null) {
            return this.localClient;
        }

        return LSImageView.getSharedClient(this.getContext());
    }

    private InternalImageLoadingListener getInternalImageLoadingListenerForContainer(
            ImageContainer container) {
        return new InternalImageLoadingListener(container.uri);
    }

    private static final class ImageContainer {

        @NonNull
        private final String uri;

        @NonNull
        private final LSClient client;
        private LSClient.OnResponseListener listener;

        ImageContainer(@NonNull final String uri,
                @NonNull final LSClient client) {
            this.uri = uri;
            this.client = client;
        }

        void cancelLoad() {
            client.cancelAllRequestsForListener(listener, uri);
        }

        void loadImage(final LSClient.OnResponseListener listener) {
            this.listener = listener;
            final BaseRequest imageRequest = new BaseRequestBuilder()
                    .setRequestMethod(BaseRequest.RequestMethod.GET)
                    .setResponseFormat(BaseRequest.ResponseFormat.IMAGE)
                    .setRequestUri(uri)
                    .create();
            this.client.performRequest(imageRequest, uri, listener, false);
        }
    }

    private final class InternalImageLoadingListener implements LSClient.OnResponseListener {

        @NonNull
        private final String acceptableUri;

        InternalImageLoadingListener(@NonNull final String url) {
            this.acceptableUri = url;
        }

        private boolean checkCurrentUri() {
            return mImageContainer != null && mImageContainer.uri.equals(acceptableUri);
        }

        @Override
        public void onResponseReceived(@NonNull BaseRequest request, @NonNull ResponseData data, @Nullable Object tag) {
            final Drawable image = (Drawable) data.getData();
            if (checkCurrentUri()) {
                superSetDrawableSkippingLayoutUpdate(image);
                applyNoImageDrawableIfNeeded();
            }

            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingComplete(LSImageView.this, image);
            }
        }

        @Override
        public void onError(@NonNull BaseRequest request, @Nullable ResponseData data, @Nullable Object tag) {
            if (checkCurrentUri()) {
                applyNoImageDrawableIfNeeded();
            }
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingFailed(LSImageView.this, data);
            }
        }

        @Override
        public void onCancel(@NonNull BaseRequest request, @Nullable Object tag) {
            if (checkCurrentUri()) {
                applyNoImageDrawableIfNeeded();
            }
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingCancelled(LSImageView.this, this.acceptableUri);
            }
        }
    }

    public interface ImageLoadingListener {

        void onImageLoadingStarted(LSImageView view, String imageURL);

        void onImageLoadingComplete(LSImageView view, Drawable image);

        void onImageLoadingFailed(LSImageView view, ResponseData data);

        void onImageLoadingCancelled(LSImageView view, String path);
    }

}
