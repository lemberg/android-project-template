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

    private Object mResponseClassSpecifier;
    private Object mErrorResponseClasSpecifier;

    public BaseRequest create()
    {
        Assert.assertNotNull("Request method must be provided",mRequestMethod);
        Assert.assertNotNull("Request url must be provided",mRequestURL);

        RequestConfig config = new RequestConfig();
        config.setResponseFormat(mResponseFormat);
        config.setErrorResponseClassSpecifier(mErrorResponseClasSpecifier);
        config.setRequestFormat(mRequestFormat);
        config.setResponseClassSpecifier(mResponseClassSpecifier);

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

    /**
     * @param requestFormat format of POST body serialization, used to form post data from {@link #setObjectToPost(Object) setObjectToPost}
     * @return
     */
    public BaseRequestBuilder setRequestFormat(BaseRequest.RequestFormat requestFormat)
    {
        this.mRequestFormat = requestFormat;
        return this;
    }

    /**
     * @param responseFormat format of response body, used to define correct deserializer for server response
     * @return
     */
    public BaseRequestBuilder setResponseFormat(BaseRequest.ResponseFormat responseFormat)
    {
        this.mResponseFormat = responseFormat;
        return this;
    }

    /**
     * @param defaultCharset used to define charset for serializer/deserializer
     * @return
     */
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

    /**
     * @param objectToPost Object to be serialized to post body.
     * @return
     */
    public BaseRequestBuilder setObjectToPost(Object objectToPost)
    {
        this.mObjectToPost = objectToPost;
        return this;
    }

    /**
     * @param responseClassSpecifier  Class or Type, returned as parsedErrorResponse field of ResultData object, can be null if you don't need one.
     */
    public BaseRequestBuilder setResponseClassSpecifier(Object responseClassSpecifier)
    {
        this.mResponseClassSpecifier = responseClassSpecifier;
        return this;
    }

    /**
     * @param errorResponseClasSpecifier  Class or Type, returned as error field of ResultData object, can be null if you don't need one.
     */
    public BaseRequestBuilder setErrorResponseClasSpecifier(Object errorResponseClasSpecifier)
    {
        this.mErrorResponseClasSpecifier = errorResponseClasSpecifier;
        return this;
    }
}
