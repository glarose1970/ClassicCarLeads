package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.adapter.ListingViewHolder;
import com.commandcenter.classiccarleads.helper.SearchListingHelper;
import com.commandcenter.classiccarleads.model.Dealer;
import com.commandcenter.classiccarleads.model.Listing;
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

public class Search_Recview_Activity extends AppCompatActivity {

    //==========TAG==========//
    private final String TAG = "SEARCH ACTIVITY";
    //==========END TAG==========//


    //==========SEARCHLISTINGHELPER==========//
    private SearchListingHelper searchHelper;
    //==========END SEARCHLISTINGHELPER==========//

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseUser mCurUser;
    private FirebaseDatabase mData;
    private DatabaseReference mUsers;
    private DatabaseReference mEventLog;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    //==========RECYCLERVIEW==========//
    private RecyclerView listingRecView;
    //==========END RECYCLERVIEW==========//

    String[] queryDetails;
    List<String> featured_id_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__recview);

        queryDetails = getIntent().getStringArrayExtra("query");

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid());
            mEventLog = mData.getReference().child("event_log");
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid());
            mEventLog = mData.getReference().child("event_log");
        }

        listingRecView = findViewById(R.id.search_activity_Recview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        listingRecView.setHasFixedSize(true);
        listingRecView.setLayoutManager(layoutManager);
        listingRecView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        Load_Featured();
        new DoSearch().execute(queryDetails[0], queryDetails[1], queryDetails[2], queryDetails[3]);

        Query query = mUsers.child("query");
        listingAdapter = new FirebaseRecyclerAdapter<Listing, ListingViewHolder>(Listing.class, R.layout.listing_single_row, ListingViewHolder.class, query) {

            @Override
            protected void populateViewHolder(ListingViewHolder viewHolder, final Listing listing, int position) {

                Picasso.with(Search_Recview_Activity.this).load(listing.getImg_url()).placeholder(R.drawable.ic_warning).into(viewHolder.iv_listingImg);
                viewHolder.tv_title.setText(listing.getTitle());
                viewHolder.tv_price.setText(listing.getPrice());
                viewHolder.tv_desc.setText(listing.getDesc());
                viewHolder.tv_loc.setText(listing.getLocation());
                viewHolder.btn_remove.setVisibility(View.INVISIBLE);
                if (listing.isFeatured()) {
                    viewHolder.main_background.setBackgroundResource(R.color.featured_listing_background);

                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String[] details = new String[] { listing.getDealerInfo().getDealer_name(), listing.getDealerInfo().getDealer_url(), listing.getImg_url(), listing.getListingID(), listing.getTitle(), listing.getPrice(), listing.getLong_desc(), listing.getLocation()};
                        Intent intent = new Intent(Search_Recview_Activity.this, Single_Listing_View.class);
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
    }

    private void Load_Featured() {

        FirebaseDatabase featuredData = FirebaseDatabase.getInstance();
        DatabaseReference featuredDataRef =featuredData.getReference().getRef().child("featured");
        featuredDataRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child : children) {
                    Listing listing = child.getValue(Listing.class);
                    featured_id_list.add(listing.getListingID());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //==========BACKGROUND ASYNC CLASS==========//
    private class DoSearch extends AsyncTask<String, Integer, String> {

        ProgressDialog pDialog = new ProgressDialog(Search_Recview_Activity.this);

        String totalListing = "";
        String imgUrl = "";
        String count = "";
        Dealer dealer;
        int listingCount = 0;

        @Override
        protected String doInBackground(String... strings) {
            String errorID = "";
            if (!strings[3].equalsIgnoreCase("")) {
                String base_url = "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&private=false&state=" + strings[3];
                try {
                    Document mainDoc = Jsoup.connect(base_url).get();
                    String[] items = mainDoc.getElementsByClass("search-result-info").text().split(" ");
                    listingCount = Integer.parseInt(items[0]);
                    count = String.valueOf(listingCount);
                    int total = 0;
                    for (int i = 1; i < listingCount - 1; i++) {
                        String pageUrl =  "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&p=" + i + "&private=false&state=" + strings[3];
                        mainDoc = Jsoup.connect(pageUrl).get();
                        Elements listingNodes = mainDoc.getElementsByClass("search-result-item");
                        if (listingNodes != null) {
                            for (Element node : listingNodes) {
                                //get title, imgLink and id here.
                               // String link = node.getElementById("listing-content").attr("data-listing-url");
                                Element link = node.select("a").first();
                                String linkHref = link.attr("href");
                                String imgLink = node.select("[src]").attr("src");
                                String title = node.getElementsByClass("item-ymm").text();
                                String id = node.getElementsByClass("item-stock-no").text().replace("(", "").replace(")", "");
                                errorID = id;
                                String desc = node.getElementsByClass("item-desc").text();
                                String price = node.getElementsByClass("item-price").text();

                                //load the main listing page to extract the listing details
                                Document nodeDoc = Jsoup.connect("https://classiccars.com" + linkHref).get();
                                //listing details
                                Elements ulNode = nodeDoc.select("div.vehicle-details > ul");
                                Elements liNode = ulNode.select("li");

                                String long_desc = nodeDoc.getElementsByClass("vehicle-description").select("p").get(0).text();
                                String loc = liNode.get(2).getElementsByClass("detail-value").text();
                                //dealer info
                                Element dealerInfoNode = nodeDoc.getElementById("seller-info");
                                Elements dealerLi = dealerInfoNode.select("li");
                                if (dealerInfoNode != null) {
                                    if (dealerLi.size() >= 3) {
                                        String dealerName = dealerLi.get(0).text();
                                        String dealerWeb = dealerLi.get(dealerLi.size() - 1).select("a").attr("href");
                                        dealer = new Dealer(dealerName, dealerWeb);
                                        if (isFeatured(id)){
                                            Listing listing = new Listing(dealer, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, true);
                                            mUsers.child("query").child(id).setValue(listing);
                                        }else {
                                            Listing listing = new Listing(dealer, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, false);
                                            mUsers.child("query").child(id).setValue(listing);
                                        }
                                    }
                                }else {
                                    if (isFeatured(id)){
                                        Listing listing = new Listing(null, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, true);
                                        mUsers.child("query").child(id).setValue(listing);
                                    }else {
                                        Listing listing = new Listing(null, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, false);
                                        mUsers.child("query").child(id).setValue(listing);
                                    }
                                }
                                publishProgress((total * 100) / listingCount);
                                total++;
                            }
                        }

                    }
                    pDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    mEventLog.child("error_log").child(mCurUser.getUid()).child(errorID).setValue(e.getMessage());
                }
            }else {
                String base_url = "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&private=false";
                try {
                    listingCount = 0;
                    Document mainDoc = Jsoup.connect(base_url).get();
                    String[] items = mainDoc.getElementsByClass("search-result-info").text().split(" ");
                    listingCount = Integer.parseInt(items[0]);
                    count = String.valueOf(listingCount);
                    int total = 0;
                    for (int i = 1; i < listingCount - 1; i++) {
                        String pageUrl = "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&p=" + i + "&private=false";
                        try {
                            mainDoc = Jsoup.connect(pageUrl).get();
                            Elements listingNodes = mainDoc.getElementsByClass("search-result-item");
                            if (listingNodes != null) {
                                for (Element node : listingNodes) {
                                    //get title, imgLink and id here.
                                    Element link = node.select("a").first();
                                    String linkHref = link.attr("href");
                                    String imgLink = node.select("[src]").attr("src");
                                    String title = node.getElementsByClass("item-ymm").text();
                                    String id = node.getElementsByClass("item-stock-no").text();
                                    errorID = id;
                                    String desc = node.getElementsByClass("item-desc").text();
                                    String price = node.getElementsByClass("item-price").text();

                                    //load the main listing page to extract the listing details
                                    Document nodeDoc = Jsoup.connect("https://classiccars.com" + linkHref).get();
                                    //listing details
                                    Elements ulNode = nodeDoc.select("div.vehicle-details > ul");
                                    Elements liNode = ulNode.select("li");

                                    String long_desc = nodeDoc.getElementsByClass("vehicle-description").select("p").get(0).text();
                                    String loc = liNode.get(2).getElementsByClass("detail-value").text();
                                    //dealer info
                                    Element dealerInfoNode = nodeDoc.getElementById("seller-info");
                                    Elements dealerLi = dealerInfoNode.select("li");
                                    if (dealerInfoNode != null) {
                                        if (dealerLi.size() >= 3) {
                                            String dealerName = dealerLi.get(0).text();
                                            String dealerWeb = dealerLi.get(dealerLi.size() - 1).select("a").attr("href");
                                            dealer = new Dealer(dealerName, dealerWeb);
                                            if (isFeatured(id)){
                                                Listing listing = new Listing(dealer, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, true);
                                                mUsers.child("query").child(id).setValue(listing);
                                            }else {
                                                Listing listing = new Listing(dealer, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, false);
                                                mUsers.child("query").child(id).setValue(listing);
                                            }

                                        }
                                    } else {
                                        if (isFeatured(id)){
                                            Listing listing = new Listing(null, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, true);
                                            mUsers.child("query").child(id).setValue(listing);
                                        }else {
                                            Listing listing = new Listing(null, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, long_desc, loc, false);
                                            mUsers.child("query").child(id).setValue(listing);
                                        }
                                    }
                                    publishProgress((total * 100) / listingCount);
                                    total++;
                                }
                            }
                        }catch (Exception e){
                            mEventLog.child("error_log").child(mCurUser.getUid()).child(errorID).setValue(e.getMessage());
                            continue;
                        }
                    }
                    pDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    mEventLog.child("error_log").child(mCurUser.getUid()).child(errorID).setValue(e.getMessage());

                }
            }
            return totalListing;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.setTitle("Searching...");
            pDialog.setMessage("The more results the longer this will take, Please wait while we gather the results...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            hideProgressDialog();
            if (listingCount == 0) {
                Toast.makeText(Search_Recview_Activity.this, "0 Results Found!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Search_Recview_Activity.this, MainActivity.class);
                startActivity(intent);
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            pDialog.setProgress(values[0]);

            if(pDialog.getProgress() == pDialog.getMax()){
                pDialog.dismiss();
            }
        }

        private boolean isFeatured(String id) {

            if (featured_id_list.size() > 0) {
               if (featured_id_list.contains(id)) {
                   return true;
               }else
                   return false;
            }else {
                return false;
            }
        }

        private void hideProgressDialog() {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage(), ex);
            }
        }

        private String getListingPageCount(String url) {
            String pageCount = "";
            try {
                Document mainDoc = Jsoup.connect(url).get();
                String[] items = mainDoc.getElementsByClass("search-result-info").text().split(" ");
                pageCount = items[0];
            } catch (IOException e) {
                e.printStackTrace();
            }

            return pageCount;
        }
    }
    //==========END BACKGROUND ASYNC CLASS==========//
}
