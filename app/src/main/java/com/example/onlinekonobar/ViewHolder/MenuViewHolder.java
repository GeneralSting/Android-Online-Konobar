package com.example.onlinekonobar.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinekonobar.Interfaces.ItemClickListener;
import com.example.onlinekonobar.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuDescription;
    public ImageView imageView;
    public TextView txtMenuName;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView) itemView.findViewById(R.id.menu_name);
        imageView = (ImageView) itemView.findViewById(R.id.menu_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void onClick(View view) {
        //null
        itemClickListener.onClick (view,getAdapterPosition(), false);
    }
}
