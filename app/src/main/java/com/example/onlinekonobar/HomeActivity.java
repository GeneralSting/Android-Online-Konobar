package com.example.onlinekonobar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.Models.Employee;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.databinding.ActivityHomeBinding;
import com.example.onlinekonobar.ui.cart.CartViewModel;
import com.example.onlinekonobar.ui.menu.MenuViewModel;
import com.example.onlinekonobar.ui.settings.SettingsViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    //Activity view
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;
    NavigationView navigationView;

    //gloabl variables/objects
    private Boolean isCategoriesDisplayed = true;
    Employee employee = new Employee();
    Cafe cafe = new Cafe();
    String recivedAdmin;
    Boolean employeeFounded, employeeCafeNameFounded;
    ToastMessage toastMessage;

    //firebase
    private FirebaseAuth mAuth;

    //viewModels
    private MenuViewModel menuViewModel;
    private CartViewModel cartViewModel;
    private SettingsViewModel settingsViewModel;

    //other
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        recivedAdmin = bundle.getString("phoneNumber");

        //when app is closed in background
        if(Objects.equals(bundle.getString("phoneNumber"), "")) {
            logout();
        }
    }

    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
    }

    private void getEmployee(NavigationView navigationView) {
        DatabaseReference cafesEmployeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
        //addListenerForSingleValueEvent -> only once will go through database, we do not need continuously listen here
        cafesEmployeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot employeeSnapshot: snapshot.getChildren()) {
                    if(recivedAdmin.equals(employeeSnapshot.getKey())) {
                        employeeFounded = true;
                        //liveData for setting logged cafe employee
                        cartViewModel = new ViewModelProvider(HomeActivity.this).get(CartViewModel.class);
                        cartViewModel.setEmployeeId(recivedAdmin);
                        employee = employeeSnapshot.getValue(Employee.class);
                        View headerView = navigationView.getHeaderView(0);
                        TextView tvAdminName = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminName);
                        TextView tvAdminGmail = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminGmail);
                        tvAdminName.setText(employee.getName() + " " + employee.getLastname());
                        tvAdminGmail.setText(employee.getGmail());
                        employeeCafeNameFounded = false;
                        getCafeName(navigationView);
                    }
                }
                if(!employeeFounded) {
                    toastMessage.showToast(getResources().getString(R.string.logged_employee_not_found), 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    private void getCafeName(NavigationView navigationView) {
        DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference("cafes");
        //addValueEventListener -> to update changes on new cafe data especially cafeTables
        cafesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cafeSnapshopt: snapshot.getChildren()) {
                    if(employee.getCafeId().equals(cafeSnapshopt.getKey())) {
                        employeeCafeNameFounded = true;
                        //liveData for setting logged cafe employee's cafe-id
                        MenuViewModel menuViewModel = new ViewModelProvider(HomeActivity.this).get(MenuViewModel.class);
                        menuViewModel.setCafeId(cafeSnapshopt.getKey());
                        cafe = cafeSnapshopt.getValue(Cafe.class);
                        //LiveData for setting cafe tables number:
                        cartViewModel = new ViewModelProvider(HomeActivity.this).get(CartViewModel.class);
                        cartViewModel.setCafeTables(cafe.getCafeTables());
                        cartViewModel.setCafeId(cafeSnapshopt.getKey());
                        View headerView = navigationView.getHeaderView(0);
                        if(cafe.getCafeName().equals("")) {
                            toastMessage.showToast(getResources().getString(R.string.unknown_employee_cafe_name), 0);
                        }
                        TextView tvAdminCafe = (TextView) headerView.findViewById(R.id.tvNavHeaderAdminCafe);
                        tvAdminCafe.setText(cafe.getCafeName());
                    }
                }
                if(!employeeCafeNameFounded) {
                    toastMessage.showToast(getResources().getString(R.string.logged_employee_cafe_not_found), 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
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
    public void onResume() {
        super.onResume();
        toastMessage = new ToastMessage(this);
        //liveData for onBackPressed
        settingsViewModel = new ViewModelProvider(HomeActivity.this).get(SettingsViewModel.class);
        menuViewModel = new ViewModelProvider(HomeActivity.this).get(MenuViewModel.class);
        final Observer<Boolean> displayingCategoriesObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                isCategoriesDisplayed = aBoolean;
            }
        };
        menuViewModel.getDisplayingCategories().observe(this, displayingCategoriesObserver);

        setSupportActionBar(binding.appBarHome2.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = binding.navView;
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
                int cartDrinksCounter = 0;
                cartViewModel = new ViewModelProvider(HomeActivity.this).get(CartViewModel.class);
                HashMap<String, DrinkBill> addedCartDrinks = cartViewModel.getDrinksInCart().getValue();
                if (addedCartDrinks != null && !addedCartDrinks.isEmpty()) {
                    for (String key : addedCartDrinks.keySet()) {
                        DrinkBill drinkBill = addedCartDrinks.get(key);
                        cartDrinksCounter += drinkBill.getDrinkAmount();
                    }
                }
                Snackbar.make(view, getResources().getString(R.string.cart_products_number) + " " + cartDrinksCounter + " " +
                                getResources().getString(R.string.cart_products_txt), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //firebasae init
        employeeFounded = false;
        getEmployee(navigationView);
    }

    @Override
    public void onBackPressed() {
        //if category drinks is displayed then from cart navigation we need to first exit
        //category drinks
        if(navigationView.getMenu().findItem(R.id.nav_menu).isChecked()) {
            if(isCategoriesDisplayed) {
                if(back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                    super.onBackPressed();
                    logout();
                }
                else {
                    toastMessage.showToast(getResources().getString(R.string.back_button_pressed), 0);
                }
                back_pressed = System.currentTimeMillis();
            }
            else {
                MenuViewModel menuViewModel = new ViewModelProvider(HomeActivity.this).get(MenuViewModel.class);
                menuViewModel.setDisplayingCategories(true);
            }
        }
        else if(navigationView.getMenu().findItem(R.id.nav_settings).isChecked()) {
            switch (settingsViewModel.getRvSettingsDisplayed().getValue()) {
                case 0:
                    if(back_pressed + TIME_DELAY > System.currentTimeMillis()) {
                        super.onBackPressed();
                        logout();
                    }
                    else {
                        toastMessage.showToast(getResources().getString(R.string.back_button_pressed), 0);
                    }
                    back_pressed = System.currentTimeMillis();
                    break;
                case 1:
                    settingsViewModel.setSettingsChangeDisplayed(true);
                    settingsViewModel.setRvSettingsDisplayed(0);
                    break;
                case 2:
                    settingsViewModel.setSettingsChangeDisplayed(true);
                    settingsViewModel.setRvSettingsDisplayed(1);
                    break;
            }
        }
        //menu_nav is not displayed
        else {
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
}