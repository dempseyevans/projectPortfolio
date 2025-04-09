package com.cookery.cookery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.repository.RecipeGeneratorRepository;

@Service
public class RecipeGeneratorService {


    @Autowired
    private RecipeGeneratorRepository recipeGeneratorRepository;


    public Recipe generateRandomRecipe(Double costRange, String descriptor, Integer maxCookTime) {

        //List of all existing recipes
        List<Recipe> allRecipes = recipeGeneratorRepository.findAll();

        //List for filtered recipes
        List<Recipe> filteredRecipes = new ArrayList<>();

        for(Recipe recipe : allRecipes) {
            boolean matches = true;

            //Cost Range Filter
            if(costRange != null){

                if(recipe.getCost() == null || !recipe.getCost().equals(costRange)){
                    matches = false;
                }
            }

            //Descriptor filter
            if(descriptor != null && !descriptor.isEmpty()){
                if(!recipe.getDescriptors().toLowerCase().contains(descriptor.toLowerCase())){
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
