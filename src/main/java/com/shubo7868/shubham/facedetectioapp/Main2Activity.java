package com.shubo7868.shubham.facedetectioapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//This is LOGIN ACTIVITY
public class Main2Activity extends AppCompatActivity {

// Login Credential
    private EditText mEmailField;
    private EditText mPasswordField;




    private ImageButton mselectMode;
    private Button mLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //Login Credentials
        mAuth = FirebaseAuth.getInstance();


        mEmailField = findViewById(R.id.main_emailField);
        mPasswordField = findViewById(R.id.main_passwordField);
        mLoginButton = findViewById(R.id.button_login);
       // mselectMode = findViewById(R.id.qrMode);


//        mselectMode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SelectMode();
//            }
//        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(Main2Activity.this, Scanner.class));
                }

            }
        };
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

    }






    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }






    //Sign in  function
    private void startSignIn() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(Main2Activity.this, "Field Empty", Toast.LENGTH_LONG).show();
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                 if(!task.isSuccessful()) {
                    Toast.makeText(Main2Activity.this, "Sign In Problem", Toast.LENGTH_LONG).show();

                }
            }
        });




    }




}
