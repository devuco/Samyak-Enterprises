package com.devco.singhal.tools;

import com.devco.singhal.models.Products;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RecentViewed {

    public static void clicked(Products products) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Recent_Viewed").child(Prevalent.currentonlineUser.getPhone());
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        String savecurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        String savecurrentTime = currentTime.format(calendar.getTime());

        String productRandomkey = savecurrentDate + savecurrentTime;
        products.setDate(productRandomkey);
        databaseReference.child(products.getPid()).setValue(products);
    }
}
