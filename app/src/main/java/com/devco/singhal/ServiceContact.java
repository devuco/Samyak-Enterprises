package com.devco.singhal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.models.ModelService;
import com.devco.singhal.viewholders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ServiceContact extends AppCompatActivity {
    String type;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference serviceref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_contact);
        type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.service_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        serviceref = FirebaseDatabase.getInstance().getReference().child("Users");
        System.out.println(type);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ModelService> options = new FirebaseRecyclerOptions.Builder<ModelService>().setQuery(serviceref.orderByChild("type").startAt(type).endAt(type), ModelService.class).build();
        FirebaseRecyclerAdapter<ModelService, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ModelService, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ModelService modelService) {
                cartViewHolder.txtproductname.setText(modelService.getName() + "\n" + modelService.getType());
                cartViewHolder.txtproductquantity.setText(modelService.getPhone());
                cartViewHolder.txtproductprice.setText(modelService.getAddress());
                cartViewHolder.txttotal.setVisibility(View.GONE);
                cartViewHolder.txtedit.setVisibility(View.GONE);
                cartViewHolder.txtremove.setVisibility((View.GONE));
                if (modelService.getImage() == null) {
                    cartViewHolder.cartproductimage.setImageResource(R.drawable.profile);
                } else
                    Picasso.get().load(modelService.getImage()).into(cartViewHolder.cartproductimage);
                cartViewHolder.itemView.setOnClickListener(v ->
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + modelService.getPhone()))));
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                return new CartViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}