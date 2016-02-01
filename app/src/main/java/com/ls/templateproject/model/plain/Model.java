package com.ls.templateproject.model.plain;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.ls.http.base.ResponseData;
import com.ls.http.base.client.LSClient;
import com.ls.templateproject.ApplicationConfig;
import com.ls.templateproject.model.HURLCookieStore;
import com.ls.templateproject.model.plain.managers.LoginManager;
import com.ls.templateproject.model.plain.managers.StubItemManager;
import com.ls.util.internal.VolleyHelperFactory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;

/**
 * Created on 21.05.2015.
 */
public class Model {

    private static Model instance;

    public static Model instance(Context theContext) {
        if (instance == null) {
            instance = new Model(theContext);
        }

        return instance;
    }


    public static Model instance() {
        if (instance == null) {
            throw new IllegalStateException("Called method on uninitialized model");
        }

        return instance;
    }

    private LSClient client;
    private LoginManager loginManager;
    private CookieStore cookieStore;
    private RequestQueue queue;

    //Managers
    private StubItemManager stubManager;


    public LSClient getClient() {
        return client;
    }

    public RequestQueue getQueue() {
        return queue;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public StubItemManager getStubManager() {
        return stubManager;
    }

    /**
     * NOTE: login is performed in synchroneus way so you must never call it from UI thread.
     */
    public ResponseData performLogin(String userName, String password) {
        return this.loginManager.login(userName, password, queue);
    }


    private Model(Context context) {
        loginManager = new LoginManager();
        queue = createNewQueue(context);
        client = new LSClient.Builder(context)
                .setRequestQueue(queue)
                .setLoginManager(loginManager)
                .build();

        stubManager = new StubItemManager(client);
    }

    //Initialization

    private RequestQueue createNewQueue(Context context) {
        cookieStore = new HURLCookieStore(context);
        CookieManager cmrCookieMan = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cmrCookieMan);

        final VolleyHelperFactory.IVolleyHelper helper = VolleyHelperFactory.newHelper();
        return newRequestQueue(context, helper.createHttpStack(context));
    }

    /**
     * volley's default implementation uses internal cache only so we've implemented our, allowing
     * external cache usage.
     */
    private static RequestQueue newRequestQueue(@NonNull final Context context,
            @Nullable HttpStack stack) {

        final VolleyHelperFactory.IVolleyHelper helper = VolleyHelperFactory.newHelper();
        final File cacheDir = helper.getBestCacheDir(context);

        if (stack == null) {
            stack = helper.createHttpStack(context);
        }

        final Network network = new BasicNetwork(stack);
        final RequestQueue queue = new RequestQueue(
                new DiskBasedCache(cacheDir, ApplicationConfig.CACHE_DISK_USAGE_BYTES), network, 1);
        queue.start();
        return queue;
    }
}
