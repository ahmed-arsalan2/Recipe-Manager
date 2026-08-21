package com.example.recipe;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etCategory;
    private EditText etIngredients;
    private EditText etInstructions;

    private Button btnUpdate;

    private RealtimeDatabaseRepository repository;

    private String recipeId;

    private boolean currentFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        etTitle =
                findViewById(R.id.etEditTitle);

        etCategory =
                findViewById(R.id.etEditCategory);

        etIngredients =
                findViewById(R.id.etEditIngredients);

        etInstructions =
                findViewById(R.id.etEditInstructions);

        btnUpdate =
                findViewById(R.id.btnUpdateRecipe);

        recipeId =
                getIntent().getStringExtra("recipeId");

        repository =
                new RealtimeDatabaseRepository();

        if (recipeId == null || recipeId.isEmpty()) {
            finish();
            return;
        }

        btnUpdate.setOnClickListener(
                v -> updateRecipe()
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipe();
    }

    private void loadRecipe() {

        DatabaseReference recipes =
                repository.getRecipesReference();

        if (recipes == null) {
            finish();
            return;
        }

        recipes.child(recipeId)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                if (!snapshot.exists()) {
                                    finish();
                                    return;
                                }

                                Recipe recipe =
                                        snapshot.getValue(
                                                Recipe.class
                                        );

                                if (recipe == null) {
                                    return;
                                }

                                etTitle.setText(
                                        recipe.getTitle()
                                );

                                etCategory.setText(
                                        recipe.getCategory()
                                );

                                etIngredients.setText(
                                        recipe.getIngredients()
                                );

                                etInstructions.setText(
                                        recipe.getInstructions()
                                );

                                currentFavorite =
                                        recipe.isFavorite();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                Toast.makeText(
                                        EditRecipeActivity.this,
                                        "Failed to load recipe",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void updateRecipe() {

        String title =
                etTitle.getText().toString().trim();

        String category =
                etCategory.getText().toString().trim();

        String ingredients =
                etIngredients.getText().toString().trim();

        String instructions =
                etInstructions.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title is required");
            return;
        }

        if (title.length() > 100) {
            etTitle.setError("Title cannot exceed 100 characters");
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required");
            return;
        }

        if (TextUtils.isEmpty(ingredients)) {
            etIngredients.setError(
                    "Ingredients are required"
            );
            return;
        }

        if (TextUtils.isEmpty(instructions)) {
            etInstructions.setError(
                    "Instructions are required"
            );
            return;
        }

        DatabaseReference recipes =
                repository.getRecipesReference();

        if (recipes == null) {
            return;
        }

        Map<String, Object> updates =
                new HashMap<>();

        updates.put("title", title);
        updates.put("category", category);
        updates.put("ingredients", ingredients);
        updates.put("instructions", instructions);
        updates.put("favorite", currentFavorite);

        btnUpdate.setEnabled(false);

        recipes.child(recipeId)
                .updateChildren(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Recipe updated",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnUpdate.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to update recipe: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}