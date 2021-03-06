package com.commandcenter.classiccarleads.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.model.Dealer;
import com.commandcenter.classiccarleads.model.Listing;
import com.commandcenter.classiccarleads.model.Request;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Single_Listing_View extends AppCompatActivity implements View.OnClickListener{

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mDataRef;
    private DatabaseReference mAppRequests;
    private FirebaseUser mCurUser;
    //==========END FIREBASE==========//

    //==========CONTROLS==========//
    private ImageView iv_mainImg;
    private EditText et_name, et_email, et_phone, et_comment;
    private TextView tv_title, tv_price, tv_desc, tv_loc;
    private Button btn_submit, btn_cancel, btn_save;
    //==========END CONTROLS==========//

    //==========CLASS VARIABLES==========//
    private String dealerName;
    private String dealerUrl;
    private String listingID;
    private String title;
    //==========END CLASS VARIABLES==========//

    private String[] details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__listing_view);

        details = getIntent().getStringArrayExtra("details");
        Init();

        if (details != null) {
            if (details.length > 6) {
                dealerName = details[0];
                dealerUrl = details[1];
                listingID = details[3];
                Picasso.with(this).load(details[2]).placeholder(R.drawable.ic_warning).into(iv_mainImg);
                title = details[4];
                tv_title.setText(details[4]);
                tv_price.setText(details[5]);
                tv_desc.setText(details[6]);
               // tv_loc.setText("test");
            }else {
                Picasso.with(this).load(details[0]).placeholder(R.drawable.ic_warning).into(iv_mainImg);
                listingID = details[1];
                tv_title.setText(details[2]);
                title = details[4];
                tv_price.setText(details[3]);
                tv_desc.setText(details[4]);
              //  tv_loc.setText("test");
            }

        }

    }

    private void clearInputs() {

        LinearLayout layout = findViewById(R.id.layout_controls);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof TextInputLayout) {
               ((TextInputLayout) view).getEditText().setText("");
            }
        }
    }

    private void submitRequest(String name, String email, String phone, String comment) {

       // Map<String, String> userDetails = new HashMap<>();
        if (!TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Dealer dealer = new Dealer(dealerName, dealerUrl);
            Request request = new Request(dealer, title, email, phone, name, comment);
            mAppRequests.child(mCurUser.getUid()).child(listingID).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    clearInputs();
                    Toast.makeText(Single_Listing_View.this, "Thank You for your interest in this listing\r\nWe will contact you soon!", Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(Single_Listing_View.this, "Name and Email Required!", Toast.LENGTH_SHORT).show();
        }


    }

    private void Init() {

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid());
            mAppRequests = mData.getReference().child("app_requests");
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid());
            mAppRequests = mData.getReference().child("app_requests");

        }

        iv_mainImg   = findViewById(R.id.single_listing_view_iv_mainImg);
        et_name      = findViewById(R.id.single_listing_view_et_firstName);
        et_email     = findViewById(R.id.single_listing_view_et_email);
        et_phone     = findViewById(R.id.single_listing_view_et_phone);
        et_comment   = findViewById(R.id.single_listing_view_et_comment);
        tv_title     = findViewById(R.id.single_listing_view_tv_title);
        tv_price     = findViewById(R.id.single_listing_view_tv_price);
        tv_desc      = findViewById(R.id.single_listing_view_tv_desc);
       // tv_loc       = findViewById(R.id.listing_single_row_tv_location);
        findViewById(R.id.single_listing_view_btn_Submit).setOnClickListener(this);
        findViewById(R.id.single_listing_view_btn_Cancel).setOnClickListener(this);
        findViewById(R.id.single_listing_view_btn_save).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.single_listing_view_btn_save:
                // save the listing to the database
                Map listingMap = new HashMap();
                if (details.length >= 6) {
                    Dealer dealer = new Dealer(dealerName, dealerUrl);
                    listingMap.put("dealerInfo", dealer);
                    listingMap.put("img_url", details[2]);
                    listingMap.put("listingID", details[3]);
                    listingMap.put("desc", details[6]);
                    listingMap.put("title", details[4]);
                    listingMap.put("price", details[5]);
                    listingMap.put("location", details[7]);
                    mDataRef.child("saved_searches").child(details[3]).setValue(listingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            buildBasicAlert(Single_Listing_View.this, "SAVE", "Listing has been saved to your history");
                        }
                    });
                }
                break;
            case R.id.single_listing_view_btn_Submit:
                if (TextUtils.isEmpty(et_name.getText().toString()) || TextUtils.isEmpty(et_email.getText().toString())) {
                    Toast.makeText(Single_Listing_View.this, "Name and Email Fields are Required!", Toast.LENGTH_SHORT).show();
                }else {
                    String name = et_name.getText().toString();
                    String email = et_email.getText().toString();
                    String phone = et_phone.getText().toString();
                    String comment = et_comment.getText().toString();

                    submitRequest(name, email, phone, comment);
                }
                break;
            case R.id.single_listing_view_btn_Cancel:

                buildAlert(this, "REQUEST INFO", "are you sure you want to cancel the REQUEST INFO?", "YES", "NO");
                break;
        }
    }

    private void buildBasicAlert(Context context, String title, String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create();
        alert.show();
    }

    private void buildAlert(Context context, String title, String message, String pButton, String nButton) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(pButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearInputs();

            }
        }).setNegativeButton(nButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.create();
        alert.show();
    }
}










