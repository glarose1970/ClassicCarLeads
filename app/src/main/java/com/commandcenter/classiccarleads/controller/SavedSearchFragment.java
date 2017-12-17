package com.commandcenter.classiccarleads.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by Command Center on 11/22/2017.
 */

public class SavedSearchFragment extends Fragment{

    //==========TAG==========//
    private final String TAG = "SAVED SEARCH FRAGMENT";
    //==========END TAG==========//
    
    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mDataRef;
    private FirebaseUser mCurUser;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    private RecyclerView listingRecView;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.saved_search_fragment, container, false);

        Init(view);
        Query query = mDataRef;
        listingAdapter = new FirebaseRecyclerAdapter<Listing, ListingViewHolder>(Listing.class, R.layout.listing_single_row, ListingViewHolder.class, query) {

            @Override
            protected void populateViewHolder(ListingViewHolder viewHolder, final Listing listing, int position) {

                Picasso.with(getContext()).load(listing.getImg_url()).placeholder(R.drawable.ic_warning).into(viewHolder.iv_listingImg);
                viewHolder.tv_title.setText(listing.getTitle());
                viewHolder.tv_price.setText(listing.getPrice());
                viewHolder.tv_desc.setText(listing.getDesc());
                viewHolder.tv_loc.setText((listing.getLocation()));
                viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDataRef.child(listing.getListingID()).removeValue();
                    }
                });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] details = new String[] {listing.getDealerInfo().getDealer_name(), listing.getDealerInfo().getDealer_url(),  listing.getImg_url(), listing.getListingID(), listing.getTitle(), listing.getPrice(), listing.getDesc() };
                        Intent intent = new Intent(getContext(), Single_Listing_View.class);
                        intent.putExtra("details", details);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onChildChanged(EventType type, DataSnapshot snapshot, int index, int oldIndex) {
                super.onChildChanged(type, snapshot, index, oldIndex);

                listingRecView.scrollToPosition(0);
            }
        };
        listingRecView.setAdapter(listingAdapter);
        return view;
    }

    private void Init(View v) {

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid()).child("saved_searches");
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid()).child("saved_searches");
        }

        listingRecView = v.findViewById(R.id.saved_search_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        listingRecView.setHasFixedSize(true);
        listingRecView.setLayoutManager(layoutManager);
        listingRecView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

}
