package com.example.onlinekonobar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.Employee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button logout;
    List<Cafe> list;
    String pavo = "pavo";
    Employee employee = new Employee();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getExtras();


        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });


        //firebasae init
        DatabaseReference cafesEmployeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
        cafesEmployeesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren())
                {
                    if(Objects.equals(bundle.getString("phoneNumber"), childSnapshot.getKey()))
                    {
                        pavo = bundle.getString("phoneNumber");
                        employee = childSnapshot.getValue(Employee.class);
                        TextView tv = findViewById(R.id.textwelcome);
                        tv.setText(employee.getName() + " " + employee.getLastname());
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HomeActivity.this, Home2.class);
                                intent.putExtra("phoneNumber", pavo);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }
}