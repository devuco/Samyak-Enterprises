package com.devco.singhal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Empty;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button create;
    EditText name, phone, password, confirm_password;
    Spinner type;
    String[] list;
    ArrayAdapter<String> adapter;
    HashMap<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        create = findViewById(R.id.Register);
        name = findViewById(R.id.Name_reg);
        phone = findViewById(R.id.Phone_reg);
        password = findViewById(R.id.Password_reg);
        confirm_password = findViewById(R.id.confirm_Password_reg);
        type = findViewById(R.id.Type_reg);
        map = new HashMap<>();
        list = new String[]{"---Please select user type---", "Customer", "Plumber", "Painter", "Carpenter", "Electrician", "Contractor"};
        adapter = new ArrayAdapter<>(Register.this, R.layout.spinner_layout, list);
        type.setAdapter(adapter);
        create.setOnClickListener(v -> create_account());
    }

    private void create_account() {
        String Name = name.getText().toString();
        String Phone = phone.getText().toString();
        String Password = password.getText().toString();
        String Confirm_Password = confirm_password.getText().toString();
        String Type = type.getSelectedItem().toString();
        if (TextUtils.isEmpty(Name)) {
            Empty.setError("Please Enter Your Name", name);
        } else if (TextUtils.isEmpty(Phone)) {
            Empty.setError("Please Enter Phone", phone);
        } else if (Phone.length() != 10) {
            Empty.setError("Please Enter a Valid 10 digit Phone Number", phone);
        } else if ((Type.equals("---Please select user type---"))) {
            CustomToast.makeText(Register.this, "Please Select a User Type", Toast.LENGTH_SHORT, Color.RED);
        } else if (TextUtils.isEmpty(Password)) {
            Empty.setError("Please enter a password", password);
        } else if (TextUtils.isEmpty(Confirm_Password) || !Confirm_Password.equals(Password)) {
            Empty.setError("Passwords do not Match", confirm_password);
        } else {
            map.put("name", Name);
            map.put("password", Password);
            map.put("phone", Phone);
            map.put("type", Type);
            Intent intent = new Intent(this, OTP.class);
            intent.putExtra("phone", Phone);
            intent.putExtra("map", map);
            startActivity(intent);
        }
    }
}