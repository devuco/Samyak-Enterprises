package com.devco.singhal;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.adapters.SearchAdapter;
import com.devco.singhal.models.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchProducts extends AppCompatActivity {

    String searchinput;
    private EditText inputtxt;
    private RecyclerView searchlist;

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        if (TextUtils.isEmpty(searchinput))
            return;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Products> prolist = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Products products = data.getValue(Products.class);
                    if (products.getPname().toUpperCase().contains(searchinput.toUpperCase()) || products.getCategory().toUpperCase().contains(searchinput.toUpperCase()))
                        prolist.add(products);
                }
                SearchAdapter searchAdapter = new SearchAdapter(SearchProducts.this, prolist, searchinput);
                searchlist.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        inputtxt = findViewById(R.id.search_product_name);
        searchlist = findViewById(R.id.search_list);
        searchlist.setLayoutManager(new LinearLayoutManager(SearchProducts.this));

        inputtxt.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inputtxt, InputMethodManager.SHOW_IMPLICIT);

        inputtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchinput = inputtxt.getText().toString();
                if (TextUtils.isEmpty(searchinput))
                    return;
                onStart();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}