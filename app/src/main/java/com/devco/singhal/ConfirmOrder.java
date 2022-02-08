package com.devco.singhal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Empty;
import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrder extends AppCompatActivity {
    EditText shipname, shipphone, shipaddress, shipcity, shiptransport, shipcompany;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        shipname = findViewById(R.id.shipname);
        shipphone = findViewById(R.id.shipphone);
        shipaddress = findViewById(R.id.shipaddress);
        shipcity = findViewById(R.id.shipcity);
        shiptransport = findViewById(R.id.shiptransport);
        shipcompany = findViewById(R.id.shipcompany);
        next = findViewById(R.id.Coninuepayment);
        final String totalamount = getIntent().getStringExtra("Total Price");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Hello", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Order Placed Successfully")
                .setSmallIcon(R.drawable.baseline_shopping_cart_black_48)
                .setContentText("Your Order has been placed successfully")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        final DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shipname.setText(snapshot.child("name").getValue().toString());
                shipphone.setText(snapshot.child("phone").getValue().toString());
                if (snapshot.child("address").exists()) {
                    shipaddress.setText(snapshot.child("address").getValue().toString());
                }
                if (snapshot.child("city").exists()) {
                    shipcity.setText(snapshot.child("city").getValue().toString());
                }
                if (snapshot.child("transport").exists()) {
                    shiptransport.setText(snapshot.child("transport").getValue().toString());
                }
                if (snapshot.child("company").exists()) {
                    shipcompany.setText(snapshot.child("company").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        next.setOnClickListener(v -> {
            if (TextUtils.isEmpty(shipname.getText().toString())) {
                Empty.setError("Please Enter Your Name", shipname);
            } else if (TextUtils.isEmpty(shipcompany.getText().toString())) {
                Empty.setError("Please Enter Your Company", shipcompany);
            } else if (TextUtils.isEmpty(shipaddress.getText().toString())) {
                Empty.setError("Please Enter Your Address", shipaddress);
            } else if (TextUtils.isEmpty(shipphone.getText().toString())) {
                Empty.setError("Please Enter Your Phone Number", shipphone);
            } else if (TextUtils.isEmpty(shipcity.getText().toString())) {
                Empty.setError("Please Enter Your City", shipcity);
            } else if (!shipcity.getText().toString().trim().equalsIgnoreCase("indore") && TextUtils.isEmpty(shiptransport.getText().toString())) {
                Empty.setError("Please Enter Your Transport Details", shiptransport);
            } else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("address", shipaddress.getText().toString());
                hashMap.put("city", shipcity.getText().toString());
                hashMap.put("transport", shiptransport.getText().toString());
                dbr.updateChildren(hashMap);

                DatabaseReference orderref = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentonlineUser.getPhone());
                String savecurrenttime, savecurrentdate;
                Calendar callForDate = Calendar.getInstance();
                SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
                savecurrentdate = currentdate.format(callForDate.getTime());
                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
                savecurrenttime = currenttime.format(callForDate.getTime());
                HashMap<String, Object> ordermap = new HashMap<>();
                ordermap.put("totalamount", totalamount);
                ordermap.put("name", shipname.getText().toString());
                ordermap.put("company", shipcompany.getText().toString());
                ordermap.put("phone", shipphone.getText().toString());
                ordermap.put("address", shipaddress.getText().toString());
                ordermap.put("city", shipcity.getText().toString());
                ordermap.put("state", "Not Shipped");
                ordermap.put("time", savecurrenttime);
                ordermap.put("date", savecurrentdate);
                ordermap.put("transport", shiptransport.getText().toString());

                orderref.updateChildren(ordermap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomToast.makeText(ConfirmOrder.this, "Order Placed Successfully", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
                        startActivity(new Intent(ConfirmOrder.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Intent notificationIntent = new Intent(getApplicationContext(), Home.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(contentIntent);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                });
            }
        });
    }
}




