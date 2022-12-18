package com.example.onlinekonobar;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.Employee;
import com.example.onlinekonobar.ui.gallery.GalleryFragment;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinekonobar.databinding.ActivityHome2Binding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Home2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHome2Binding binding;

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    //for firebase
    private FirebaseAuth mAuth;
    Employee employee = new Employee();
    Cafe cafe = new Cafe();
    String recivedAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome2.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_menu, R.id.nav_cart, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            logout();
            return true;
        });

        binding.appBarHome2.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "U košarici je :" + " 0 proizvoda", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        recivedAdmin = bundle.getString("phoneNumber");
        //firebasae init
        getAdmin(navigationView);
    }

    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(Home2.this, MainActivity.class));
        finish();
    }

    private void getAdmin(NavigationView navigationView) {
        DatabaseReference cafesEmployeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
        cafesEmployeesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren())
                {
                    if(recivedAdmin.equals(childSnapshot.getKey()))
                    {
                        employee = childSnapshot.getValue(Employee.class);
                        View headerView = navigationView.getHeaderView(0);
                        TextView tvAdminName = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminName);
                        TextView tvAdminGmail = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminGmail);
                        tvAdminName.setText(employee.getName() + " " + employee.getLastname());
                        tvAdminGmail.setText(employee.getGmail());

                        getCafeName(navigationView);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home2.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCafeName(NavigationView navigationView) {
        DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference("cafes");
        cafesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot childSnapshot: snapshot.getChildren())
                {
                    if(employee.getCafeId().equals(childSnapshot.getKey()))
                    {
                        cafe = childSnapshot.getValue(Cafe.class);
                        View headerView = navigationView.getHeaderView(0);
                        TextView tvAdminCafe = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminCafe);
                        tvAdminCafe.setText(cafe.getCafeName());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home2.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.back_button_pressed), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}