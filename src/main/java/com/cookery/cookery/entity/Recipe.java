package com.cookery.cookery.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recipeID;

    @ManyToOne
    @JoinColumn(name= "userID", nullable=false)
    private User user;

    private String name;

    @Column(name = "cook_time")
    private String cookTime;

    private String instructions;

    @Column(nullable=true)
    private Double cost;
    private String descriptors;

    //Connect the RecipeIngredients to the Recipe as a list of recipe ingredients
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    public Long getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(Long recipeID) {
        this.recipeID = recipeID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(String descriptors) {
        this.descriptors = descriptors;
    }

    public void setUser(Long user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }

    //Adds the recipe ingredients from the model object to the recipes ingredient list
    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        if (recipeIngredients == null){
            recipeIngredients = new ArrayList<>();
        }
        this.recipeIngredients.add(recipeIngredient); // Add the RecipeIngredient to the list
        recipeIngredient.setRecipe(this); // Set the parent recipe in the RecipeIngredient
    }
}
