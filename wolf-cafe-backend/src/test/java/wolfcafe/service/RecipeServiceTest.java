package wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import wolfcafe.dto.IngredientDto;
import wolfcafe.dto.RecipeDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.exception.ResourceNotFoundException;
import wolfcafe.repository.RecipeRepository;

/**
 * Tests RecipeService (and RecipeServiceImpl).
 */
@ActiveProfiles("localtest")
@SpringBootTest
class RecipeServiceTest {

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository  recipeRepository;

    /** Reference to RecipeService (and RecipeServiceImpl). */
    @Autowired
    private RecipeService     recipeService;

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;

    /** First ingredients list for testing */
    private List<Ingredient>  ingredientsList;
    /** Second ingredients list for testing */
    private List<Ingredient>  ingredientsList2;

    /**
     * Sets up the test cases and created Ingredients in Inventory.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        recipeRepository.deleteAll();

        ingredientService.createIngredient( new IngredientDto( 1L, "coffee", 3 ) );
        ingredientService.createIngredient( new IngredientDto( 2L, "milk", 5 ) );
        ingredientService.createIngredient( new IngredientDto( 3L, "cream", 4 ) );
        ingredientService.createIngredient( new IngredientDto( 4L, "sugar", 6 ) );
        ingredientService.createIngredient( new IngredientDto( 5L, "pumpkin spice", 8 ) );
        ingredientService.createIngredient( new IngredientDto( 6L, "vanilla", 10 ) );

        ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( new Ingredient( "coffee", 3 ) );
        ingredientsList.add( new Ingredient( "milk", 5 ) );
        ingredientsList.add( new Ingredient( "cream", 4 ) );

        ingredientsList2 = new ArrayList<Ingredient>();
        ingredientsList2.add( new Ingredient( "sugar", 6 ) );
        ingredientsList2.add( new Ingredient( "pumpkin spice", 8 ) );
        ingredientsList2.add( new Ingredient( "vanilla", 10 ) );
    }

    /**
     * Tests RecipeService.createRecipe().
     */
    @Test
    @Transactional
    void testCreateRecipe () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Coffee", 50, ingredientsList );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", savedRecipe.getName() ),
                () -> assertEquals( 50, savedRecipe.getPrice() ) );

        final Ingredient i1 = savedRecipe.getIngredients().get( 0 );
        final Ingredient i2 = savedRecipe.getIngredients().get( 1 );
        final Ingredient i3 = savedRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 3, i1.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 5, i2.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 4, i3.getAmount() ) );

        final RecipeDto retrievedRecipe = recipeService.getRecipeById( savedRecipe.getId() );
        assertAll( "Recipe contents", 
                () -> assertEquals( savedRecipe.getId(), retrievedRecipe.getId() ),
                () -> assertEquals( "Coffee", retrievedRecipe.getName() ),
                () -> assertEquals( 50, retrievedRecipe.getPrice() ) );

        final Ingredient i4 = retrievedRecipe.getIngredients().get( 0 );
        final Ingredient i5 = retrievedRecipe.getIngredients().get( 1 );
        final Ingredient i6 = retrievedRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", i4.getName() ),
                () -> assertEquals( 3, i4.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "milk", i5.getName() ),
                () -> assertEquals( 5, i5.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "cream", i6.getName() ),
                () -> assertEquals( 4, i6.getAmount() ) );

        // add second
        final RecipeDto recipeDto2 = new RecipeDto( 1L, "Latte", 50, ingredientsList );
        recipeService.createRecipe( recipeDto2 );

        // add third
        final RecipeDto recipeDto3 = new RecipeDto( 2L, "Chai", 50, ingredientsList2 );
        final RecipeDto savedRecipe3 = recipeService.createRecipe( recipeDto3 );

        // test adding fourth
        final Exception ex1 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Mocha", 50, ingredientsList ) ) );
        assertEquals( "Too many recipes in the system. You cannot add more than 3 recipes.", ex1.getMessage() );

        // make space for more attempts
        recipeService.deleteRecipe( savedRecipe3.getId() );

        // test duplicate name
        final Exception ex2 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Latte", 50, ingredientsList ) ) );
        assertEquals( "Recipe name already exists. Please choose a different name.", ex2.getMessage() );

        // test negative price
        final Exception ex3 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Mocha", -1, ingredientsList ) ) );
        assertEquals( "Recipe price must be a positive integer.", ex3.getMessage() );

        // test null ingredients list
        final Exception ex4 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Mocha", 50, null ) ) );
        assertEquals( "A recipe must have at least one ingredient.", ex4.getMessage() );

        // test empty ingredients list
        final Exception ex5 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Mocha", 50, new ArrayList<Ingredient>() ) ) );
        assertEquals( "A recipe must have at least one ingredient.", ex5.getMessage() );

        // test negative ingredient amount
        ingredientsList.add( new Ingredient( "fake", -1 ) );
        final Exception ex6 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.createRecipe( new RecipeDto( 3L, "Mocha", 50, ingredientsList ) ) );
        assertEquals( "All ingredient amounts should be positive integers.", ex6.getMessage() );
    }

    /**
     * Tests RecipeService.isDuplicateName().
     */
    @Test
    @Transactional
    void testIsDuplicateName () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Chai", 60, ingredientsList );
        recipeService.createRecipe( recipeDto );

        assertTrue( recipeService.isDuplicateName( "Chai" ) );
        assertFalse( recipeService.isDuplicateName( "Espresso" ) );
    }

    /**
     * Tests RecipeService.getAllRecipes().
     */
    @Test
    @Transactional
    void testGetAllRecipes () {
        final RecipeDto recipe1 = new RecipeDto( 0L, "Chai", 30, ingredientsList );
        final RecipeDto recipe2 = new RecipeDto( 1L, "Latte", 60, ingredientsList2 );
        final RecipeDto recipe3 = new RecipeDto( 2L, "Coffee", 50, ingredientsList );

        recipeService.createRecipe( recipe1 );
        recipeService.createRecipe( recipe2 );

        final List<RecipeDto> recipes = recipeService.getAllRecipes();
        assertEquals( 2, recipes.size() );
        assertTrue( recipes.stream().anyMatch( r -> r.getName().equals( "Chai" ) ) );
        assertTrue( recipes.stream().anyMatch( r -> r.getName().equals( "Latte" ) ) );

        assertTrue( recipes.stream().anyMatch( r -> r.getPrice().equals( 30 ) ) );
        assertTrue( recipes.stream().anyMatch( r -> r.getPrice().equals( 60 ) ) );

        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> recipeService.getRecipeById( recipe3.getId() ) );
        assertEquals( "Recipe does not exist with id " + recipe3.getId(), ex1.getMessage() );
    }

    /**
     * Tests RecipeService.updateRecipe().
     */
    @Test
    @Transactional
    void testUpdateRecipe () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 70, ingredientsList );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );

        final RecipeDto updateDto = new RecipeDto( savedRecipe.getId(), savedRecipe.getName(), 100, ingredientsList2 );
        final RecipeDto updatedRecipe = recipeService.updateRecipe( savedRecipe.getId(), updateDto );

        assertEquals( "Mocha", updatedRecipe.getName() );
        assertEquals( 100, updatedRecipe.getPrice() );

        final Ingredient i1 = updatedRecipe.getIngredients().get( 0 );
        final Ingredient i2 = updatedRecipe.getIngredients().get( 1 );
        final Ingredient i3 = updatedRecipe.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "sugar", i1.getName() ),
                () -> assertEquals( 6, i1.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "pumpkin spice", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "vanilla", i3.getName() ),
                () -> assertEquals( 10, i3.getAmount() ) );
    }

    /**
     * Tests RecipeService.updateRecipe() with invalid inputs
     */
    @Test
    @Transactional
    void testUpdateRecipeInvalid () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 70, ingredientsList );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );

        final RecipeDto updateDto1 = new RecipeDto( 10L, savedRecipe.getName(), 100, ingredientsList2 );

        // test with id that doesn't correspond with a created recipe
        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> recipeService.updateRecipe( updateDto1.getId(), updateDto1 ) );
        assertEquals( "Recipe does not exist with id " + updateDto1.getId(), ex1.getMessage() );

        // test negative price
        final RecipeDto updateDto2 = new RecipeDto( savedRecipe.getId(), savedRecipe.getName(), -1, ingredientsList2 );
        final Exception ex2 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.updateRecipe( savedRecipe.getId(), updateDto2 ) );
        assertEquals( "Recipe price must be a positive integer.", ex2.getMessage() );

        // test null ingredients list
        final RecipeDto updateDto3 = new RecipeDto( savedRecipe.getId(), savedRecipe.getName(), 100, null );
        final Exception ex3 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.updateRecipe( savedRecipe.getId(), updateDto3 ) );
        assertEquals( "A recipe must have ingredients.", ex3.getMessage() );

        // test empty ingredients list
        final RecipeDto updateDto4 = new RecipeDto( savedRecipe.getId(), savedRecipe.getName(), 100,
                new ArrayList<Ingredient>() );
        final Exception ex4 = assertThrows( IllegalArgumentException.class,
                () -> recipeService.updateRecipe( savedRecipe.getId(), updateDto4 ) );
        assertEquals( "A recipe must have ingredients.", ex4.getMessage() );

        // test ingredient that's not in the inventory
        ingredientsList2.add( new Ingredient( "fake", 10 ) );
        final RecipeDto updateDto5 = new RecipeDto( savedRecipe.getId(), savedRecipe.getName(), 100, ingredientsList2 );
        final Exception ex5 = assertThrows( ResourceNotFoundException.class,
                () -> recipeService.updateRecipe( savedRecipe.getId(), updateDto5 ) );
        assertEquals( "Ingredient fake does not exist.", ex5.getMessage() );
    }

    /**
     * Tests RecipeService.deleteRecipe().
     */
    @Test
    @Transactional
    void testDeleteRecipe () {
        final RecipeDto recipeDto = new RecipeDto( 0L, "Mocha", 90, ingredientsList );
        final RecipeDto savedRecipe = recipeService.createRecipe( recipeDto );

        recipeService.deleteRecipe( savedRecipe.getId() );

        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> recipeService.deleteRecipe( savedRecipe.getId() ) );
        assertEquals( "Recipe does not exist with id " + savedRecipe.getId(), ex1.getMessage() );

    }

}
