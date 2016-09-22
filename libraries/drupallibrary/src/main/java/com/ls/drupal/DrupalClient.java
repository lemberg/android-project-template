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

package com.ls.drupal;

import com.android.volley.RequestQueue;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.RequestConfig;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;
import com.ls.http.base.login.ILoginManager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Map;

/**
 * Class is used to generate requests based on DrupalEntities and attach them to request queue
 *
 * @author lemberg
 */
public class DrupalClient extends LSClient {

    private final BaseRequest.RequestFormat requestFormat;
    private String baseURL;

    /**
     * @param theBaseURL this URL will be appended with {@link AbstractBaseDrupalEntity#getPath()}
     * @param theContext application context, used to create request queue
     */
    public DrupalClient(@NonNull String theBaseURL, @NonNull Context theContext) {
        this(theBaseURL, theContext, null);
    }

    /**
     * @param theBaseURL this URL will be appended with {@link AbstractBaseDrupalEntity#getPath()}
     * @param theContext application context, used to create request queue
     * @param theFormat  server request/response format. Defines format of serialized objects and server response format, see {@link BaseRequest.RequestFormat}
     */
    public DrupalClient(@NonNull String theBaseURL, @NonNull Context theContext, @Nullable BaseRequest.RequestFormat theFormat) {
        this(theBaseURL, theContext, theFormat, null);
    }

    /**
     * @param theBaseURL      this URL will be appended with {@link AbstractBaseDrupalEntity#getPath()}
     * @param theContext      application context, used to create request queue
     * @param theFormat       server request/response format. Defines format of serialized objects and server response format, see {@link BaseRequest.RequestFormat}
     * @param theLoginManager contains user profile data and can update request parameters and headers in order to apply it.
     */
    public DrupalClient(@NonNull String theBaseURL, @NonNull Context theContext, @Nullable BaseRequest.RequestFormat theFormat, @Nullable ILoginManager theLoginManager) {
        this(theBaseURL, getDefaultQueue(theContext), theFormat, theLoginManager);
    }

    /**
     * @param theBaseURL      this URL will be appended with {@link AbstractBaseDrupalEntity#getPath()}
     * @param theQueue        queue to execute requests. You can customize cache management, by setting custom queue
     * @param theFormat       server request/response format. Defines format of serialized objects and server response format, see {@link BaseRequest.RequestFormat}
     * @param theLoginManager contains user profile data and can update request parameters and headers in order to apply it.
     */
    public DrupalClient(@NonNull String theBaseURL, @NonNull RequestQueue theQueue, @Nullable BaseRequest.RequestFormat theFormat,
            @Nullable ILoginManager theLoginManager) {
        super(theQueue, theLoginManager);
        this.setBaseURL(theBaseURL);
        if (theFormat != null) {
            this.requestFormat = theFormat;
        } else {
            this.requestFormat = BaseRequest.RequestFormat.JSON;
        }
    }


    /**
     * @param entity      Object, specifying request parameters, retrieved data will be merged to this object.
     * @param config      Entity, containing additional request parameters
     * @param tag         will be attached to request and returned in listener callback, can be used in order to cancel request
     * @param synchronous if true - result will be returned synchronously.
     * @return ResponseData object or null if request was asynchronous.
     */
    public ResponseData getObject(AbstractBaseDrupalEntity entity, RequestConfig config, Object tag, OnResponseListener listener, boolean synchronous) {
        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.GET, getURLForEntity(entity), applyDefaultFormat(config));
        request.setGetParameters(entity.getItemRequestGetParameters(BaseRequest.RequestMethod.GET));
        request.addRequestHeaders(entity.getItemRequestHeaders(BaseRequest.RequestMethod.GET));
        return this.performRequest(request, tag, listener, synchronous);
    }

    /**
     * @param entity      Object, specifying request parameters
     * @param config      Entity, containing additional request parameters
     * @param tag         will be attached to request and returned in listener callback, can be used in order to cancel request
     * @param synchronous if true - result will be returned synchronously.
     * @return ResponseData object or null if request was asynchronous.
     */
    public ResponseData postObject(AbstractBaseDrupalEntity entity, RequestConfig config, Object tag, OnResponseListener listener, boolean synchronous) {
        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.POST, getURLForEntity(entity), applyDefaultFormat(config));
        Map<String, String> postParams = entity.getItemRequestPostParameters();
        if (postParams == null || postParams.isEmpty()) {
            request.setObjectToPost(entity.getManagedData());
        } else {
            request.setPostParameters(postParams);
        }
        request.setGetParameters(entity.getItemRequestGetParameters(BaseRequest.RequestMethod.POST));
        request.addRequestHeaders(entity.getItemRequestHeaders(BaseRequest.RequestMethod.POST));
        return this.performRequest(request, tag, listener, synchronous);
    }

    /**
     * @param entity      Object, specifying request parameters
     * @param config      Entity, containing additional request parameters
     * @param tag         will be attached to request and returned in listener callback, can be used in order to cancel request
     * @param synchronous if true - result will be returned synchronously.
     * @return ResponseData object or null if request was asynchronous.
     */
    public ResponseData putObject(AbstractBaseDrupalEntity entity, RequestConfig config, Object tag, OnResponseListener listener, boolean synchronous) {
        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.PUT, getURLForEntity(entity), applyDefaultFormat(config));
        Map<String, String> postParams = entity.getItemRequestPostParameters();
        if (postParams == null || postParams.isEmpty()) {
            request.setObjectToPost(entity.getManagedData());
        } else {
            request.setPostParameters(postParams);
        }
        request.setGetParameters(entity.getItemRequestGetParameters(BaseRequest.RequestMethod.PUT));
        request.addRequestHeaders(entity.getItemRequestHeaders(BaseRequest.RequestMethod.PUT));
        return this.performRequest(request, tag, listener, synchronous);
    }


    /**
     * @param entity      Object, specifying request parameters, must have "createFootPrint" called before.
     * @param config      Entity, containing additional request parameters
     * @param tag         will be attached to request and returned in listener callback, can be used in order to cancel request
     * @param synchronous if true - result will be returned synchronously.
     * @return ResponseData object or null if request was asynchronous.
     */
    public ResponseData patchObject(AbstractBaseDrupalEntity entity, RequestConfig config, Object tag, OnResponseListener listener, boolean synchronous) {
        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.PATCH, getURLForEntity(entity), applyDefaultFormat(config));
        request.setGetParameters(entity.getItemRequestGetParameters(BaseRequest.RequestMethod.PATCH));
        request.setObjectToPost(entity.getPatchObject());
        request.addRequestHeaders(entity.getItemRequestHeaders(BaseRequest.RequestMethod.PATCH));
        return this.performRequest(request, tag, listener, synchronous);
    }

    /**
     * @param entity      Object, specifying request parameters
     * @param config      Entity, containing additional request parameters
     * @param tag         will be attached to request and returned in listener callback, can be used in order to cancel request
     * @param synchronous if true - result will be returned synchronously.
     * @return ResponseData object or null if request was asynchronous.
     */
    public ResponseData deleteObject(AbstractBaseDrupalEntity entity, RequestConfig config, Object tag, OnResponseListener listener,
            boolean synchronous) {
        BaseRequest request = new BaseRequest(BaseRequest.RequestMethod.DELETE, getURLForEntity(entity), applyDefaultFormat(config));
        request.setGetParameters(entity.getItemRequestGetParameters(BaseRequest.RequestMethod.DELETE));
        request.addRequestHeaders(entity.getItemRequestHeaders(BaseRequest.RequestMethod.DELETE));
        return this.performRequest(request, tag, listener, synchronous);
    }


    private String getURLForEntity(AbstractBaseDrupalEntity entity) {
        String path = entity.getPath();

        boolean pathAlreadyHasDomain = !TextUtils.isEmpty(path) && (path.startsWith("http://") || path.startsWith("https://"));

        if (TextUtils.isEmpty(baseURL) || pathAlreadyHasDomain) {
            return path;
        } else {
            if (!TextUtils.isEmpty(path) && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            return this.baseURL + path;
        }
    }


    private RequestConfig applyDefaultFormat(RequestConfig config) {
        if (config == null) {
            config = new RequestConfig();
        }

        if (config.getRequestFormat() == null) {
            config.setRequestFormat(this.requestFormat);
        }

        return config;
    }

    public void setBaseURL(String theBaseURL) {
        if (!TextUtils.isEmpty(theBaseURL) && theBaseURL.charAt(theBaseURL.length() - 1) != '/') {
            theBaseURL += '/';
        }
        ;
        this.baseURL = theBaseURL;
    }

    public String getBaseURL() {
        return baseURL;
    }
}