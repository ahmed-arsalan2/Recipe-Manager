package com.example.recipe;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final Context context;
    private final List<Recipe> recipeList;
    private final DatabaseReference recipesReference;

    public RecipeAdapter(
            Context context,
            List<Recipe> recipeList,
            DatabaseReference recipesReference
    ) {
        this.context = context;
        this.recipeList = recipeList;
        this.recipesReference = recipesReference;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {

        Recipe recipe = recipeList.get(position);

        holder.tvTitle.setText(
                recipe.getTitle()
        );

        holder.tvCategory.setText(
                recipe.getCategory()
        );

        updateFavoriteButton(
                holder.btnFavorite,
                recipe.isFavorite()
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    RecipeDetailActivity.class
            );

            intent.putExtra(
                    "recipeId",
                    recipe.getId()
            );

            context.startActivity(intent);
        });

        holder.btnFavorite.setOnClickListener(v -> {

            if (recipesReference == null ||
                    recipe.getId() == null) {

                Toast.makeText(
                        context,
                        "Unable to update favorite",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            boolean newFavoriteState =
                    !recipe.isFavorite();

            holder.btnFavorite.setEnabled(false);

            recipesReference
                    .child(recipe.getId())
                    .child("favorite")
                    .setValue(newFavoriteState)
                    .addOnSuccessListener(unused -> {

                        recipe.setFavorite(
                                newFavoriteState
                        );

                        updateFavoriteButton(
                                holder.btnFavorite,
                                newFavoriteState
                        );

                        holder.btnFavorite.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {

                        holder.btnFavorite.setEnabled(true);

                        Toast.makeText(
                                context,
                                "Failed to update favorite",
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }

    private void updateFavoriteButton(
            ImageButton button,
            boolean favorite
    ) {

        if (favorite) {
            button.setImageResource(R.drawable.ic_favorite_filled);
            button.setContentDescription(
                    context.getString(R.string.cd_favorite_filled)
            );
        } else {
            button.setImageResource(R.drawable.ic_favorite_outline);
            button.setContentDescription(
                    context.getString(R.string.cd_favorite_outline)
            );
        }
    }

    public void updateRecipeList(
            List<Recipe> newList
    ) {

        recipeList.clear();

        recipeList.addAll(
                new ArrayList<>(newList)
        );

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvCategory;
        ImageButton btnFavorite;

        public RecipeViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvTitle =
                    itemView.findViewById(
                            R.id.tvItemTitle
                    );

            tvCategory =
                    itemView.findViewById(
                            R.id.tvItemCategory
                    );

            btnFavorite =
                    itemView.findViewById(
                            R.id.btnFavorite
                    );
        }
    }
}