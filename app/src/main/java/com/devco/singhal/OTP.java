package com.devco.singhal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.tools.CustomToast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTP extends AppCompatActivity {
    Button button_verify;
    EditText otp_txt;
    FirebaseAuth mauth;
    String otpid;
    String phone_number;
    HashMap<String, Object> map;
    DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        otp_txt = findViewById(R.id.pin_view);
        button_verify = findViewById(R.id.verify);
        mauth = FirebaseAuth.getInstance();

        phone_number = getIntent().getStringExtra("phone");

        userref = FirebaseDatabase.getInstance().getReference().child("Users");

        Intent intent = getIntent();
        map = (HashMap<String, Object>) intent.getSerializableExtra("map");

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mauth)
                .setPhoneNumber("+91" + phone_number)
                .setTimeout(10L, TimeUnit.SECONDS)
                .setActivity(OTP.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signinwithcredentials(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        System.out.println(e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        otpid = s;
                    }
                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        button_verify.setOnClickListener(v ->
        {
            String otp = otp_txt.getText().toString();
            if (otp.isEmpty()) {
                CustomToast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT, Color.RED);
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpid, otp);
                signinwithcredentials(credential);
            }
        });
    }


    private void signinwithcredentials(PhoneAuthCredential phoneAuthCredential) {
        mauth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userref.child(phone_number).updateChildren(map);
                startActivity(new Intent(OTP.this, Login.class));
                finish();
            }
        });
    }
}