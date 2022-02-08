package com.devco.singhal.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.R;
import com.devco.singhal.interfaces.ItemClickListner;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView txtproductname;
    public final TextView txtproductprice;
    public final TextView txtproductquantity;
    public final TextView txtedit;
    public final TextView txtremove;
    public final TextView txttotal;
    public final ImageView cartproductimage;
    private ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txtproductname = itemView.findViewById(R.id.cart_product_name);
        txtproductquantity = itemView.findViewById(R.id.cart_product_quantity);
        txtproductprice = itemView.findViewById(R.id.cart_product_price);
        cartproductimage = itemView.findViewById(R.id.cart_product_image);
        txtedit = itemView.findViewById(R.id.cart_edit);
        txtremove = itemView.findViewById(R.id.cart_remove);
        txttotal = itemView.findViewById(R.id.cart_product_total);
    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v, getAdapterPosition(), false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
