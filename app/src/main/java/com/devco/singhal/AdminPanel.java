package com.devco.singhal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

public class AdminPanel extends AppCompatActivity {
    Button view_customers, edit_product, new_category, new_product, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        new_product = findViewById(R.id.new_product);
        view_customers = findViewById(R.id.view_customers);
        edit_product = findViewById(R.id.edit_product);
        new_category = findViewById(R.id.new_category);
        logout = findViewById(R.id.logout_admin);

        new_product.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, AdminProductAdd.class)));
        view_customers.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, ServiceContact.class).putExtra("type", "Customer")));
        edit_product.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, Home.class).putExtra("Admin", "Admin")));
        new_category.setOnClickListener(v -> startActivity(new Intent(AdminPanel.this, NewCategory.class)));
        logout.setOnClickListener(v -> {
            Paper.book().destroy();
            startActivity(new Intent(this, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });
    }
}