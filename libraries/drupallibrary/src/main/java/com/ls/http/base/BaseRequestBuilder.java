package com.ls.http.base;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 07.12.2015.
 */
public class BaseRequestBuilder
{
    private BaseRequest.RequestFormat mRequestFormat;
    private BaseRequest.ResponseFormat mResponseFormat;

    private String mDefaultCharset;

    private String mRequestURL;
    private BaseRequest.RequestMethod mRequestMethod;

    private Map<String, String> mRequestHeaders;
    private Map<String, String> mPostParameters;
    private Map<String, Object> mGetParameters;
    private Object mObjectToPost;

    private Object mResponseClasSpecifier;
    private Object mErrorResponseClasSpecifier;

    public BaseRequest create()
    {
        Assert.assertNotNull("Request method must be provided",mRequestMethod);
        Assert.assertNotNull("Request url must be provided",mRequestURL);

        RequestConfig config = new RequestConfig();
        config.setResponseFormat(mResponseFormat);
        config.setErrorResponseClassSpecifier(mErrorResponseClasSpecifier);
        config.setRequestFormat(mRequestFormat);
        config.setResponseClassSpecifier(mResponseClasSpecifier);

        BaseRequest request = new BaseRequest(mRequestMethod,mRequestURL,config);
        request.setObjectToPost(mObjectToPost);
        request.setDefaultCharset(mDefaultCharset);
        request.setGetParameters(mGetParameters);
        request.setPostParameters(mPostParameters);
        request.setRequestHeaders(mRequestHeaders);

        return request;

    }


    public BaseRequestBuilder setRequestURL(String requestURL)
    {
        this.mRequestURL = requestURL;
        return this;
    }

    public BaseRequestBuilder setRequestMethod(BaseRequest.RequestMethod requestMethod)
    {
        mRequestMethod = requestMethod;
        return this;
    }

    public BaseRequestBuilder setRequestFormat(BaseRequest.RequestFormat requestFormat)
    {
        this.mRequestFormat = requestFormat;
        return this;
    }

    public BaseRequestBuilder setResponseFormat(BaseRequest.ResponseFormat responseFormat)
    {
        this.mResponseFormat = responseFormat;
        return this;
    }

    public BaseRequestBuilder setDefaultCharset(String defaultCharset)
    {
        this.mDefaultCharset = defaultCharset;
        return this;
    }

    public BaseRequestBuilder setRequestHeaders(Map<String, String> requestHeaders)
    {
        this.mRequestHeaders = requestHeaders;
        return this;
    }

    public BaseRequestBuilder addRequestHeaders(Map<String, String> requestHeaders)
    {
        if(this.mRequestHeaders == null)
        {
            this.mRequestHeaders = new HashMap<String,String>();
        }

        this.mRequestHeaders.putAll(requestHeaders);
        return this;
    }

    public BaseRequestBuilder addRequestHeader(String key, String value)
    {
        if(this.mRequestHeaders == null)
        {
            this.mRequestHeaders = new HashMap<String,String>();
        }

        this.mRequestHeaders.put(key, value);
        return this;
    }

    public BaseRequestBuilder setPostParameters(Map<String, String> postParameters)
    {
        this.mPostParameters = postParameters;
        return this;
    }

    public BaseRequestBuilder addPostParameters(Map<String, String> postParameters)
    {
        if(this.mPostParameters == null)
        {
            this.mPostParameters = new HashMap<String,String>();
        }

        this.mPostParameters.putAll(postParameters);
        return this;
    }

    public BaseRequestBuilder addPostParameter(String key, String value)
    {
        if(this.mPostParameters == null)
        {
            this.mPostParameters = new HashMap<String,String>();
        }

        this.mPostParameters.put(key, value);
        return this;
    }

    public BaseRequestBuilder setGetParameters(Map<String, Object> getParameters)
    {
        this.mGetParameters = getParameters;
        return this;
    }

    public BaseRequestBuilder addGetParameters(Map<String, String> getParameters)
    {
        if(this.mGetParameters == null)
        {
            this.mGetParameters = new HashMap<String,Object>();
        }

        this.mGetParameters.putAll(getParameters);
        return this;
    }

    public BaseRequestBuilder addGetParameter(String key, String value)
    {
        if(this.mGetParameters == null)
        {
            this.mGetParameters = new HashMap<String,Object>();
        }

        this.mGetParameters.put(key, value);
        return this;
    }

    public BaseRequestBuilder setObjectToPost(Object objectToPost)
    {
        this.mObjectToPost = objectToPost;
        return this;
    }

    public BaseRequestBuilder setResponseClasSpecifier(Object responseClasSpecifier)
    {
        this.mResponseClasSpecifier = responseClasSpecifier;
        return this;
    }

    public BaseRequestBuilder setErrorResponseClasSpecifier(Object errorResponseClasSpecifier)
    {
        this.mErrorResponseClasSpecifier = errorResponseClasSpecifier;
        return this;
    }
}
