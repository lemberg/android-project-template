package com.ls.templateproject.model.drupal;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.ls.drupal.DrupalClient;
import com.ls.http.base.BaseRequest;
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
 * Class is unused and represented just as sample you may use drupal-based model if you like it
 */
public class DrupalModel {

    private static DrupalModel instance;

    @NonNull
    public static DrupalModel instance(Context theContext) {
        if (instance == null) {
            instance = new DrupalModel(theContext);
        }

        return instance;
    }


    @NonNull
    public static DrupalModel instance() {
        if (instance == null) {
            throw new IllegalStateException("Called method on uninitialized model");
        }

        return instance;
    }

    @NonNull
    private final DrupalClient client;

    @NonNull
    private final LoginManager loginManager;

    @NonNull
    private final CookieStore cookieStore;

    @NonNull
    private RequestQueue queue;

    //Managers
    @NonNull
    private final StubItemManager stubManager;

    @NonNull
    public LSClient getClient() {
        return client;
    }

    @NonNull
    public RequestQueue getQueue() {
        return queue;
    }

    @NonNull
    public LoginManager getLoginManager() {
        return loginManager;
    }

    @NonNull
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    @NonNull
    public StubItemManager getStubManager() {
        return stubManager;
    }

    /**
     * NOTE: login is performed in synchroneus way so you must never call it from UI thread.
     */
    public ResponseData performLogin(String userName, String password) {
        return this.loginManager.login(userName, password, queue);
    }


    private DrupalModel(@NonNull final Context context) {
        loginManager = new LoginManager();
        cookieStore = new HURLCookieStore(context);
        queue = createNewQueue(context);
        client = new DrupalClient(ApplicationConfig.BASE_URL, queue, BaseRequest.RequestFormat.JSON,
                loginManager);

        stubManager = new StubItemManager(client);
    }

    //Initialization

    @NonNull
    private RequestQueue createNewQueue(@NonNull final Context context) {
        CookieManager cmrCookieMan = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cmrCookieMan);

        final VolleyHelperFactory.IVolleyHelper helper = VolleyHelperFactory.newHelper();
        return newRequestQueue(context, helper.createHttpStack(context));
    }

    /**
     * volley's default implementation uses internal cache only so we've implemented our, allowing
     * external cache usage.
     */
    @NonNull
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
