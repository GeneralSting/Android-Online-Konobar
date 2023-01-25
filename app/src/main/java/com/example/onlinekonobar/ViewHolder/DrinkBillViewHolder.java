package com.example.onlinekonobar.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.R;

public class DrinkBillViewHolder extends RecyclerView.ViewHolder {
    public TextView txtDrinkBillName;
    public TextView txtDrinkBillPrice;
    public TextView txtDrinkBillTotalPrice;
    public ImageView drinkBillImageView;

    public TextView txtDrinkBillAddedNumber;
    public Button btnDrinkBillAdd;
    public Button btnDrinkBillRemove;

    private ItemClickListener itemClickListener;

    public DrinkBillViewHolder(View itemView) {
        super(itemView);

        txtDrinkBillName = (TextView) itemView.findViewById(R.id.cart_drink_name);
        txtDrinkBillPrice = (TextView) itemView.findViewById(R.id.cart_drink_price_value);
        txtDrinkBillTotalPrice = (TextView) itemView.findViewById(R.id.cart_drink_total_price_value);
        drinkBillImageView = (ImageView) itemView.findViewById(R.id.cart_drink_image);

        txtDrinkBillAddedNumber = (TextView) itemView.findViewById(R.id.cart_drink_added_number);
        btnDrinkBillAdd = (Button) itemView.findViewById(R.id.cart_drink_add);
        btnDrinkBillRemove = (Button) itemView.findViewById(R.id.cart_drink_remove);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
