package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvIngredients;
    private TextView tvInstructions;
    private TextView tvAiDescription;

    private Button btnEdit;
    private Button btnDelete;
    private Button btnFavorite;
    private Button btnGenerateDescription;

    private RealtimeDatabaseRepository repository;
    private GeminiRepository geminiRepository;

    private String recipeId;

    private boolean currentFavorite;

    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvTitle =
                findViewById(R.id.tvDetailTitle);

        tvCategory =
                findViewById(R.id.tvDetailCategory);

        tvIngredients =
                findViewById(R.id.tvDetailIngredients);

        tvInstructions =
                findViewById(R.id.tvDetailInstructions);

        tvAiDescription =
                findViewById(R.id.tvAiDescription);

        btnEdit =
                findViewById(R.id.btnEditRecipe);

        btnDelete =
                findViewById(R.id.btnDeleteRecipe);

        btnFavorite =
                findViewById(R.id.btnDetailFavorite);

        btnGenerateDescription =
                findViewById(R.id.btnGenerateDescription);

        recipeId =
                getIntent().getStringExtra("recipeId");

        repository =
                new RealtimeDatabaseRepository();

        geminiRepository =
                new GeminiRepository();

        if (recipeId == null || recipeId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Recipe not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecipeDetailActivity.this,
                    EditRecipeActivity.class
            );

            intent.putExtra(
                    "recipeId",
                    recipeId
            );

            startActivity(intent);
        });

        btnDelete.setOnClickListener(v ->
                deleteRecipe()
        );

        btnFavorite.setOnClickListener(v ->
                toggleFavorite()
        );

        btnGenerateDescription.setOnClickListener(v ->
                generateAiDescription()
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

                                    Toast.makeText(
                                            RecipeDetailActivity.this,
                                            "Recipe not found",
                                            Toast.LENGTH_LONG
                                    ).show();

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

                                currentRecipe = recipe;

                                tvTitle.setText(
                                        recipe.getTitle()
                                );

                                tvCategory.setText(
                                        recipe.getCategory()
                                );

                                tvIngredients.setText(
                                        recipe.getIngredients()
                                );

                                tvInstructions.setText(
                                        recipe.getInstructions()
                                );

                                currentFavorite =
                                        recipe.isFavorite();

                                updateFavoriteButton();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                Toast.makeText(
                                        RecipeDetailActivity.this,
                                        "Failed to load recipe",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void generateAiDescription() {

        if (currentRecipe == null) {

            Toast.makeText(
                    this,
                    "Recipe is still loading",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnGenerateDescription.setEnabled(false);

        btnGenerateDescription.setText(
                "Generating..."
        );

        tvAiDescription.setVisibility(
                View.VISIBLE
        );

        tvAiDescription.setText(
                "Generating recipe description..."
        );

        geminiRepository.generateRecipeDescription(
                currentRecipe,
                new GeminiRepository.GeminiCallback() {

                    @Override
                    public void onSuccess(
                            String description
                    ) {

                        runOnUiThread(() -> {

                            tvAiDescription.setText(
                                    description
                            );

                            btnGenerateDescription.setEnabled(
                                    true
                            );

                            btnGenerateDescription.setText(
                                    "Generate AI Description"
                            );
                        });
                    }

                    @Override
                    public void onError(
                            Throwable error
                    ) {

                        runOnUiThread(() -> {

                            tvAiDescription.setVisibility(
                                    View.GONE
                            );

                            btnGenerateDescription.setEnabled(
                                    true
                            );

                            btnGenerateDescription.setText(
                                    "Generate AI Description"
                            );

                            Toast.makeText(
                                    RecipeDetailActivity.this,
                                    "Failed to generate description: "
                                            + error.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
        );
    }

    private void toggleFavorite() {

        DatabaseReference recipes =
                repository.getRecipesReference();

        if (recipes == null) {
            return;
        }

        boolean newFavoriteState =
                !currentFavorite;

        btnFavorite.setEnabled(false);

        recipes.child(recipeId)
                .child("favorite")
                .setValue(newFavoriteState)
                .addOnSuccessListener(unused -> {

                    currentFavorite =
                            newFavoriteState;

                    updateFavoriteButton();

                    btnFavorite.setEnabled(true);
                })
                .addOnFailureListener(e -> {

                    btnFavorite.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to update favorite",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void updateFavoriteButton() {

        if (currentFavorite) {

            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_favorite_filled, 0, 0, 0
            );

            btnFavorite.setContentDescription(
                    getString(R.string.cd_favorite_filled)
            );

        } else {

            btnFavorite.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_favorite_outline, 0, 0, 0
            );

            btnFavorite.setContentDescription(
                    getString(R.string.cd_favorite_outline)
            );
        }
    }

    private void deleteRecipe() {

        DatabaseReference recipes =
                repository.getRecipesReference();

        if (recipes == null) {
            return;
        }

        btnDelete.setEnabled(false);

        recipes.child(recipeId)
                .removeValue()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Recipe deleted",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnDelete.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to delete recipe: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onDestroy() {

        if (geminiRepository != null) {
            geminiRepository.shutdown();
        }

        super.onDestroy();
    }
}