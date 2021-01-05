package com.hfridland.multitimernew.ui.timers;

import android.content.Intent;

import com.hfridland.multitimernew.common.SingleFragmentActivity;

import androidx.fragment.app.Fragment;

public class TimersActivity extends SingleFragmentActivity {

    @Override
    protected Fragment getFragment() {
        return TimersFragment.newInstance();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
