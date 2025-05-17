package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public String showRecipeGeneratorForm(Model model, Principal principal) {
        System.out.println("GET: loading generator page");
        return "recipeGenerator";
    }
    



    @PostMapping
    public String recipeGeneratorForm(
        @RequestParam(value = "costRange", required = false) Double costRange,
        @RequestParam(value = "descriptors", required = false) String descriptorInput,
        @RequestParam(value = "ingredientNames", required = false) String ingredientInput,
        @RequestParam(value = "maxCookTime", required = false) Integer maxCookTime,
        Model model,
        Principal principal) {

        //DEBUGGING
        System.out.println("RecipeGeneratorController method");

        String username = principal.getName();
        
        Recipe recipe = null;

        try{
            recipe = recipeGeneratorService.generateRandomRecipe(descriptorInput, ingredientInput, costRange, maxCookTime, username);
            System.out.println("Generated recipe: " + (recipe != null ? recipe.getName() : "NULL"));
            model.addAttribute("recipe", recipe);
        
        }catch (NoSuchElementException e){
            model.addAttribute("errorMessage", "No recipes match your search");
            
        }

        //Debugging
        System.out.println("Generating recipe with filters:");
        System.out.println("Cost Range: " + costRange);
        System.out.println("Descriptors: " + descriptorInput);
        System.out.println("Ingredients: " + ingredientInput);
        System.out.println("Max Cook Time: " + maxCookTime);

        return "recipeGenerator";

    }

    
    
    
}
