package com.example.onlinekonobar.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.R;

public class UpdateDrinkViewHolder extends RecyclerView.ViewHolder{
    public EditText txtDrinkName, txtDrinkDescription, txtDrinkPrice;
    public ImageView drinkImageView, oldDrinkImageView;
    public ImageButton btnAcceptUpdate, btnDeleteDrink;

    private ItemClickListener itemClickListener;

    public UpdateDrinkViewHolder(View itemView) {
        super(itemView);

        txtDrinkName = (EditText) itemView.findViewById(R.id.settingsUpdateDrinkName);
        txtDrinkDescription = (EditText) itemView.findViewById(R.id.settingsUpdateDrinkDescription);
        txtDrinkPrice = (EditText) itemView.findViewById(R.id.settingsUpdateDrinkPrice);
        drinkImageView = (ImageView) itemView.findViewById(R.id.settingsUpdateDrinkImage);
        oldDrinkImageView = (ImageView) itemView.findViewById(R.id.settingsUpdateOldImage);

        btnAcceptUpdate = (ImageButton) itemView.findViewById(R.id.btnSettingsUpdateAccept);
        btnDeleteDrink = (ImageButton) itemView.findViewById(R.id.btnSettingsUpdateRemove);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
