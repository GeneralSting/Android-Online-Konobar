package com.example.onlinekonobar.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.R;

public class UpdateDrinkViewHolder extends RecyclerView.ViewHolder{
    public TextView txtDrinkName, txtDrinkDescription, txtDrinkPrice;
    public ImageView drinkImageView;
    public ImageButton btnAcceptUpdate, btnDeleteDrink;

    private ItemClickListener itemClickListener;

    public UpdateDrinkViewHolder(View itemView) {
        super(itemView);

        txtDrinkName = (TextView) itemView.findViewById(R.id.settingsUpdateDrinkName);
        txtDrinkDescription = (TextView) itemView.findViewById(R.id.settingsUpdateDrinkDescription);
        txtDrinkPrice = (TextView) itemView.findViewById(R.id.settingsUpdateDrinkPrice);
        drinkImageView = (ImageView) itemView.findViewById(R.id.settingsUpdateDrinkImage);

        btnAcceptUpdate = (ImageButton) itemView.findViewById(R.id.btnSettingsUpdateAccept);
        btnDeleteDrink = (ImageButton) itemView.findViewById(R.id.btnSettingsUpdateRemove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
