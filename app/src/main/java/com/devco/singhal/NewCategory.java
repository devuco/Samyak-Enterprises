package com.devco.singhal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.devco.singhal.AdminProductAdd.Gallerypick;

public class NewCategory extends AppCompatActivity {

    ImageView category_image;
    EditText category_name_txt;
    Button add_category;
    DatabaseReference catref;
    String category_name, downloadurl;
    StorageReference catstore;
    StorageReference filepath;
    Uri imageuri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        category_image = findViewById(R.id.category_image);
        category_name_txt = findViewById(R.id.category_name);
        add_category = findViewById(R.id.add_category);
        catref = FirebaseDatabase.getInstance().getReference().child("Categories");
        catstore = FirebaseStorage.getInstance().getReference().child("Category Images");


        category_image.setOnClickListener(v -> OpenGallery());

        add_category.setOnClickListener(v -> {
            filepath = catstore.child(imageuri.getLastPathSegment() + ".jpg");
            final UploadTask uploadTask = filepath.putFile(imageuri);
            uploadTask.addOnFailureListener(e -> {
                String messgae = e.toString();
                Toast.makeText(NewCategory.this, messgae, Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(NewCategory.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }

                    downloadurl = filepath.getDownloadUrl().toString();
                    return filepath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        downloadurl = task.getResult().toString();
                        Toast.makeText(NewCategory.this, "Product image url uploaded ", Toast.LENGTH_SHORT).show();
                        SaveProductinfo();
                    }
                });
            });
        });
    }

    private void SaveProductinfo() {
        category_name = category_name_txt.getText().toString();
        HashMap<String, Object> productmap = new HashMap<>();
        productmap.put("image", downloadurl);
        productmap.put("cname", category_name);
        catref.child(category_name).updateChildren(productmap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NewCategory.this, "Product is added Succesfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewCategory.this, AdminPanel.class).addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else {
                String msg = task.getException().toString();
                Toast.makeText(NewCategory.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void OpenGallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, Gallerypick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallerypick && resultCode == RESULT_OK && data != null) {
            imageuri = data.getData();
            category_image.setImageURI(imageuri);
        }
    }
}