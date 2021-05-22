package com.example.datingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG ="LoginActivity";
    private ProgressBar spinner;
    private Button mLogin;
    private EditText mEmail,mPassword;
    private TextView mForgetPassword;
    private boolean loginBtnClicked;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginBtnClicked=false;
        spinner=(ProgressBar)findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);
        mAuth=FirebaseAuth.getInstance();
        mLogin=(Button)findViewById(R.id.Login);
        mEmail=(EditText)findViewById(R.id.email);
        mPassword=(EditText)findViewById(R.id.password);
        mForgetPassword=(TextView)findViewById(R.id.forgetPasswordButton);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtnClicked=true;
                spinner.setVisibility(View.VISIBLE);
                final String email=mEmail.getText().toString();
                final String password=mPassword.getText().toString();
                if(isStringNull(email)||isStringNull(password)){
                    Toast.makeText(LoginActivity.this,"You must fill out the fields",Toast.LENGTH_SHORT).show();

                }
                else{
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull  Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    Intent i=new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                    return;
                                }else{
                                    Toast.makeText(LoginActivity.this,"Please verify your mail ", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });

                }
                spinner.setVisibility(View.GONE);
            }
        });
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                Intent i=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });
        firebaseAuthStateListenter=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user !=null && user.isEmailVerified() && !loginBtnClicked){
                    spinner.setVisibility(View.VISIBLE);
                    Intent i =new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }
            }
        };


    }

    private boolean isStringNull(String email) {
        return email.equals((""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListenter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.addAuthStateListener(firebaseAuthStateListenter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i =new Intent(LoginActivity.this,Choose_Login_And_Reg.class);
        startActivity(i);
        finish();
        return;
    }
}