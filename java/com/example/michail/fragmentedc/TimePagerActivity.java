package com.example.michail.fragmentedc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import java.util.List;
import java.util.UUID;

public class TimePagerActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Time> mTimes;
    private static final String EXTRA_TIME_ID = "com.example.michail.fragmentedc.time_id";


    public static Intent newIntent(Context packageContext, UUID timeId) {
        Intent intent = new Intent(packageContext, TimePagerActivity.class);
        intent.putExtra(EXTRA_TIME_ID, timeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pager);

        UUID timeId = (UUID) getIntent().getSerializableExtra(EXTRA_TIME_ID);

        mViewPager = findViewById(R.id.time_view_pager);
        mViewPager.setClipToPadding(true);
        mViewPager.setPadding(60, 16, 60, 16);

        mTimes = TimeLab.get(getBaseContext()).getTimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Time time = mTimes.get(position);
                return TimeFragment.newInstance(time.getId());
            }

            @Override
            public int getCount() {
                return mTimes.size();
            }
        });
        for (int i = 0; i < mTimes.size(); i++) {
            if (mTimes.get(i).getId().equals(timeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
