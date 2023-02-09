package com.example.onlinekonobar.ui.settings;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.onlinekonobar.Filters.PriceDigitsInputFilter;
import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.Models.Category;
import com.example.onlinekonobar.Models.Drink;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.Models.ToastMessage;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.ViewHolder.MenuViewHolder;
import com.example.onlinekonobar.ViewHolder.UpdateDrinkViewHolder;
import com.example.onlinekonobar.databinding.FragmentSettingsBinding;
import com.example.onlinekonobar.ui.cart.CartViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SettingsFragment extends Fragment {

    //codes for image selecting
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;

    //fragment views
    private FragmentSettingsBinding binding;
    ConstraintLayout constraintLayout;
    Button btnUpdateDrinks, btnAddNewDrink, btnUpdateTables, drinkDialogAccept;
    TextView txtSettingsNewDrink, txtSettingsUpdateMenu;
    ImageView drinkImage;
    RecyclerView rvCategories, rvCategoryDrinks;
    RecyclerView.LayoutManager layoutManagerCategories, layoutManagerCategoryDrinks;

    //others
    CartViewModel cartViewModel;
    SettingsViewModel settingsViewModel;
    ToastMessage toastMessage;
    Toast toast;
    ProgressDialog progressDialog;
    DecimalFormat decimalFormat;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);

    //global variables
    Boolean checkDrinkName, checkDrinkDescription, checkDrinkPrice,
            newDrinkSecondConfirm, imageSelected, firstDrinkNewCategory,
            addingNewDrink;

    //firebase
    DatabaseReference categoriesRef, cafeRef, cafeCategoriesRef, cafeCategoryRef;
    StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        cartViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);

        toastMessage = new ToastMessage(getActivity());
        //for drink prices, 2 decimals
        decimalFormat = new DecimalFormat("0.00");

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        constraintLayout = binding.clSettingsUpdateContainer;
        btnUpdateDrinks = binding.btnSettingsUpdateMenu;
        btnAddNewDrink = binding.btnSettingsAddDrink;
        btnUpdateTables = binding.btnSettingsUpdateTables;
        txtSettingsUpdateMenu = binding.txtSettingsUpdateMenu;
        txtSettingsNewDrink = binding.txtSettingsNewDrink;
        rvCategories = (RecyclerView)binding.rvSettingsCategories;
        rvCategories.setVisibility(View.GONE);
        rvCategoryDrinks = (RecyclerView)binding.rvSettingsCategoryDrinks;
        rvCategoryDrinks.setVisibility(View.GONE);
        layoutManagerCategories = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(layoutManagerCategories);
        layoutManagerCategoryDrinks = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvCategoryDrinks.setLayoutManager(layoutManagerCategoryDrinks);

        btnUpdateDrinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsViewModel.setSettingsChangeDisplayed(false);
                addingNewDrink = false;
                insertCategories(addingNewDrink);
                //when dispalying rvCategories this text is over recycler for short peroid of time
                txtSettingsUpdateMenu.setVisibility(View.GONE);
                rvCategories.setVisibility(View.VISIBLE);
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                txtSettingsUpdateMenu.setVisibility(View.VISIBLE);
                            }
                        },
                400);
            }
        });

        btnAddNewDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsViewModel.setSettingsChangeDisplayed(false);
                addingNewDrink = true;
                insertCategories(addingNewDrink);
                //when dispalying rvCategories this text is over recycler for short peroid of time
                txtSettingsNewDrink.setVisibility(View.GONE);
                rvCategories.setVisibility(View.VISIBLE);
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                txtSettingsNewDrink.setVisibility(View.VISIBLE);
                            }
                        },
                        400);
            }
        });

        btnUpdateTables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTables();
            }
        });

        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        final Observer<Integer> settingsDisplayedObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(settingsViewModel.getSettingsChangeDisplayed().getValue() == true) {
                    switch (integer) {
                        case 0:
                            rvCategories.setVisibility(View.GONE);
                            constraintLayout.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            rvCategoryDrinks.setVisibility(View.GONE);
                            rvCategories.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            }
        };
        settingsViewModel.getRvSettingsDisplayed().observe(requireActivity(), settingsDisplayedObserver);

        View root = binding.getRoot();
        return root;
    }

    /*  functions for adding new drink */
    //inserting all categories, we add new drink for selected category
    //or inserting cafe categories if it is updating menu
    public void insertCategories(boolean addingNewDrink) {
        settingsViewModel.setRvSettingsDisplayed(1);
        if(addingNewDrink) {
            categoriesRef = FirebaseDatabase.getInstance().getReference("drinksCategories");
            Query query = categoriesRef;
            FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                    .setQuery(query, Category.class)
                    .build();
            FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                    String categoryId = getRef(position).getKey();
                    categoriesRef.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("image")) {
                                Category category = new Category(categoryId,
                                        snapshot.child("description").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("name").getValue().toString());

                                if(isAdded()) {
                                    int id = getResources().getIdentifier(category.getImage(), "drawable", getActivity().getPackageName());
                                    Drawable drawable;
                                    if (id == 0) {
                                        id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                        drawable = getResources().getDrawable(id);
                                    }
                                    else
                                        drawable = getResources().getDrawable(id);
                                    Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);
                                }
                                holder.txtMenuName.setText(category.getName());
                                holder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        rvCategories.setVisibility(View.GONE);
                                        checkCafeCategoryDrinks(category);
                                    }
                                });
                            }
                            else {
                                Category category = new Category(categoryId,
                                        snapshot.child("description").getValue().toString(),
                                        "no_image",
                                        snapshot.child("name").getValue().toString());

                                if(isAdded()) {
                                    Drawable drawable;
                                    int id = getResources().getIdentifier(category.getImage(), "drawable", getActivity().getPackageName());
                                    drawable = getResources().getDrawable(id);
                                    Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);
                                }
                                holder.txtMenuName.setText(category.getName());
                                holder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        rvCategories.setVisibility(View.GONE);
                                        checkCafeCategoryDrinks(category);
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
                @NonNull
                @Override
                public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.menu_item, parent, false);
                    return new MenuViewHolder(view);
                }
            };
            rvCategories.setAdapter(adapter);
            adapter.startListening();
        }
        else {
            cafeCategoriesRef = FirebaseDatabase.getInstance().getReference("cafes/" + cartViewModel.getCafeId().getValue() +
                    "/cafeDrinksCategories");
            Query query = cafeCategoriesRef;
            FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                    .setQuery(query, Category.class)
                    .build();
            FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                    String cafeCategoryId = getRef(position).getKey();
                    cafeCategoriesRef.child(cafeCategoryId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists())
                                return;
                            if(snapshot.hasChild("image")) {
                                Category cafeCategory = new Category(snapshot.child("description").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("name").getValue().toString());

                                if(isAdded()) {
                                    int id = getResources().getIdentifier(cafeCategory.getImage(), "drawable", getActivity().getPackageName());
                                    Drawable drawable;
                                    if (id == 0) {
                                        id = getResources().getIdentifier("no_image", "drawable", getActivity().getPackageName());
                                        drawable = getResources().getDrawable(id);
                                    }
                                    else
                                        drawable = getResources().getDrawable(id);
                                    Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);
                                }
                                holder.txtMenuName.setText(cafeCategory.getName());
                                holder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        rvCategories.setVisibility(View.GONE);
                                        insertCategoryDrinks(cafeCategoryId);
                                    }
                                });
                            }
                            else {
                                Category category = new Category(snapshot.child("description").getValue().toString(),
                                        "no_image",
                                        snapshot.child("name").getValue().toString());

                                if(isAdded()) {
                                    Drawable drawable;
                                    int id = getResources().getIdentifier(category.getImage(), "drawable", getActivity().getPackageName());
                                    drawable = getResources().getDrawable(id);
                                    Glide.with(getActivity()).load(drawable).centerCrop().into(holder.imageView);
                                }
                                holder.txtMenuName.setText(category.getName());
                                holder.setItemClickListener(new ItemClickListener() {
                                    @Override
                                    public void onClick(View view, int position, boolean isLongClick) {
                                        rvCategories.setVisibility(View.GONE);
                                        insertCategoryDrinks(cafeCategoryId);
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
                @NonNull
                @Override
                public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.menu_item, parent, false);
                    return new MenuViewHolder(view);
                }
            };
            rvCategories.setAdapter(adapter);
            adapter.startListening();
        }
    }

    //checking if the cafe already have drink/drinks for selected category
    //is the new drink first drink in selected category
    public void checkCafeCategoryDrinks(Category selectedCategory) {
        firstDrinkNewCategory = true;
        cafeCategoriesRef = FirebaseDatabase.getInstance().getReference("cafes")
                .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories");
        cafeCategoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cafeCategorySnapshot: snapshot.getChildren()) {
                    //first drink in selected category
                    if(selectedCategory.getCategoryId().equals(cafeCategorySnapshot.getKey())) {
                        firstDrinkNewCategory = false;
                        break;
                    }
                }
                addNewDrinkPreparation(selectedCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }

    //function for checking input correctness (dialog)
    //bottom sheet for adding image
    public void addNewDrinkPreparation(Category selectedCategory) {
        checkDrinkName = checkDrinkDescription = checkDrinkPrice = false;
        newDrinkSecondConfirm = imageSelected = true;
        View addNewDrinkView = getLayoutInflater().inflate(R.layout.settings_dialog_drink_input, null);
        EditText etDrinkName = addNewDrinkView.findViewById(R.id.settingsDialogDrinkName);
        EditText etDrinkDescription = addNewDrinkView.findViewById(R.id.settingsDialogDrinkDescription);
        EditText etDrinkPrice = addNewDrinkView.findViewById(R.id.settingsDialogDrinkPrice);
        drinkImage = addNewDrinkView.findViewById(R.id.settingsDialogDrinkImage);
        drinkDialogAccept = addNewDrinkView.findViewById(R.id.settingsDialogDrinkAccept);
        Button drinkDialogCancel = addNewDrinkView.findViewById(R.id.settingsDialogDrinkCancel);
        //PriceDigitsInputFilter for new drink pirce
        etDrinkPrice.setFilters(new InputFilter[] {new PriceDigitsInputFilter(6, 2, 1000000)});
        // Dialog modal for entering new drink
        final AlertDialog settingsDrinkDialog = new AlertDialog.Builder(getActivity())
                .setView(addNewDrinkView)
                .setTitle(getResources().getString(R.string.settings_dialog_add_drink_title) + " " + selectedCategory.getName())
                .create();
        settingsDrinkDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                /*  Observing drink name input  */
                etDrinkName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkSecondConfirm = true;
                        drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if(etDrinkName.getText().length() >= 25) {
                            checkDrinkName = true;
                            toastMessage.showToast(getResources().getString(R.string.settings_dialog_drink_name_overflow), 0);
                            etDrinkName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.drink_remove_button)));
                        }
                        else if(checkDrinkName) {
                            etDrinkName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkDrinkName = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
                /*  Observing drink description input  */
                etDrinkDescription.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkSecondConfirm = true;
                        drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if(etDrinkDescription.getText().length() >= 50) {
                            checkDrinkDescription = true;
                            toastMessage.showToast(getResources().getString(R.string.settings_dialog_drink_description_overflow), 0);
                            etDrinkDescription.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.drink_remove_button)));
                        }
                        else if(checkDrinkDescription) {
                            etDrinkDescription.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkDrinkDescription = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
                /*  Observing drink price input  */
                etDrinkPrice.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        newDrinkSecondConfirm = true;
                        drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if(etDrinkPrice.getText().length() == 2 &&
                                etDrinkPrice.getText().charAt(0) == '0' &&
                                etDrinkPrice.getText().charAt(1) != '.' ||
                                etDrinkPrice.getText().length() == 1 &&
                                etDrinkPrice.getText().charAt(0) == '.') {
                            toastMessage.showToast(getResources().getString(R.string.settings_new_drink_price_incorrect_input), 0);
                            etDrinkPrice.setText("");
                        }
                        if(etDrinkPrice.getText().length() >= 9) {
                            checkDrinkPrice = true;
                            toastMessage.showToast(getResources().getString(R.string.settings_dialog_drink_price_overflow), 0);
                            etDrinkPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.drink_remove_button)));
                        }
                        else if(checkDrinkPrice) {
                            etDrinkPrice.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.tumbleweed)));
                            checkDrinkPrice = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {}
                });
                drinkImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //bottom sheet dialog
                        View selectImagePickerView = getLayoutInflater().inflate(R.layout.settings_bottom_dialog_image, null);
                        final BottomSheetDialog selectImageDialog = new BottomSheetDialog(getActivity());
                        selectImageDialog.setContentView(selectImagePickerView);
                        ImageView ivPhotoPicker = selectImagePickerView.findViewById(R.id.bottomDialogImagePhoto);
                        //setting rounded image with Glide
                        Glide.with(selectImagePickerView)
                                .load(getResources().getDrawable(R.drawable.select_image_camera))
                                .transform(new RoundedCorners(350))
                                .into(ivPhotoPicker);
                        ImageView ivGalleryPicker = selectImagePickerView.findViewById(R.id.bottomDialogImageGallery);
                        Glide.with(selectImagePickerView)
                                .load(getResources().getDrawable(R.drawable.select_image_gallery))
                                .transform(new RoundedCorners(350))
                                .into(ivGalleryPicker);
                        selectImageDialog.show();

                        ivPhotoPicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                askCameraPermission();
                            }
                        });

                        ivGalleryPicker.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openPhoneGallery();
                            }
                        });
                    }
                });
                drinkDialogAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etDrinkName.getText().length() > 0 && etDrinkDescription.getText().length() > 0 && etDrinkPrice.getText().length() > 0) {
                            DecimalFormat decimalFormat = new DecimalFormat("#.00");
                            double drinkPrice = Double.parseDouble(etDrinkPrice.getText().toString());
                            if(decimalFormat.format(drinkPrice).toString().equals(",00")) {
                                if(!newDrinkSecondConfirm)
                                    newDrinkSecondConfirm = true;
                                else {
                                    newDrinkSecondConfirm = false;
                                    drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_red_16, 0, 0, 0);
                                }
                            }
                            else if(drinkImage.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.add_drink_image).getConstantState()) {
                                if(!newDrinkSecondConfirm) {
                                    newDrinkSecondConfirm = true;
                                    imageSelected = false;
                                }
                                else {
                                    newDrinkSecondConfirm = false;
                                    drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warning_16, 0, 0, 0);
                                }
                            }
                            if(newDrinkSecondConfirm) {
                                settingsDrinkDialog.dismiss();
                                //cafeDrinkImage is empty string, image will be added after
                                //if we chose not to pick image, drink will get default no_image
                                Drink newCafeDrink = new Drink(selectedCategory.getCategoryId(), etDrinkName.getText().toString(), etDrinkDescription.getText().toString(),
                                        "", Float.parseFloat(etDrinkPrice.getText().toString()));
                                if(imageSelected)
                                    uploadImage(newCafeDrink, selectedCategory);
                                else
                                    addNewDrink(newCafeDrink, selectedCategory);
                            }
                        }
                    }
                });
                drinkDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingsDrinkDialog.dismiss();
                    }
                });
            }
        });
        settingsDrinkDialog.show();
    }

    public void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        else
            openCamera();
    }

    public void openCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, CAMERA_REQUEST_CODE);
    }

    public void openPhoneGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQUEST_CODE);
    }

    //if we add drink image, image will be saved into firebase storage
    //collecting new drink image uri
    private void uploadImage(Drink recivedNewDrink, Category selectedCategory) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getResources().getString(R.string.settings_new_drink_uploading));
        progressDialog.show();
        Date now = new Date();
        String fileName = cartViewModel.getCafeId().getValue() + "_" + recivedNewDrink.getCafeDrinkName() + "_" + simpleDateFormat.format(now);
        Bitmap bitmap = ((BitmapDrawable) drinkImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
                Task<Uri> downloadImageUri = taskSnapshot.getStorage().getDownloadUrl();
                downloadImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        recivedNewDrink.setCafeDrinkImage(downloadImageUri.getResult().toString());
                        addNewDrink(recivedNewDrink, selectedCategory);
                    }
                });
                downloadImageUri.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMessage.showToast(getResources().getString(R.string.settings_new_drink_image_upload_failed), 0);
                    }
                });
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMessage.showToast(getResources().getString(R.string.settings_new_drink_failure), 0);
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
    //adding new drink, checking if this is first drink in selected category (firstDrinkNewCategory)
    //and checking if image is added or it will get default no_image
    public void addNewDrink(Drink recivedNewDrink, Category selectedCategory) {
        boolean imageAdded = true;
        if(firstDrinkNewCategory) {
            cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
                    .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories")
                    .child(recivedNewDrink.getCafeDrinkCategoryId());
            Category categoryDbFormat = new Category(selectedCategory.getDescription(),
                    selectedCategory.getImage(), selectedCategory.getName());
            cafeCategoryRef.setValue(categoryDbFormat);
        }
        if(recivedNewDrink.getCafeDrinkImage().toString().equals("")) {
            imageAdded = false;
            storageReference = FirebaseStorage.getInstance().getReference("images/" + "no_image.jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    recivedNewDrink.setCafeDrinkImage(uri.toString());
                    cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
                            .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories")
                            .child(recivedNewDrink.getCafeDrinkCategoryId()).child("cafeDrinks");
                    Drink newDrinkDbFormat = new Drink(recivedNewDrink.getCafeDrinkName(), recivedNewDrink.getCafeDrinkDescription(),
                            recivedNewDrink.getCafeDrinkPrice(), recivedNewDrink.getCafeDrinkImage());
                    String key = cafeCategoryRef.push().getKey();
                    cafeCategoryRef.child(key).setValue(newDrinkDbFormat);
                    toastMessage.showToast(getResources().getString(R.string.settings_new_drink_sucess), 0);
                }
            });
            storageReference.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    toastMessage.showToast(getResources().getString(R.string.settings_new_drink_image_download_failed),0);
                }
            });
        }
        if(imageAdded) {
            cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
                    .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories")
                    .child(recivedNewDrink.getCafeDrinkCategoryId()).child("cafeDrinks");
            Drink newDrinkDbFormat = new Drink(recivedNewDrink.getCafeDrinkName(), recivedNewDrink.getCafeDrinkDescription(),
                    recivedNewDrink.getCafeDrinkPrice(), recivedNewDrink.getCafeDrinkImage());
            String key = cafeCategoryRef.push().getKey();
            cafeCategoryRef.child(key).setValue(newDrinkDbFormat);
            toastMessage.showToast(getResources().getString(R.string.settings_new_drink_sucess), 0);
        }
    }
    /*  end of adding new drink */

    /*  functions for updating cafe menu*/

    public void insertCategoryDrinks(String cafeCategoryId) {
        settingsViewModel.setRvSettingsDisplayed(2);
        constraintLayout.setVisibility(View.GONE);
        rvCategoryDrinks.setVisibility(View.VISIBLE);
        cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
                .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories")
                .child(cafeCategoryId).child("cafeDrinks");
        FirebaseRecyclerOptions<Drink> options = new FirebaseRecyclerOptions.Builder<Drink>()
                .setQuery(cafeCategoryRef, Drink.class)
                .build();
        FirebaseRecyclerAdapter<Drink, UpdateDrinkViewHolder> adapterDrinks = new FirebaseRecyclerAdapter<Drink, UpdateDrinkViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UpdateDrinkViewHolder holder, int position, @NonNull Drink model) {
                String drinkId = getRef(position).getKey();
                cafeCategoryRef.child(drinkId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists())
                            return;
                        Bitmap bmap = null;
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
                                Glide.with(getActivity()).load(cafeCategoryDrink.getCafeDrinkImage()).into(holder.oldDrinkImageView);
                            }
                        }

                        holder.txtDrinkName.setText(cafeCategoryDrink.getCafeDrinkName());
                        holder.txtDrinkDescription.setText(cafeCategoryDrink.getCafeDrinkDescription());
                        holder.txtDrinkPrice.setText(decimalFormat.format(cafeCategoryDrink.getCafeDrinkPrice()));

                        holder.txtDrinkDescription.setSelection(holder.txtDrinkDescription.getText().length());

                        holder.btnAcceptUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                boolean imageAdded = true;
                                if(areImagesSame(holder.oldDrinkImageView, holder.drinkImageView))
                                    imageAdded = false;
                                Drink updatedDrink = new Drink(snapshot.getKey(),
                                holder.txtDrinkName.getText().toString(), holder.txtDrinkDescription.getText().toString(),
                                holder.txtDrinkPrice.getText().toString(), cafeCategoryDrink.getCafeDrinkImage());
                                if(cafeCategoryDrink.getCafeDrinkName().equals(updatedDrink.getCafeDrinkName()) &&
                                cafeCategoryDrink.getCafeDrinkDescription().equals(updatedDrink.getCafeDrinkDescription()) &&
                                priceToTextConverter(cafeCategoryDrink.getCafeDrinkPrice()).equals(updatedDrink.getCafeDrinkPriceString())) {
                                    if(imageAdded)
                                        updateDrink(cafeCategoryId, updatedDrink, holder.drinkImageView, 0);
                                    else
                                        toastMessage.showToast(getResources().getString(R.string.drink_same_update), 0);
                                }
                                else {
                                    if(imageAdded)
                                        updateDrinkCheck(cafeCategoryId, updatedDrink, holder.drinkImageView, 2);
                                    else
                                        updateDrinkCheck(cafeCategoryId, updatedDrink, holder.drinkImageView, 1);
                                }
                            }
                        });

                        holder.btnDeleteDrink.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDrink(cafeCategoryId, cafeCategoryDrink);
                            }
                        });


                        holder.drinkImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drinkImage = holder.drinkImageView;
                                //bottom sheet dialog
                                View selectImagePickerView = getLayoutInflater().inflate(R.layout.settings_bottom_dialog_image, null);
                                final BottomSheetDialog selectImageDialog = new BottomSheetDialog(getActivity());
                                selectImageDialog.setContentView(selectImagePickerView);
                                ImageView ivPhotoPicker = selectImagePickerView.findViewById(R.id.bottomDialogImagePhoto);
                                //setting rounded image with Glide
                                Glide.with(selectImagePickerView)
                                        .load(getResources().getDrawable(R.drawable.select_image_camera))
                                        .transform(new RoundedCorners(350))
                                        .into(ivPhotoPicker);
                                ImageView ivGalleryPicker = selectImagePickerView.findViewById(R.id.bottomDialogImageGallery);
                                Glide.with(selectImagePickerView)
                                        .load(getResources().getDrawable(R.drawable.select_image_gallery))
                                        .transform(new RoundedCorners(350))
                                        .into(ivGalleryPicker);
                                selectImageDialog.show();

                                ivPhotoPicker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        askCameraPermission();
                                    }
                                });

                                ivGalleryPicker.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openPhoneGallery();
                                    }
                                });
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
            public UpdateDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.drink_update_item, parent, false);
                return new UpdateDrinkViewHolder(view);
            }
        };
        rvCategoryDrinks.setAdapter(adapterDrinks);
        adapterDrinks.startListening();
    }
    //checking if new image is added
    public boolean areImagesSame(ImageView imageView1, ImageView imageView2) {
        Bitmap bitmap1 = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) imageView2.getDrawable()).getBitmap();
        return bitmap1.sameAs(bitmap2);
    }
    //converting float price into string with , (2,00)
    public String priceToTextConverter(float number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(number);
    }

    public void updateDrinkCheck(String cafeCategoryId, Drink recivedUpdatedDrink, ImageView drinkImageView, int updates) {
        if(recivedUpdatedDrink.getCafeDrinkName().length() > 25 || recivedUpdatedDrink.getCafeDrinkName().length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.drink_name_update_condition), 0);
            return;
        }
        if(recivedUpdatedDrink.getCafeDrinkDescription().length() > 50 || recivedUpdatedDrink.getCafeDrinkDescription().length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.drink_description_update_condition), 0);
            return;
        }
        if(recivedUpdatedDrink.getCafeDrinkPriceString().toString().length() > 9 ||
                recivedUpdatedDrink.getCafeDrinkPriceString().toString().length() < 1) {
            toastMessage.showToast(getResources().getString(R.string.drink_price_update_condition), 0);
            return;
        }
        if(recivedUpdatedDrink.getCafeDrinkPriceString().toString().length() == 2 &&
                recivedUpdatedDrink.getCafeDrinkPriceString().toString().charAt(0) == '0') {
            if(recivedUpdatedDrink.getCafeDrinkPriceString().charAt(1) != ',' &&
                    recivedUpdatedDrink.getCafeDrinkPriceString().charAt(1) != '.') {
                toastMessage.showToast(getResources().getString(R.string.settings_new_drink_price_incorrect_input), 0);
                return;
            }
        }
        if(recivedUpdatedDrink.getCafeDrinkPriceString().toString().length() == 1 &&
                recivedUpdatedDrink.getCafeDrinkPriceString().toString().charAt(0) == '.' ||
                recivedUpdatedDrink.getCafeDrinkPriceString().charAt(0) == ',') {
            toastMessage.showToast(getResources().getString(R.string.settings_new_drink_price_incorrect_input), 0);
            return;
        }
        updateDrink(cafeCategoryId, recivedUpdatedDrink, drinkImageView, updates);
    }

    private void updateDrink(String selectedCategory, Drink recivedUpdatedDrink, ImageView drinkImageView, int updates) {
        progressDialog = new ProgressDialog(getActivity());
        switch (updates) {
            case 0: {
                progressDialog.setTitle(getResources().getString(R.string.settings_update_drink_uploading_image));
                progressDialog.show();
                Date now = new Date();
                String fileName = cartViewModel.getCafeId().getValue() + "_" + recivedUpdatedDrink.getCafeDrinkName() + "_" + simpleDateFormat.format(now);
                Bitmap bitmap = ((BitmapDrawable) drinkImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadImageUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                cafeCategoryRef = FirebaseDatabase.getInstance().getReference(
                                        "cafes/" + cartViewModel.getCafeId().getValue() + "/cafeDrinksCategories/"
                                                + selectedCategory + "/cafeDrinks/" + recivedUpdatedDrink.getCafeDrinkId() + "/cafeDrinkImage");
                                cafeCategoryRef.setValue(downloadImageUri.getResult().toString());
                                insertCategoryDrinks(selectedCategory);
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if(!recivedUpdatedDrink.getCafeDrinkImage().equals(
                                        "https://firebasestorage.googleapis.com/v0/b/online-konobar-pica.appspot.com/o/images%2Fno_image.jpg?alt=media&token=fdb9ce0a-6695-45c6-815f-e3ebba362f12")) {
                                    StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(recivedUpdatedDrink.getCafeDrinkImage());
                                    oldImageRef.delete();
                                }
                            }
                        });
                        downloadImageUri.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
            break;
            case 1: {
                progressDialog.setTitle(getResources().getString(R.string.settings_update_drink_uploading));
                progressDialog.show();
                String drinkPriceConvertFormat = recivedUpdatedDrink.getCafeDrinkPriceString().replace(',', '.');
                float drinkPrice = Float.parseFloat(drinkPriceConvertFormat);
                cafeCategoryRef = FirebaseDatabase.getInstance().getReference(
                        "cafes/" + cartViewModel.getCafeId().getValue() + "/cafeDrinksCategories/"
                                + selectedCategory + "/cafeDrinks/" + recivedUpdatedDrink.getCafeDrinkId());
                Drink cafeDrinkDbFormat = new Drink(recivedUpdatedDrink.getCafeDrinkName(),
                        recivedUpdatedDrink.getCafeDrinkDescription(), drinkPrice,
                        recivedUpdatedDrink.getCafeDrinkImage());
                cafeCategoryRef.setValue(cafeDrinkDbFormat);
                insertCategoryDrinks(selectedCategory);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
            break;
            case 2: {
                progressDialog.setTitle(getResources().getString(R.string.settings_update_drink_uploading));
                progressDialog.show();
                Date now = new Date();
                String fileName = cartViewModel.getCafeId().getValue() + "_" + recivedUpdatedDrink.getCafeDrinkName() + "_" + simpleDateFormat.format(now);
                Bitmap bitmap = ((BitmapDrawable) drinkImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);
                UploadTask uploadTask = storageReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadImageUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadImageUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String drinkPriceConvertFormat = recivedUpdatedDrink.getCafeDrinkPriceString().replace(',', '.');
                                float drinkPrice = Float.parseFloat(drinkPriceConvertFormat);
                                cafeCategoryRef = FirebaseDatabase.getInstance().getReference(
                                        "cafes/" + cartViewModel.getCafeId().getValue() + "/cafeDrinksCategories/"
                                                + selectedCategory + "/cafeDrinks/" + recivedUpdatedDrink.getCafeDrinkId());
                                Drink cafeDrinkDbFormat = new Drink(recivedUpdatedDrink.getCafeDrinkName(),
                                        recivedUpdatedDrink.getCafeDrinkDescription(), drinkPrice,
                                        downloadImageUri.getResult().toString());
                                cafeCategoryRef.setValue(cafeDrinkDbFormat);
                                insertCategoryDrinks(selectedCategory);
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                if(!recivedUpdatedDrink.getCafeDrinkImage().equals(
                                        "https://firebasestorage.googleapis.com/v0/b/online-konobar-pica.appspot.com/o/images%2Fno_image.jpg?alt=media&token=fdb9ce0a-6695-45c6-815f-e3ebba362f12")) {
                                    StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(recivedUpdatedDrink.getCafeDrinkImage());
                                    oldImageRef.delete();
                                }
                            }
                        });
                        downloadImageUri.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage.showToast(getResources().getString(R.string.settings_update_uploading_image_failed), 0);
                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toastMessage.showToast(getResources().getString(R.string.settings_update_uploading_failed), 0);
                    }
                });
            }
            break;
        }
    }

    public void deleteDrink(String cafeCategoryId, Drink cafeDrink) {
        View deleteDrinkView = getLayoutInflater().inflate(R.layout.settings_dialog_delete_drink, null);
        TextView txtDeleteDrinkName = (TextView) deleteDrinkView.findViewById(R.id.settingsDialogDeleteDrinkName);
        TextView txtDeleteDrinkCategory = (TextView) deleteDrinkView.findViewById(R.id.settingsDialogDeleteDrinkCategory);
        ImageView ivDeleteDrinkImage = (ImageView) deleteDrinkView.findViewById(R.id.settingsDialogDeleteDrinkImage);
        Button btnDeleteAccept = (Button) deleteDrinkView.findViewById(R.id.btnSettingsDeleteAccept);
        Button btnDeleteCancel = (Button) deleteDrinkView.findViewById(R.id.btnSettingsDeleteCancel);

        cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
                .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories").child(cafeCategoryId).child("name");
        cafeCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtDeleteDrinkCategory.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
        txtDeleteDrinkName.setText(cafeDrink.getCafeDrinkName());
        Glide.with(getActivity()).load(cafeDrink.getCafeDrinkImage()).into(ivDeleteDrinkImage);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(deleteDrinkView)
                .setTitle(getResources().getString(R.string.drink_delete_confirm))
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                btnDeleteCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnDeleteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean cartDrink = false;
                        HashMap<String, DrinkBill> drinksInCart = cartViewModel.getDrinksInCart().getValue();
                        if (drinksInCart != null && !drinksInCart.isEmpty()) {
                            //if cart is empty cannot iterate through empty HashMap
                            for (String key : drinksInCart.keySet()) {
                                DrinkBill drinkBill = drinksInCart.get(key);
                                if(drinkBill.getDrinkId().equals(cafeDrink.getCafeDrinkId())) {
                                    cartDrink = true;
                                }
                            }
                        }
                        if(cartDrink) {
                            drinksInCart.remove(cafeDrink.getCafeDrinkId());
                            cartViewModel.setDrinksInCart(drinksInCart);
                        }

                        cafeCategoryRef = FirebaseDatabase.getInstance().getReference(
                                "cafes/" + cartViewModel.getCafeId().getValue() + "/cafeDrinksCategories/"
                                        + cafeCategoryId + "/cafeDrinks/" + cafeDrink.getCafeDrinkId());
                        cafeCategoryRef.removeValue();
                        toastMessage.showToast("Obrisano", 0);
                        checkEmptyCategory(cafeCategoryId);
                        if(!cafeDrink.getCafeDrinkImage().equals(
                                "https://firebasestorage.googleapis.com/v0/b/online-konobar-pica.appspot.com/o/images%2Fno_image.jpg?alt=media&token=fdb9ce0a-6695-45c6-815f-e3ebba362f12")) {
                            StorageReference deletedDrinkImage = FirebaseStorage.getInstance().getReferenceFromUrl(cafeDrink.getCafeDrinkImage());
                            deletedDrinkImage.delete();
                        }
                        dialog.dismiss();
                        insertCategoryDrinks(cafeCategoryId);
                    }
                });
            }
        });
        dialog.show();
    }

    public void checkEmptyCategory(String categoryId) {
        cafeCategoryRef = FirebaseDatabase.getInstance().getReference("cafes")
            .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories").child(categoryId)
            .child("cafeDrinks");
        cafeCategoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emptyCategory = true;
                if(!snapshot.exists()) {
                    //category is empty
                    Log.d("PROBA123", "NEMA");
                    DatabaseReference cafeCategoryDrink = FirebaseDatabase.getInstance().getReference("cafes")
                            .child(cartViewModel.getCafeId().getValue()).child("cafeDrinksCategories").child(categoryId);
                    cafeCategoryDrink.removeValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toastMessage.showToast(getResources().getString(R.string.unknown_error), 0);
            }
        });
    }


    /*  end of updating cafe menu*/

    //main and only function for updating cafe tables number
    public void updateTables() {
        View setTablesNumberView = getLayoutInflater().inflate(R.layout.settings_dialog_input, null);
        EditText etCurrentTables = setTablesNumberView.findViewById(R.id.settingsCurrentTablesNumber);
        EditText etNewTablesNumber = setTablesNumberView.findViewById(R.id.settingsNewTablesNumber);
        Button tableDialogCancel = setTablesNumberView.findViewById(R.id.settingsDialogCancel);
        Button tableDialogAccept = setTablesNumberView.findViewById(R.id.settingsDialogAccept);

        String cafeId = cartViewModel.getCafeId().getValue();
        etCurrentTables.setText(String.valueOf(cartViewModel.getCafeTables().getValue()));
        // Dialog modal for entering cafe tables number
        final AlertDialog settingsTableDialog = new AlertDialog.Builder(getActivity())
                .setView(setTablesNumberView)
                .setTitle(getResources().getString(R.string.settings_dialog_title_text))
                .create();
        settingsTableDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                etNewTablesNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() == 1 && editable.toString().equals("0")) {
                            etNewTablesNumber.setText("");
                            toastMessage.showToast(getResources().getString(R.string.settings_dialog_starting_zero), 0);
                        }
                    }
                });
                tableDialogAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etNewTablesNumber.length() > 0) {
                            if(Integer.parseInt(String.valueOf(etNewTablesNumber.getText())) > 1) {
                                Integer cafeTablesNumber = Integer.parseInt(String.valueOf(etNewTablesNumber.getText()));
                                cafeRef = FirebaseDatabase.getInstance().getReference("cafes").
                                child(cafeId).child("cafeTables");
                                cafeRef.setValue(cafeTablesNumber);
                                etCurrentTables.setText(String.valueOf(cafeTablesNumber));
                                etNewTablesNumber.setText("");
                                etNewTablesNumber.setHint("");
                                toastMessage.showToast(getResources().getString(R.string.settings_dialog_successful), 0);
                            }
                            else {
                                toastMessage.showToast(getResources().getString(R.string.settings_dialog_zero_greater), 0);
                            }
                        }
                        else {
                            toastMessage.showToast(getResources().getString(R.string.settings_dialog_zero_greater), 0);
                        }
                    }
                });
                tableDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingsTableDialog.dismiss();
                    }
                });
            }
        });
        settingsTableDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        newDrinkSecondConfirm = true;
        if(addingNewDrink)
            drinkDialogAccept.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if(data != null && data.getData() != null) {
                    drinkImage.setImageURI(data.getData());
                }
                break;
            case CAMERA_REQUEST_CODE:
                if(data != null) {
                    drinkImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}