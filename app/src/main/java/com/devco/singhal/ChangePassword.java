package com.devco.singhal;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.models.Users;
import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.paperdb.Paper;

public class ChangePassword extends AppCompatActivity {
    public List<Users> mpost;
    EditText pass, conpass, oldtxt;
    Button reset;
    DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpassword);
        pass = findViewById(R.id.ed_pass);
        conpass = findViewById(R.id.ed_confipass);
        reset = findViewById(R.id.resetpass);
        oldtxt = findViewById(R.id.ed_oldpass);

        reset.setOnClickListener(view -> {

            String newpassword = pass.getText().toString();
            String confirmpassword = conpass.getText().toString();
            String oldpass = oldtxt.getText().toString();
            userref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    String data_old = users.getPassword();
                    if (data_old.equals(oldpass)) {
                        if (confirmpassword.equals(newpassword)) {
                            userref.child("password").setValue(newpassword).addOnCompleteListener(task -> {
                                CustomToast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
                                finish();
                                Paper.book().write(Prevalent.UserPasswordKey, newpassword);
                            });
                        } else {
                            conpass.setError("Passwords do not match");
                            conpass.requestFocus();
                        }
                    } else {
                        oldtxt.setError("Password Entered is incorrect");
                        oldtxt.requestFocus();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });
    }
}