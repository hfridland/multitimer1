package com.hfridland.multitimernew.ui.alarm;

import android.os.Bundle;
import android.view.WindowManager;

import com.hfridland.multitimernew.common.SingleFragmentActivity;

import androidx.fragment.app.Fragment;

public class AlarmActivity extends SingleFragmentActivity {
    @Override
    protected Fragment getFragment() {
        return AlarmFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    }
}
