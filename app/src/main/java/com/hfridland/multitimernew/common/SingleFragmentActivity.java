package com.hfridland.multitimernew.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hfridland.multitimernew.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_container);

        if (savedInstanceState == null) {
            changeFragment(getFragment());
        }
    }

    protected abstract Fragment getFragment();

    public void changeFragment(Fragment fragment) {
        boolean addToBackStack = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) != null;

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        transaction.commit();
    }

}
