package com.devco.singhal.tools;

import androidx.annotation.NonNull;

import com.devco.singhal.models.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;

public class Liked {


    public static void initlike(LikeButton likeButton, Products products) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved").child(Prevalent.currentonlineUser.getPhone());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(products.getPid()).exists()) {
                    likeButton.setLiked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                databaseReference.child(products.getPid()).setValue(products);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                databaseReference.child(products.getPid()).removeValue();
            }
        });
    }

}
