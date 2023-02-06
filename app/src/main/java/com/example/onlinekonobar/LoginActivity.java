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

import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.ToastMessage;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    //Activity views
    EditText etPhoneNumber, etRecivedOtp;
    Button btnSendPhoneNumber, btnVerifyOtp;

    //global variables/objects
    String phoneNumber = "";
    String authNumber, verificationID;
    Boolean showProgressBar, employeeFounded, bossFounded;
    ToastMessage toastMessage;

    //firebase
    FirebaseAuth mAuth;

    //other
    ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toastMessage = new ToastMessage(this);

        Bundle bundle = getIntent().getExtras();
        if(Objects.equals(bundle.getString("phoneNumber"), ""))
            toastMessage.showToast(getResources().getString(R.string.phone_number_not_sent), 0);
        else
            phoneNumber = bundle.getString("phoneNumber");

        authNumber = "";
        employeeFounded = false;
        bossFounded = false;
        showProgressBar = true;

        etPhoneNumber = findViewById(R.id.etEnteredPhoneNumber);
        etPhoneNumber.setText(phoneNumber);
        etRecivedOtp = findViewById(R.id.etEnteredRecivedOtp);
        btnSendPhoneNumber = findViewById(R.id.btnGenerateOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        mAuth = FirebaseAuth.getInstance();
        loginProgressBar = findViewById(R.id.loginProgessBar);
        btnSendPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // regex to check the validity of the mobile phone number, it works for Croatian numbers
                String phoneNumberValidator = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$";
                if(!(etPhoneNumber.getText().toString().matches(phoneNumberValidator))) {
                    toastMessage.showToast(getResources().getString(R.string.phone_number_validation_failed), 0);
                }
                else {
                    authNumber = etPhoneNumber.getText().toString();
                    if(showProgressBar) {
                        loginProgressBar.setVisibility(View.VISIBLE);
                        showProgressBar = false;
                    }
                    sendverificationcode(authNumber);
                }
            }
        });
        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty(etRecivedOtp.getText().toString())) {
                    toastMessage.showToast(getResources().getString(R.string.login_empty_otp), 0);
                }
                else
                    verifycode(etRecivedOtp.getText().toString());
            }
        });
    }

    private void sendverificationcode(String phoneNumber) {
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
            //they changed this, callbacks are never called
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            final String code = credential.getSmsCode();
            if(code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            toastMessage.showToast(getResources().getString(R.string.login_verification_failed), 0);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onCodeSent(@NonNull String s,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            super.onCodeSent(s, token);
            verificationID = s;
            toastMessage.showToast(getResources().getString(R.string.login_code_sent), 0);
            btnVerifyOtp.setEnabled(true);
            loginProgressBar.setVisibility(View.INVISIBLE);
        }
    };

    private void verifycode(String Code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,Code);
        signinbyCredentials(credential);
    }

    private void signinbyCredentials(PhoneAuthCredential credential) {
        showProgressBar = true;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        DatabaseReference cafesEmployeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
                        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
                        cafesEmployeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot employeeSnapshot: snapshot.getChildren()) {
                                    if(Objects.equals(authNumber, employeeSnapshot.getKey())) {
                                        employeeFounded = true;
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        //flag -> If set, this activity will become the start of a new task on this history stack.
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                                        //Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                                        //afinitet, srodstvo
                                        finishAffinity();
                                        startActivity(intent);
                                    }
                                }
                                if(!employeeFounded) {
                                    checkBoss();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
                            }
                        });
                    } else {
                        toastMessage.showToast(getResources().getString(R.string.login_wrong_otp), 0);
                    }
                });
    }

    public void checkBoss() {
        DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference("cafes");
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        cafesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cafeSnapshot: snapshot.getChildren()) {
                    Cafe cafe = cafeSnapshot.getValue(Cafe.class);
                    if(authNumber.equals(cafe.getCafeOwnerPhoneNumber())) {
                        bossFounded = true;
                        Toast.makeText(LoginActivity.this, cafe.getCafeOwnerName().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                if(!bossFounded) {
                    toastMessage.showToast(getResources().getString(R.string.login_user_not_found), 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //boss is already login, open BossActivity
        if(bossFounded) {

        }
        else {
            //user is already login, open HomeActivity
            if(currentUser != null) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phoneNumber", etPhoneNumber.getText().toString());
                finishAffinity();
                startActivity(intent);
            }}
        }
    }
