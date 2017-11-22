package com.commandcenter.classiccarleads;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register_Activity extends AppCompatActivity implements View.OnClickListener{

    //==========TAG==========//
    private final String TAG = "REGISTER ACTIVITY :";
    //==========END TAG==========//

    //==========FIREBASE==========//
    FirebaseAuth mAuth;
    //==========END FIREBASE==========//

    //==========CONTROLS==========//
    EditText et_email, et_password;
    //==========END CONTROLS==========//

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
        mAuth  = FirebaseAuth.getInstance();
        //==========END INITIALIZE FIREBASE==========//

        //==========INITIALIZE CONTROLS==========//
        findViewById(R.id.register_btnRegister).setOnClickListener(this);
        findViewById(R.id.register_btnRegister).setOnClickListener(this);
        et_email    = findViewById(R.id.register_et_email);
        et_password = findViewById(R.id.register_et_password);
        //==========END INITIALIZE CONTROLS==========//
    }
    
    private void Register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail: Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                }else {
                    Log.d(TAG, "createUserWithEmail: Failure ", task.getException());
                    Toast.makeText(Register_Activity.this, "Create New Account Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int buttonID = v.getId();

        switch(buttonID) {
            case R.id.register_btnRegister:
                Register(et_email.getText().toString(), et_password.getText().toString());
                break;
            case R.id.register_btnCancel:

                break;

        }
    }
}
