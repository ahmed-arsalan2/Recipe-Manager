package com.example.recipe;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RealtimeDatabaseRepository {

    private final DatabaseReference recipesReference;

    public RealtimeDatabaseRepository() {

        FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            recipesReference = null;
            return;
        }

        String uid = currentUser.getUid();

        recipesReference =
                FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(uid)
                        .child("recipes");
    }

    public DatabaseReference getRecipesReference() {
        return recipesReference;
    }
}