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

package com.ls.http.base.client;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.BaseRequest.OnResponseListener;
import com.ls.http.base.ResponseData;
import com.ls.http.base.login.AnonymousLoginManager;
import com.ls.http.base.login.ILoginManager;
import com.ls.util.internal.VolleyResponseUtils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Class is used to generate requests based on DrupalEntities and attach them to request queue
 *
 * @author lemberg
 */
public class LSClient implements OnResponseListener {
    public enum DuplicateRequestPolicy {ALLOW,ATTACH,REJECT}


    private RequestQueue queue;
    private ResponseListenersSet listeners;
    private String defaultCharset;

    private ILoginManager loginManager;
    private RequestProgressListener progressListener;

    private int requestTimeout = 1500;

    private DuplicateRequestPolicy duplicateRequestPolicy = DuplicateRequestPolicy.ATTACH;

    public static interface OnResponseListener {

        void onResponseReceived(ResponseData data, Object tag);

        void onError(ResponseData data, Object tag);

        void onCancel(Object tag);
    }

    /**
     * Can be used in order to react on request count changes (start/success/failure or canceling).
     *
     * @author lemberg
     */
    public interface RequestProgressListener {

        /**
         * Called after new request was added to queue
         *
         * @param activeRequests number of requests pending
         */
        void onRequestStarted(LSClient theClient, int activeRequests);

        /**
         * Called after current request was complete
         *
         * @param activeRequests number of requests pending
         */
        void onRequestFinished(LSClient theClient, int activeRequests);
    }

    /**
     * @param theContext application context, used to create request queue
     */
    public LSClient(@NonNull Context theContext) {
        this(theContext, null);
    }

    /**
     * @param theContext      application context, used to create request queue
     * @param theLoginManager contains user profile data and can update request parameters and headers in order to apply it.
     */
    public LSClient(@NonNull Context theContext, @Nullable ILoginManager theLoginManager) {
        this(getDefaultQueue(theContext), theLoginManager);
    }

    @NonNull
    protected static RequestQueue getDefaultQueue(@NonNull Context theContext) {
        return Volley.newRequestQueue(theContext.getApplicationContext());
    }

    /**
     * @param theQueue        queue to execute requests. You can customize cache management, by setting custom queue
     * @param theLoginManager contains user profile data and can update request parameters and headers in order to apply it.
     */
    public LSClient(@NonNull RequestQueue theQueue, @Nullable ILoginManager theLoginManager) {
        this.listeners = new ResponseListenersSet();
        this.queue = theQueue;

        if (theLoginManager != null) {
            this.setLoginManager(theLoginManager);
        } else {
            this.setLoginManager(new AnonymousLoginManager());
        }
    }

    /**
     * @param request     Request object to be performed
     * @param synchronous if true request result will be returned synchronously
     * @return {@link ResponseData} object, containing request result code and string or error and deserialized object, specified in request.
     */
    public ResponseData performRequest(BaseRequest request, boolean synchronous) {
        return performRequest(request, null, null, synchronous);
    }

    /**
     * @param request     Request object to be performed
     * @param tag         will be applied to the request and returned in listener
     * @param synchronous if true request result will be returned synchronously
     * @return {@link ResponseData} object, containing request result code and string or error and deserialized object, specified in request.
     */
    public ResponseData performRequest(BaseRequest request, Object tag, final OnResponseListener listener, boolean synchronous) {
        request.setRetryPolicy(new DefaultRetryPolicy(requestTimeout, 1, 1));
        if (!loginManager.shouldRestoreLogin()) {
            return performRequestNoLoginRestore(request, tag, listener, synchronous);
        } else {
            return performRequestLoginRestore(request, tag, listener, synchronous);
        }
    }

    protected ResponseData performRequestNoLoginRestore(BaseRequest request, Object tag, OnResponseListener listener, boolean synchronous) {
        request.setTag(tag);
        request.setResponseListener(this);
        this.loginManager.applyLoginDataToRequest(request);
        request.setSmartComparisonEnabled(this.duplicateRequestPolicy != DuplicateRequestPolicy.ALLOW);

        boolean wasRegisterred ;
        boolean skipDuplicateRequestListeners = this.duplicateRequestPolicy == LSClient.DuplicateRequestPolicy.REJECT;
        synchronized (listeners) {
            wasRegisterred = this.listeners.registerListenerForRequest(request, listener,tag,skipDuplicateRequestListeners);
        }

        if(wasRegisterred||synchronous) {
            this.onNewRequestStarted();
            return request.performRequest(synchronous, queue);
        }else{
            if(skipDuplicateRequestListeners && listener != null)
            {
                listener.onCancel(tag);
            }
            return null;
        }
    }

    private ResponseData performRequestLoginRestore(final BaseRequest request, Object tag, final OnResponseListener listener, final boolean synchronous) {
        if (synchronous) {
            return performRequestLoginRestoreSynchrounous(request, tag, listener);
        } else {
            return performRequestLoginRestoreAsynchrounous(request, tag, listener);
        }
    }

    private ResponseData performRequestLoginRestoreAsynchrounous(final BaseRequest request, Object tag, final OnResponseListener listener) {
        final OnResponseListener loginRestoreResponseListener = new OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                if (listener != null) {
                    listener.onResponseReceived(data, tag);
                }
            }

            @Override
            public void onError(ResponseData data, Object tag) {
                if (VolleyResponseUtils.isAuthError(data.getError())) {
                    if (loginManager.canRestoreLogin()) {
                        new RestoreLoginAttemptTask(request, listener, tag, data).execute();
                    } else {
                        loginManager.onLoginRestoreFailed();
                        if (listener != null) {
                            listener.onError(data, tag);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onError(data, tag);
                    }
                }
            }

            @Override
            public void onCancel(Object tag) {
                if (listener != null) {
                    listener.onCancel(tag);
                }
            }
        };

        return performRequestNoLoginRestore(request, tag, loginRestoreResponseListener, false);
    }

    private ResponseData performRequestLoginRestoreSynchrounous(final BaseRequest request, Object tag, final OnResponseListener listener) {
        final OnResponseListener loginRestoreResponseListener = new OnResponseListener() {
            @Override
            public void onResponseReceived(ResponseData data, Object tag) {
                if (listener != null) {
                    listener.onResponseReceived(data, tag);
                }
            }

            @Override
            public void onError(ResponseData data, Object tag) {
                if (VolleyResponseUtils.isAuthError(data.getError())) {
                    if (!loginManager.canRestoreLogin()) {
                        if (listener != null) {
                            listener.onError(data, tag);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onError(data, tag);
                    }
                }
            }

            @Override
            public void onCancel(Object tag) {
                if (listener != null) {
                    listener.onCancel(tag);
                }
            }
        };

        ResponseData result = performRequestNoLoginRestore(request, tag, loginRestoreResponseListener, true);
        if (VolleyResponseUtils.isAuthError(result.getError())) {
            if (loginManager.canRestoreLogin()) {
                boolean restored = loginManager.restoreLoginData(queue);
                if (restored) {
                    result = performRequestNoLoginRestore(request, tag, new OnResponseAuthListenerDecorator(listener), true);
                } else {
                    listener.onError(result, tag);
                }
            } else {
                loginManager.onLoginRestoreFailed();
            }
        }
        return result;
    }


    /**
     * @return request timeout millis
     */
    public int getRequestTimeout() {
        return requestTimeout;
    }

    /**
     * @param requestTimeout request timeout millis
     */
    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }


    /**
     * This request is always synchronous and has no callback
     */
    public final Object login(final String userName, final String password) {
        return this.loginManager.login(userName, password, queue);
    }

    /**
     * This request is always synchronous
     */
    public final void logout() {
        this.loginManager.logout(queue);
    }

    /**
     * @return true all necessary user id data is fetched and login can be restored automatically
     */
    public boolean isLogged() {
        return this.loginManager.canRestoreLogin();
    }

    public ILoginManager getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(ILoginManager loginManager) {
        this.loginManager = loginManager;
    }

    /**
     * Synchronous login restore attempt
     *
     * @return false if login restore failed.
     */
    public boolean restoreLogin() {
        if (this.loginManager.canRestoreLogin()) {
            return this.loginManager.restoreLoginData(queue);
        }
        return false;
    }

    @Override
    public void onResponseReceived(ResponseData data, BaseRequest request) {
        synchronized (listeners) {
            List<ResponseListenersSet.ListenerHolder> listenerList = this.listeners.getListenersForRequest(request);
            this.listeners.removeListenersForRequest(request);
            this.onRequestComplete();
            if (listenerList != null) {
                for (ResponseListenersSet.ListenerHolder holder : listenerList) {
                    holder.getListener().onResponseReceived(data, holder.getTag());
                }
            }
        }
    }

    @Override
    public void onError(ResponseData data, BaseRequest request) {
        synchronized (listeners) {
            List<ResponseListenersSet.ListenerHolder> listenerList = this.listeners.getListenersForRequest(request);
            this.listeners.removeListenersForRequest(request);
            this.onRequestComplete();
            if (listenerList != null) {
                for (ResponseListenersSet.ListenerHolder holder : listenerList) {
                    holder.getListener().onError(data, holder.getTag());
                }
            }
        }
    }

    /**
     * @return Charset, used to encode/decode server request post body and response.
     */
    public String getDefaultCharset() {
        return defaultCharset;
    }

    /**
     * @param defaultCharset Charset, used to encode/decode server request post body and response.
     */
    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    /**
     * @param tag Cancel all requests, containing given tag. If no tag is specified - all requests are canceled.
     */
    public void cancelByTag(final @NonNull Object tag) {
        this.cancelAllRequestsForListener(null, tag);
    }

    /**
     * Cancel all requests
     */
    public void cancelAll() {
        this.cancelAllRequestsForListener(null, null);
    }

    /**
     * @return current duplicate request policy
     */
    public DuplicateRequestPolicy getDuplicateRequestPolicy() {
        return duplicateRequestPolicy;
    }

    /**
     * Sets duplicate request handling policy according to parameter provided. Only simultaneous requests are compared (executing at the same time).
     * @param duplicateRequestPolicy in case if
     *      "ALLOW" - all requests are performed
     *      "ATTACH" - only first unique request from queue will be performed all other listeners will be attached to this request and triggered.
     *      "REJECT" - only first unique request from queue will be performed and it's listener triggered. "onCancel()" listener method will be called for all requests skipped.
     * Default value is "ALLOW"
     */
    public void setDuplicateRequestPolicy(DuplicateRequestPolicy duplicateRequestPolicy) {
        this.duplicateRequestPolicy = duplicateRequestPolicy;
    }

    /**
     * Cancel all requests for given listener with tag
     *
     * @param theListener listener to cancel requests for in case if null passed- all requests for given tag will be canceled
     * @param theTag      to cancel requests for, in case if null passed- all requests for given listener will be canceled
     */
    public void cancelAllRequestsForListener(final @Nullable OnResponseListener theListener, final @Nullable Object theTag) {
        this.queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                if (theTag == null || theTag.equals(request.getTag())) {
                    synchronized (listeners) {
                        List<ResponseListenersSet.ListenerHolder> listenerList = listeners.getListenersForRequest(request);

                        if (theListener == null || (listenerList != null &&  holderListContainsListener(listenerList,theListener))) {
                            if (listenerList != null) {
                                listeners.removeListenersForRequest(request);
                                for (ResponseListenersSet.ListenerHolder holder : listenerList) {
                                    holder.getListener().onCancel(holder.getTag());
                                }
                                LSClient.this.onRequestComplete();
                            }
                            return true;
                        }
                    }
                }

                return false;
            }
        });
    }

    protected static boolean holderListContainsListener( List<ResponseListenersSet.ListenerHolder> listenerList,OnResponseListener theListener)
    {
        if(theListener == null)
        {
            return false;
        }

        boolean listContainsListener = false;
        for(ResponseListenersSet.ListenerHolder holder:listenerList)
        {
            if(theListener.equals(holder.getListener()))
            {
                listContainsListener = true;
            }
        }

        return listContainsListener;
    }

    // Manage request progress

    /**
     * @return number of requests pending
     */
    public int getActiveRequestsCount() {
        synchronized (listeners) {
            return this.listeners.registeredRequestCount();
        }
    }

    public RequestProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(RequestProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    private void onNewRequestStarted() {
        if (this.progressListener != null) {

            int requestCount = this.getActiveRequestsCount();
            this.progressListener.onRequestStarted(this, requestCount);

        }
    }

    private void onRequestComplete() {
        if (this.progressListener != null) {
            int requestCount = this.getActiveRequestsCount();
            this.progressListener.onRequestFinished(this, requestCount);
        }
    }

    private class OnResponseAuthListenerDecorator implements OnResponseListener {

        private OnResponseListener listener;

        OnResponseAuthListenerDecorator(OnResponseListener listener) {
            this.listener = listener;
        }

        @Override
        public void onResponseReceived(ResponseData data, Object tag) {
            if (listener != null) {
                this.listener.onResponseReceived(data, tag);
            }
        }

        @Override
        public void onError(ResponseData data, Object tag) {
            if (VolleyResponseUtils.isAuthError(data.getError())) {
                loginManager.onLoginRestoreFailed();
            }
            if (listener != null) {
                this.listener.onError(data, tag);
            }
        }

        @Override
        public void onCancel(Object tag) {
            if (listener != null) {
                this.listener.onCancel(tag);
            }
        }
    }

    private class RestoreLoginAttemptTask {

        private final BaseRequest request;
        private final OnResponseListener listener;
        private final Object tag;
        private final ResponseData originData;

        RestoreLoginAttemptTask(BaseRequest request, OnResponseListener listener, Object tag, ResponseData originData) {
            this.request = request;
            this.listener = listener;
            this.tag = tag;
            this.originData = originData;
        }

        public void execute() {
            new Thread() {
                @Override
                public void run() {

                    boolean restored = loginManager.restoreLoginData(queue);
                    if (restored) {
                        performRequestNoLoginRestore(request, tag, new OnResponseAuthListenerDecorator(listener), false);
                    } else {
                        listener.onError(originData, tag);
                    }
                }
            }.start();
        }
    }

}