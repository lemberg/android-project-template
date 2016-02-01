package com.ls.templateproject.ui.base.callbacks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

/**
 * Created on 11.12.2015.
 * Provides workaround for nested fragment's callbacks listening issue
 * NOTE:child fragments must be inherited from corresponding {@link CallbackFragment} to trigger/handle events
 */
public class FragmentCallbackActivity extends ActionBarActivity implements OnFragmentCallbackListener {

    @Override
    public void onFragmentCallback(Fragment fragment, String action, Object data) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentCallbackUtils.notifyAllFragmentsAboutCallback(manager, fragment, action, data);
    }
}
