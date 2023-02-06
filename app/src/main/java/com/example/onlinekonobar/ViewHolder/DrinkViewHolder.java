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

        txtDrinkName = (TextView) itemView.findViewById(R.id.drinkName);
        txtDrinkDescription = (TextView) itemView.findViewById(R.id.drinkDescription);
        txtDrinkPrice = (TextView) itemView.findViewById(R.id.drinkPrice);
        drinkImageView = (ImageView) itemView.findViewById(R.id.drinkImage);

        txtDrinkAddedNumber = (TextView) itemView.findViewById(R.id.drinkAmount);
        btnDrinkAdd = (Button) itemView.findViewById(R.id.drinkAdd);
        btnDrinkRemove = (Button) itemView.findViewById(R.id.drinkRemove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

}
