package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cookery.cookery.CookeryApplication;
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

    private final CookeryApplication cookeryApplication;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    @Autowired
    private IngredientService ingredientService;

    RecipeController(CookeryApplication cookeryApplication) {
        this.cookeryApplication = cookeryApplication;
    }

    //Display Recipes as a list
    @GetMapping
    public String home(Model model, Principal principal) {
        //Display only recipes by the logged-in user
        Long userId = userService.findByUsername(principal.getName()).getId();
        List<Recipe> recipes = recipeService.findAllByUser(userId);
        
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    //Search recipes
    @GetMapping("/search")
    public String searchRecipes(@RequestParam("query") String query, Model model, Principal principal) {
        //Use the service class to get a filtered list of recipes
        List<Recipe> recipes = recipeService.searchRecipe(query, principal.getName());

        model.addAttribute("recipes", recipes);
        model.addAttribute("query", query);
        return "recipes";
    }
    
    //Show the edit recipe form
    @GetMapping("/edit/{id}")
    public String showEditRecipeForm(@PathVariable("id") Long id, Model model, Principal principal) {
        
        //Find recipe by user
        Recipe recipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }

        //List of the recipes ingredients
        List<RecipeIngredient> existingIngredients = recipe.getRecipeIngredients();
        
        //List of users available ingredients
        List<Ingredient> availableIngredients = ingredientService.findAllByUser(recipe.getUser().getId());
        
        //Sort ingredients to be displayed separately and alphabetically
        List<Long> existingIngredientIds = new ArrayList<>();
        for(RecipeIngredient ri : recipe.getRecipeIngredients()) {
            existingIngredientIds.add(ri.getIngredient().getId());
        }

        //List to sort available ingredients
        List<Ingredient> sortedAvailableIngredients = new ArrayList<>();
        for(Ingredient ingredient : availableIngredients) {
            if(!existingIngredientIds.contains(ingredient.getId())) {
                sortedAvailableIngredients.add(ingredient);
            }
        }

        Collections.sort(sortedAvailableIngredients, Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER));


        model.addAttribute("recipe", recipe);
        model.addAttribute("existingIngredients", existingIngredients);
        model.addAttribute("availableIngredientsSorted", sortedAvailableIngredients);
        return "editRecipe"; // Thymeleaf template for editing a recipe
    }

    //Show the recipe details
    @GetMapping("/view/{id}")
    public String viewRecipe(@PathVariable("id") Long id, Model model, Principal principal) {
        //Retrieve the recipe
        Recipe recipe = recipeService.findById(id).orElseThrow(() -> new RuntimeException("Recipe not found"));
        if (!recipe.getUser().getUsername().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("recipe", recipe);
    
        return "viewRecipe";
    }
    

    //Display create new recipe form
    @GetMapping("/new")
    public String showAddRecipeForm(Model model, Principal principal) {
        
        model.addAttribute("recipe", new Recipe());
        
        //User information to associate with recipe
        User user = userService.findByUsername(principal.getName());

        //List of users existing ingredients and debugging
        List<Ingredient> ingredients = ingredientService.findAllByUser(user.getId());
        ingredients.forEach(ingredient -> System.out.println("ID: " + ingredient.getId() + "Name: " + ingredient.getName()));

        model.addAttribute("ingredients", ingredientService.findAllByUser(user.getId()));
        
        // Empty list of ingredients to manage ingredients in the creation/save process
        if (!model.containsAttribute("selectedIngredients")) {
            model.addAttribute("selectedIngredients", new ArrayList<>());
            System.out.println("Created an empty list for ingredients");
        }
        
        return "addRecipeForm";
    }

    //Add new Ingredients in Recipe form
    @PostMapping("/addIngredient")
    @ResponseBody
    public ResponseEntity<Ingredient> addIngredient(@RequestParam String name, @RequestParam Integer priceCategory, Principal principal) {
        
        //Get user info and debugging
        User user = userService.findByUsername(principal.getName());
        System.out.println("Principal: " + user.getUsername());


        //Create and save new ingredient and debugging
        Ingredient newIngredient = new Ingredient();
        newIngredient.setName(name);
        System.out.println("Ingredient " + name + " saved");
        newIngredient.setPriceCategory(priceCategory);
        System.out.println(priceCategory + " saved");
        newIngredient.setUser(user);

        //Save new ingredient to ingredients table
        Ingredient savedIngredient = ingredientService.save(newIngredient);

        return ResponseEntity.ok(savedIngredient);
    }
    

    // Save a new recipe and associate it with the logged-in user
    @PostMapping
    public String saveRecipe(@ModelAttribute Recipe recipe, @RequestParam List<Long> ingredientIds,@RequestParam List<String> quantities,
    Principal principal) {

        // Get the logged-in user and associate them with the recipe
        recipe.setUser(userService.findByUsername(principal.getName()));

        // Process ingredients and their corresponding quantities
        for (int i = 0; i < ingredientIds.size(); i++) {
            Long ingredientId = ingredientIds.get(i);
            String quantity = quantities.get(i);

            // Validate quantity
            if (quantity != null && !quantity.trim().isEmpty() && !quantity.equalsIgnoreCase("0")) {
                // Fetch the Ingredient entity
                Ingredient ingredient = ingredientService.findById(ingredientId)
                    .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientId));

                // Create and add RecipeIngredient
                recipe.addRecipeIngredient(new RecipeIngredient(recipe, ingredient, quantity));

                // Debugging
                System.out.println("Added RecipeIngredient: Ingredient ID = " + ingredientId + ", Quantity = " + quantity);
            }
        }

            // Save the recipe, including its associated ingredients
            recipeService.save(recipe);


            return "redirect:/recipes"; // Redirect to the list of recipes
    }
    
    //Edit Recipe
    @PostMapping("/edit/{id}")
    public String editRecipe(@ModelAttribute Recipe recipe,
    @PathVariable("id") Long id,
    @RequestParam(value="existingRecipeIngredientIds", required=false) List<Long> existingRecipeIngredientIds,
    @RequestParam(value="existingQuantities", required=false) List<String> existingQuantities,
    @RequestParam(value="newIngredientIds", required=false) List<Long> newIngredientIds,
    @RequestParam(value="newQuantities", required=false) List<String> newQuantities,
    Principal principal ) {

        //Retrieve the recipe with error handling
        Recipe existingRecipe = recipeService.findById(id)
        .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Update the basic recipe details:
        existingRecipe.setName(recipe.getName());
        existingRecipe.setCookTime(recipe.getCookTime());
        existingRecipe.setInstructions(recipe.getInstructions());
        existingRecipe.setDescriptors(recipe.getDescriptors());

        //List to process existing ingredients
        List<RecipeIngredient> updatedIngredients = new ArrayList<>();

        // Process existing ingredients and add them if the quantity isn’t empty.
        if (existingRecipeIngredientIds != null && existingQuantities != null) {
            for (int i = 0; i < existingRecipeIngredientIds.size(); i++) {
                Long ingredientId = existingRecipeIngredientIds.get(i);
                String quantity = existingQuantities.get(i);
            if (quantity != null && !quantity.trim().isEmpty() && !quantity.equalsIgnoreCase("0")) {
                Ingredient ingredient = ingredientService.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientId));
                updatedIngredients.add(new RecipeIngredient(existingRecipe, ingredient, quantity));
            }
            }
        }

        // Process any new ingredients from add new ingredient button
        if (newIngredientIds != null && newQuantities != null) {
            for (int i = 0; i < newIngredientIds.size(); i++) {
                Long ingredientId = newIngredientIds.get(i);
                String quantity = newQuantities.get(i);
            if (quantity != null && !quantity.trim().isEmpty() && !quantity.equalsIgnoreCase("0")) {
                Ingredient ingredient = ingredientService.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found: " + ingredientId));
                updatedIngredients.add(new RecipeIngredient(existingRecipe, ingredient, quantity));
            }
            }
        }

        //Update recipe ingredients
        existingRecipe.setRecipeIngredients(updatedIngredients);

        // Save the updated recipe with its ingredients:
        recipeService.save(existingRecipe);

    
        return "redirect:/recipes";
    }

    //Delete Recipe
    @GetMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long id) {
        recipeService.deleteById(id);
        return "redirect:/recipes";
    }
    
}
