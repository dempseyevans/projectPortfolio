package com.cookery.cookery.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cookery.cookery.entity.Ingredient;
import com.cookery.cookery.entity.User;
import com.cookery.cookery.service.IngredientService;
import com.cookery.cookery.service.UserService;


@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    private static final Logger logger = LoggerFactory.getLogger(IngredientController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private IngredientService ingredientService;

    //Display Ingredients as a list
    //Exception handling for debugging service error 500
    @GetMapping
    public String listIngredients(Model model, Principal principal) {
        try{
            User currentUser = userService.findByUsername(principal.getName());
            List<Ingredient> ingredients = ingredientService.findAllByUser(currentUser.getId());
        model.addAttribute("ingredients", ingredients);
        } catch (Exception e){
            logger.error("Error displaying ingredient: " + e.getMessage());
        }
        return "ingredients";
    }

    //Display create new ingredient Form
    @GetMapping("/new")
    public String newIngredientForm(Model model) {
        model.addAttribute("ingredient", new Ingredient());
        return "addIngredientForm";
    }

    //CRUD METHODS BELOW
    @PostMapping
public String saveIngredient(@ModelAttribute Ingredient ingredient, Principal principal) {
    // Retrieve the logged-in user
    User user = userService.findByUsername(principal.getName());
    
    // Associate the ingredient with the user
    ingredient.setUser(user);
    
    // Save the ingredient
    ingredientService.save(ingredient);
    
    return "redirect:/ingredients";
}

    @GetMapping("/edit/{id}")
    public String editIngredientForm(@PathVariable Long id, Model model) {
        Optional<Ingredient> ingredient = ingredientService.findById(id);
        if(ingredient.isPresent()){
        model.addAttribute("ingredient", ingredient.get());
        return "editIngredientForm";
        }
        return "redirect:/ingredients";
    }

    @PostMapping("/update")
    public String updateIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientService.save(ingredient);
        return "redirect:/ingredients";
    }

    @GetMapping("/delete/{id}")
    public String deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteById(id);
        return "redirect:/ingredients";
    }

    //Handle adding new ingredients on the recipe form
    @PostMapping("/add")
    public String addIngredient(@ModelAttribute Ingredient newIngredient, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        newIngredient.setUser(user); // Associate the ingredient with the logged-in user

        // Save the ingredient
        ingredientService.save(newIngredient);

        // Redirect back to the recipe creation form
        return "redirect:/recipes/new";
    }
    
}

