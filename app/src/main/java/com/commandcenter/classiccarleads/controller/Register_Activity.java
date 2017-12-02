package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.commandcenter.classiccarleads.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener{

    //==========TAG==========//
    private final String TAG = "REGISTER ACTIVITY :";
    //==========END TAG==========//

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mData;
    private DatabaseReference mUserProfile;
    private FirebaseUser mUser;
    //==========END FIREBASE==========//

    //==========CONTROLS==========//
    private EditText et_email, et_password, et_zipcode;
    //==========END CONTROLS==========//

    //==========PROGRESSDIALOG==========//
    private ProgressDialog pDialog;
    //==========END PROGRESSDIALOG==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //==========INITIALIZE==========//
        Init();
        //==========END INITIALIZE==========//

        //==========INTENT GET EXTRA==========//
        String[] userDetails = getIntent().getStringArrayExtra("user_details");
        //==========END INTENT GET EXTRA==========//

        //==========SET CONTROLS==========//
        if (userDetails != null) {
            et_email.setText(userDetails[0]);
            et_password.setText(userDetails[1]);
        }
        //==========END SET CONTROLS==========//
    }

    private void Init() {

        //==========INITIALIZE FIREBASE==========//
        if (mAuth != null) {
            mData = FirebaseDatabase.getInstance();
            mUserProfile = mData.getReference();
        }else {
            mAuth  = FirebaseAuth.getInstance();
            mData = FirebaseDatabase.getInstance();
            mUserProfile = mData.getReference();
        }
        //==========END INITIALIZE FIREBASE==========//

        //==========INITIALIZE CONTROLS==========//
        findViewById(R.id.register_btnRegister).setOnClickListener(this);
        findViewById(R.id.register_btnCancel).setOnClickListener(this);
        et_email    = findViewById(R.id.register_et_email);
        et_password = findViewById(R.id.register_et_password);
        et_zipcode  = findViewById(R.id.register_et_zipcode);
        //==========END INITIALIZE CONTROLS==========//
    }
    
    private void Register(String email, String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Create Account");
        pDialog.setMessage("Please wait while Class Car Leads creates your account...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail: Success");
                    mUser = mAuth.getCurrentUser();
                    User user = new User(mUser.getUid(), et_email.getText().toString(), et_zipcode.getText().toString());
                    mUserProfile.child(mUser.getUid()).child("profile").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pDialog.dismiss();
                            Toast.makeText(Register_Activity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register_Activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    pDialog.dismiss();
                    Log.d(TAG, "createUserWithEmail: Failure ", task.getException());
                    Toast.makeText(Register_Activity.this, "Create Account Failed!\r\nTry a different email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int buttonID = v.getId();

        switch(buttonID) {
            case R.id.register_btnRegister:
                if (!TextUtils.isEmpty(et_email.getText().toString()) || !TextUtils.isEmpty(et_password.getText().toString()) || !TextUtils.isEmpty(et_zipcode.getText().toString())) {
                    Register(et_email.getText().toString(), et_password.getText().toString());
                }else
                    Toast.makeText(this, "All Fields Required", Toast.LENGTH_SHORT).show();

                break;
            case R.id.register_btnCancel:
                buildAlert(this, "REGISTRATION DIALOG", "Are you sure you want to cancel the new account registration?");
                break;

        }
    }

    private void buildAlert(Context ctx, String title, String message) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertBuilder.create();
        alertBuilder.show();
    }
}
