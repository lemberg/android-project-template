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

import com.ls.httpclient.R;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequestBuilder;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created on 22.04.2015.
 */
public class LSImageView extends ImageView
{

    private static LSClient sharedClient;

    private static LSClient getSharedClient(Context context)
    {
        synchronized (LSImageView.class) {
            if (sharedClient == null) {
                sharedClient = new LSClient(context);
            }
        }

        return sharedClient;
    }

    /**
     * Use this method to provide default drupal client, used by all image views.
     *
     * @param client to be used in order to load images.
     */
    public static void setupSharedClient(LSClient client)
    {
        synchronized (LSImageView.class) {
            LSImageView.sharedClient = client;
        }
    }

    private LSClient localClient;

    private ImageContainer imageContainer;

    private Drawable noImageDrawable;

    private ImageLoadingListener imageLoadingListener;

    private boolean fixedBounds;

    public LSImageView(Context context)
    {
        super(context);
        initView(context, null);
    }

    public LSImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public LSImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public void initView(Context context, AttributeSet attrs)
    {
        if (this.isInEditMode()) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LSImageView);

        Drawable noImageDrawable = array.getDrawable(R.styleable.LSImageView_noImageResource);
        if (noImageDrawable != null) {
            this.setNoImageDrawable(noImageDrawable);
        }

        String imagePath = array.getString(R.styleable.LSImageView_srcPath);
        if (!TextUtils.isEmpty(imagePath)) {
            this.setImageWithURL(imagePath);
        }

        this.fixedBounds = array.getBoolean(R.styleable.LSImageView_fixedBounds, false);

        array.recycle();
    }

    public void setImageWithURL(String imagePath)
    {
        if (this.isInEditMode()) {
            return;
        }

        LSClient client = this.getClient();
        if (client == null) {
            throw new IllegalStateException("No DrupalClient set. Please provide local or shared DrupalClient to perform loading");
        }

        if (this.imageContainer != null && this.imageContainer.url.equals(imagePath)) {
            return;
        }

        this.setImageDrawable(null);//cancels loading automatically
        this.applyNoImageDrawableIfNeeded();

        if (TextUtils.isEmpty(imagePath)) {
            return;
        }

        this.imageContainer = new ImageContainer(imagePath, client);
        this.startLoading();
    }

    public String getImageURL()
    {
        if (this.imageContainer != null) {
            return this.imageContainer.url;
        }
        return null;
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        cancelLoading();
        this.imageContainer = null;
        superSetImageDrawable(drawable);
    }


    /**
     * Layout update skipping workaround
     */
    private boolean skipLayoutUpdate = false;

    /**
     * Layout update skipping workaround
     */
    protected void superSetDrawableSkippingLayoutUpdate(Drawable drawable)
    {
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
    public void requestLayout()
    {
        if (!skipLayoutUpdate) {
            super.requestLayout();
        }
    }

    /**
     * Method is calling original ImageView setDrawable method directly
     */
    protected void superSetImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
    }

    public Drawable getNoImageDrawable()
    {
        return noImageDrawable;
    }

    public void setNoImageDrawableResource(int resource)
    {
        this.setImageDrawable(this.getContext().getResources().getDrawable(resource));
    }

    public void setNoImageDrawable(Drawable noImageDrawable)
    {
        if (this.noImageDrawable != noImageDrawable) {
            if (this.getDrawable() == this.noImageDrawable) {
                superSetImageDrawable(noImageDrawable);
            }
            this.noImageDrawable = noImageDrawable;
        }
    }

    public ImageLoadingListener getImageLoadingListener()
    {
        return imageLoadingListener;
    }

    public void setImageLoadingListener(ImageLoadingListener imageLoadingListener)
    {
        this.imageLoadingListener = imageLoadingListener;
    }


    public LSClient getLocalClient()
    {
        return localClient;
    }

    public void setLocalClient(LSClient localClient)
    {
        this.localClient = localClient;
    }

    public void cancelLoading()
    {
        if (this.imageContainer != null) {
            this.imageContainer.cancelLoad();
        }
    }

    public void startLoading()
    {
        if (this.imageContainer != null) {
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingStarted(LSImageView.this, this.imageContainer.url);
            }
            this.imageContainer.loadImage(getInternalImageLoadingListenerForContainer(this.imageContainer));
        }
    }

    /**
     * @return true if drawable bounds are predefined and there is no need in onLayout call after drawable loading is complete
     */
    public boolean isFixedBounds()
    {
        return fixedBounds;
    }

    /**
     * @param fixedBounds if true drawable bounds are predefined and there is no need in onLayout call after drawable loading is complete
     */
    public void setFixedBounds(boolean fixedBounds)
    {
        this.fixedBounds = fixedBounds;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        this.cancelLoading();
    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        this.startLoading();
//    }

    protected void applyNoImageDrawableIfNeeded()
    {
        if (this.getDrawable() == null) {
            superSetDrawableSkippingLayoutUpdate(noImageDrawable);
        }
    }

    private LSClient getClient()
    {
        if (this.localClient != null) {
            return this.localClient;
        }

        return LSImageView.getSharedClient(this.getContext());
    }

    private InternalImageLoadingListener getInternalImageLoadingListenerForContainer(ImageContainer container)
    {
        return new InternalImageLoadingListener(container.url);
    }

    private static class ImageContainer
    {

        BaseRequest imageRequest;
        String url;
        LSClient client;
        LSClient.OnResponseListener listener;

        ImageContainer(String url, LSClient client)
        {
            this.url = url;
            this.client = client;
            imageRequest = new BaseRequestBuilder()
                    .setRequestMethod(BaseRequest.RequestMethod.GET)
                    .setResponseFormat(BaseRequest.ResponseFormat.IMAGE)
                    .setRequestURL(url)
                    .create();
        }

        void cancelLoad()
        {
            client.cancelAllRequestsForListener(listener, url);
        }

        void loadImage(LSClient.OnResponseListener listener)
        {
            this.listener = listener;
            this.client.performRequest(imageRequest, url, listener, false);
        }
    }

    private class InternalImageLoadingListener implements LSClient.OnResponseListener
    {

        private String acceptableURL;

        InternalImageLoadingListener(String url)
        {
            this.acceptableURL = url;
        }

        private boolean checkCurrentURL()
        {
            return imageContainer != null && imageContainer.url != null && imageContainer.url.equals(acceptableURL);
        }

        @Override
        public void onResponseReceived(BaseRequest request,ResponseData data, Object tag)
        {
            Drawable image = (Drawable) data.getData();
            if (checkCurrentURL()) {
                superSetDrawableSkippingLayoutUpdate(image);
                applyNoImageDrawableIfNeeded();
            }

            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingComplete(LSImageView.this, image);
            }
        }

        @Override
        public void onError(BaseRequest request,ResponseData data, Object tag)
        {
            if (checkCurrentURL()) {
                applyNoImageDrawableIfNeeded();
            }
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingFailed(LSImageView.this, data);
            }
        }

        @Override
        public void onCancel(BaseRequest request,Object tag)
        {
            if (checkCurrentURL()) {
                applyNoImageDrawableIfNeeded();
            }
            if (imageLoadingListener != null) {
                imageLoadingListener.onImageLoadingCancelled(LSImageView.this, this.acceptableURL);
            }
        }
    }

    public interface ImageLoadingListener
    {

        void onImageLoadingStarted(LSImageView view, String imageURL);

        void onImageLoadingComplete(LSImageView view, Drawable image);

        void onImageLoadingFailed(LSImageView view, ResponseData data);

        void onImageLoadingCancelled(LSImageView view, String path);
    }

}
