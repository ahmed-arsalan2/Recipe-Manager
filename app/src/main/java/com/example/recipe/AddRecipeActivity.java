package com.example.recipe;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etCategory;
    private EditText etIngredients;
    private EditText etInstructions;

    private Button btnSave;

    private RealtimeDatabaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        etTitle = findViewById(R.id.etTitle);
        etCategory = findViewById(R.id.etCategory);
        etIngredients = findViewById(R.id.etIngredients);
        etInstructions = findViewById(R.id.etInstructions);

        btnSave = findViewById(R.id.btnSaveRecipe);

        repository = new RealtimeDatabaseRepository();

        btnSave.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {

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
            etTitle.requestFocus();
            return;
        }

        if (title.length() > 100) {
            etTitle.setError("Title cannot exceed 100 characters");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required");
            etCategory.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ingredients)) {
            etIngredients.setError("Ingredients are required");
            etIngredients.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(instructions)) {
            etInstructions.setError("Instructions are required");
            etInstructions.requestFocus();
            return;
        }

        DatabaseReference recipes =
                repository.getRecipesReference();

        if (recipes == null) {

            Toast.makeText(
                    this,
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        btnSave.setEnabled(false);

        String recipeId =
                recipes.push().getKey();

        if (recipeId == null) {

            btnSave.setEnabled(true);

            Toast.makeText(
                    this,
                    "Failed to create recipe ID",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Recipe recipe = new Recipe(
                title,
                category,
                ingredients,
                instructions,
                false
        );

        recipe.setId(recipeId);

        recipes.child(recipeId)
                .setValue(recipe)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Recipe added",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnSave.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to add recipe: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}