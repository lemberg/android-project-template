package com.ls.http.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 07.12.2015.
 */
public class BaseRequestBuilder {

    private BaseRequest.RequestFormat mRequestFormat;
    private BaseRequest.ResponseFormat mResponseFormat;

    private String mDefaultCharset;

    private String mRequestUri;
    private BaseRequest.RequestMethod mRequestMethod;

    private Map<String, String> mRequestHeaders;
    private Map<String, String> mPostParameters;
    private Map<String, Object> mGetParameters;
    private Object mObjectToPost;

    private Object mResponseClassSpecifier;
    private Object mErrorResponseClassSpecifier;

    @NonNull
    public BaseRequest create() {
        if (mRequestMethod == null) {
            throw new IllegalStateException("Request method must be provided");
        }
        if (mRequestUri == null) {
            throw new IllegalStateException("Request Uri must be provided");
        }

        final RequestConfig config = new RequestConfig();
        config.setResponseFormat(mResponseFormat);
        config.setErrorResponseClassSpecifier(mErrorResponseClassSpecifier);
        config.setRequestFormat(mRequestFormat);
        config.setResponseClassSpecifier(mResponseClassSpecifier);

        final BaseRequest request = new BaseRequest(mRequestMethod, mRequestUri, config);
        request.setObjectToPost(mObjectToPost);
        request.setDefaultCharset(mDefaultCharset);
        request.setGetParameters(mGetParameters);
        request.setPostParameters(mPostParameters);
        request.setRequestHeaders(mRequestHeaders);

        return request;

    }

    /**
     * @deprecated use {@link #setRequestUri(String)} instead
     */
    @Deprecated
    public BaseRequestBuilder setRequestURL(String requestURL) {
        setRequestUri(requestURL);
        return this;
    }

    public BaseRequestBuilder setRequestUri(@NonNull final String uri) {
        mRequestUri = uri;
        return this;
    }

    public BaseRequestBuilder setRequestMethod(BaseRequest.RequestMethod requestMethod) {
        mRequestMethod = requestMethod;
        return this;
    }

    /**
     * @param requestFormat format of POST body serialization, used to form post data from {@link #setObjectToPost(Object) setObjectToPost}
     */
    public BaseRequestBuilder setRequestFormat(BaseRequest.RequestFormat requestFormat) {
        this.mRequestFormat = requestFormat;
        return this;
    }

    /**
     * @param responseFormat format of response body, used to define correct deserializer for server response
     */
    public BaseRequestBuilder setResponseFormat(BaseRequest.ResponseFormat responseFormat) {
        this.mResponseFormat = responseFormat;
        return this;
    }

    /**
     * @param defaultCharset used to define charset for serializer/deserializer
     */
    public BaseRequestBuilder setDefaultCharset(String defaultCharset) {
        this.mDefaultCharset = defaultCharset;
        return this;
    }

    public BaseRequestBuilder setRequestHeaders(Map<String, String> requestHeaders) {
        this.mRequestHeaders = requestHeaders;
        return this;
    }

    public BaseRequestBuilder addRequestHeaders(Map<String, String> requestHeaders) {
        if (this.mRequestHeaders == null) {
            this.mRequestHeaders = new HashMap<String, String>();
        }

        this.mRequestHeaders.putAll(requestHeaders);
        return this;
    }

    public BaseRequestBuilder addRequestHeader(String key, String value) {
        if (this.mRequestHeaders == null) {
            this.mRequestHeaders = new HashMap<String, String>();
        }

        this.mRequestHeaders.put(key, value);
        return this;
    }

    public BaseRequestBuilder setPostParameters(Map<String, String> postParameters) {
        this.mPostParameters = postParameters;
        return this;
    }

    public BaseRequestBuilder addPostParameters(Map<String, String> postParameters) {
        if (this.mPostParameters == null) {
            this.mPostParameters = new HashMap<String, String>();
        }

        this.mPostParameters.putAll(postParameters);
        return this;
    }

    public BaseRequestBuilder addPostParameter(String key, String value) {
        if (this.mPostParameters == null) {
            this.mPostParameters = new HashMap<String, String>();
        }

        this.mPostParameters.put(key, value);
        return this;
    }

    public BaseRequestBuilder setGetParameters(Map<String, Object> getParameters) {
        this.mGetParameters = getParameters;
        return this;
    }

    public BaseRequestBuilder addGetParameters(Map<String, String> getParameters) {
        if (this.mGetParameters == null) {
            this.mGetParameters = new HashMap<String, Object>();
        }

        this.mGetParameters.putAll(getParameters);
        return this;
    }

    public BaseRequestBuilder addGetParameter(String key, String value) {
        if (this.mGetParameters == null) {
            this.mGetParameters = new HashMap<String, Object>();
        }

        this.mGetParameters.put(key, value);
        return this;
    }

    /**
     * @param objectToPost Object to be serialized to post body.
     */
    public BaseRequestBuilder setObjectToPost(Object objectToPost) {
        this.mObjectToPost = objectToPost;
        return this;
    }

    /**
     * @param responseClassSpecifier Class or Type, returned as parsedErrorResponse field of ResultData object, can be null if you don't need one.
     */
    public BaseRequestBuilder setResponseClassSpecifier(Object responseClassSpecifier) {
        this.mResponseClassSpecifier = responseClassSpecifier;
        return this;
    }

    /**
     * @param errorResponseClassSpecifier Class or Type, returned as error field of ResultData object, can be null if you don't need one.
     * @deprecated use {@link #setErrorResponseClassSpecifier(Object)}
     */
    public BaseRequestBuilder setErrorResponseClasSpecifier(@Nullable final Object errorResponseClassSpecifier) {
        return setErrorResponseClassSpecifier(errorResponseClassSpecifier);
    }

    /**
     * @param errorResponseClassSpecifier Class or Type, returned as error field of ResultData object, can be null if you don't need one.
     */
    public BaseRequestBuilder setErrorResponseClassSpecifier(@Nullable final Object errorResponseClassSpecifier) {
        this.mErrorResponseClassSpecifier = errorResponseClassSpecifier;
        return this;
    }
}
