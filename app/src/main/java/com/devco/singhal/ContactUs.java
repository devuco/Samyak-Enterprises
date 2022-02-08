package com.devco.singhal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ContactUs extends AppCompatActivity {

    ImageView map;
    RelativeLayout whatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        map = findViewById(R.id.map);
        whatsapp = findViewById(R.id.whatsapp);

        map.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/maps/9sw4s52KamTG6K7D8"))));
        whatsapp.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=919770060032"))));
    }
}