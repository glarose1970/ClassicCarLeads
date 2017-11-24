package com.commandcenter.classiccarleads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Single_Listing_View extends AppCompatActivity {

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
    private Button btn_submit, btn_cancel;
    //==========END CONTROLS==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__listing_view);

        String[] details = getIntent().getStringArrayExtra("details");
        Init();

        if (details != null) {
            Picasso.with(this).load(details[0]).placeholder(R.drawable.ic_warning).into(iv_mainImg);
            tv_title.setText(details[2]);
            tv_listingID.setText(details[1]);
            tv_price.setText(details[3]);
            tv_desc.setText(details[4]);
        }

    }

    private void Init() {

        if (mAuth != null) {
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid());
        }else {
            mAuth = FirebaseAuth.getInstance();
            mCurUser = mAuth.getCurrentUser();
            mData = FirebaseDatabase.getInstance();
            mDataRef = mData.getReference().child(mCurUser.getUid());
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

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
