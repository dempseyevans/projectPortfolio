package com.cookery.cookery.Controller;

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
import com.cookery.cookery.service.IngredientService;

@Controller
@RequestMapping("/ingredients")
public class IngredientController {

private static final Logger logger = LoggerFactory.getLogger(IngredientController.class);

    @Autowired
    private IngredientService ingredientService;

    //Display Ingredients as a list
    //Exception handling for debugging service error 500
    @GetMapping
    public String listIngredients(Model model) {
        try{
            List<Ingredient> ingredients = ingredientService.findAll();
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
    public String saveIngredient(@ModelAttribute Ingredient ingredient) {
        ingredientService.save(ingredient);
        return "redirect:/ingredients";
    }

    @GetMapping("/edit/{id}")
    public String editIngredientForm(@PathVariable Long id, Model model) {
        Optional<Ingredient> ingredient = ingredientService.findById(id);
        model.addAttribute("ingredient", ingredient.get());
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
}

