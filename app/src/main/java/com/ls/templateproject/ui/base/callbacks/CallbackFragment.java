package com.ls.templateproject.ui.base.callbacks;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created on 11.12.2015.
 * Provides workaround for nested fragment's callbacks listening issue
 * NOTE: parent activity must be inherited from corresponding {@link FragmentCallbackActivity}
 */
public class CallbackFragment extends Fragment implements OnFragmentCallbackListener {

    private OnFragmentCallbackListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentCallbackListener) {
            mListener = (OnFragmentCallbackListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Use this method to notify all fragments about event
     */
    public void notifyFragmentAction(String action, Object data) {
        if (mListener != null) {
            mListener.onFragmentCallback(this, action, data);
        }
    }

    @Override
    public void onFragmentCallback(Fragment fragment, String action, Object data) {
        FragmentManager manager = getChildFragmentManager();
        FragmentCallbackUtils.notifyAllFragmentsAboutCallback(manager, fragment, action, data);
    }
}
