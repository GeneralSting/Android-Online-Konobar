package com.example.onlinekonobar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {


    EditText etPhoneNumber, etRecivedOtp;
    Button btnSendPhoneNumber, btnVerifyOtp;
    FirebaseAuth mAuth;
    String verificationID;
    ProgressBar loginProgressBar;
    String phoneNumber = "";
    Boolean showProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            Toast.makeText(this, getResources().getString(R.string.phone_number_not_sent), Toast.LENGTH_SHORT).show();
        else
            phoneNumber = bundle.getString("phoneNumber");

        etPhoneNumber = findViewById(R.id.etEnteredPhoneNumber);
        etPhoneNumber.setText(phoneNumber);
        etRecivedOtp = findViewById(R.id.etEnteredRecivedOtp);
        btnSendPhoneNumber = findViewById(R.id.btnGenerateOtp);
        btnVerifyOtp =findViewById(R.id.btnVerifyOtp);
        mAuth = FirebaseAuth.getInstance();
        loginProgressBar = findViewById(R.id.loginProgessBar);
        showProgressBar = true;
        btnSendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // regex za provjeru valjanosti broja mobitela, radi za hrv brojeve
                String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                if(!(etPhoneNumber.getText().toString().matches(phoneNumberValidator)))
                {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.phone_number_validation_failed), Toast.LENGTH_SHORT).show();
                }
                else {
                    String number = etPhoneNumber.getText().toString();
                    if(showProgressBar) {
                        loginProgressBar.setVisibility(View.VISIBLE);
                        showProgressBar = false;
                    }
                    sendverificationcode(number);
                }
            }
        });
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(etRecivedOtp.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_empty_otp), Toast.LENGTH_SHORT).show();
                }
                else
                    verifycode(etRecivedOtp.getText().toString());
            }
        });
    }

    private void sendverificationcode(String phoneNumber)
    {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)  // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            //ovo su promjenili, nikada se ne poziva mCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential)
        {
            final String code = credential.getSmsCode();
            if(code!=null)
            {
                verifycode(code);
            }
        }
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_verification_failed), Toast.LENGTH_SHORT).show();
            loginProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificationID = s;
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_code_sent), Toast.LENGTH_SHORT).show();
            btnVerifyOtp.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    };
    private void verifycode(String Code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential)
    {
        showProgressBar = true;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                        finishAffinity();
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_wrong_otp), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser!=null)
        {
            /*startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();*/

            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
            finishAffinity();
            startActivity(intent);
        }}
    }
