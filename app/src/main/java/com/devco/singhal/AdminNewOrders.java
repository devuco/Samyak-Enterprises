package com.devco.singhal;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devco.singhal.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminNewOrders extends AppCompatActivity {
    int count1 = 0;
    private DatabaseReference orderref;
    private RecyclerView orderlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderref = FirebaseDatabase.getInstance().getReference().child("Orders");
        orderlist = findViewById(R.id.order_list);
        orderlist.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(orderref, AdminOrders.class).build();
        final FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, final int i, @NonNull final AdminOrders adminOrders) {
                adminOrdersViewHolder.username.setText("Name : " + adminOrders.getName());
                adminOrdersViewHolder.userphone.setText("Phone : " + adminOrders.getPhone());
                adminOrdersViewHolder.userttlprice.setText("Total : " + "₹" + adminOrders.getTotalamount());
                adminOrdersViewHolder.datetime.setText("Order at : " + adminOrders.getDate() + " " + adminOrders.getTime());
                adminOrdersViewHolder.useraddress.setText("Address : " + adminOrders.getAddress() + " " + adminOrders.getCity());
                adminOrdersViewHolder.state.setText("Status : " + adminOrders.getState());
                adminOrdersViewHolder.transport.setText("Transport : " + adminOrders.getTransport());
                adminOrdersViewHolder.txtlrno.setText("LR No : " + adminOrders.getLrno());

                adminOrdersViewHolder.showordersbtn.setOnClickListener(v -> {
                    String uid = getRef(i).getKey();
                    startActivity(new Intent(AdminNewOrders.this, AdminUserProducts.class).putExtra("uid", uid));
                });

                adminOrdersViewHolder.lrno.setOnClickListener(v -> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AdminNewOrders.this);
                    alert.setTitle("LR No");
                    alert.setMessage("Please update Lr.no");
                    final EditText editText = new EditText(AdminNewOrders.this);
                    alert.setView(editText);
                    alert.setPositiveButton("Update", (dialog, which) -> {
                        String lr = editText.getText().toString();
                        HashMap<String, Object> hm = new HashMap<>();
                        hm.put("lrno", lr);
                        String uid = getRef(i).getKey();
                        orderref.child(uid).updateChildren(hm);
                    });
                    alert.setNegativeButton("Cancel", (dialog, which) -> {
                    });
                    alert.show();
                });

                adminOrdersViewHolder.itemView.setOnClickListener(v -> {
                    final CharSequence[] options1 = {"Yes", "No"};
                    final String uid = getRef(i).getKey();
                    orderref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child(uid).child("state").getValue().equals("Shipped")) {
                                    AlertDialog.Builder builderdeliver = new AlertDialog.Builder(AdminNewOrders.this);
                                    builderdeliver.setTitle("Have you Delivered this product?");
                                    builderdeliver.setItems(options1, (dialog, which) -> {
                                        if (which == 0) {
                                            String uid1 = getRef(i).getKey();
                                            HashMap<String, Object> hm = new HashMap<>();
                                            hm.put("state", "Delivered");
                                            orderref.child(uid1).updateChildren(hm);
                                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(uid1).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(uid1).removeValue();
                                            orderref.child(uid1).removeValue().addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    count1--;
                                                }
                                            });
                                            send_sms(uid1, "delivered");
                                        }
                                    }).show();
                                } else if (snapshot.child(uid).child("state").getValue().equals("Not Shipped")) {
                                    AlertDialog.Builder buildership = new AlertDialog.Builder(AdminNewOrders.this);
                                    buildership.setTitle("Have you shipped this product?");
                                    buildership.setItems(options1, (dialog, which) -> {
                                        if (which == 0) {
                                            String uid12 = getRef(i).getKey();
                                            HashMap<String, Object> hm = new HashMap<>();
                                            hm.put("state", "Shipped");
                                            orderref.child(uid12).updateChildren(hm);
                                            send_sms(uid12, "shipped");
                                        }
                                    }).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                });
            }

            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layput, parent, false);
                return new AdminOrdersViewHolder(view);
            }

        };

        orderlist.setAdapter(adapter);
        adapter.startListening();

        orderref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int count = adapter.getItemCount();

                    if (count > count1) {
                        send_notification();
                        count1 = count;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void send_sms(String uuid, String status) {
        int permission_check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (permission_check == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(uuid, null, "Greetings from singhal Ceramics\nYour order has been " + status + "\nThank You", null, null);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }

    void send_notification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Hello", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Order Placed Successfully")
                .setSmallIcon(R.drawable.baseline_shopping_cart_black_48)
                .setContentText("New Order")
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);
        Intent notificationIntent = new Intent(getApplicationContext(), AdminNewOrders.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {
        final TextView username;
        final TextView userphone;
        final TextView userttlprice;
        final TextView datetime;
        final TextView useraddress;
        final TextView state;
        final TextView transport;
        final TextView txtlrno;
        final Button showordersbtn;
        final Button lrno;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.order_username);
            userphone = itemView.findViewById(R.id.order_phone);
            userttlprice = itemView.findViewById(R.id.order_total_price);
            datetime = itemView.findViewById(R.id.order_date_time);
            useraddress = itemView.findViewById(R.id.order_address_city);
            showordersbtn = itemView.findViewById(R.id.order_show_button);
            state = itemView.findViewById(R.id.order_state);
            lrno = itemView.findViewById(R.id.order_lrno);
            txtlrno = itemView.findViewById(R.id.order_txtlrno);
            transport = itemView.findViewById(R.id.order_transport);
        }
    }
}