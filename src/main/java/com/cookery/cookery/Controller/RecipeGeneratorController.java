package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.service.RecipeGeneratorService;


@Controller
@RequestMapping("/generator")
public class RecipeGeneratorController {

    @Autowired
    RecipeGeneratorService recipeGeneratorService;
    
    //Show the Recipe Generator Page
    @GetMapping
    public String recipeGeneratorForm(
        @RequestParam(value = "costRange", required = false) Double costRange,
        @RequestParam(value = "descriptors", required = false) String descriptorInput,
        @RequestParam(value = "ingredientNames", required = false) String ingredientInput,
        @RequestParam(value = "maxCookTime", required = false) Integer maxCookTime,
        Model model,
        Principal principal) {

        // Check if this is an initial page load (no filters provided)
        if (costRange == null && 
            (descriptorInput == null || descriptorInput.trim().isEmpty()) &&
            (ingredientInput == null || ingredientInput.trim().isEmpty()) &&
            maxCookTime == null) {
            return "recipeGenerator"; // Return the page without generating a recipe
        }

        String username = principal.getName();

        try {
            // Generate random recipe using filters.
            Recipe recipe = recipeGeneratorService.generateRandomRecipe(descriptorInput, ingredientInput, costRange, maxCookTime, username);

            // Add the recipe to the model after submitting
            model.addAttribute("recipe", recipe);
        } catch (NoSuchElementException e) {
            // Error message if no matching recipes are found
            model.addAttribute("errorMessage", "No recipes match your criteria");
        }

        return "recipeGenerator";
    }
    
}
