package com.example.onlinekonobar.ui.menu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.ViewHolder.DrinkViewHolder;
import com.example.onlinekonobar.ViewHolder.MenuViewHolder;
import com.example.onlinekonobar.databinding.FragmentMenuBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;

    private FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerMenu;
    RecyclerView recyclerMenuDrinks;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    DatabaseReference categoriesRef, usersRef, drinksCategoryRef, cafeCategoriesRef;
    private MenuViewModel menuViewModel;
    ProgressDialog progressDialog;
    HashSet<String> uniqueCategories = new HashSet<>();
    List<String> allCategories = new ArrayList<>();


    Uri imageUri;
    StorageReference storageReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MenuViewModel menuViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);


        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textGallery;
        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);*/

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
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        menuViewModel.getCafeId().observe(requireActivity(), loggedCafeId);

        /*categoriesRef = FirebaseDatabase.getInstance().getReference("drinksCategories");
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(categoriesRef, Category.class)
                .build();

        FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                String categoryId = getRef(position).getKey();
                categoriesRef.child(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild("image")) {
                            String description = snapshot.child("description").getValue().toString();
                            String profileImage = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();


                            int id = getResources().getIdentifier(profileImage, "drawable", getActivity().getPackageName());
                            Drawable drawable;
                            if (id == 0) {
                                id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                drawable = getResources().getDrawable(id);
                            }
                            else
                                drawable = getResources().getDrawable(id);
                            Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);


                            holder.txtMenuName.setText(name);
                            holder.setItemClickListener(new ItemClickListener() {
                                @Override
                                public void onClick(View view, int position, boolean isLongClick) {
                                    recyclerMenu.setVisibility(View.GONE);
                                    insertDrinks(name);
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
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
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
        adapter.startListening();*/

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


                            int id = getResources().getIdentifier(profileImage, "drawable", getActivity().getPackageName());
                            Drawable drawable;
                            if (id == 0) {
                                id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                drawable = getResources().getDrawable(id);
                            }
                            else
                                drawable = getResources().getDrawable(id);
                            Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);


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
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
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

        drinksCategoryRef = FirebaseDatabase.getInstance().getReference("cafes").child(cafeId).child("cafeDrinksCategories").child(categoryId).child("cafeDrinks");
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
                        String description = snapshot.child("cafeDrinkDescription").getValue().toString();
                        String name = snapshot.child("cafeDrinkName").getValue().toString();
                        String price = snapshot.child("cafeDrinkPrice").getValue().toString();
                        String drinkImage = snapshot.child("cafeDrinkImage").getValue().toString();

                        if(drinkImage.equals("")) {
                            int id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                            Drawable drawable = getResources().getDrawable(id);
                            Glide.with(getActivity()).load(drawable).centerCrop().into(holder.drinkImageView);
                        }
                        else {
                            Glide.with(getActivity()).load(drinkImage).into(holder.drinkImageView);
                        }

                        holder.txtDrinkName.setText(name);
                        holder.txtDrinkDescription.setText(description);
                        holder.txtDrinkPrice.setText(price.toString() + "€");


                        final int[] drinkCounter = {0};
                        holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));

                        holder.btnDrinkAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drinkCounter[0]++;
                                holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));
                            }
                        });

                        holder.btnDrinkRemove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(drinkCounter[0] > 0) {
                                    drinkCounter[0]--;
                                    holder.txtDrinkAddedNumber.setText(String.valueOf(drinkCounter[0]));

                                }
                            }
                        });

                        //for adding image to firebase storage
                        /*Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 100);
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();*/

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Error!", Toast.LENGTH_SHORT).show();
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

    private void uploadImage() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String fileName = simpleDateFormat.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "uspjeno", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "nije uspjeno", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() !=null) {
            imageUri = data.getData();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}