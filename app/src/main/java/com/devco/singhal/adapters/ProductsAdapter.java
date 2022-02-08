package com.devco.singhal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.devco.singhal.ProductDetails;
import com.devco.singhal.R;
import com.devco.singhal.models.Products;
import com.devco.singhal.tools.Liked;
import com.devco.singhal.tools.RecentViewed;
import com.like.LikeButton;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    final Context context;
    final List<Products> productsList;

    public ProductsAdapter(Context context, List<Products> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Products products = productsList.get(position);

        holder.txtproductname.setText(products.getPname());
        holder.txtproductprice.setText("MRP. ₹" + products.getPrice() + "/-");
        Glide.with(context).load(products.getImage().get(0)).placeholder(R.drawable.default_image)
                .apply(new RequestOptions().signature(new ObjectKey("imageLoader")))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v ->
        {
            Intent intent = new Intent(context, ProductDetails.class);
            intent.putExtra("pid", products.getPid());
            context.startActivity(intent);
            RecentViewed.clicked(products);
        });

        Liked.initlike(holder.likeButton, products);

    }

    @Override
    public int getItemCount() {
        return Math.min(productsList.size(), 15);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView txtproductname;
        public final TextView txtproductprice;
        public final ImageView imageView;
        final LikeButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtproductname = itemView.findViewById(R.id.productname);
            txtproductprice = itemView.findViewById(R.id.product_price);
            imageView = itemView.findViewById(R.id.product_image);
            likeButton = itemView.findViewById(R.id.product_save);

        }
    }
}
