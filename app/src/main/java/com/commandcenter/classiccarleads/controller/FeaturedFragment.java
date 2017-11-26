package com.commandcenter.classiccarleads.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.adapter.ListingViewHolder;
import com.commandcenter.classiccarleads.model.Listing;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Command Center on 11/22/2017.
 */

public class FeaturedFragment extends Fragment {

    //==========TAG==========//
    private final String TAG = "FEATURED FRAGMENT";
    //==========END TAG==========//

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mDataRef;
    private FirebaseUser mCurUser;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    //==========RECYCLERVIEW==========//
    private RecyclerView featuredRecView;
    //==========END RECYCLERVIEW==========//

    //==========GPS==========//
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double mLatitude;
    private double mLongitude;
    //==========END GPS==========//

    Button btn_test;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.featured_fragment, container, false);

        Init(view);

        Query query = mDataRef;
        listingAdapter = new FirebaseRecyclerAdapter<Listing, ListingViewHolder>(Listing.class, R.layout.listing_single_row, ListingViewHolder.class, query) {

            @Override
            protected void populateViewHolder(ListingViewHolder viewHolder, final Listing listing, int position) {

                Picasso.with(getContext()).load(listing.getImg_url()).placeholder(R.drawable.ic_warning).into(viewHolder.iv_listingImg);
                viewHolder.tv_listingID.setText(listing.getListingID());
                viewHolder.tv_title.setText(listing.getTitle());
                viewHolder.tv_price.setText(listing.getPrice());
                viewHolder.tv_desc.setText(listing.getDesc());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] details = new String[]{listing.getDealerInfo().getDealer_name(), listing.getDealerInfo().getDealer_url(), listing.getImg_url(), listing.getListingID(), listing.getTitle(), listing.getPrice(), listing.getDesc()};
                        Intent intent = new Intent(getContext(), Single_Listing_View.class);
                        intent.putExtra("details", details);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onChildChanged(EventType type, DataSnapshot snapshot, int index, int oldIndex) {
                super.onChildChanged(type, snapshot, index, oldIndex);

                featuredRecView.scrollToPosition(index);
            }
        };
        featuredRecView.setAdapter(listingAdapter);

        return view;
    }

    private void Init(View view) {

        if (mAuth != null) {
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child("featured_dealers");
            mCurUser = mAuth.getCurrentUser();
        } else {
            mAuth = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child("featured_dealers");
            mCurUser = mAuth.getCurrentUser();
        }

        featuredRecView = view.findViewById(R.id. featured_listingRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        featuredRecView.setHasFixedSize(true);
        featuredRecView.setLayoutManager(layoutManager);
        featuredRecView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

    }

    private String getZipCode() {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> adresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            String zip = adresses.get(0).getPostalCode();
            return zip;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private class DoSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }
    }
}
