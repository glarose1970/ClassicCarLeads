package com.commandcenter.classiccarleads.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commandcenter.classiccarleads.R;

/**
 * Created by Command Center on 11/22/2017.
 */

public class FeaturedFragment extends Fragment{

    //==========TAG==========//
    private final String TAG = "FEATURED FRAGMENT";
    //==========END TAG==========//

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.featured_fragment, container, false);

        return view;
    }
}
