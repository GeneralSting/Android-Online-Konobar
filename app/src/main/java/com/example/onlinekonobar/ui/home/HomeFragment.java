package com.example.onlinekonobar.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.CafeCategory;
import com.example.onlinekonobar.Models.Category;
import com.example.onlinekonobar.Models.Employee;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.databinding.FragmentHomeBinding;
import com.example.onlinekonobar.ui.employees.EmployeesViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

private FragmentHomeBinding binding;

    //fragment views
    TextView txtCafeName, txtCafeTablesNumber, txtCafeEmployeesNumber, txtCafeCategoriesNumber, txtCafeDrinkNumber;
    LinearLayoutCompat llEmployeesNumbers, llEmployeesNames;
    CardView cvCafeTables, cvCafeEmployees, cvCafeCategories, cvCafeDrinks, cvCafeNumber, cvCafeEmployee;

    //global variables/objects
    ToastMessage toastMessage;

    //firebase
    private DatabaseReference cafeRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

    binding = FragmentHomeBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        txtCafeName = binding.txtCafeName;
        txtCafeTablesNumber = binding.txtCafeTablesNumber;
        txtCafeEmployeesNumber = binding.txtCafeEmployeesNumber;
        txtCafeCategoriesNumber = binding.txtCafeCategoriesNumber;
        txtCafeDrinkNumber = binding.txtCafeDrinkNumber;
        llEmployeesNumbers = binding.llEmployeesNumbers;
        llEmployeesNames = binding.llEmployeesNames;
        cvCafeTables = binding.cvHomeTables;
        cvCafeTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_tables_cv), 0);
            }
        });
        cvCafeEmployees = binding.cvHomeEmployees;
        cvCafeEmployees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_employees_cv), 0);
            }
        });
        cvCafeCategories = binding.cvHomeCategories;
        cvCafeCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_categories_cv), 0);
            }
        });
        cvCafeDrinks = binding.cvHomeDrinks;
        cvCafeDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_drinks_cv), 0);
            }
        });
        cvCafeNumber = binding.cvHomeNumber;
        cvCafeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_number_cv), 0);
            }
        });
        cvCafeEmployee = binding.cvHomeEmployee;
        cvCafeEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage.showToast(getResources().getString(R.string.boss_home_employee_cv), 0);
            }
        });


        toastMessage = new ToastMessage(getActivity());
        HomeViewModel homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        txtCafeName.setText(homeViewModel.getOwnerNumber().getValue());


        final Observer<String> ObservingCafeId = new Observer<String>() {
            @Override
            public void onChanged(String value) {
                findCafe(value);
            }
        };
        homeViewModel.getOwnerNumber().observe(getActivity(), ObservingCafeId);


        return root;
    }

    public void findCafe(String ownerNumber) {
        cafeRef = FirebaseDatabase.getInstance().getReference("cafes");
        cafeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cafeSnapshot: snapshot.getChildren()) {
                    Cafe cafe = cafeSnapshot.getValue(Cafe.class);
                    if(cafe.getCafeOwnerPhoneNumber().toString().equals(ownerNumber)) {
                        EmployeesViewModel employeesViewModel = new ViewModelProvider(getActivity()).get(EmployeesViewModel.class);
                        employeesViewModel.setCafeId(cafeSnapshot.getKey());
                        populateViews(cafeSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    public void populateViews(String cafeId) {
        cafeRef = FirebaseDatabase.getInstance().getReference("cafes/" + cafeId);
        cafeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(isAdded()) {
                    int drinks = 0;
                    Cafe cafe = snapshot.getValue(Cafe.class);
                    txtCafeTablesNumber.setText(cafe.getCafeTables().toString());
                    txtCafeCategoriesNumber.setText(String.valueOf(cafe.getCafeDrinksCategories().size()));
                    txtCafeName.setText(cafe.getCafeName());
                    for(Map.Entry<String, CafeCategory> value: cafe.getCafeDrinksCategories().entrySet()) {
                        CafeCategory cafeCategory = value.getValue();
                        drinks += cafeCategory.getCafeDrinks().size();
                    }
                    txtCafeDrinkNumber.setText(String.valueOf(drinks));
                    DatabaseReference employeesRef = FirebaseDatabase.getInstance().getReference("cafesEmployees");
                    employeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int employees = 0;
                            for (DataSnapshot employeeSnapshot: snapshot.getChildren()) {
                                Employee employee = employeeSnapshot.getValue(Employee.class);
                                if(employee.getCafeId().equals(cafeId)) {
                                    employees++;
                                    TextView txtEmployeeNumber = new TextView(getActivity());
                                    txtEmployeeNumber.setText(employeeSnapshot.getKey());
                                    TextView txtEmployeeName = new TextView(getActivity());
                                    txtEmployeeName.setText(employee.getLastname());
                                    txtEmployeeNumber.setTextColor(getResources().getColor(R.color.black));
                                    txtEmployeeName.setTextColor(getResources().getColor(R.color.black));
                                    txtEmployeeNumber.setTextSize(16);
                                    txtEmployeeName.setTextSize(16);
                                    llEmployeesNumbers.addView(txtEmployeeNumber);
                                    llEmployeesNames.addView(txtEmployeeName);
                                    //settings this after putting textviews inside linear layout so we could get views position inside it
                                    txtEmployeeNumber.setGravity(Gravity.CENTER_HORIZONTAL);
                                    txtEmployeeName.setGravity(Gravity.CENTER_HORIZONTAL);
                                    setMargins(txtEmployeeNumber, 0, 0, 0, 24);
                                    setMargins(txtEmployeeName, 0, 0, 0, 24);
                                }
                            }
                            txtCafeEmployeesNumber.setText(String.valueOf(employees));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}