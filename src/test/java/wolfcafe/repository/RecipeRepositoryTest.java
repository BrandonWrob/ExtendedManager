package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;

/**
 * Tests RecipeRepository.
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class RecipeRepositoryTest {

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /** first list of ingredients */
    private List<Ingredient> ingredientsList;
    /** second list of ingredients */
    private List<Ingredient> ingredientsList2;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();

        ingredientsList = new ArrayList<Ingredient>();
        final Ingredient ir1 = new Ingredient( "coffee", 3 );
        final Ingredient ir2 = new Ingredient( "milk", 5 );
        final Ingredient ir3 = new Ingredient( "cream", 4 );
        ingredientsList.add( ir1 );
        ingredientsList.add( ir2 );
        ingredientsList.add( ir3 );

        ingredientsList2 = new ArrayList<Ingredient>();
        final Ingredient ir4 = new Ingredient( "sugar", 6 );
        final Ingredient ir5 = new Ingredient( "pumpkin spice", 8 );
        final Ingredient ir6 = new Ingredient( "vanilla", 10 );
        ingredientsList2.add( ir4 );
        ingredientsList2.add( ir5 );
        ingredientsList2.add( ir6 );

        final Recipe recipe1 = new Recipe( 1L, "Coffee", 50, ingredientsList );
        final Recipe recipe2 = new Recipe( 2L, "Latte", 100, ingredientsList2 );

        recipeRepository.save( recipe1 );
        recipeRepository.save( recipe2 );
    }

    /**
     * Tests retrieving the Coffee recipe by name.
     */
    @Test
    public void testGetRecipeByNameCoffee () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Coffee" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 50, actualRecipe.getPrice() ) );

        final Ingredient i1 = actualRecipe.getIngredients().get( 0 );
        final Ingredient i2 = actualRecipe.getIngredients().get( 1 );
        final Ingredient i3 = actualRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 3, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 5, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 4, i3.getAmount() ) );
    }

    /**
     * Tests retrieving the Latte recipe by name.
     */
    @Test
    public void testGetRecipeByNameLatte () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Latte" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Latte", actualRecipe.getName() ),
                () -> assertEquals( 100, actualRecipe.getPrice() ) );

        final Ingredient i1 = actualRecipe.getIngredients().get( 0 );
        final Ingredient i2 = actualRecipe.getIngredients().get( 1 );
        final Ingredient i3 = actualRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "sugar", i1.getName() ),
                () -> assertEquals( 6, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "pumpkin spice", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "vanilla", i3.getName() ),
                () -> assertEquals( 10, i3.getAmount() ) );
    }

    /**
     * Tests retrieving then changing a recipe's values.
     */
    @Test
    public void testGetRecipeByNameNewValues () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Coffee" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 50, actualRecipe.getPrice() ) );

        actualRecipe.setPrice( 75 );
        actualRecipe.setIngredients( ingredientsList2 );

        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 75, actualRecipe.getPrice() ) );

        final Ingredient i1 = actualRecipe.getIngredients().get( 0 );
        final Ingredient i2 = actualRecipe.getIngredients().get( 1 );
        final Ingredient i3 = actualRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "sugar", i1.getName() ),
                () -> assertEquals( 6, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "pumpkin spice", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "vanilla", i3.getName() ),
                () -> assertEquals( 10, i3.getAmount() ) );
    }

    /**
     * Tests retrieving a non-existent recipe.
     */
    @Test
    public void testGetRecipeByNameInvalid () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Unknown" );
        assertTrue( recipe.isEmpty() );
    }
}
