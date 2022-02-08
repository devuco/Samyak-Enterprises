package com.devco.singhal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.tools.CustomToast;
import com.devco.singhal.tools.Empty;
import com.devco.singhal.tools.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    CircleImageView profileimageview;
    EditText fullnameedittxt, phoneedittxt, addressedittxt, passwordedittxt, companytxt;
    TextView profilechange, close, update;
    Uri imageUri;
    String myurl = "";
    StorageReference storageReferenceprofilepic;
    String checker = "";
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileimageview = findViewById(R.id.settingsprofileimage);
        fullnameedittxt = findViewById(R.id.settingsfullname);
        phoneedittxt = findViewById(R.id.settingsphonenumber);
        addressedittxt = findViewById(R.id.settingsaddress);
        profilechange = findViewById(R.id.settingsprofilechange);
        close = findViewById(R.id.settingsclose);
        update = findViewById(R.id.settingsupdate);
        passwordedittxt = findViewById(R.id.settingspassword);
        companytxt = findViewById(R.id.settingscompany);
        storageReferenceprofilepic = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userinfodisplay(profileimageview, fullnameedittxt, phoneedittxt, addressedittxt, passwordedittxt);
        close.setOnClickListener(v -> finish());
        update.setOnClickListener(v -> {
            if (checker.equals("clicked")) {
                userinfosaved();
            } else {
                updateonlyuserinfo();
            }
        });
        profilechange.setOnClickListener(v -> {
            checker = "clicked";
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                    .start(Profile.this);
        });
        profileimageview.setOnClickListener(v -> {
            checker = "clicked";
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                    .start(Profile.this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileimageview.setImageURI(imageUri);
        } else {
            CustomToast.makeText(this, "Try Again", Toast.LENGTH_SHORT, Color.RED);
            startActivity(new Intent(Profile.this, Profile.class));
            finish();
        }
    }

    private void updateonlyuserinfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullnameedittxt.getText().toString());
        userMap.put("address", addressedittxt.getText().toString());
        userMap.put("phoneOrder", phoneedittxt.getText().toString());
        userMap.put("password", passwordedittxt.getText().toString());
        ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(Profile.this, Home.class));
        CustomToast.makeText(Profile.this, "Profile Info Updated Succesfully", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
        finish();
    }

    private void userinfosaved() {
        if (TextUtils.isEmpty(fullnameedittxt.getText().toString())) {
            Empty.setError("Name is Mandatory", fullnameedittxt);
        } else if (TextUtils.isEmpty(phoneedittxt.getText().toString())) {
            Empty.setError("Phone Number Cannot be Empty", phoneedittxt);
        } else if (TextUtils.isEmpty(addressedittxt.getText().toString())) {
            Empty.setError("Address is Mandatory", addressedittxt);
        } else if (TextUtils.isEmpty(passwordedittxt.getText().toString())) {
            Empty.setError("Password is Mandatory", passwordedittxt);
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait while we update your account");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null) {
            final StorageReference fileref = storageReferenceprofilepic.child(Prevalent.currentonlineUser.getPhone() + ".jpg");
            uploadTask = fileref.putFile(imageUri);
            uploadTask.continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileref.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadURL = task.getResult();
                    myurl = downloadURL.toString();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("name", fullnameedittxt.getText().toString());
                    userMap.put("address", addressedittxt.getText().toString());
                    userMap.put("phoneOrder", phoneedittxt.getText().toString());
                    userMap.put("image", myurl);
                    userMap.put("password", passwordedittxt.getText().toString());
                    ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);

                    progressDialog.dismiss();
                    startActivity(new Intent(Profile.this, Home.class));
                    CustomToast.makeText(Profile.this, "Profile Info Updated Succesfully", Toast.LENGTH_SHORT, Color.parseColor("#32CD32"));
                    finish();

                } else {
                    Toast.makeText(Profile.this, "Empty", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Image is not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userinfodisplay(final CircleImageView profileimageview, final EditText fullnameedittxt, final EditText phoneedittxt, final EditText addressedittxt, final EditText passwordedittxt) {
        DatabaseReference userref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("image").exists()) {
                        String image = snapshot.child("image").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileimageview);
                        addressedittxt.setText(address);
                    }
                    String phone = snapshot.child("phone").getValue().toString();
                    phoneedittxt.setText(phone);
                    String name = snapshot.child("name").getValue().toString();
                    fullnameedittxt.setText(name);
                    String password = snapshot.child("password").getValue().toString();
                    passwordedittxt.setText(password);
                    String company = snapshot.child("type").getValue().toString();
                    companytxt.setText(company);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}