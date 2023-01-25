package com.example.onlinekonobar.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.R;

public class DrinkViewHolder extends RecyclerView.ViewHolder {
    public TextView txtDrinkName;
    public TextView txtDrinkDescription;
    public TextView txtDrinkPrice;
    public ImageView drinkImageView;

    public TextView txtDrinkAddedNumber;
    public Button btnDrinkAdd;
    public Button btnDrinkRemove;

    private ItemClickListener itemClickListener;

    public DrinkViewHolder(View itemView) {
        super(itemView);

        txtDrinkName = (TextView) itemView.findViewById(R.id.drink_name);
        txtDrinkDescription = (TextView) itemView.findViewById(R.id.drink_description);
        txtDrinkPrice = (TextView) itemView.findViewById(R.id.drink_price);
        drinkImageView = (ImageView) itemView.findViewById(R.id.drink_image);

        txtDrinkAddedNumber = (TextView) itemView.findViewById(R.id.drink_added_number);
        btnDrinkAdd = (Button) itemView.findViewById(R.id.drink_add);
        btnDrinkRemove = (Button) itemView.findViewById(R.id.drink_remove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
