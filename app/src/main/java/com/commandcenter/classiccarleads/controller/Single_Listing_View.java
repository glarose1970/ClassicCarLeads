package com.commandcenter.classiccarleads.controller;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
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
    private FirebaseUser mCurUser;
    //==========END FIREBASE==========//

    //==========CONTROLS==========//
    private ImageView iv_mainImg;
    private EditText et_name, et_email, et_phone;
    private TextView tv_title, tv_price, tv_desc, tv_listingID;
    private Button btn_submit, btn_cancel, btn_save;
    //==========END CONTROLS==========//

    //==========CLASS VARIABLES==========//
    private String dealerName;
    private String dealerUrl;
    //==========END CLASS VARIABLES==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__listing_view);

        String[] details = getIntent().getStringArrayExtra("details");
        Init();

        if (details != null) {
            dealerName = details[0];
            dealerUrl = details[1];
            Picasso.with(this).load(details[2]).placeholder(R.drawable.ic_warning).into(iv_mainImg);
            tv_title.setText(details[4]);
            tv_listingID.setText(details[3]);
            tv_price.setText(details[5]);
            tv_desc.setText(details[6]);
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

    private void submitRequest(String name, String email) {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("dealer_name", dealerName);
        userDetails.put("dealer_url", dealerUrl);
        userDetails.put("name", name);
        userDetails.put("email", email);
        userDetails.put("phone", et_phone.getText().toString());
        mDataRef.child("app_requests").child(dealerName).child(tv_listingID.getText().toString()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                clearInputs();
                Toast.makeText(Single_Listing_View.this, "Thank You for your interest in this listing\r\nWe will contact you soon!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Init() {

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid()).child("query");
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid()).child("query");
        }

        iv_mainImg   = findViewById(R.id.single_listing_view_iv_mainImg);
        et_name      = findViewById(R.id.single_listing_view_et_firstName);
        et_email     = findViewById(R.id.single_listing_view_et_email);
        et_phone     = findViewById(R.id.single_listing_view_et_phone);
        tv_title     = findViewById(R.id.single_listing_view_tv_title);
        tv_price     = findViewById(R.id.single_listing_view_tv_price);
        tv_desc      = findViewById(R.id.single_listing_view_tv_desc);
        tv_listingID = findViewById(R.id.single_listing_view_tv_listingID);
        btn_submit   = findViewById(R.id.single_listing_view_btn_Submit);
        btn_cancel   = findViewById(R.id.single_listing_view_btn_Cancel);
        btn_save     = findViewById(R.id.single_listing_view_btn_save);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id) {
            case R.id.single_listing_view_btn_save:

                break;
            case R.id.single_listing_view_btn_Submit:
                if (TextUtils.isEmpty(et_name.getText().toString()) || TextUtils.isEmpty(et_email.getText().toString())) {
                    Toast.makeText(Single_Listing_View.this, "Name and Email Fields are Required!", Toast.LENGTH_SHORT).show();
                }else {
                    String name = et_name.getText().toString();
                    String email = et_email.getText().toString();

                    submitRequest(name, email);
                }
                break;
            case R.id.single_listing_view_btn_Cancel:

                break;
        }
    }
}