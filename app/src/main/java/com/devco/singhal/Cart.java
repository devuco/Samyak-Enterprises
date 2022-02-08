package com.devco.singhal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.models.ModelCart;
import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Prevalent;
import com.devco.singhal.viewholders.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Cart extends AppCompatActivity {
    TextView txttotalamount;
    int Totalprice = 0;
    private RecyclerView recyclerView;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        next = findViewById(R.id.next_process_btn);
        txttotalamount = findViewById(R.id.cart_total_price);
        Home.load.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartlstref = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<ModelCart> options = new FirebaseRecyclerOptions.Builder<ModelCart>()
                .setQuery(cartlstref.child("User View")
                        .child(Prevalent.currentonlineUser.getPhone()).child("Products"), ModelCart.class).build();

        final FirebaseRecyclerAdapter<ModelCart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ModelCart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final ModelCart cart) {
                cartViewHolder.txtproductquantity.setText("Quantity = " + cart.getQuantity());
                cartViewHolder.txtproductname.setText(cart.getPname());
                cartViewHolder.txtproductprice.setText("Price = " + "₹" + cart.getPrice());
                int onecardprice = (Integer.parseInt(cart.getPrice())) * (Integer.parseInt(cart.getQuantity()));
                cartViewHolder.txttotal.setText("Total Price = " + "₹" + onecardprice);
                Picasso.get().load(cart.getImage()).into(cartViewHolder.cartproductimage);
                Totalprice = Totalprice + onecardprice;
                txttotalamount.setText("Total Price = Rs." + Totalprice);
                cartViewHolder.txtedit.setOnClickListener(v -> {
                    (Cart.this).finish();
                    (Cart.this).overridePendingTransition(0, 0);
                    startActivity(Cart.this.getIntent());
                    (Cart.this).overridePendingTransition(0, 0);
                    startActivity(new Intent(Cart.this, ProductDetails.class).putExtra("pid", cart.getPid()));

                });
                cartViewHolder.txtremove.setOnClickListener(v -> {
                    cartlstref.child("Admin View").child(Prevalent.currentonlineUser.getPhone()).child("Products").child(cart.getPid()).removeValue();
                    cartlstref.child("User View").child(Prevalent.currentonlineUser.getPhone()).child("Products").child(cart.getPid()).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CustomToast.makeText(Cart.this, "Removed Successfully", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
                            (Cart.this).finish();
                            (Cart.this).overridePendingTransition(0, 0);
                            startActivity(Cart.this.getIntent());
                            (Cart.this).overridePendingTransition(0, 0);
                        }
                    });
                });
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

        next.setOnClickListener(v -> startActivity(new Intent(Cart.this, ConfirmOrder.class).putExtra("Total Price", String.valueOf(Totalprice))));
    }
}