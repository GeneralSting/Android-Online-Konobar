package com.example.onlinekonobar.ui.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.Models.Category;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.ViewHolder.MenuViewHolder;
import com.example.onlinekonobar.databinding.FragmentGalleryBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private FirebaseDatabase database;
    DatabaseReference category;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerMenu;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    DatabaseReference categoriesRef, usersRef;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
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

        categoriesRef = FirebaseDatabase.getInstance().getReference("drinksCategories");
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
                                    Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}