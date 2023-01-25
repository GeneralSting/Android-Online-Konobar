package com.example.onlinekonobar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onlinekonobar.Interfaces.CallBackCart;
import com.example.onlinekonobar.Models.DrinkBill;
import com.example.onlinekonobar.R;
import com.example.onlinekonobar.ViewHolder.DrinkBillViewHolder;
import com.example.onlinekonobar.ui.cart.CartViewModel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<DrinkBillViewHolder> {

    Context context;
    HashMap<String, DrinkBill> cartDrinks;
    CallBackCart callBackCart;

    public CartAdapter(Context context, HashMap<String, DrinkBill> cartDrinks, CallBackCart callBackCart) {
        this.context = context;
        this.cartDrinks = cartDrinks;
        this.callBackCart = callBackCart;
    }

    @NonNull
    @Override
    public DrinkBillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_drink_item, parent, false);

        return new DrinkBillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkBillViewHolder holder, int position) {
        int i = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        HashMap<String, DrinkBill> newCartDrinks = new HashMap<>();
        for (Map.Entry<String, DrinkBill> entry : cartDrinks.entrySet()) {
            if(position == i){
                DrinkBill drinkBill = entry.getValue();
                holder.txtDrinkBillName.setText(drinkBill.getDrinkName());
                holder.txtDrinkBillPrice.setText(decimalFormat.format(drinkBill.getDrinkPrice()) + "€");
                holder.txtDrinkBillTotalPrice.setText(decimalFormat.format(drinkBill.getDrinkTotalPrice()) + "€");
                holder.txtDrinkBillAddedNumber.setText(String.valueOf(drinkBill.getDrinkAmount()));
                if(drinkBill.getDrinkImage().equals("")) {
                    Glide.with(context)
                            .load("https://firebasestorage.googleapis.com/v0/b/online-konobar-pica.appspot.com/o/images%2Fno_image.jpg?alt=media&token=fdb9ce0a-6695-45c6-815f-e3ebba362f12")
                            .into(holder.drinkBillImageView);
                }
                else {
                    Glide.with(context).load(drinkBill.getDrinkImage()).into(holder.drinkBillImageView);
                }

                holder.btnDrinkBillAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        drinkBill.setDrinkAmount((drinkBill.getDrinkAmount() + 1));
                        drinkBill.setDrinkTotalPrice((Float) (drinkBill.getDrinkPrice() * drinkBill.getDrinkAmount()));
                        holder.txtDrinkBillTotalPrice.setText(decimalFormat.format(drinkBill.getDrinkTotalPrice()) + "€");
                        holder.txtDrinkBillAddedNumber.setText(String.valueOf(drinkBill.getDrinkAmount()));
                        cartDrinks.put(drinkBill.getDrinkId(), drinkBill);
                        callBackCart.updateCartDrinks(cartDrinks);
                    }
                });

                holder.btnDrinkBillRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(drinkBill.getDrinkAmount() > 0) {
                            drinkBill.setDrinkAmount((drinkBill.getDrinkAmount() - 1));
                            if(drinkBill.getDrinkAmount() > 0) {
                                drinkBill.setDrinkTotalPrice((Float) (drinkBill.getDrinkPrice() * drinkBill.getDrinkAmount()));
                                holder.txtDrinkBillTotalPrice.setText(decimalFormat.format(drinkBill.getDrinkTotalPrice()) + "€");
                                holder.txtDrinkBillAddedNumber.setText(String.valueOf(drinkBill.getDrinkAmount()));
                                cartDrinks.put(drinkBill.getDrinkId(), drinkBill);
                                callBackCart.updateCartDrinks(cartDrinks);
                            }
                            else {
                                cartDrinks.remove(drinkBill.getDrinkId());
                                callBackCart.updateCartDrinks(cartDrinks);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, cartDrinks.size());
                            }
                        }
                    }
                });
                break;
            }
            i++;
        }
    }

    @Override
    public int getItemCount() {
        return cartDrinks.size();
    }
}
