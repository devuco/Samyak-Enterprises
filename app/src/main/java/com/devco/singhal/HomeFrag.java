package com.devco.singhal;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.adapters.BannerAdapter;
import com.devco.singhal.adapters.ProductsAdapter;
import com.devco.singhal.models.Products;
import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HomeFrag extends Fragment {

    RecyclerView recyclerView, recent_recycler;
    DatabaseReference productref;
    SliderView sliderView;
    DatabaseReference recentref;
    TextView recenttxt;

    public HomeFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = v.findViewById(R.id.recycler_menu);
        recent_recycler = v.findViewById(R.id.recent_recycler);
        sliderView = v.findViewById(R.id.banner_slider);
        recenttxt = v.findViewById(R.id.recent_txt);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        sliderView.startAutoCycle();

        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recent_recycler.setItemViewCacheSize(50);
        recent_recycler.setDrawingCacheEnabled(true);
        recent_recycler.setHasFixedSize(true);
        recent_recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recent_recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


        productref = FirebaseDatabase.getInstance().getReference().child("Products");
        recentref = FirebaseDatabase.getInstance().getReference().child("Recent_Viewed").child(Prevalent.currentonlineUser.getPhone());


        productref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    productsList.add(products);
                    Collections.shuffle(productsList);
                }
                ProductsAdapter productsAdapter = new ProductsAdapter(getContext(), productsList);
                recyclerView.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recentref.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    productsList.add(products);
                }
                if (productsList.isEmpty()) {
                    recenttxt.setVisibility(View.GONE);
                }
                Collections.reverse(productsList);
                ProductsAdapter productsAdapter = new ProductsAdapter(getContext(), productsList);
                recent_recycler.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        List<Integer> imagelist = new ArrayList<>();
        imagelist.add(R.drawable.asian_paints);
        imagelist.add(R.drawable.lifetimewarranty);
        imagelist.add(R.drawable.astral);
        BannerAdapter adapter = new BannerAdapter(getContext(), imagelist);
        sliderView.setSliderAdapter(adapter);

        return v;
    }
}