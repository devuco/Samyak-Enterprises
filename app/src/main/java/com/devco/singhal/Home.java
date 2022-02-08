package com.devco.singhal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Prevalent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class Home extends AppCompatActivity {
    static ProgressBar load;
    DatabaseReference productref;
    BottomNavigationView bottomNavigationView;
    Fragment home, cat, service, saved, pending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        String type = getIntent().getStringExtra("Admin");

        ImageView search = findViewById(R.id.home_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Paper.init(this);
        load = findViewById(R.id.loadingbarhome);
        load.setVisibility(View.GONE);
        productref = FirebaseDatabase.getInstance().getReference().child("Products");
        bottomNavigationView = findViewById(R.id.bottom_nav_bar);

        home = new HomeFrag();
        cat = new CategoriesFragment();
        service = new ServiceFrag();
        saved = new SavedProductsFrag();
        pending = new PendingOrderFrag();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFrag()).commit();

        FloatingActionButton fab = findViewById(R.id.fab);
        if (type != null)
            if (type.equals("Admin")) {
                fab.setVisibility(View.GONE);
            }

        fab.setOnClickListener(view -> mycart());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    fragment = home;
                    break;
                case R.id.bottom_category:
                    fragment = cat;
                    break;
                case R.id.bottom_saved:
                    fragment = saved;
                    break;
                case R.id.bottom_service:
                    fragment = service;
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment, fragment.getTag()).commit();
            return true;
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        toggle.getDrawerArrowDrawable().setBarThickness(11f);
        toggle.getDrawerArrowDrawable().setBarLength(60f);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (type != null) {
            navigationView.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
        } else
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
//                if (id == R.id.nav_category) {
//                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new CategoriesFragment()).commit();
//                    drawer.closeDrawers();
//                }
                if (id == R.id.nav_profile) {
                    startActivity(new Intent(Home.this, Profile.class));
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(Home.this, Login.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    Paper.book().destroy();
                    finish();
                } else if (id == R.id.nav_contact_us) {
                    startActivity(new Intent(Home.this, ContactUs.class));
                } else if (id == R.id.nav_orders) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PendingOrderFrag()).addToBackStack("home").commit();
                    drawer.closeDrawers();
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(Home.this, Settings.class));
                }
                return false;
            });

        View headerview = navigationView.getHeaderView(0);
        TextView username = headerview.findViewById(R.id.nav_username);
        CircleImageView profileImagView = headerview.findViewById(R.id.profile_image);
        if (type == null) {
            username.setText("Welcome\n" + Prevalent.currentonlineUser.getName());
            Picasso.get().load(Prevalent.currentonlineUser.getImage()).placeholder(R.drawable.profile).into(profileImagView);
        }
        profileImagView.setOnClickListener(v -> startActivity(new Intent(Home.this, Profile.class)));
        username.setOnClickListener(v -> startActivity(new Intent(Home.this, Profile.class)));

        search.setOnClickListener(v -> startActivity(new Intent(Home.this, SearchProducts.class)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    void mycart() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(Prevalent.currentonlineUser.getPhone()))
                    CustomToast.makeText(Home.this, "Your Cart is Empty", Toast.LENGTH_SHORT, Color.RED);
                else {
                    load.setVisibility(View.VISIBLE);
                    startActivity(new Intent(Home.this, Cart.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}