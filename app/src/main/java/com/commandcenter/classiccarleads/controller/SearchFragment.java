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
import android.widget.AutoCompleteTextView;
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
import java.util.Map;

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
    //==========END EDIT TEXT==========//

    //==========SEARCHLISTINGHELPER==========//
    private SearchListingHelper searchHelper;
    //==========END SEARCHLISTINGHELPER==========//

    LinearLayout controls;
    private List<String> model_list = new ArrayList<>();
    private AutoCompleteTextView tv_autoMake, tv_autoModel, tv_autoLoc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.search_fragment, container, false);

        years = new ArrayList<>();
        controls = view.findViewById(R.id.search_fragment_layout_controls);
        spinner_year = view.findViewById(R.id.search_fragment_spinner_year);
        view.findViewById(R.id.search_fragment_btn_cancel).setOnClickListener(SearchFragment.this);
        view.findViewById(R.id.search_fragment_btn_search).setOnClickListener(SearchFragment.this);
        view.findViewById(R.id.search_fragment_btn_clear).setOnClickListener(SearchFragment.this);
        ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states));
        final ArrayAdapter<String> makeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.makes));

        tv_autoMake = view.findViewById(R.id.search_fragment_autoMake);
        tv_autoModel = view.findViewById(R.id.search_fragment_autoModel);
        tv_autoLoc  =  view.findViewById(R.id.search_fragment_autoLoc);
        tv_autoMake.setAdapter(makeAdapter);

        tv_autoLoc.setAdapter(statesAdapter);
        GeneratYears();
        yearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, years);
        spinner_year.setAdapter(yearAdapter);

        tv_autoMake.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                tv_autoMake.showDropDown();
            }
        });
        tv_autoModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeModel(tv_autoMake.getText().toString());
                ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, model_list);
                tv_autoModel.setAdapter(modelAdapter);
                tv_autoModel.showDropDown();
            }
        });
        tv_autoLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_autoLoc.showDropDown();
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        clearInput();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.search_fragment_btn_search:
                if (TextUtils.isEmpty(tv_autoMake.getText().toString()) || TextUtils.isEmpty(tv_autoModel.getText().toString()) || spinner_year.getSelectedItem().toString() == "") {
                    Toast.makeText(getContext(), "All Fields Required", Toast.LENGTH_SHORT).show();
                } else {
                    String make = tv_autoMake.getText().toString();
                    String model = tv_autoModel.getText().toString();
                    String loc = tv_autoLoc.getText().toString();
                    String year = spinner_year.getSelectedItem().toString();
                    Intent intent = new Intent(getContext(), Search_Recview_Activity.class);
                    String[] query = new String[]{year, make, model, loc};
                    intent.putExtra("query", query);
                    startActivity(intent);

                }
             break;

            case R.id.search_fragment_btn_clear:
                clearInput();
                break;
            case R.id.search_fragment_btn_cancel:
                clearInput();
             break;

        }
    }

    private void GeneratYears() {

        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1920; i <= curYear; i++) {
            years.add(i);
        }
    }

    private void clearInput() {
        tv_autoModel.setText("");
        tv_autoMake.setText("");
        tv_autoLoc.setText("");
        spinner_year.setSelection(0);
    }

    /*
    method to load a list of models for each make
    which ever make is entered into the make textfield
    the model list will be loaded with only the models
    for the selected make
     */
    private List<String> makeModel(String make) {

        model_list.clear();
        String upperMake = make.toUpperCase();
        switch(upperMake) {
            case "FORD":
                model_list.add("Mustang");
                model_list.add("Thunderbird");
                model_list.add("F-100");
                model_list.add("F-150");
                model_list.add("Model T");
                model_list.add("Shelby");
                model_list.add("Shelby Cobra");
                model_list.add("F-Series");
                model_list.add("Galaxie");
                model_list.add("Escort");
                break;
            case "CHEVROLET":
                model_list.add("Bel Air");
                model_list.add("Camero");
                model_list.add("Corvette");
                model_list.add("Chevelle SS");
                model_list.add("El Camino");

                model_list.add("G-Van");
                model_list.add("Impala");
                model_list.add("Monte Carlo SS");
                model_list.add("S-10");

                break;
            case "DODGE":
                model_list.add("Coronet");
                model_list.add("Charger");
                model_list.add("Charger SRT-8");
                model_list.add("Challenger");
                model_list.add("Dart");
                model_list.add("Dart GTS 440");
                model_list.add("Neon SRT-8");
                model_list.add("Shelby Omni GLH-S");
                model_list.add("Viper");
                model_list.add("Viper RT/10");
                break;
            case "PONTIAC":
                model_list.add("GTO");
                model_list.add("Grand Prix");
                model_list.add("LeMans");
                model_list.add("Firebird");
                model_list.add("Tempest");
                model_list.add("Sunbird");
                model_list.add("Torpedo");
                break;
            case "BUICK":
                model_list.add("Riviera");
                model_list.add("Special");
                model_list.add("Buick GSX");
                model_list.add("Century");
                model_list.add("Regal");
                break;

            case "PLYMOUTH":
                model_list.add("Barracuda");
                model_list.add("Cuda");
                model_list.add("Duster");
                model_list.add("Duster 340");
                model_list.add("Fury");
                model_list.add("GTX");
                model_list.add("Road Runner");
                model_list.add("Roadster");
                model_list.add("Satalite");
                model_list.add("Street Rod");
                model_list.add("Valiant");
                break;
            case "GMC":
                model_list.add("3100");
                model_list.add("3500");
                model_list.add("Sprint");
                model_list.add("Panel Truck");
                break;
        }
        return model_list;
    }
}
