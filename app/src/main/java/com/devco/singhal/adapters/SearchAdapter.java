package com.devco.singhal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.ProductDetails;
import com.devco.singhal.R;
import com.devco.singhal.models.Products;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {

    final Context context;
    final List<Products> list;
    final String input;


    public SearchAdapter(Context context, List<Products> products, String input) {
        this.context = context;
        this.list = products;
        this.input = input;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.ordered_items_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Products products = list.get(position);
        holder.name.setText(products.getPname());
        holder.des.setText(products.getDescription());
        holder.price.setText("MRP. ₹" + products.getPrice() + "/-");
        Picasso.get().load(products.getImage().get(0)).into(holder.img);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("pid", products.getPid());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView des;
        final TextView price;
        final ImageView img;
        final CardView card;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_product_name);
            des = itemView.findViewById(R.id.cart_product_quantity);
            price = itemView.findViewById(R.id.cart_product_price);
            img = itemView.findViewById(R.id.cart_product_image);
            card = itemView.findViewById(R.id.card1);
        }

    }
}
