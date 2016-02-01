package com.ls.templateproject.ui.base.resultfix;

import com.ls.http.base.BaseRequest;
import com.ls.templateproject.ui.base.callbacks.CallbackFragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.List;

/**
 * Created on 11.12.2015.
 * Provides workaround for nested fragment's activity execution for result issue
 * NOTE: parent activity must be inherited from corresponding {@link ActivityResultFixActivity}
 */
public class ActivityResultFixFragment extends CallbackFragment {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager manager = getChildFragmentManager();
        List<Fragment> fragements = manager.getFragments();
        if (fragements != null) {
            for (Fragment fragment : fragements) {
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            Log.e(BaseRequest.class.getName(), "No activity context available to perform execution for result");
        }
    }
}
