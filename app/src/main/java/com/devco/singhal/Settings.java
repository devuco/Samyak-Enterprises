package com.devco.singhal;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.FirebaseDatabase;

public class Settings extends AppCompatActivity {

    TextView change_password, clear_recent, clear_wishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        change_password = findViewById(R.id.change_password);
        clear_recent = findViewById(R.id.clear_recent);
        clear_wishlist = findViewById(R.id.clear_wishlist);

        change_password.setOnClickListener(v -> startActivity(new Intent(Settings.this, ChangePassword.class)));
        clear_recent.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this)
                    .setTitle("Clear Recently Viewed")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> FirebaseDatabase.getInstance().getReference()
                            .child("Recent_Viewed")
                            .child(Prevalent.currentonlineUser.getPhone())
                            .removeValue())
                    .setNegativeButton("No", null);
            alert.show();
        });
        clear_wishlist.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this)
                    .setTitle("Clear Wishlist")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> FirebaseDatabase.getInstance().getReference()
                            .child("Saved")
                            .child(Prevalent.currentonlineUser.getPhone())
                            .removeValue())
                    .setNegativeButton("No", null);
            alert.show();
        });
    }
}