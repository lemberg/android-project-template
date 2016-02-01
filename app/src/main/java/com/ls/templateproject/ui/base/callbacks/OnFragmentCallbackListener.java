package com.ls.templateproject.ui.base.callbacks;

import android.support.v4.app.Fragment;

/**
 * Created on 14.12.2015.
 */
interface OnFragmentCallbackListener {

    /**
     * @param action callback action key
     * @param data   callback data: may be list, map or any custom object.
     */
    void onFragmentCallback(Fragment fragment, String action, Object data);
}
