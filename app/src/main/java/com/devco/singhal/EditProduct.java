package com.devco.singhal;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.devco.singhal.models.Products;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class EditProduct extends AppCompatActivity {
    static final int Gallerypick = 1;
    ImageView img;
    String Savedate, Savetime, productkey;
    Button update;
    EditText productname, productdes, productprice;
    Spinner Category;
    ArrayList<String> SpinnerList;
    ArrayAdapter adapter;
    DatabaseReference catref;
    Uri imageuri;
    StorageReference filepath;
    StorageReference productstore;
    List<String> downloadurl;
    private String id = "";
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        id = getIntent().getStringExtra("pid");
        img = findViewById(R.id.edit_image_select);
        update = findViewById(R.id.edit_addproduct);
        productname = findViewById(R.id.edit_productname);
        productdes = findViewById(R.id.edit_productdes);
        productprice = findViewById(R.id.edit_productprice);
        Category = findViewById(R.id.edit_productcategory);
        catref = FirebaseDatabase.getInstance().getReference().child("Categories");
        downloadurl = new ArrayList<>();
        productstore = FirebaseStorage.getInstance().getReference().child("Product Images");


        SpinnerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(EditProduct.this, android.R.layout.simple_spinner_dropdown_item, SpinnerList);
        CategorySpinner();
        Category.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference().child("Products").child(id);

        img.setOnClickListener(v -> OpenGallery());

        displaySpecificProductInfo();
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

    private void StorageImageInformation() {

//        loadingbar.setTitle("Add new Product");
//        loadingbar.setMessage("Please wait while we add new product");
//        loadingbar.setCanceledOnTouchOutside(false);
//        loadingbar.show();


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
            Toast.makeText(EditProduct.this, messgae, Toast.LENGTH_SHORT).show();
//            loadingbar.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(EditProduct.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    downloadurl.add(task.getResult().toString());
                    System.out.println("on complete " + downloadurl);
                    Toast.makeText(EditProduct.this, "Product image url uploaded ", Toast.LENGTH_SHORT).show();

                }
            });
        });
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

    private void displaySpecificProductInfo() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);
                    productname.setText(products.getPname());
                    productprice.setText(products.getPrice());
                    productdes.setText(products.getDescription());
                    Picasso.get().load(products.getImage().get(0)).into(img);

                    update.setOnClickListener(v -> {
                                String pname = productname.getText().toString();
                                String pprice = productprice.getText().toString();
                                String pdescription = productdes.getText().toString();
                                String category = Category.getSelectedItem().toString();

                                List<String> list = products.getImage();
                                List<String> newList = new ArrayList<>(list);
                                newList.addAll(downloadurl);
                                System.out.println(newList);

                                if (TextUtils.isEmpty(pname)) {
                                    Toast.makeText(EditProduct.this, "Name is empty", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(pprice)) {
                                    Toast.makeText(EditProduct.this, "Price is empty", Toast.LENGTH_SHORT).show();
                                } else if (TextUtils.isEmpty(pdescription)) {
                                    Toast.makeText(EditProduct.this, "Description is empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    HashMap<String, Object> updatemap = new HashMap<>();
                                    updatemap.put("pid", id);
                                    updatemap.put("description", pdescription);
                                    updatemap.put("price", pprice);
                                    updatemap.put("pname", pname);
                                    updatemap.put("category", category);
                                    updatemap.put("image", newList);

                                    reference.updateChildren(updatemap).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(EditProduct.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(EditProduct.this, AdminPanel.class).addFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            finish();
                                        }
                                    });
                                }
                            }
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}