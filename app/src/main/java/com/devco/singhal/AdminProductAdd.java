package com.devco.singhal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AdminProductAdd extends AppCompatActivity {
    static final int Gallerypick = 1;
    String category;
    String Des, Price, Name;
    String Savedate, Savetime, productkey;
    List<String> downloadurl;
    ImageView img;
    Button add;
    EditText productname, productdes, productprice;
    Spinner categories;
    Uri imageuri;
    StorageReference productstore;
    DatabaseReference productref;
    ProgressDialog loadingbar;
    StorageReference filepath;
    ArrayAdapter<String> adapter;
    ArrayList<String> SpinnerList;
    DatabaseReference catref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_product_add);
        img = findViewById(R.id.image_select);
        add = findViewById(R.id.addproduct);
        categories = findViewById(R.id.product_category);
        productname = findViewById(R.id.productname);
        productdes = findViewById(R.id.productdes);
        productprice = findViewById(R.id.productprice);
        loadingbar = new ProgressDialog(this);
        downloadurl = new ArrayList<>();
        productstore = FirebaseStorage.getInstance().getReference().child("Product Images");
        productref = FirebaseDatabase.getInstance().getReference().child("Products");
        catref = FirebaseDatabase.getInstance().getReference().child("Categories");

        SpinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(AdminProductAdd.this, android.R.layout.simple_spinner_dropdown_item, SpinnerList);
        CategorySpinner();
        categories.setAdapter(adapter);

        img.setOnClickListener(v -> OpenGallery());

        add.setOnClickListener(v -> Validateproductdata());

    }

    private void CategorySpinner() {
        catref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    SpinnerList.add(item.child("cname").getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void Validateproductdata() {
        Name = productname.getText().toString();
        Des = productdes.getText().toString();
        Price = productprice.getText().toString();
        category = categories.getSelectedItem().toString();
        if (imageuri == null) {
            Toast.makeText(this, "Product image empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Des)) {
            Toast.makeText(this, "Description Empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "Name Empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Price Empty", Toast.LENGTH_SHORT).show();
        } else
            SaveProductinfo();

    }

    private void StorageImageInformation() {
        loadingbar.setTitle("Add new Product");
        loadingbar.setMessage("Please wait while we add new product");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentdate = new SimpleDateFormat("MMM dd,yyyy");
        Savedate = currentdate.format(calendar.getTime());
        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss a");
        Savetime = currenttime.format(calendar.getTime());
        productkey = Savedate + Savetime;

        filepath = productstore.child(imageuri.getLastPathSegment() + productkey + ".jpg");
        final UploadTask uploadTask = filepath.putFile(imageuri);
        uploadTask.addOnFailureListener(e -> {
            String messgae = e.toString();
            Toast.makeText(AdminProductAdd.this, messgae, Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AdminProductAdd.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadurl.add(task.getResult().toString());
                    System.out.println("on complete " + downloadurl);
                    Toast.makeText(AdminProductAdd.this, "Product image url uploaded ", Toast.LENGTH_SHORT).show();

                }
            });
        });
    }

    private void SaveProductinfo() {
        HashMap<String, Object> productmap = new HashMap<>();
        productmap.put("pid", productkey);
        productmap.put("date", Savedate);
        productmap.put("time", Savetime);
        productmap.put("description", Des);
        productmap.put("image", downloadurl);
        productmap.put("category", category);
        productmap.put("price", Price);
        productmap.put("pname", Name);
        productref.child(productkey).updateChildren(productmap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminProductAdd.this, "Product is added Succesfully", Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
                startActivity(new Intent(AdminProductAdd.this, AdminPanel.class).addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            } else {
                String msg = task.getException().toString();
                Toast.makeText(AdminProductAdd.this, msg, Toast.LENGTH_SHORT).show();
                loadingbar.dismiss();
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
            img.setImageURI(imageuri);
            StorageImageInformation();
        }
    }
}