package com.commandcenter.classiccarleads.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.adapter.ListingViewHolder;
import com.commandcenter.classiccarleads.model.Dealer;
import com.commandcenter.classiccarleads.model.Listing;
import com.commandcenter.classiccarleads.utils.VariableHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
    private DatabaseReference mProfile;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    //==========RECYCLERVIEW==========//
    private RecyclerView featuredRecView;
    //==========END RECYCLERVIEW==========//

    //==========ZIPCODE==========//
    private String Zipcode;
    //==========END ZIPCODE==========//



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
                viewHolder.tv_title.setText(listing.getTitle());
                viewHolder.tv_price.setText(listing.getPrice());
                viewHolder.tv_desc.setText(listing.getDesc());
                viewHolder.btn_remove.setVisibility(View.INVISIBLE);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] details = new String[]{listing.getDealerInfo().getDealer_name(), listing.getDealerInfo().getDealer_url(), listing.getImg_url(), listing.getListingID(), listing.getTitle(), listing.getPrice(), listing.getLong_desc()};
                        Intent intent = new Intent(getContext(), Single_Listing_View.class);
                        intent.putExtra("details", details);
                        startActivity(intent);
                    }
                });

            }


            @Override
            public void onChildChanged(EventType type, DataSnapshot snapshot, int index, int oldIndex) {
                super.onChildChanged(type, snapshot, index, oldIndex);
                featuredRecView.scrollToPosition(0);

            }
        };
        featuredRecView.setAdapter(listingAdapter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();


    }


    private void Init(View view) {

        if (mAuth != null) {
            mAuth = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mProfile = mData.getReference(mCurUser.getUid()).child("profile");
            mDataRef = mData.getReference("featured");

        } else {
            mAuth = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mProfile = mData.getReference(mCurUser.getUid()).child("profile");
            mDataRef = mData.getReference("featured");
        }


        featuredRecView = view.findViewById(R.id. featured_listingRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        featuredRecView.setHasFixedSize(true);
        featuredRecView.setLayoutManager(layoutManager);
        featuredRecView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        //new DoFeaturedSearch().execute("");

    }

    private class DoFeaturedSearch extends AsyncTask<String, Void, String> {

        //==========PROGRESSDIALOG==========//
       // private ProgressDialog pDialog;
        //==========END PROGRESSDIALOG==========//
        @Override
        protected String doInBackground(String... strings) {

            try {
                Document mainDoc = Jsoup.connect("https://classiccars.com/listings/find?auction=false&dealer=true&featured=true&private=false").get();
                String[] items = mainDoc.getElementsByClass("search-result-info").text().split(" ");
                String listingItems = items[8].replace(".", "");
                int listingPageCount = Integer.parseInt(listingItems);
                int count = 0;

                for (int i = 1; i < listingPageCount; i++) {
                    if (count < 7) {

                        Document pageDoc = Jsoup.connect("https://classiccars.com/listings/find?auction=false&dealer=true&featured=true&p=" + i + "&private=false").get();
                        Elements listingElements = pageDoc.getElementsByClass("search-result-item");

                        if (listingElements != null) {
                            for (Element node :listingElements) {
                                //get title, imgLink and id here.
                                Element link = node.select("a").first();
                                String linkHref = link.attr("href");
                                String imgLink = node.select("[src]").attr("src");
                                String title = node.getElementsByClass("item-ymm").text();
                                String id = node.getElementsByClass("item-stock-no").text();
                                String desc = node.getElementsByClass("item-desc").text();
                                String price = node.getElementsByClass("item-price").text();

                                //load the main listing page to extract the listing details
                                Document nodeDoc = Jsoup.connect("https://classiccars.com" + linkHref).get();
                                String long_desc = nodeDoc.getElementsByClass("vehicle-description").select("p").text();

                                //listing details
                                Elements ulNode = nodeDoc.select("div.vehicle-details > ul");
                                Elements liNode = ulNode.select("li");
                                String year = liNode.get(3).getElementsByClass("detail-value").text();
                                String make = liNode.get(4).getElementsByClass("detail-value").text();
                                String model = liNode.get(5).getElementsByClass("detail-value").text();
                                //dealer info
                                Element dealerInfoNode = nodeDoc.getElementById("seller-info");
                                Elements dealerLi = dealerInfoNode.select("li");

                                if (dealerInfoNode != null) {
                                    if (dealerLi.size() >= 3) {
                                        String dealerName = dealerLi.get(0).text();
                                        String dealerWeb = dealerLi.get(dealerLi.size() - 1).select("a").attr("href");
                                        Dealer dealer = new Dealer(dealerName, dealerWeb);

                                        Listing listing = new Listing(dealer, id, imgLink, title, make, model, year, price, desc, long_desc, strings[0]);
                                        mDataRef.child(id).setValue(listing);
                                    }
                                }else {
                                    Listing listing = new Listing(null, id, imgLink, title, make, model, year, price, desc, long_desc, strings[3]);
                                    mDataRef.child(id).setValue(listing);
                                }
                            }
                        }
                        count++;
                      } else {
                        //  pDialog.dismiss();
                        return null;
                    }
                    }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  pDialog = new ProgressDialog(getActivity());
            pDialog.setTitle("Loading Featured Listings");
            pDialog.setMessage("Please wait while the Featured Listings are Loaded...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // pDialog.dismiss();
        }
    }
}
