## **CODE SNIPPETS**

**Show New Recipe Form**
The code below demonstrates how Principal is used to link the user to the data


        @GetMapping("/new")
        public String showAddRecipeForm(Model model, Principal principal) {
            model.addAttribute("recipe", new Recipe());
            
            //User information to associate with recipe
            User user = userService.findByUsername(principal.getName());
    
            //List of users existing ingredients and debugging
            List<Ingredient> ingredients = ingredientService.findAllByUser(user.getId());
            ingredients.forEach(ingredient -> System.out.println("ID: " + ingredient.getId() + "Name: " + ingredient.getName()));
    
            model.addAttribute("ingredients", ingredientService.findAllByUser(user.getId()));

##
**Recipe Cost Calculation**
The numbers used in these calculations are still under review and likely to change


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

## 
**RecipeIngredient Entity Attributes**
This entity supports the separation between the recipe and ingredient entities


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recipeIngredientID")
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipeID", nullable = false)
    private Recipe recipe; //Link the recipeIngredients to the recipe

    //Connect the recipe ingredients to the ingredients (they have different attributes)
    @ManyToOne
    @JoinColumn(name = "ingredientID", nullable = false)
    private Ingredient ingredient; //Link the recipeIngredients with ingredients

    @Column(name="quantity")
    private String quantity;
