package com.example.onlinekonobar.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.onlinekonobar.Adapter.CartAdapter;
import com.example.onlinekonobar.Interfaces.CallBackCart;
import com.example.onlinekonobar.Models.CafeBill;
import com.example.onlinekonobar.Models.Drink;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.databinding.FragmentCartBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class CartFragment extends Fragment implements CallBackCart {
    //fragment views
    private FragmentCartBinding binding;
    RecyclerView recyclerCartDrinks;
    TextView txtBillAmount, txtBillPrice;
    Button btnAcceptBill;
    ImageButton btnCancelBill;

    //gloabl variables/objects
    HashMap<String, DrinkBill> drinksInCart, modifiedDrinksInCart;
    HashMap<String, DrinkBill> cafeBillDrinks;
    int cafeBillProductsAmount;
    String cafeBillTotalPrice;
    ToastMessage toastMessage;

    //firebase
    DatabaseReference drinksCategoryRef, cafeCategoriesRef, newCafeBillRef;

    //other
    CartViewModel cartViewModel;
    CartAdapter cartAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        toastMessage = new ToastMessage(getActivity());

        btnAcceptBill = (Button)binding.cartBillAccept;
        btnCancelBill = (ImageButton)binding.cartBillCancel;
        disableBillConfirm();

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        drinksInCart = cartViewModel.getDrinksInCart().getValue();
        if (drinksInCart != null && !drinksInCart.isEmpty()) {
            if(cartViewModel.getCafeId().getValue() != null && !cartViewModel.getCafeId().getValue().isEmpty()) {
                modifiedDrinksInCart = new HashMap<String, DrinkBill>();
                insertCartDrinks();
            }
        }
        //cancel purchase
        btnCancelBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCartDrinks();
            }
        });
        //complete purchase
        btnAcceptBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterTable();
            }
        });

        return root;
    }

    public void insertCartDrinks() {
        String cafeId = cartViewModel.getCafeId().getValue();

        cafeCategoriesRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeDrinksCategories");
        cafeCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotCategory) {
                for(DataSnapshot category: snapshotCategory.getChildren()) {
                    checkDrinksByCategory(category.getKey(), cafeId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    public void checkDrinksByCategory(String category, String cafeId) {
        drinksCategoryRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeDrinksCategories")
                .child(category).child("cafeDrinks");
        drinksCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotDrink)
            {
                    for(DataSnapshot drink: snapshotDrink.getChildren()) {
                        Boolean addNewDrink = false;
                        DrinkBill addNewDrinkBill = new DrinkBill();
                        for (String key : drinksInCart.keySet()) {
                            DrinkBill drinkBill = drinksInCart.get(key);
                            if(Objects.equals(drinkBill.getDrinkId(), drink.getKey())) {
                                Drink mDrink = new Drink(
                                        drink.getKey().toString(),
                                        drink.child("cafeDrinkName").getValue().toString(),
                                        drink.child("cafeDrinkDescription").getValue().toString(),
                                        Float.valueOf(drink.child("cafeDrinkPrice").getValue().toString()),
                                        drink.child("cafeDrinkImage").getValue().toString()
                                );
                                DrinkBill newDrinkBill = new DrinkBill(
                                        drinkBill.getDrinkId(),
                                        mDrink.getCafeDrinkName(),
                                        Float.valueOf(mDrink.getCafeDrinkPrice().toString()),
                                        ((Float) mDrink.getCafeDrinkPrice() * drinkBill.getDrinkAmount()),
                                        drinkBill.getDrinkAmount(),
                                        mDrink.getCafeDrinkImage()
                                );
                                addNewDrink = true;
                                addNewDrinkBill = newDrinkBill;
                            }
                        }
                        if(addNewDrink) {
                            modifiedDrinksInCart.put(addNewDrinkBill.getDrinkId(), addNewDrinkBill);
                            if(modifiedDrinksInCart.size() == drinksInCart.size()) {
                                createCartRecylcer();
                            }
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    public void createCartRecylcer() {
        updateBillSummary(modifiedDrinksInCart);
        recyclerCartDrinks = (RecyclerView)binding.rvCartDrinks;
        recyclerCartDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), modifiedDrinksInCart, this);
        recyclerCartDrinks.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        btnAcceptBill.setEnabled(true);
        btnAcceptBill.setBackgroundColor(getResources().getColor(R.color.drink_add_button));
        btnAcceptBill.setTextColor(getResources().getColor(R.color.white));
        btnCancelBill.setEnabled(true);
    }

    public void updateBillSummary(HashMap<String, DrinkBill> recivedCartDrinks) {
        if (recivedCartDrinks == null || recivedCartDrinks.isEmpty()) {
            txtBillAmount = (TextView)binding.cartBillAmount;
            txtBillPrice = (TextView)binding.cartBillPrice;
            txtBillAmount.setText(getResources().getString(R.string.cart_bill_amount_empty));
            txtBillPrice.setText(getResources().getString(R.string.cart_bill_price_empty));
            disableBillConfirm();
        }
        else {
            txtBillAmount = (TextView)binding.cartBillAmount;
            txtBillPrice = (TextView)binding.cartBillPrice;
            Float billTotalPrice = 0f;
            int billProductsAmount = 0;
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            for (String key : recivedCartDrinks.keySet()) {
                DrinkBill drinkBill = recivedCartDrinks.get(key);
                billTotalPrice += (Float)drinkBill.getDrinkTotalPrice();
                billProductsAmount += (int)drinkBill.getDrinkAmount();
            }
            cafeBillTotalPrice = String.format(Locale.US, "%.2f", billTotalPrice);
            cafeBillProductsAmount = (int) billProductsAmount;
            cafeBillDrinks = (HashMap<String, DrinkBill>) recivedCartDrinks;
            txtBillAmount.setText(String.valueOf(billProductsAmount) + " " + getResources().getString(R.string.cart_bill_amount_text));
            txtBillPrice.setText(decimalFormat.format(billTotalPrice) + "€");
        }
    }

    public void disableBillConfirm() {
        btnAcceptBill.setEnabled(false);
        btnAcceptBill.setBackgroundColor(getResources().getColor(R.color.bill_disabled_button));
        btnAcceptBill.setTextColor(getResources().getColor(R.color.bill_disabled_text));
        btnCancelBill.setEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void updateCartDrinks(HashMap<String, DrinkBill> recivedCartDrinks) {
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        cartViewModel.setDrinksInCart(recivedCartDrinks);
        updateBillSummary(recivedCartDrinks);
    }

    public void removeCartDrinks() {
        recyclerCartDrinks.setAdapter(null);
        updateCartDrinks(new HashMap<>());
    }

    public void enterTable() {
        // Set up the view
        View selectTableView = getLayoutInflater().inflate(R.layout.cart_dialog_input, null);
        NumberPicker npTableNumber = selectTableView.findViewById(R.id.cartDialogInput);
        npTableNumber.setMinValue(1);
        npTableNumber.setMaxValue(cartViewModel.getCafeTables().getValue());
        Button tableDialogAccept = selectTableView.findViewById(R.id.cartDialogAccept);
        Button tableDialogCancel = selectTableView.findViewById(R.id.cartDialogCancel);
        // Dialog modal for selecting table number
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(selectTableView)
                .setTitle(getResources().getString(R.string.cart_dialog_title_text))
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                tableDialogAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        completePurchase(npTableNumber.getValue());
                        dialog.dismiss();
                    }
                });
                tableDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    public void completePurchase(Integer tableNumber) {
        String cafeId = cartViewModel.getCafeId().getValue();
        newCafeBillRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeBills");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        String currentDateTime = simpleDateFormat.format(new Date());
        HashMap<String, DrinkBill> databaseCafeBillDrinks = new HashMap<>();
        for (String key : cafeBillDrinks.keySet()) {
            DrinkBill drinkBill = cafeBillDrinks.get(key);
            DrinkBill databaseDrinkBill = new DrinkBill(drinkBill.getDrinkId(), drinkBill.getDrinkName(),
                    String.format(Locale.US, "%.2f", drinkBill.getDrinkPrice()),
                    String.format(Locale.US, "%.2f", drinkBill.getDrinkTotalPrice()),
                    drinkBill.getDrinkAmount(), drinkBill.getDrinkImage());
            databaseCafeBillDrinks.put(databaseDrinkBill.getDrinkId(), databaseDrinkBill);
        }
        CafeBill cafeBill = new CafeBill(currentDateTime, cafeBillTotalPrice, cafeBillProductsAmount,
                cartViewModel.getEmployeeId().getValue(), tableNumber, databaseCafeBillDrinks);
        String key = newCafeBillRef.push().getKey();
        toastMessage.showToast(getResources().getString(R.string.cafe_bill_completed) + " " + key, 0);
        removeCartDrinks();
        newCafeBillRef.child(key).setValue(cafeBill);
    }
}