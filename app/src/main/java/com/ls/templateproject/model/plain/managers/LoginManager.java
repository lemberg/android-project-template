package com.ls.templateproject.model.plain.managers;

import com.android.volley.RequestQueue;
import com.ls.templateproject.ApplicationConfig;
import com.ls.http.base.BaseRequest;
import com.ls.http.base.RequestConfig;
import com.ls.http.base.ResponseData;
import com.ls.http.base.login.ILoginManager;

/**
 * Created on 22.05.2015.
 */
public class LoginManager implements ILoginManager {

    @Override
    public ResponseData login(String userName, String password, RequestQueue queue) {
        RequestConfig config = new RequestConfig();
        config.setResponseFormat(BaseRequest.ResponseFormat.JSON);
//        Use to add custom response class to be parsed;
//        config.setResponseClassSpecifier(SomeClass.class);
        BaseRequest loginRequest = new BaseRequest(BaseRequest.RequestMethod.POST, ApplicationConfig.LOGIN_URL,config);
        loginRequest.addPostParameter("username",userName);
        loginRequest.addGetParameter("password",password);
        ResponseData loginResponseData = loginRequest.performRequest(true, queue);
        return loginResponseData;
    }

    @Override
    public boolean shouldRestoreLogin() {
        //return true if login session restoring with no user interaction is required during runtime (e.g - it can be timed out but you would like to restore it with no user interaction)
        return false;
    }

    @Override
    public boolean canRestoreLogin() {
        //return true if you are able to restore login without user interaction
        return false;
    }

    @Override
    public void applyLoginDataToRequest(BaseRequest request) {
        //apply some session-dependent data to request
    }

    @Override
    public boolean restoreLoginData(RequestQueue queue) {
        return false;
    }

    @Override
    public void onLoginRestoreFailed() {
        //handle background login restore failure here
    }

    @Override
    public Object logout(RequestQueue queue) {
        return null;
    }
}
