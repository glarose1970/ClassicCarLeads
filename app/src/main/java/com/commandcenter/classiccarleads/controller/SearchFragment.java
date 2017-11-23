package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.adapter.ListingViewHolder;
import com.commandcenter.classiccarleads.helper.SearchListingHelper;
import com.commandcenter.classiccarleads.model.Dealer;
import com.commandcenter.classiccarleads.model.Listing;
import com.firebase.ui.database.ChangeEventListener;
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
import java.util.Calendar;
import java.util.List;

/**
 * Created by Command Center on 11/22/2017.
 */

public class SearchFragment extends Fragment implements View.OnClickListener{

    //==========TAG==========//
    private final String TAG = "SEARCH FRAGMENT";
    //==========END TAG==========//

    //==========SPINNER==========//
    private Spinner spinner_year;
    private List<Integer> years;
    private ArrayAdapter<Integer> yearAdapter;
    //==========END SPINNER==========//

    //==========BUTTONS==========//
    private Button btn_search, btn_cancel;
    //==========END BUTTONS==========//

    //==========EDIT TEXT==========//
    private EditText et_make, et_model, et_location;
    private TextView tv_results;
    //==========END EDIT TEXT==========//

    //==========SEARCHLISTINGHELPER==========//
    private SearchListingHelper searchHelper;
    //==========END SEARCHLISTINGHELPER==========//

    //==========FIREBASE==========//
    FirebaseAuth mAuth;
    FirebaseUser mCurUser;
    FirebaseDatabase mData;
    DatabaseReference mUsers;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    //==========RECYCLERVIEW==========//
    private RecyclerView listingRecView;
    //private ListingViewHolder listingViewHolder;
    //==========END RECYCLERVIEW==========//

    ProgressDialog pDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_fragment, container, false);

        if (mAuth != null) {

            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid());
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid());
        }

        years = new ArrayList<>();
        spinner_year = view.findViewById(R.id.search_fragment_spinner_year);
        view.findViewById(R.id.search_fragment_btn_cancel).setOnClickListener(SearchFragment.this);
        view.findViewById(R.id.search_fragment_btn_search).setOnClickListener(SearchFragment.this);
        et_make = view.findViewById(R.id.search_fragment_et_make);
        et_model = view.findViewById(R.id.search_fragment_et_model);
        et_location = view.findViewById(R.id.search_fragment_et_location);
        tv_results = view.findViewById(R.id.search_fragment_totalResults);

        GeneratYears();
        yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        spinner_year.setAdapter(yearAdapter);
        pDialog = new ProgressDialog(getActivity());
        listingRecView = view.findViewById(R.id.search_fragment_listing_recView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(false);
        listingRecView.setHasFixedSize(true);
        listingRecView.setLayoutManager(layoutManager);

        Query query = mUsers;
        listingAdapter = new FirebaseRecyclerAdapter<Listing, ListingViewHolder>(Listing.class, R.layout.listing_single_row, ListingViewHolder.class, query) {

            @Override
            protected void populateViewHolder(ListingViewHolder viewHolder, Listing listing, int position) {
                Picasso.with(getContext()).load(listing.getImg_url()).placeholder(R.drawable.ic_warning).into(viewHolder.iv_listingImg);
                viewHolder.tv_listingID.setText(listing.getListingID());
                viewHolder.tv_title.setText(listing.getTitle());
                viewHolder.tv_price.setText(listing.getPrice());
                viewHolder.tv_desc.setText(listing.getDesc());

            }

            @Override
            public void onChildChanged(EventType type, DataSnapshot snapshot, int index, int oldIndex) {
                super.onChildChanged(type, snapshot, index, oldIndex);

                listingRecView.scrollToPosition(index);
            }
        };
        listingRecView.setAdapter(listingAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pDialog.dismiss();
        listingAdapter.cleanup();
        mUsers.removeValue();
    }

    private void Init() {



    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_fragment_btn_search:
                if (TextUtils.isEmpty(et_make.getText().toString()) || TextUtils.isEmpty(et_model.getText().toString()) || TextUtils.isEmpty(et_location.getText().toString())) {

                }else {
                    String make = et_make.getText().toString();
                    String model = et_model.getText().toString();
                    String loc = et_location.getText().toString();
                    String year = spinner_year.getSelectedItem().toString();
                    new DoSearch().execute(year, make, model, loc);
                }
                break;
            case R.id.search_fragment_btn_cancel:

                break;
        }
    }

    private void GeneratYears() {

        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1920; i <= curYear; i++) {
            years.add(i);
        }
    }

    //==========BACKGROUND ASYNC CLASS==========//
    private class DoSearch extends AsyncTask<String, Integer, String> {


        String totalListing = "";
        String imgUrl = "";
        Dealer dealer;
        @Override
        protected String doInBackground(String... strings) {

            String base_url = "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&private=false&state=" + strings[3] ;
            try {
                Document mainDoc = Jsoup.connect(base_url).get();
                Elements listingNodes = mainDoc.getElementsByClass("search-result-item");
                int total = 0;
                if (listingNodes != null) {
                    for (Element node : listingNodes) {
                        //get title, imgLink and id here.
                        Element link = listingNodes.select("a").first();
                        String linkHref = link.attr("href");
                        String imgLink = node.select("[src]").attr("src");
                        String title = node.getElementsByClass("item-ymm").text();
                        String id = node.getElementsByClass("item-stock-no").text();
                        String desc = node.getElementsByClass("item-desc").text();
                        String price = node.getElementsByClass("item-price").text();

                        //load the main listing page to extract the listing details
                        Document nodeDoc = Jsoup.connect("https://classiccars.com" + linkHref).get();
                        //listing details
                        Elements ulNode = nodeDoc.select("div.vehicle-details > ul");
                        Elements liNode = ulNode.select("li");

                        //dealer info
                        Element dealerInfoNode = nodeDoc.getElementById("seller-info");
                        Elements dealerLi = dealerInfoNode.select("li");
                        if (dealerInfoNode != null) {
                            if (dealerLi.size() >= 3) {
                                String dealerName = dealerLi.get(0).text();
                                String dealerWeb = dealerLi.get(dealerLi.size() - 1).select("a").attr("href");
                                dealer = new Dealer(dealerName, dealerWeb);

                                Listing listing = new Listing(dealer, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, strings[3]);
                                mUsers.child(id).setValue(listing);
                            }
                        }else {
                            Listing listing = new Listing(null, id, imgLink, title, strings[2], strings[1], strings[0], price, desc, strings[3]);
                            mUsers.child(id).setValue(listing);
                        }



                        publishProgress((total * 100) / listingNodes.size());
                        total++;

                    }
                }



                //totalListing = mainDoc.getElementsByClass("search-result-info").text();
                String[] values = totalListing.split(" ");
                totalListing = imgUrl;

            } catch (IOException e) {
                e.printStackTrace();
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

            pDialog.dismiss();

          //  tv_results.setText(s.toString());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            pDialog.setProgress(values[0]);
        }
    }
    //==========END BACKGROUND ASYNC CLASS==========//
}
