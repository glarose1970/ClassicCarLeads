package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.helper.SearchListingHelper;
import com.commandcenter.classiccarleads.model.Dealer;
import com.commandcenter.classiccarleads.model.Listing;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private FirebaseAuth mAuth;
    private FirebaseUser mCurUser;
    private FirebaseDatabase mData;
    private DatabaseReference mUsers;
    private FirebaseRecyclerAdapter listingAdapter;
    //==========END FIREBASE==========//

    //==========RECYCLERVIEW==========//
    private RecyclerView listingRecView;
    //private ListingViewHolder listingViewHolder;
    //==========END RECYCLERVIEW==========//

    LinearLayout controls;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.search_fragment, container, false);

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid()).child("query");
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mUsers = mData.getReference().child(mCurUser.getUid()).child("query");
        }

        years = new ArrayList<>();
        controls = view.findViewById(R.id.search_fragment_layout_controls);
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


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        listingAdapter.cleanup();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_fragment_btn_search:
                if (TextUtils.isEmpty(et_make.getText().toString()) || TextUtils.isEmpty(et_model.getText().toString()) || spinner_year.getSelectedItem().toString() == "") {
                    Toast.makeText(getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                }else {
                     mUsers.removeValue();
                     String make = et_make.getText().toString();
                     String model = et_model.getText().toString();
                     String loc = et_location.getText().toString();
                     String year = spinner_year.getSelectedItem().toString();
                     Intent intent = new Intent(getContext(), Search_Recview_Activity.class);
                     String[] query = new String[] { year, make, model, loc };
                     intent.putExtra("query", query);
                     startActivity(intent);
                    //new DoSearch().execute(year, make, model, loc);
                }
                break;
            case R.id.search_fragment_btn_cancel:
                clearInput(controls);
                break;
        }
    }

    private void GeneratYears() {

        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1920; i <= curYear; i++) {
            years.add(i);
        }
    }

    private void clearInput(LinearLayout controlBox) {

        for (int i = 0; i < controlBox.getChildCount(); i++) {
            View view = controlBox.getChildAt(i);
            if (view instanceof TextInputLayout) {
                View parent = ((TextInputLayout) view).getEditText();
                ((EditText) parent).setText("");
            }
        }

    }
}
