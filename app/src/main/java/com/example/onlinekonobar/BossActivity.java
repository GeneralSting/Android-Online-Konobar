package com.example.onlinekonobar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.ui.home.HomeViewModel;
import com.example.onlinekonobar.ui.logout.LogoutViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.onlinekonobar.databinding.ActivityBossBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class BossActivity extends AppCompatActivity {

private ActivityBossBinding binding;

    //firebase
    private FirebaseAuth mAuth;

    //global variables/objects
    ToastMessage toastMessage;
    String recivedAdmin;

    //other
    LogoutViewModel logoutViewModel;
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    binding = ActivityBossBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    toastMessage = new ToastMessage(this);

        logoutViewModel = new ViewModelProvider(this).get(LogoutViewModel.class);
        final Observer<Boolean> ObservingLogout = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if(value)
                    logout();
            }
        };
        logoutViewModel.getLogout().observe(this, ObservingLogout);

        Bundle bundle = getIntent().getExtras();
        recivedAdmin = bundle.getString("phoneNumber");

        //when app is closed in background
        if(Objects.equals(bundle.getString("phoneNumber"), "")) {
            logout();
        }
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setOwnerNumber(recivedAdmin);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.bottom_navigation_home, R.id.bottom_navigation_employees, R.id.bottom_navigation_logout)
                .build();*/
        //no action bar
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_boss);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }

    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(BossActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if(back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
            logout();
        }
        else {
            toastMessage.showToast(getResources().getString(R.string.back_button_pressed), 0);
        }
        back_pressed = System.currentTimeMillis();
    }
}