package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryService (and InventoryServiceImpl).
 */
@SpringBootTest
public class InventoryServiceTest {

    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService  inventoryService;

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;

    /**
     * Sets up the test case. We assume only one inventory row. Because
     * inventory is treated as a singleton (only one row), we must truncate for
     * auto increment on the id to work correctly.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        // Query query = entityManager.createNativeQuery("TRUNCATE TABLE
        // inventory");
        // query.executeUpdate();

        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();
    }

    /**
     * Tests InventoryService.createInventory().
     */
    @Test
    @Transactional
    public void testCreateInventory () {
        final List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
        final Ingredient ir1 = new Ingredient( "coffee", 10 );
        final Ingredient ir2 = new Ingredient( "milk", 8 );
        final Ingredient ir3 = new Ingredient( "cream", 2 );
        ingredientsList.add( ir1 );
        ingredientsList.add( ir2 );
        ingredientsList.add( ir3 );

        final InventoryDto inventoryDto = new InventoryDto( 1L, ingredientsList );

        final InventoryDto createdInventoryDto = inventoryService.createInventory( inventoryDto );
        // Check contents of returned InventoryDto
        assertAll( "InventoryDto contents", () -> assertEquals( 1L, createdInventoryDto.getId() ) );

        final Ingredient i1 = createdInventoryDto.getIngredients().get( 0 );
        final Ingredient i2 = createdInventoryDto.getIngredients().get( 1 );
        final Ingredient i3 = createdInventoryDto.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 10, i1.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 2, i3.getAmount() ) );
    }

    /**
     * Tests InventoryService.getInventory().
     */
    @Test
    @Transactional
    public void testGetInventory () {
        // make sure inventory is initially empty
        final InventoryDto initialInventoryDto = inventoryService.getInventory();
        assertEquals( 0, initialInventoryDto.getIngredients().size() );

        // create ingredients, which are added to the inventory
        ingredientService.createIngredient( new IngredientDto( 1L, "coffee", 10 ) );
        ingredientService.createIngredient( new IngredientDto( 2L, "milk", 8 ) );
        ingredientService.createIngredient( new IngredientDto( 3L, "cream", 2 ) );

        final InventoryDto fetchedInventoryDto = inventoryService.getInventory();

        // Check contents of returned InventoryDto
        assertAll( "InventoryDto contents", () -> assertEquals( 1L, fetchedInventoryDto.getId() ) );

        final Ingredient i1 = fetchedInventoryDto.getIngredients().get( 0 );
        final Ingredient i2 = fetchedInventoryDto.getIngredients().get( 1 );
        final Ingredient i3 = fetchedInventoryDto.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 10, i1.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 2, i3.getAmount() ) );
    }

    /**
     * Tests InventoryService.updateInventory().
     */
    @Test
    @Transactional
    public void testUpdateInventory () {

        // create ingredients, which are added to the inventory
        ingredientService.createIngredient( new IngredientDto( 1L, "coffee", 10 ) );
        ingredientService.createIngredient( new IngredientDto( 2L, "milk", 8 ) );
        ingredientService.createIngredient( new IngredientDto( 3L, "cream", 2 ) );

        final List<Ingredient> newList = new ArrayList<Ingredient>();
        final Ingredient ir1 = new Ingredient( "coffee", 0 );
        final Ingredient ir2 = new Ingredient( "milk", 8 );
        final Ingredient ir3 = new Ingredient( "cream", 15 );
        newList.add( ir1 );
        newList.add( ir2 );
        newList.add( ir3 );

        final InventoryDto inventoryDto = inventoryService.getInventory();
        inventoryDto.setIngredients( newList );

        final InventoryDto updatedInventoryDto = inventoryService.updateInventory( inventoryDto );
        assertAll( "InventoryDto contents", () -> assertEquals( 1L, updatedInventoryDto.getId() ) );

        final Ingredient i1 = updatedInventoryDto.getIngredients().get( 0 );
        final Ingredient i2 = updatedInventoryDto.getIngredients().get( 1 );
        final Ingredient i3 = updatedInventoryDto.getIngredients().get( 2 );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 10, i1.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 16, i2.getAmount() ) );

        assertAll( "Ingredient contents", 
                () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 17, i3.getAmount() ) );

        // test with ingredient that doesn't exist in inventory
        final Ingredient i4 = new Ingredient( "fake", 10 );
        newList.add( i4 );

        final InventoryDto inventoryDto2 = inventoryService.getInventory();
        inventoryDto2.setIngredients( newList );

        final Exception ex1 = assertThrows( ResourceNotFoundException.class,
                () -> inventoryService.updateInventory( inventoryDto2 ) );
        assertEquals( "Ingredient fake does not exist.", ex1.getMessage() );

        // test with negative updated amount
        newList.remove( i4 );
        ir2.setAmount( -1 );
        final InventoryDto inventoryDto3 = inventoryService.getInventory();
        inventoryDto3.setIngredients( newList );

        final Exception ex2 = assertThrows( IllegalArgumentException.class,
                () -> inventoryService.updateInventory( inventoryDto3 ) );
        assertEquals( "Ingredient amount cannot be negative.", ex2.getMessage() );
    }
}
