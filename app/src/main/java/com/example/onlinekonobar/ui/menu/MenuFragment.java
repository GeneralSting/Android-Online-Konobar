package com.example.onlinekonobar.ui.menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.Models.Cafe;
import com.example.onlinekonobar.Models.CafeCategory;
import com.example.onlinekonobar.Models.Category;
import com.example.onlinekonobar.Models.Drink;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.ViewHolder.DrinkViewHolder;
import com.example.onlinekonobar.ViewHolder.MenuViewHolder;
import com.example.onlinekonobar.databinding.FragmentMenuBinding;
import com.example.onlinekonobar.ui.cart.CartViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

public class MenuFragment extends Fragment {
    //fragment views
    private FragmentMenuBinding binding;
    RecyclerView recyclerMenu;
    RecyclerView recyclerMenuDrinks;
    //global variables/objects
    CartViewModel cartViewModel;
    Boolean emptyCart;
    ToastMessage toastMessage;
    //firebase
    private FirebaseDatabase database;
    DatabaseReference category, drinksCategoryRef, cafeCategoriesRef;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;

    //other
    RecyclerView.LayoutManager layoutManager;
    private MenuViewModel menuViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MenuViewModel menuViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);

        toastMessage = new ToastMessage(getActivity());

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //firebase init
        database = FirebaseDatabase.getInstance();
        category = database.getReference("drinksCategories");

        //laod menu
        recyclerMenu = (RecyclerView)binding.rvCategories;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerMenu.setLayoutManager(layoutManager);

        //firebase UI
        //cafe-id is founded and stored inside liveData
        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        final Observer<String> loggedCafeId = new Observer<String>() {
            @Override
            public void onChanged(String cafeId) {
                DatabaseReference cafesRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId.toString());
                cafesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Cafe cafe = snapshot.getValue(Cafe.class);
                        insertCafeCategories(cafeId.toString());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
                    }
                });
            }
        };
        menuViewModel.getCafeId().observe(requireActivity(), loggedCafeId);

        return root;
    }

    public void insertCafeCategories(String cafeId){

        cafeCategoriesRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeDrinksCategories");
        Query query = cafeCategoriesRef;
        FirebaseRecyclerOptions<CafeCategory> options = new FirebaseRecyclerOptions.Builder<CafeCategory>()
                .setQuery(query, CafeCategory.class)
                .build();
        FirebaseRecyclerAdapter<CafeCategory, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<CafeCategory, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull CafeCategory model) {
                String categoryId = getRef(position).getKey();
                cafeCategoriesRef.child(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("image")) {
                            String description = snapshot.child("description").getValue().toString();
                            String profileImage = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();

                            if(isAdded()) {
                                int id = getResources().getIdentifier(profileImage, "drawable", getActivity().getPackageName());
                                Drawable drawable;
                                if (id == 0) {
                                    id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                    drawable = getResources().getDrawable(id);
                                }
                                else
                                    drawable = getResources().getDrawable(id);
                                Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);
                            }



                            holder.txtMenuName.setText(name);
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    recyclerMenu.setVisibility(View.GONE);
                                    insertCategoryDrinks(cafeId, snapshot.getKey());
                                    recyclerMenuDrinks.setVisibility(view.VISIBLE);
                                }
                            });
                        }
                        else {
                            String name = snapshot.child("name").getKey().toString();
                            String description = snapshot.child("description").getKey().toString();

                            holder.txtMenuName.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
                    }
                });

            }
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.menu_item, parent, false);
                return new MenuViewHolder(view);
            }
        };
        recyclerMenu.setAdapter(adapter);
        adapter.startListening();
    }

    public void insertCategoryDrinks(String cafeId, String categoryId) {
        recyclerMenuDrinks = (RecyclerView)binding.rvCategoriesDrinks;
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerMenuDrinks.setLayoutManager(layoutManager);

        menuViewModel = new ViewModelProvider(requireActivity()).get(MenuViewModel.class);
        menuViewModel.setDisplayingCategories(false);
        //displaying ceratin recyclerView (categories/drinks of category)
        final Observer<Boolean> removeDrinksCategories = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    recyclerMenuDrinks.setVisibility(View.GONE);
                    recyclerMenu.setVisibility(View.VISIBLE);
                }
            }
        };
        menuViewModel.getDisplayingCategories().observe(requireActivity(), removeDrinksCategories);

        //cart for drinks
        final HashMap<String, DrinkBill>[] cartDrinks = new HashMap[]{new HashMap<>()};
        emptyCart = false;
        //observing drinks in cart:
        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        HashMap<String, DrinkBill> addedCartDrinks = cartViewModel.getDrinksInCart().getValue();
        if (addedCartDrinks == null || addedCartDrinks.isEmpty()) {
            emptyCart = true;
        }
        final Observer<HashMap<String, DrinkBill>> observingCartDrinks = new Observer<HashMap<String, DrinkBill>>() {
            @Override
            public void onChanged(HashMap<String, DrinkBill> stringDrinkBillHashMap) {
            }
        };
        cartViewModel.getDrinksInCart().observe(requireActivity(), observingCartDrinks);
        //sending cafeId to cartFragment
        cartViewModel.setCafeId(cafeId);
        //for drink prices, 2 decimals
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        drinksCategoryRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeDrinksCategories")
                .child(categoryId).child("cafeDrinks");
        FirebaseRecyclerOptions<Drink> options = new FirebaseRecyclerOptions.Builder<Drink>()
                .setQuery(drinksCategoryRef, Drink.class)
                .build();
        FirebaseRecyclerAdapter<Drink, DrinkViewHolder> adapterDrinks = new FirebaseRecyclerAdapter<Drink, DrinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DrinkViewHolder holder, int position, @NonNull Drink model) {
                String drinkId = getRef(position).getKey();
                drinksCategoryRef.child(drinkId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists())
                            return;
                        Drink cafeCategoryDrink = new Drink(
                                snapshot.getKey().toString(),
                                snapshot.child("cafeDrinkName").getValue().toString(),
                                snapshot.child("cafeDrinkDescription").getValue().toString(),
                                Float.valueOf(snapshot.child("cafeDrinkPrice").getValue().toString()),
                                snapshot.child("cafeDrinkImage").getValue().toString()
                        );
                        if(isAdded()) {
                            if(cafeCategoryDrink.getCafeDrinkImage().equals("")) {
                                int id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                Drawable drawable = getResources().getDrawable(id);
                                Glide.with(getActivity()).load(drawable).centerCrop().into(holder.drinkImageView);
                            }
                            else {
                                Glide.with(getActivity()).load(cafeCategoryDrink.getCafeDrinkImage()).into(holder.drinkImageView);
                            }
                        }

                        holder.txtDrinkName.setText(cafeCategoryDrink.getCafeDrinkName());
                        holder.txtDrinkDescription.setText(cafeCategoryDrink.getCafeDrinkDescription());
                        holder.txtDrinkPrice.setText(decimalFormat.format(cafeCategoryDrink.getCafeDrinkPrice()) + "€");

                        holder.txtDrinkDescription.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TextView txtFullDescription = new TextView(getActivity());
                                txtFullDescription.setTextSize(24);
                                final AlertDialog fullDescriptionDialog = new AlertDialog.Builder(getActivity()).create();
                                        fullDescriptionDialog.setView(
                                                txtFullDescription, 90, 120, 130, 140);
                                        fullDescriptionDialog.setTitle(
                                                getResources().getString(R.string.drink_full_description) + " " + cafeCategoryDrink.getCafeDrinkName());
                                fullDescriptionDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {
                                        txtFullDescription.setText(cafeCategoryDrink.getCafeDrinkDescription());
                                    }
                                });
                                fullDescriptionDialog.show();
                            }
                        });

                        final int[] drinkCounter = {0};
                        if (addedCartDrinks != null && !addedCartDrinks.isEmpty()) {
                            for (String key : addedCartDrinks.keySet()) {
                                DrinkBill value = addedCartDrinks.get(key);
                                if(value.getDrinkId() == cafeCategoryDrink.getCafeDrinkId()) {
                                    drinkCounter[0] = value.getDrinkAmount();
                                }
                            }
                            cartDrinks[0] = addedCartDrinks;
                            for(String key : cartDrinks[0].keySet()) {
                                DrinkBill value = cartDrinks[0].get(key);
                            }
                        }
                        holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));
                        holder.btnDrinkAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drinkCounter[0]++;
                                if(drinkCounter[0] > 1) {
                                    DrinkBill addingDrinkBill = cartDrinks[0].get(cafeCategoryDrink.getCafeDrinkId());
                                    addingDrinkBill.setDrinkAmount(drinkCounter[0]);
                                    addingDrinkBill.setDrinkTotalPrice(round((float) (addingDrinkBill.getDrinkPrice() * drinkCounter[0]), 2));
                                    addingDrinkBill.setDrinkTotalPrice(addingDrinkBill.getDrinkPrice() * drinkCounter[0]);
                                    cartDrinks[0].put(cafeCategoryDrink.getCafeDrinkId(), addingDrinkBill);
                                    cartViewModel.setDrinksInCart(cartDrinks[0]);
                                }
                                else {
                                    DrinkBill addingDrinkBill = new DrinkBill(
                                            cafeCategoryDrink.getCafeDrinkId(),
                                            cafeCategoryDrink.getCafeDrinkName(),
                                            Float.valueOf(cafeCategoryDrink.getCafeDrinkPrice().toString()),
                                            Float.valueOf(cafeCategoryDrink.getCafeDrinkPrice().toString()),
                                            drinkCounter[0]
                                    );
                                    cartDrinks[0].put(addingDrinkBill.getDrinkId(), addingDrinkBill);
                                    cartViewModel.setDrinksInCart(cartDrinks[0]);
                                }
                                holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));
                            }
                        });

                        holder.btnDrinkRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(drinkCounter[0] > 0) {
                                    drinkCounter[0]--;
                                    if(drinkCounter[0] != 0) {
                                        DrinkBill addingDrinkBill = cartDrinks[0].get(cafeCategoryDrink.getCafeDrinkId());
                                        addingDrinkBill.setDrinkAmount(drinkCounter[0]);
                                        addingDrinkBill.setDrinkTotalPrice(round((float) (addingDrinkBill.getDrinkPrice() * drinkCounter[0]), 2));
                                        cartDrinks[0].put(cafeCategoryDrink.getCafeDrinkId(), addingDrinkBill);
                                        cartViewModel.setDrinksInCart(cartDrinks[0]);
                                    }
                                    else {
                                        cartDrinks[0].remove(cafeCategoryDrink.getCafeDrinkId());
                                        cartViewModel.setDrinksInCart(cartDrinks[0]);
                                    }
                                    holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));
                                }
                            }
                        });

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
                    }
                });
            }

            @NonNull
            @Override
            public DrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drink_item, parent, false);
                return new DrinkViewHolder(view);
            }
        };
        recyclerMenuDrinks.setAdapter(adapterDrinks);
        adapterDrinks.startListening();
    }

    //for setting 2 decimals for Float numbers
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}