package com.commandcenter.classiccarleads.controller;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.adapter.SectionsPageAdapter;

public class MainActivity extends AppCompatActivity {

    //==========TAG==========//
    private final String TAG = "MAIN ACTIVITY";
    //==========END TAG==========//

    //==========SECTION PAGE ADAPTER==========//
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    //==========END SECTION PAGE ADAPTER==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setUpViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    //==========SETUP VIEW PAGER==========//
    private void setUpViewPager(ViewPager viewPager) {

        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new SearchFragment(), "Search");
        adapter.AddFragment(new FeaturedFragment(), "Featured");
        adapter.AddFragment(new SavedSearchFragment(), "Saved");

        viewPager.setAdapter(adapter);
    }
    //==========END SETUP VIEW PAGER==========//
}
