package com.example.onlinekonobar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnLogin;

    int REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER = 102;
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }

        btnLogin = findViewById(R.id.btnLogin);
        //kako bi nastavili u login activity-u trebamo prihvatit dopustenje
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

    //postavljanje modala za prihvat dopustenja
    @SuppressLint("HardwareIds")    // -> Using getLine1Number to get device identifiers is not recommended
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSION_READ_PHONE_NUMBER) {
            //prihvaceno
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
            else    //otvaranje modala upozorenja o ne prihvacanju dopustenja
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_NUMBERS))
                    getErrorDialog(getResources().getString(R.string.dialog_alert_phone_number_body), MainActivity.this, true).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //pravljenje modala koji ce se prikazati ako se prvi put ne prihvati dopustenje
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
        if(currentUser!=null)
        {
            /*startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();*/

            Intent intent = new Intent(this, Home2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("phoneNumber", phoneNumber.toString());
            finishAffinity();
            startActivity(intent);
        }}
}