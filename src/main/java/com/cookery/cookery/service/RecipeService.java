package com.cookery.cookery.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.CookeryApplication;
import com.cookery.cookery.entity.Ingredient;
import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.entity.RecipeIngredient;
import com.cookery.cookery.repository.IngredientRepository;
import com.cookery.cookery.repository.RecipeRepository;

@Service
public class RecipeService {

    private final CustomUserDetailsService customUserDetailsService;

    private final CookeryApplication cookeryApplication;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    RecipeService(CookeryApplication cookeryApplication, CustomUserDetailsService customUserDetailsService) {
        this.cookeryApplication = cookeryApplication;
        this.customUserDetailsService = customUserDetailsService;
    }

    public List<Recipe> findAll(){
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findById(Long id){
        return recipeRepository.findById(id);
    }

    public List<Recipe> findAllByUser(Long userId){
        return recipeRepository.findByUserId(userId);
    }

    //Associates the created list of ingredients in the recipe form the individual recipe upon being saved
    public void saveRecipeWithIngredients(Recipe recipe, List<Long> ingredientIds){
        Recipe savedRecipe = recipeRepository.save(recipe);
        for (Long ingredientId: ingredientIds){
            Optional<Ingredient> optionalIngredient = ingredientRepository.findById(ingredientId);
                if (optionalIngredient.isPresent()) { 
                    Ingredient ingredient = optionalIngredient.get();
                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipe(savedRecipe);
                    recipeIngredient.setIngredient(ingredient);
                    recipeIngredientService.save(recipeIngredient);
                    System.out.println("Saved ingredients " + ingredient.getName());
                } else {
                    System.err.println("Ingredient with ID " + ingredientId + " not saved");
                }
            }
            //Calculate price range for the recipe
            calculatePriceRange(savedRecipe);
        }
    
    //Calculate the price range for the recipe based off the ingredient price ranges
    public void calculatePriceRange(Recipe recipe){
        int recipeCostRange = 0;

        for (RecipeIngredient recipeingredient : recipe.getRecipeIngredients()) {
            recipeCostRange += recipeingredient.getIngredient().getPriceCategory();
        }

        if (recipeCostRange <= 6) {
            recipe.setCost(1.0);}
        else if (recipeCostRange >6 && recipeCostRange <12){
            recipe.setCost(2.0);}
        else {
            recipe.setCost(3.0);}
    }

    //CRUD FUNCTIONALITY BELOW
    public void save(Recipe recipe){
        calculatePriceRange(recipe);
        recipeRepository.save(recipe);
    }

    public void deleteById(Long id){
        recipeRepository.deleteById(id);
    }
}
