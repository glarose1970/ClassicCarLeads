package com.commandcenter.classiccarleads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login_Activity extends AppCompatActivity {

    //==========FIREBASE==========//
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mParentNodeRef;
    private FirebaseUser mCurrentUser;

    //==========CONTROLS==========//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //==========INITIALIZE FIREBASE==========//
        Init();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void Init() {

        //==========INITIALIZE FIREBASE==========//
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mParentNodeRef = mDatabase.getReference();
        mParentNodeRef.child("test").setValue("Test");
        mCurrentUser = mAuth.getCurrentUser();
        //==========END INITIALIZE FIREBASE==========//

        //==========INITIALIZE CONTROLS==========//

        //==========END INITIALIZE CONTROLS==========//
    }
}
