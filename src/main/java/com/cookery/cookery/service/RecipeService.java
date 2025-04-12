package com.cookery.cookery.service;

import java.util.ArrayList;
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
    private IngredientService ingredientService;

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    RecipeService(CookeryApplication cookeryApplication, CustomUserDetailsService customUserDetailsService) {
        this.cookeryApplication = cookeryApplication;
        this.customUserDetailsService = customUserDetailsService;
    }

    //Find all existing recipes
    public List<Recipe> findAll(){
        return recipeRepository.findAll();
    }

    //Find recipes by ID
    public Optional<Recipe> findById(Long id){
        return recipeRepository.findById(id);
    }

    //Find recipe by User
    public List<Recipe> findAllByUser(Long userId){
        return recipeRepository.findByUserId(userId);
    }

    //Save ingredients from recipe form
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
    
    //Get Ingredients with Quantities
    public List<Ingredient> findAvailableIngredients(Recipe recipe) {
        List<Ingredient> allIngredients = ingredientService.findAll();
        List<RecipeIngredient> recipeIngredients = recipe.getRecipeIngredients();
        List<Ingredient> available = new ArrayList<>();
        
        for (Ingredient ingredient : allIngredients) {
            boolean found = false;
            for (RecipeIngredient ri : recipeIngredients) {
                if (ri.getIngredient().getId() == ingredient.getId()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                available.add(ingredient);
            }
        }
        return available;
    }

    //Search for recipes
    public List<Recipe> searchRecipe(String query, String username){

        List<Recipe> filteredRecipes = new ArrayList<>();
        // Retrieve recipes belonging to the logged-in user
        List<Recipe> allRecipes = recipeRepository.findByUserUsername(username);
        String lowerCaseQuery = query.toLowerCase();
        
        // Loop through each recipe
        for (Recipe recipe : allRecipes) {
            boolean matchFound = false;
            
            // Check the recipe's name
            if (recipe.getName() != null && recipe.getName().toLowerCase().contains(lowerCaseQuery)) {
                matchFound = true;
            }
            // Check the descriptors
            else if (recipe.getDescriptors() != null && recipe.getDescriptors().toLowerCase().contains(lowerCaseQuery)) {
                matchFound = true;
            }
            // Check the instructions
            else if (recipe.getInstructions() != null && recipe.getInstructions().toLowerCase().contains(lowerCaseQuery)) {
                matchFound = true;
            }
            // Check the cost, converting it to a string
            else if (String.valueOf(recipe.getCost()).contains(lowerCaseQuery)) {
                matchFound = true;
            }
            // Check if any of the associated ingredients match
            else if (recipe.getRecipeIngredients() != null) {
                for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                    if (ri.getIngredient() != null && ri.getIngredient().getName() != null) {
                        if (ri.getIngredient().getName().toLowerCase().contains(lowerCaseQuery)) {
                            matchFound = true;
                            break;
                        }
                    }
                }
            }
            
            // If any field matched, add the recipe to the results list
            if (matchFound) {
                filteredRecipes.add(recipe);
            }
        }
        
        return filteredRecipes;
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
