package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Tests IngredientService (and IngredientServiceImpl).
 */
@SpringBootTest
public class IngredientServiceTest {

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * Sets up the test cases.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        ingredientService.deleteAllIngredients();
    }

    /**
     * Tests IngredientService.createIngredient().
     */
    @Test
    @Transactional
    public void testCreateIngredient () {

        final IngredientDto ingredient1 = new IngredientDto( 1L, "coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", createdIngredient1.getName() ),
                () -> assertEquals( 5, createdIngredient1.getAmount() ) );

        final IngredientDto ingredient2 = new IngredientDto( 2L, "pumpkin spice", 10 );
        final IngredientDto createdIngredient2 = ingredientService.createIngredient( ingredient2 );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "pumpkin spice", createdIngredient2.getName() ),
                () -> assertEquals( 10, createdIngredient2.getAmount() ) );

        assertEquals( 2, inventoryService.getInventory().getIngredients().size() );
        assertTrue( inventoryService.isDuplicateName( inventoryService.getInventory(), createdIngredient1.getName() ) );
        assertTrue( inventoryService.isDuplicateName( inventoryService.getInventory(), createdIngredient2.getName() ) );

        final Exception ex1 = assertThrows( IllegalArgumentException.class,
                () -> ingredientService.createIngredient( new IngredientDto( 3L, "coffee", 8 ) ) );
        assertEquals( "Cannot add duplicate Ingredients.", ex1.getMessage() );
        final Exception ex2 = assertThrows( IllegalArgumentException.class,
                () -> ingredientService.createIngredient( new IngredientDto( 4L, "mocha", -1 ) ) );
        assertEquals( "Ingredient amount cannot be negative.", ex2.getMessage() );
    }

    /**
     * Tests IngredientService.getIngredientById().
     */
    @Test
    @Transactional
    public void testGetIngredientById () {

        final IngredientDto ingredient1 = new IngredientDto( 1L, "coffee", 5 );

        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );
        final IngredientDto fetchedIngredient1 = ingredientService.getIngredientById( createdIngredient1.getId() );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", fetchedIngredient1.getName() ),
                () -> assertEquals( 5, fetchedIngredient1.getAmount() ) );

        final IngredientDto ingredient2 = new IngredientDto( 2L, "pumpkin spice", 10 );
        final IngredientDto createdIngredient2 = ingredientService.createIngredient( ingredient2 );
        final IngredientDto fetchedIngredient2 = ingredientService.getIngredientById( createdIngredient2.getId() );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "pumpkin spice", fetchedIngredient2.getName() ),
                () -> assertEquals( 10, fetchedIngredient2.getAmount() ) );

        final IngredientDto ingredient3 = new IngredientDto( 3L, "cream", 10 );
        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> ingredientService.getIngredientById( ingredient3.getId() ) );
        assertEquals( "Ingredient does not exist with id " + ingredient3.getId(), ex1.getMessage() );
    }

    /**
     * Tests IngredientService.updateIngredient().
     */
    @Test
    @Transactional
    public void testUpdateIngredient () {
        final IngredientDto ingredient1 = new IngredientDto( 1L, "coffee", 5 );
        final IngredientDto createdIngredient1 = ingredientService.createIngredient( ingredient1 );

        final IngredientDto newPrice = new IngredientDto( 10L, "coffee", 10 );
        final IngredientDto fetchedIngredient1 = ingredientService.updateIngredient( createdIngredient1.getId(),
                newPrice );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", fetchedIngredient1.getName() ),
                () -> assertEquals( 10, fetchedIngredient1.getAmount() ) );

        final IngredientDto newName = new IngredientDto( 2L, "sugar", 10 );
        final IngredientDto fetchedIngredient2 = ingredientService.updateIngredient( createdIngredient1.getId(),
                newName );
        assertAll( "Ingredient contents", 
                () -> assertEquals( "sugar", fetchedIngredient2.getName() ),
                () -> assertEquals( 10, fetchedIngredient2.getAmount() ) );

        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> ingredientService.updateIngredient( newPrice.getId(), newName ) );
        assertEquals( "Ingredient does not exist with id " + newPrice.getId(), ex1.getMessage() );
    }

}
