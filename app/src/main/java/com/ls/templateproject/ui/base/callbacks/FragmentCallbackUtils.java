package com.ls.templateproject.ui.base.callbacks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

/**
 * Created on 14.12.2015.
 */
class FragmentCallbackUtils {

    static void notifyAllFragmentsAboutCallback(FragmentManager fragmentManager, Fragment fragment, String action, Object data) {
        List<Fragment> fragements = fragmentManager.getFragments();
        if (fragements != null) {
            for (Fragment aFragment : fragements) {
                if (aFragment != null && aFragment instanceof OnFragmentCallbackListener) {
                    ((OnFragmentCallbackListener) aFragment).onFragmentCallback(fragment, action, data);
                }
            }
        }
    }
}
