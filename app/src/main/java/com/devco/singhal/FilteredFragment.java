package com.devco.singhal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.adapters.SaveAdapter;
import com.devco.singhal.models.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FilteredFragment extends Fragment {
    String type = null;

    public FilteredFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filtered, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.filtered_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Bundle bundle;
        DatabaseReference productref = FirebaseDatabase.getInstance().getReference().child("Products");

        bundle = this.getArguments();
        if (bundle != null)
            type = bundle.getString("category");
        System.out.println(type);
        productref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> productsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Products products = dataSnapshot.getValue(Products.class);
                    if (type != null) {
                        if (products.getCategory().equals(type))
                            productsList.add(products);
                    }
                }
                SaveAdapter productsAdapter = new SaveAdapter(getContext(), productsList);
                recyclerView.setAdapter(productsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }
}