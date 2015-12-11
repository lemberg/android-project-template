package com.ls.templateproject.ui.resultfix;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import java.util.List;

/**
 * Created on 11.12.2015.
 * Provides workaround for nested fragment's activity execution for result issue
 * NOTE:child fragments must be inherited from corresponding {@link ActivityResultFixFragment}
 */
public class ActivityResultFixActivity extends ActionBarActivity
{
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> fragements = manager.getFragments();
        if(fragements != null) {
            for (Fragment fragment : fragements) {
                if(fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
