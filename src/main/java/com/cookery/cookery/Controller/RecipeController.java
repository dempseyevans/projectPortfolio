package com.cookery.cookery.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cookery.cookery.entity.Recipe;
import com.cookery.cookery.service.RecipeService;


@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    //Display Recipes as a list
    @GetMapping
    public String home(Model model) {
        List<Recipe> recipes = recipeService.findAll();
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    //Display create new recipe form
    @GetMapping("/new")
    public String showAddRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "addRecipeForm";
    }
    
    //Show edit recipe form
    @GetMapping("/edit/{id}")
    public String showEditRecipeForm(@PathVariable("id") Long id, Model model) {
        Optional<Recipe> recipe = recipeService.findById(id);
        model.addAttribute("recipe", recipe);
        return "editRecipe";
    }

    //CRUD METHODS BELOW
    @PostMapping
    public String saveRecipe(@ModelAttribute Recipe recipe) {
        recipeService.save(recipe);
        return "redirect:/recipes";
    }
    
    @PostMapping("/edit/{id}")
    public String editRecipe(@ModelAttribute Recipe recipe) {
        recipeService.save(recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/delete/{id}")
    public String deleteRecipe(@PathVariable("id") Long id) {
        recipeService.deleteById(id);
        return "redirect:/recipes";
    }
    
}
