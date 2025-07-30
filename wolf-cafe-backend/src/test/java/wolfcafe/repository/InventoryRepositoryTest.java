package wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import wolfcafe.entity.Ingredient;
import wolfcafe.entity.Inventory;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryRepository. Uses the real database - not an embedded one.
 */
@DataJpaTest
@ActiveProfiles("localtest")
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class InventoryRepositoryTest {

    /** Reference to inventory repository */
    @Autowired
    private InventoryRepository inventoryRepository;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager       entityManager;

    /** Reference to inventory */
    private Inventory           inventory;

    /** list of ingredients */
    private List<Ingredient>    ingredientsList;

    /**
     * Sets up the test case. We assume only one inventory row.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        // EntityManager entityManager = testEntityManager.getEntityManager();
        // Query query = entityManager.createNativeQuery("TRUNCATE TABLE
        // inventory");
        // query.executeUpdate();

        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        ingredientsList = new ArrayList<Ingredient>();
        final Ingredient i1 = new Ingredient( "coffee", 10 );
        final Ingredient i2 = new Ingredient( "milk", 8 );
        final Ingredient i3 = new Ingredient( "cream", 2 );
        ingredientsList.add( i1 );
        ingredientsList.add( i2 );
        ingredientsList.add( i3 );

        // Make sure that Inventory always has an id of 1L.
        inventory = new Inventory( 1L, ingredientsList );
        inventoryRepository.save( inventory );
    }

    /**
     * Test saving the inventory and retrieving from the repository.
     */
    @Test
    public void testSaveAndGetInventory () {
        final Inventory fetchedInventory = inventoryRepository.findById( 1L ).get();
        assertAll( "Inventory contents", () -> assertEquals( 1L, fetchedInventory.getId() ) );

        final Ingredient i1 = fetchedInventory.getIngredients().get( 0 );
        final Ingredient i2 = fetchedInventory.getIngredients().get( 1 );
        final Ingredient i3 = fetchedInventory.getIngredients().get( 2 );

        assertAll( "Ingredient contents", () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 10, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 2, i3.getAmount() ) );
    }

    /**
     * Tests updating the inventory.
     */
    @Test
    public void testUpdateInventory () {
        final Inventory fetchedInventory = inventoryRepository.findById( 1L ).get();

        final List<Ingredient> newList = new ArrayList<Ingredient>();
        final Ingredient ir1 = new Ingredient( "coffee", 0 );
        final Ingredient ir2 = new Ingredient( "milk", 8 );
        final Ingredient ir3 = new Ingredient( "cream", 15 );
        newList.add( ir1 );
        newList.add( ir2 );
        newList.add( ir3 );

        fetchedInventory.setIngredients( newList );
        final Inventory updatedInventory = inventoryRepository.save( fetchedInventory );

        final Ingredient i1 = updatedInventory.getIngredients().get( 0 );
        final Ingredient i2 = updatedInventory.getIngredients().get( 1 );
        final Ingredient i3 = updatedInventory.getIngredients().get( 2 );

        assertAll( "Inventory contents", () -> assertEquals( 1L, updatedInventory.getId() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "coffee", i1.getName() ),
                () -> assertEquals( 0, i1.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "milk", i2.getName() ),
                () -> assertEquals( 8, i2.getAmount() ) );

        assertAll( "Ingredient contents", () -> assertEquals( "cream", i3.getName() ),
                () -> assertEquals( 15, i3.getAmount() ) );
    }
}
