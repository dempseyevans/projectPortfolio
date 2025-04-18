package com.cookery.cookery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.entity.RecipeIngredient;
import com.cookery.cookery.repository.RecipeRepository;

@Service
public class RecipeGeneratorService {



    @Autowired
    private RecipeRepository recipeRepository;


    public Recipe generateRandomRecipe(String descriptorInput, String ingredientInput, Double costRange, Integer maxCookTime, String username) {

        //List of all existing recipes
        List<Recipe> allRecipes = recipeRepository.findByUserUsername(username);

        //List for filtered recipes
        List<Recipe> filteredRecipes = new ArrayList<>();

        //User input list
        //Separates the user input using the inputted commas and creates a list
        List<String> descriptorToken = new ArrayList<>();
        if(descriptorInput != null && descriptorInput.trim().isEmpty()){
            for(String token : descriptorInput.split(",")){
                if(!token.trim().isEmpty()) {
                    descriptorToken.add(token.trim().toLowerCase());
                }
            }
        }

        List<String> ingredientToken = new ArrayList<>();
        if(ingredientInput != null && ingredientInput.trim().isEmpty()){
            for(String token : ingredientInput.split(",")){
                if(!token.trim().isEmpty()){
                    ingredientToken.add(token.trim().toLowerCase());
                }
            }
        }

        for(Recipe recipe : allRecipes) {
            boolean matches = true;

            //Cost Range Filter
            if(costRange != null){

                if(recipe.getCost() == null || !recipe.getCost().equals(costRange)){
                    matches = false;
                }
            }

            // Descriptors filter.
            if (!descriptorToken.isEmpty()) {
                String recipeDescriptors = (recipe.getDescriptors() != null) ? recipe.getDescriptors().toLowerCase() : "";
                boolean found = false;
                for (String token : descriptorToken) {
                    if (recipeDescriptors.contains(token)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    matches = false;
                }
            }

            // Ingredient filter.
            if (!ingredientToken.isEmpty()) {
                boolean found = false;
                if (recipe.getRecipeIngredients() != null) {
                    for (RecipeIngredient ri : recipe.getRecipeIngredients()) {
                        String ingName = (ri.getIngredient() != null && ri.getIngredient().getName() != null)
                                            ? ri.getIngredient().getName().toLowerCase() : "";
                        for (String token : ingredientToken) {
                            if (ingName.contains(token)) {
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }
                if (!found) {
                    matches = false;
                }
            }

            //Max Cook Time filter
            if(maxCookTime != null){
                int recipeCookTime = parseCookTime(recipe.getCookTime());
                if (recipeCookTime > maxCookTime){
                    matches = false;
                }
            }

            //Add recipe to filtered list if matches is true
            if(matches){
                filteredRecipes.add(recipe);
            }
        }

        //Error for no found recipes
        if (filteredRecipes.isEmpty()){
            throw new NoSuchElementException("No recipes could be generated");
        }

        //Pick a random recipe from the filtered list
        Random random = new Random();
        return filteredRecipes.get(random.nextInt(filteredRecipes.size()));
        
    }

    //Parse cook time to string - Accounts for the recipe string cookTime vs the user input int
    private int parseCookTime(String cookTime){
        try{
            return Integer.parseInt(cookTime.replaceAll("[^0-9]", ""));
        }catch (NumberFormatException e){
            return Integer.MAX_VALUE; //Default to the maximum value if there is an error
        }
    }
    
}
