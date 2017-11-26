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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //==========TAG==========//
    private final String TAG = "MAIN ACTIVITY";
    //==========END TAG==========//

    //==========FIREBASE==========//
    FirebaseAuth mAuth;
    FirebaseDatabase mData;
    DatabaseReference mUsers;
    //==========END FIREBASE==========//

    //==========SECTION PAGE ADAPTER==========//
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    //==========END SECTION PAGE ADAPTER==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mAuth != null) {
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mAuth.getCurrentUser().getUid()).child("query");
            mUsers.removeValue();
        }else {
            mAuth = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mAuth.getCurrentUser().getUid()).child("query");
            mUsers.removeValue();
        }

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setUpViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUsers.removeValue();
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
