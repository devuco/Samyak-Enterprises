package com.devco.singhal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.models.ModelCart;
import com.devco.singhal.viewholders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminUserProducts extends AppCompatActivity {
    RecyclerView.LayoutManager layoutManager;
    String puid = "";
    private RecyclerView productslist;
    private DatabaseReference cartref;

    @Override
    protected void onStart() {
        super.onStart();
        recycle(cartref);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        puid = getIntent().getStringExtra("User");
        String uid = getIntent().getStringExtra("uid");

        if (uid == null) {
            cartref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(puid).child("Products");
        } else if (puid == null) {
            cartref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(uid).child("Products");
        }

        productslist = findViewById(R.id.products_list);
        productslist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productslist.setLayoutManager(layoutManager);
    }

    void recycle(DatabaseReference cartrefs) {
        FirebaseRecyclerOptions<ModelCart> options = new FirebaseRecyclerOptions.Builder<ModelCart>().setQuery(cartrefs, ModelCart.class).build();
        FirebaseRecyclerAdapter<ModelCart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ModelCart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ModelCart cart) {
                cartViewHolder.txtproductquantity.setText("Quantity= " + cart.getQuantity());
                cartViewHolder.txtproductname.setText(cart.getPname());
                cartViewHolder.txtproductprice.setText("Price=" + "₹" + cart.getPrice());
                Picasso.get().load(cart.getImage()).into(cartViewHolder.cartproductimage);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordered_items_layout, parent, false);
                return new CartViewHolder(view);
            }
        };

        productslist.setAdapter(adapter);
        adapter.startListening();
    }

}