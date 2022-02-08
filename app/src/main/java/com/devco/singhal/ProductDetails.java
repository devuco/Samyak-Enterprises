package com.devco.singhal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.devco.singhal.adapters.SliderAdapter;
import com.devco.singhal.models.Products;
import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Liked;
import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.smarteist.autoimageslider.SliderView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetails extends AppCompatActivity {
    static ProgressBar loadingproductdetails;
    String productID = "";
    String imageuri, prices;
    TextView cantorder;
    Button addtocart;
    ElegantNumberButton numberButton;
    SliderView sliderView;
    LikeButton likeButton;
    private TextView price, description, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        addtocart = findViewById(R.id.add_to_cart_btn);
        numberButton = findViewById(R.id.number_btn);
        price = findViewById(R.id.Product_price_details);
        description = findViewById(R.id.Product_description_details);
        name = findViewById(R.id.Product_name_details);
        cantorder = findViewById(R.id.Product_cannot_order);
        likeButton = findViewById(R.id.product_details_save);
        Home.load.setVisibility(View.GONE);
        productID = getIntent().getStringExtra("pid");
        loadingproductdetails = findViewById(R.id.loadingbarproductdetails);
        loadingproductdetails.setVisibility(View.GONE);
        sliderView = findViewById(R.id.product_image_details);


        getProductDetails(productID);
        pendingorder();

    }

    private void pendingorder() {
        DatabaseReference cartlistref = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentonlineUser.getPhone());
        cartlistref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("state").getValue().toString().equals("Shipped")) {
                        cantorder.setVisibility(View.VISIBLE);
                        addtocart.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProductDetails(String productID) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        DatabaseReference productRef1 = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(Prevalent.currentonlineUser.getPhone()).child("Products").child(productID);
        productRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    numberButton.setNumber(snapshot.child("quantity").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    name.setText(products.getPname());
                    price.setText("₹" + products.getPrice() + "/-");
                    description.setText("Description :\n" + products.getDescription());

                    List<String> list = products.getImage();
                    sliderView.setSliderAdapter(new SliderAdapter(ProductDetails.this, list));
                    sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
                    sliderView.setScrollTimeInSec(3);
                    sliderView.setAutoCycle(true);
                    sliderView.startAutoCycle();

                    imageuri = products.getImage().get(0);

                    prices = products.getPrice();
                    Liked.initlike(likeButton, products);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

        addtocart.setOnClickListener(v -> {
            if (Integer.parseInt(numberButton.getNumber()) == 0) {
                CustomToast.makeText(ProductDetails.this, "Please Select Quantity", Toast.LENGTH_SHORT, Color.RED);
            } else {
                loadingproductdetails.setVisibility(View.VISIBLE);
                addingtocartlist();
            }
        });
    }

    private void addingtocartlist() {
        String savecurrenttime, savecurrentdate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentdate = currentdate.format(callForDate.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
        savecurrenttime = currenttime.format(callForDate.getTime());

        final DatabaseReference cartlistRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", name.getText().toString());
        cartMap.put("price", prices);
        cartMap.put("date", savecurrentdate);
        cartMap.put("time", savecurrenttime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("image", imageuri);
        cartMap.put("discount", "");

        cartlistRef.child("User View").child(Prevalent.currentonlineUser.getPhone()).child("Products").child(productID)
                .updateChildren(cartMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartlistRef.child("Admin View").child(Prevalent.currentonlineUser.getPhone()).child("Products").child(productID)
                        .updateChildren(cartMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        CustomToast.makeText(ProductDetails.this, "Added to Cart", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
                        finish();
                    }
                });
            }
        });

    }
}