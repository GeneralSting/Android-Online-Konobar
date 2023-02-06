package com.example.onlinekonobar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Activity views
    Button btnLogin, btnApplication;

    //global variables/objects
    private String phoneNumber = "";

    //premissions codes
    int REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This flag is not normally set by application code, but set for you by the system
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            Toast.makeText(this, "pavo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //description about this application
        btnApplication = findViewById(R.id.btnApplication);
        btnApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GeneralSting"));
                                                startActivity(browserIntent);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                dialog.dismiss();
                                                break;
                                        }
                                    }
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage(getResources().getString(R.string.dialog_show_privacy_policy)).setPositiveButton(
                                        getResources().getString(R.string.open_web_privacy_policy), dialogClickListener)
                                        .setNegativeButton(getResources().getString(R.string.dialog_show_close), dialogClickListener).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getResources().getString(R.string.dialog_show_app_purpose)).setPositiveButton(
                        getResources().getString(R.string.dialog_show_open_privacy_policy), dialogClickListener)
                        .setNegativeButton(getResources().getString(R.string.dialog_show_close), dialogClickListener).show();
            }
        });
        btnLogin = findViewById(R.id.btnLogin);
        //in order to continue in the login activity, we need to accept the permission
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions = {
                        Manifest.permission.READ_PHONE_NUMBERS
                };
                requestPermissions(permissions, REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER);
            }
        });
    }

    //setting the permission acceptance modal
    @SuppressLint("HardwareIds")    // -> Using getLine1Number to get device identifiers is not recommended
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER) {
            //accepted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, LoginActivity.class);
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //mandatory to have another permission check
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                phoneNumber = telephonyManager.getLine1Number().toString();
                intent.putExtra("phoneNumber", telephonyManager.getLine1Number().toString());
                startActivity(intent);
            }
            else    //permission denied for the first time -> opening warning modal
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_NUMBERS))
                    getErrorDialog(getResources().getString(R.string.dialog_alert_phone_number_body), MainActivity.this, true).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //making a modal that will be displayed if permission is not accepted the first time
    public AlertDialog.Builder getErrorDialog(String message, Context context, final boolean isFromCamera) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(getString(R.string.app_name)).setMessage(message);
        alertDialog.setPositiveButton(getResources().getString(R.string.dialog_alert_phone_number), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFromCamera) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_NUMBERS},
                            REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER);
                }
            }
        });
        return alertDialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //user is already login, open LoginActivity
        if(currentUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("phoneNumber", phoneNumber.toString());
            finishAffinity();
            startActivity(intent);
        }}
}