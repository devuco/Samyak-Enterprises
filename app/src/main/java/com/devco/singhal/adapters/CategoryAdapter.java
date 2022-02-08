package com.devco.singhal.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.devco.singhal.FilteredFragment;
import com.devco.singhal.R;
import com.devco.singhal.models.ModelCategory;
import com.like.LikeButton;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    final Context context;
    final List<ModelCategory> categoryList;

    public CategoryAdapter(Context context, List<ModelCategory> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        ModelCategory category = categoryList.get(position);
        holder.txtproductname.setText(category.getCname());
        holder.txtproductprice.setText("");
        holder.likeButton.setVisibility(View.GONE);
        Glide.with(context).load(category.getImage()).placeholder(R.drawable.default_image)
                .apply(new RequestOptions().signature(new ObjectKey("image")))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v ->
        {
            Bundle bundle = new Bundle();
            bundle.putString("category", category.getCname());
            bundle.putString("categories", "category");

            Fragment fragment = new FilteredFragment();
            fragment.setArguments(bundle);
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).addToBackStack("yes").commit();
        });

        holder.likeButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView txtproductname;
        public final TextView txtproductprice;
        public final ImageView imageView;
        final LikeButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtproductname = itemView.findViewById(R.id.save_name);
            txtproductprice = itemView.findViewById(R.id.save_price);
            imageView = itemView.findViewById(R.id.saved_image);
            likeButton = itemView.findViewById(R.id.saved_save);

        }
    }
}
