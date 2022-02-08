package com.devco.singhal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.models.Users;
import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Empty;
import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Login extends AppCompatActivity {
    EditText phone, password;
    Button login;
    ProgressDialog loadingbar;
    String Parentdbname = "Users";
    TextView register;
    ToggleButton Admin;
    ImageView white;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        phone = findViewById(R.id.Phone);
        password = findViewById(R.id.Password);
        login = findViewById(R.id.Log_in_in);
        register = findViewById(R.id.register);
        white = findViewById(R.id.frontpage);
        loadingbar = new ProgressDialog(this);
        Admin = findViewById(R.id.admin);
        Paper.init(this);

        Admin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                login.setText("Login Admin");
                Parentdbname = "Admin";
            } else if (!isChecked) {
                login.setText("Login User");
                Parentdbname = "Users";
            }
        });


        register.setOnClickListener(v -> startActivity(new Intent(this, Register.class)));

        String UserPhonekey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordkey = Paper.book().read(Prevalent.UserPasswordKey);


        if (UserPhonekey != "" && UserPasswordkey != "") {
            if (!TextUtils.isEmpty(UserPhonekey) && !TextUtils.isEmpty(UserPasswordkey)) {
                if (UserPhonekey.equals("6264133175") || UserPhonekey.equals("9770060032"))
                    Parentdbname = "Admin";
                white.setVisibility(View.VISIBLE);
                AllowAccess(UserPhonekey, UserPasswordkey);
            }
        }

        login.setOnClickListener(v -> LoginUser());
    }

    private void LoginUser() {
        String Phone = phone.getText().toString();
        String Password = password.getText().toString();
        if (TextUtils.isEmpty(Phone))
            Empty.setError("Please Enter Your Phone Number", phone);
        else if (TextUtils.isEmpty(Password))
            Empty.setError("Please Enter Your Password", password);
        else {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Please wait while we check the credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            AllowAccesstoAccount(Phone, Password);
        }
    }

    private void AllowAccesstoAccount(final String phone, final String password) {
        Paper.book().write(Prevalent.UserPhoneKey, phone);
        Paper.book().write(Prevalent.UserPasswordKey, password);

        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Parentdbname).child(phone).exists()) {
                    Users usersdata = snapshot.child(Parentdbname).child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if (Parentdbname.equals("Admin")) {
                                Toast.makeText(Login.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Prevalent.currentonlineUser = usersdata;
                                startActivity(new Intent(Login.this, AdminPanel.class));
                            } else if (Parentdbname.equals("Users")) {
                                loadingbar.dismiss();
                                Prevalent.currentonlineUser = usersdata;
                                startActivity(new Intent(Login.this, Home.class).addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                        } else {
                            CustomToast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT, Color.RED);
                            loadingbar.dismiss();
                        }
                    }
                } else {
                    CustomToast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT, Color.RED);
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void AllowAccess(final String phone, final String password) {

        DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(Parentdbname).child(phone).exists()) {
                    Users usersdata = snapshot.child(Parentdbname).child(phone).getValue(Users.class);
                    if (usersdata.getPhone().equals(phone)) {
                        if (usersdata.getPassword().equals(password)) {
                            if (Parentdbname.equals("Admin")) {
                                Toast.makeText(Login.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Prevalent.currentonlineUser = usersdata;
                                startActivity(new Intent(Login.this, AdminPanel.class));
                            } else if (Parentdbname.equals("Users")) {
                                loadingbar.dismiss();
                                Prevalent.currentonlineUser = usersdata;
                                startActivity(new Intent(Login.this, Home.class).addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            }
                        } else {
                            CustomToast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT, Color.RED);
                            loadingbar.dismiss();
                        }
                    }
                } else {
                    CustomToast.makeText(Login.this, "Account does not exist", Toast.LENGTH_SHORT, Color.RED);
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}


