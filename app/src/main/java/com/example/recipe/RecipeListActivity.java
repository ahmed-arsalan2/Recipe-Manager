package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class RecipeListActivity extends AppCompatActivity {

    private Button btnLogout;
    private EditText etSearchRecipe;
    private Spinner spinnerCategory;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddRecipe;
    private android.view.View emptyState;

    private RecipeAdapter adapter;

    private final List<Recipe> recipeList =
            new ArrayList<>();

    private final List<String> categoryList =
            new ArrayList<>();

    private RealtimeDatabaseRepository repository;

    private String selectedCategory =
            "All Categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        etSearchRecipe =
                findViewById(R.id.etSearchRecipe);

        spinnerCategory =
                findViewById(R.id.spinnerCategory);

        recyclerView =
                findViewById(R.id.recyclerViewRecipes);

        fabAddRecipe =
                findViewById(R.id.fabAddRecipe);

        emptyState =
                findViewById(R.id.emptyState);

        btnLogout =
                findViewById(R.id.btnLogout);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        repository =
                new RealtimeDatabaseRepository();

        adapter = new RecipeAdapter(
                this,
                new ArrayList<>(),
                repository.getRecipesReference()
        );

        recyclerView.setAdapter(adapter);

        fabAddRecipe.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RecipeListActivity.this,
                    AddRecipeActivity.class
            );

            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    RecipeListActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        etSearchRecipe.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filterRecipes();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }
                }
        );

        spinnerCategory.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {

                        selectedCategory =
                                categoryList.get(position);

                        filterRecipes();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        loadRecipes();
    }

    private void loadRecipes() {

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

        recipes
                .orderByChild("createdAt")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                recipeList.clear();

                                TreeSet<String> categories =
                                        new TreeSet<>(
                                                String.CASE_INSENSITIVE_ORDER
                                        );

                                for (DataSnapshot child :
                                        snapshot.getChildren()) {

                                    Recipe recipe =
                                            child.getValue(
                                                    Recipe.class
                                            );

                                    if (recipe != null) {

                                        if (recipe.getId() == null) {
                                            recipe.setId(
                                                    child.getKey()
                                            );
                                        }

                                        recipeList.add(recipe);

                                        if (recipe.getCategory() != null
                                                && !recipe.getCategory()
                                                .trim()
                                                .isEmpty()) {

                                            categories.add(
                                                    recipe.getCategory()
                                                            .trim()
                                            );
                                        }
                                    }
                                }

                                updateCategorySpinner(
                                        categories
                                );

                                filterRecipes();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                Toast.makeText(
                                        RecipeListActivity.this,
                                        "Failed to load recipes: "
                                                + error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void updateCategorySpinner(
            TreeSet<String> categories
    ) {

        String previousSelection =
                selectedCategory;

        categoryList.clear();

        categoryList.add("All Categories");
        categoryList.add("Favorites");

        categoryList.addAll(categories);

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryList
                );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(
                spinnerAdapter
        );

        int selectedPosition =
                categoryList.indexOf(
                        previousSelection
                );

        if (selectedPosition >= 0) {

            spinnerCategory.setSelection(
                    selectedPosition
            );

        } else {

            selectedCategory =
                    "All Categories";

            spinnerCategory.setSelection(0);
        }
    }

    private void filterRecipes() {

        String searchQuery =
                etSearchRecipe.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        List<Recipe> filteredList =
                new ArrayList<>();

        for (Recipe recipe : recipeList) {

            String title =
                    recipe.getTitle() == null
                            ? ""
                            : recipe.getTitle()
                            .toLowerCase();

            String category =
                    recipe.getCategory() == null
                            ? ""
                            : recipe.getCategory()
                            .toLowerCase();

            boolean matchesSearch =
                    title.contains(searchQuery)
                            || category.contains(searchQuery);

            boolean matchesCategory;

            if ("All Categories".equals(
                    selectedCategory
            )) {

                matchesCategory = true;

            } else if ("Favorites".equals(
                    selectedCategory
            )) {

                matchesCategory =
                        recipe.isFavorite();

            } else {

                matchesCategory =
                        category.equals(
                                selectedCategory.toLowerCase()
                        );
            }

            if (matchesSearch && matchesCategory) {
                filteredList.add(recipe);
            }
        }

        adapter.updateRecipeList(
                filteredList
        );

        if (emptyState != null) {

            emptyState.setVisibility(
                    filteredList.isEmpty()
                            ? android.view.View.VISIBLE
                            : android.view.View.GONE
            );
        }
    }
}