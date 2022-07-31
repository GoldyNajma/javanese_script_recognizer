package com.example.javanesescriptrecognizer.base;

import android.os.Bundle;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.javanesescriptrecognizer.R;

public abstract class BaseActivity extends FragmentActivity implements BaseFragmentListener {
    protected BaseFragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
        initializeFragment();
    }

    protected abstract void initializeFragment();

    protected abstract void initializeView();

    protected void setCurrentFragment(BaseFragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null && addToBackStack) {
            fragmentTransaction.addToBackStack(currentFragment.getTitle());
        }

        fragmentTransaction.replace(R.id.flFragmentContainer, fragment, fragment.getTitle());
        fragmentTransaction.commit();

        this.currentFragment = fragment;
    }
}