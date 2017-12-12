package com.commandcenter.classiccarleads.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.commandcenter.classiccarleads.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    //==========TAG==========//
    private final String TAG = "LOGIN ACTIVITY :";
    //==========END TAG==========//
    
    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mParentNodeRef;
    private FirebaseUser mCurrentUser;
    //==========END FIREBASE==========//

    //==========CONTROLS==========//
    EditText et_email, et_password;
    //==========END CONTROLS==========//

    //==========PROGRESS DIALOR==========//
    private ProgressDialog loginProgress;
    //==========END PROGRESS DIALOR==========//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //==========INITIALIZE FIREBASE / CONTROLS==========//
        Init();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void Init() {

        //==========INITIALIZE FIREBASE==========//
        mAuth  = FirebaseAuth.getInstance();
        //==========END INITIALIZE FIREBASE==========//

        //==========INITIALIZE CONTROLS==========//
        findViewById(R.id.login_btnLogin).setOnClickListener(this);
        findViewById(R.id.login_btnRegister).setOnClickListener(this);
        et_email    = findViewById(R.id.login_et_email);
        et_password = findViewById(R.id.login_et_password);
        //==========END INITIALIZE CONTROLS==========//
    }
    
    private void Login(String email, String password) {

        if (TextUtils.isEmpty(et_email.getText().toString()) || TextUtils.isEmpty(et_password.getText().toString())) {
            Toast.makeText(this, "Email and Password Fields Required!", Toast.LENGTH_SHORT).show();
        }else {
            loginProgress = new ProgressDialog(this);
            loginProgress.setTitle("Signing In!");
            loginProgress.setMessage("Please wait while you are signed in!");
            loginProgress.setCanceledOnTouchOutside(false);
            loginProgress.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInUserWithEmail: Success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                loginProgress.dismiss();
                                Intent mainIntent = new Intent(Login_Activity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }else {
                                Log.d(TAG, "signInUserWithEmail: Failure", task.getException());
                                loginProgress.dismiss();
                                Toast.makeText(Login_Activity.this, "Authentication Failed.\r\n Please Register New Account.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }); 
        }
       
    }

    @Override
    public void onClick(View v) {

        int buttonID = v.getId();

        switch(buttonID) {
            case R.id.login_btnLogin:
                Login(et_email.getText().toString(), et_password.getText().toString());
                break;
            case R.id.login_btnRegister:
                Intent registerIntent = new Intent(Login_Activity.this, Register_Activity.class);
                String[] userDetails = new String[] { et_email.getText().toString(), et_password.getText().toString() };
                registerIntent.putExtra("user_details", userDetails);
                startActivity(registerIntent);
                finish();
                break;
        }
    }
}
