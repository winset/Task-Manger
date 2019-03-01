package com.example.michail.fragmentedc;

import android.support.v4.app.Fragment;

public class TimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new TimeListFragment();
    }
}
