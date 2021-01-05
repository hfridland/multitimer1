package com.hfridland.multitimernew.ui.about;

import com.hfridland.multitimernew.common.SingleFragmentActivity;

import androidx.fragment.app.Fragment;

public class AboutActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
        return AboutFragment.newInstance();
    }
}
