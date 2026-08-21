package com.example.recipe;

public class Recipe {

    private String id;
    private String title;
    private String category;
    private String ingredients;
    private String instructions;
    private boolean favorite;
    private long createdAt;

    public Recipe() {
        // Required by Firebase Realtime Database
    }

    public Recipe(
            String title,
            String category,
            String ingredients,
            String instructions,
            boolean favorite
    ) {
        this.title = title;
        this.category = category;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.favorite = favorite;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}