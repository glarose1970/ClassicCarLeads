package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.commandcenter.classiccarleads.helper.SearchListingHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        Init(view);

        return view;
    }

    private void Init(View view) {

        years = new ArrayList<>();
        spinner_year = view.findViewById(R.id.search_fragment_spinner_year);
        view.findViewById(R.id.search_fragment_btn_cancel).setOnClickListener(SearchFragment.this);
        view.findViewById(R.id.search_fragment_btn_search).setOnClickListener(SearchFragment.this);
        et_make      = view.findViewById(R.id.search_fragment_et_make);
        et_model     = view.findViewById(R.id.search_fragment_et_model);
        et_location  = view.findViewById(R.id.search_fragment_et_location);
        tv_results   = view.findViewById(R.id.search_fragment_totalResults);

        GeneratYears();
        yearAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_item, years);
        spinner_year.setAdapter(yearAdapter);



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

        ProgressDialog pDialog = new ProgressDialog(getContext());
        String totalListing = "";
        String imgUrl = "";
        @Override
        protected String doInBackground(String... strings) {

            String base_url = "https://classiccars.com/listings/find/" + strings[0] + "/" + strings[1] + "/" + strings[2] + "?auction=false&dealer=true&private=false&state=" + strings[3] ;
            try {
                Document mainDoc = Jsoup.connect(base_url).get();
                Elements listingNodes = mainDoc.getElementsByClass("search-result-item");

                for (Element node : listingNodes) {
                    Element link = listingNodes.select("a").first();
                    String linkHref = link.attr("href");
                    String imgLink = node.select("[src]").attr("src");
                    String title = node.getElementsByClass("item-ymm").text();
                    String id = node.getElementsByClass("item-stock-no").text();
                    Document nodeDoc = Jsoup.connect("https://classiccars.com" + linkHref).get();

                    imgUrl = title + id;
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
            pDialog.setMessage("Please wait while we gather the results...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pDialog.dismiss();
            tv_results.setText(s.toString());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    //==========END BACKGROUND ASYNC CLASS==========//
}
