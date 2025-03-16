package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cookery.cookery.entity.Ingredient;
import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.entity.RecipeIngredient;
import com.cookery.cookery.entity.User;
import com.cookery.cookery.service.IngredientService;
import com.cookery.cookery.service.RecipeService;
import com.cookery.cookery.service.UserService;



@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    @Autowired
    private IngredientService ingredientService;

    //Display Recipes as a list
    @GetMapping
    public String home(Model model, Principal principal) {
        //Display on recipes by the logged-in user
        Long userId = userService.findByUsername(principal.getName()).getId();
        List<Recipe> recipes = recipeService.findAllByUser(userId);
        
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    //Display create new recipe form
    @GetMapping("/new")
    public String showAddRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("ingredients", ingredientService.findAll());
        
        // Initialize/retrieve selectedIngredients for user session
        if (!model.containsAttribute("selectedIngredients")) {
            model.addAttribute("selectedIngredients", new ArrayList<>());
            System.out.println("Created an empty list for ingredients");
        }
        
        return "addRecipeForm";
    }


    //Recipe Form - Ingredient Related Methods
    //Display the ingredient selection page for the recipe form
    @GetMapping("/choose-ingredients")
    public String showChooseIngredientsForm(Model model) {
        model.addAttribute("ingredients", ingredientService.findAll()); // All ingredients
        model.addAttribute("newIngredient", new Ingredient()); // Form for adding new ingredients
        return "chooseIngredients";
    }

    //Add selected ingredients in choose ingredients form to the recipes form and redirect back to the new recipe form
    @PostMapping("/add-ingredients")
    public String addIngredients(@RequestParam List<Long> ingredientIds, Model model) {
        List<Ingredient> selectedIngredients = (List<Ingredient>) model.getAttribute("selectedIngredients");
    
       //Create list for ingredients if not existing
        if (selectedIngredients == null) {
            selectedIngredients = new ArrayList<>();
            model.addAttribute("selectedIngredients", selectedIngredients);
            System.out.println("New selectedIngredients list created in addIngredients method");
        }
        
        //Add ingredients to the list
        for (Long id : ingredientIds) {
            Ingredient ingredient = ingredientService.findById(id).orElseThrow(() -> new RuntimeException("Ingredient not found"));
            if (!selectedIngredients.contains(ingredient)) {
                selectedIngredients.add(ingredient); // Add to the recipe's temporary ingredient list
                System.out.println("Ingredient added to list: " + ingredient.getName());
            }
        }

        //Debugging
        System.out.println("Selected ingredients for recipe: " + selectedIngredients);

        return "redirect:/recipes/new"; // Redirect back to the recipe form
    }

    // Remove an ingredient from the selected list
    @PostMapping("/remove-ingredient")
    public String removeIngredient(@RequestParam Long ingredientId, Model model) {
        List<Ingredient> selectedIngredients = (List<Ingredient>) model.getAttribute("selectedIngredients");
        if (selectedIngredients != null) {
            boolean removed = selectedIngredients.removeIf(ingredient -> Long.valueOf(ingredient.getId()).equals(ingredientId));
            if (!removed) {
                System.err.println("Ingredient not found in the list.");
            }
        }
        return "redirect:/recipes/new";
    }

    
    //Show the edit recipe form
    @GetMapping("/edit/{id}")
    public String showEditRecipeForm(@PathVariable("id") Long id, Model model, Principal principal) {
        Recipe recipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("ingredients", ingredientService.findAll());
        
        return "editRecipe"; // Thymeleaf template for editing a recipe
    }

    //Show the recipe details
    @GetMapping("/view/{id}")
    public String viewRecipe(@PathVariable("id") Long id, Model model, Principal principal) {
        Recipe recipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("recipe", recipe);
    
        return "viewRecipe";
    }
    

    //CRUD METHODS BELOW
    // Save a new recipe and associate it with the logged-in user
    @PostMapping
    public String saveRecipe(@ModelAttribute Recipe recipe, @RequestParam Map<String, String> ingredientQuantities, // Ingredient ID → Quantity
    Principal principal) {
    // Get the logged-in user and associate them with the recipe
    User user = userService.findByUsername(principal.getName());
    recipe.setUser(user);

    // Process ingredients and their quantities
    ingredientQuantities.forEach((key, quantity) -> {
        
        //Save only ingredients with valid quantities and manage the csrf error caused by quantity use in mapping
        if(key.matches("\\d+") && quantity != null && !quantity.equalsIgnoreCase("0") && !quantity.trim().isEmpty()){
        
        //Convert http string Id data it's Long stored type
        Long ingredientId = Long.valueOf(key);

        // Fetch the Ingredient entity
        Ingredient ingredient = ingredientService.findById(ingredientId)
            .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        // Create a RecipeIngredient
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity(quantity);

        // Add RecipeIngredient to the Recipe's list of ingredients
        recipe.addRecipeIngredient(recipeIngredient);
        }
    });

    // Save the recipe, including its associated ingredients
    recipeService.save(recipe);

    return "redirect:/recipes"; // Redirect to the list of recipes
}
    
    @PostMapping("/edit/{id}")
    public String editRecipe(@ModelAttribute Recipe recipe, @PathVariable("id") Long id, Principal principal) {
        Recipe existingRecipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!existingRecipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }
        recipe.setUser(existingRecipe.getUser()); // Retain the original user
        recipeService.save(recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long id, Principal principal) {
        Recipe recipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }
        recipeService.deleteById(id);
        return "redirect:/recipes";
    }
    
}
