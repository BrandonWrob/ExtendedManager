package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderHistoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.MultiRecipe;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderHistory;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.OrderHistoryRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * tests OrderService
 */
@SpringBootTest
class OrderHistoryServiceTest {
    /** Reference to EntityManager */
    @Autowired
    private EntityManager          entityManager;

    /** reference to user repository */
    @Autowired
    private UserRepository         userRepository;

    /** reference to user repository */
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    /** Reference to IngredientService . */
    @Autowired
    private IngredientService      ingredientService;

    /** Reference to TaxService. */
    @Autowired
    private TaxService             taxService;

    /** Reference to InventoryService. */
    @Autowired
    private InventoryService       inventoryService;

    /** Reference to OrderService . */
    @Autowired
    private OrderService           orderService;

    /** Reference to OrderHistoryService. */
    @Autowired
    private OrderHistoryService    orderHistoryService;

    /** reference to OrderRepository */
    @Autowired
    private OrderRepository        orderRepository;

    /** a valid order to be tested */
    private OrderDto               order1;

    /** a valid order to be tested */
    private OrderDto               order2;

    /** users used to test making an order */
    private User                   user1;

    /** users used to test making an order */
    private User                   user2;

    /**
     * Helper method to check that recipes are equal
     *
     * @param r1
     *            represents one of the multi-recipes being compaired
     * @param r2
     *            represents one of the multi-recipes being compaired
     */
    private void checkEquals ( final MultiRecipe r1, final MultiRecipe r2 ) {
        assertEquals( r1.getPrice(), r2.getPrice() );
        assertEquals( r1.getName(), r2.getName() );

        assertEquals( r1.getAmount(), r2.getAmount() );

        final List<Ingredient> l1 = r1.getIngredients();
        final List<Ingredient> l2 = r2.getIngredients();
        assertEquals( l1.size(), l2.size() );
        for ( int i = 0; i < l1.size(); i++ ) {
            final Ingredient i1 = l1.get( i );
            final Ingredient i2 = l2.get( i );
            assertEquals( i1.getName(), i2.getName() );
            assertEquals( i1.getAmount(), i2.getAmount() );
        }
    }

    /**
     * Checks that orders are equal to their orderDto
     *
     * @param o1
     *            represents the OrderDto being compared
     * @param o2
     *            represents the Order being compared
     */
    private void checkEquals ( final OrderDto o1, final Order o2 ) {
        assertEquals( o1.getFulfilled(), o2.getFulfilled() );
        for ( int i = 0; i < o1.getRecipes().size(); i++ ) {
            checkEquals( o1.getRecipes().get( i ), o2.getRecipes().get( i ) );
        }
    }

    /**
     * Creates orders for users before test cases that can be used to assist in
     * making Order Histories for them
     *
     * @throws Exception
     *             if setup does not work correctly
     */
    @BeforeEach
    void setUp () throws Exception {
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        ingredientService.deleteAllIngredients();
        final List<User> users = userRepository.findAll();
        for ( int i = users.size() - 1; i > 0; i-- ) {
            userRepository.deleteById( users.get( i ).getId() );
        }
        orderRepository.deleteAll();

        ingredientService.createIngredient( new IngredientDto( 1L, "coffee", 33 ) );
        ingredientService.createIngredient( new IngredientDto( 2L, "milk", 20 ) );
        ingredientService.createIngredient( new IngredientDto( 3L, "cream", 100 ) );
        ingredientService.createIngredient( new IngredientDto( 4L, "sugar", 34 ) );
        ingredientService.createIngredient( new IngredientDto( 5L, "pumpkin spice", 46 ) );
        ingredientService.createIngredient( new IngredientDto( 6L, "vanilla", 50 ) );

        final List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( new Ingredient( "coffee", 3 ) );
        ingredientsList.add( new Ingredient( "milk", 5 ) );
        ingredientsList.add( new Ingredient( "cream", 4 ) );

        final List<Ingredient> ingredientsList2 = new ArrayList<Ingredient>();
        ingredientsList2.add( new Ingredient( "cream", 6 ) );
        ingredientsList2.add( new Ingredient( "pumpkin spice", 8 ) );
        ingredientsList2.add( new Ingredient( "vanilla", 10 ) );

        final List<MultiRecipe> recipes1 = new ArrayList<MultiRecipe>();

        recipes1.add( new MultiRecipe( 0L, "Coffee", 50, ingredientsList, 4 ) );
        recipes1.add( new MultiRecipe( 0L, "Latte", 100, ingredientsList2, 3 ) );

        order1 = new OrderDto( 0L, false, recipes1 );

        final List<Ingredient> ingredientsList3 = new ArrayList<Ingredient>();
        ingredientsList3.add( new Ingredient( "coffee", 9 ) );

        final List<MultiRecipe> recipes2 = new ArrayList<MultiRecipe>();
        recipes2.add( new MultiRecipe( 0L, "Just Coffee", 100, ingredientsList3, 2 ) );

        order2 = new OrderDto( 2L, false, recipes2 );

        user1 = new User( 0L, "alex", "user1", "user1@gmail.com", "pass", null, new ArrayList<Order>() );
        user2 = new User( 2L, "maddy", "user2", "user2@gmail.com", "012345", null, new ArrayList<Order>() );

        userRepository.save( user1 );
        userRepository.save( user2 );

        orderService.makeOrder( user1.getUsername(), order1 );

        final List<Ingredient> ingredients = inventoryService.getInventory().getIngredients();

        assertAll( "testing updated inventory correctly", () -> assertEquals( 21, ingredients.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients.get( 5 ).getAmount() ) );

        assertThrows( ResourceNotFoundException.class, () -> orderService.makeOrder( "turkey", order2 ) );

        orderService.makeOrder( user2.getUsername(), order2 );

        // check that the order is still the same
        checkEquals( order1, userRepository.findByUsername( user1.getUsername() ).get().getOrders().get( 0 ) );

        checkEquals( order2, userRepository.findByUsername( user2.getUsername() ).get().getOrders().get( 0 ) );

        final List<Ingredient> ingredients2 = inventoryService.getInventory().getIngredients();

        assertAll( "testing updated inventory correctly", () -> assertEquals( 3, ingredients2.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients2.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients2.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients2.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients2.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients2.get( 5 ).getAmount() ) );
    }

    /**
     * Test making a order history from a current order
     */
    @Test
    @Transactional
    void testMakeOrderHistory () {

        // uses the makeOrderHistory method on order1
        final OrderHistoryDto orderHistory = orderHistoryService.makeOrderHistory( user1.getUsername(), order1 );

        // confirms the object is made
        assertNotNull( orderHistory );

        // confirms it has the correct id
        assertEquals( order1.getId(), orderHistory.getId() );

        // confirms it has the correct total cost
        double expectedTotal = 50 * 4 + 100 * 3;
        expectedTotal += taxService.calcTax( expectedTotal );
        assertEquals( expectedTotal, orderHistory.getTotal() );

        // confirms the recipes in the order are correct
        final String expectedRecipes = "Coffee: 4, Latte: 3";
        assertEquals( expectedRecipes, orderHistory.getRecipesInOrder(),
                "Recipes string should match expected format" );

        // confirms the ingredients in the order are correct
        final String expectedIngredients = "coffee:12, cream:34, milk:20, pumpkin spice:24, vanilla:30";
        assertEquals( expectedIngredients, orderHistory.getIngredientsUsed(),
                "Ingredients string should match expected format" );

        // confirms order is not picked up should auto set to false)
        assertFalse( orderHistory.getPickedUp() );
    }

    /**
     * Test updating a order status
     */
    @Test
    @Transactional
    void testUpdateOrderStatus () {

        // uses the makeOrderHistory method on order1
        final OrderHistoryDto orderHistory = orderHistoryService.makeOrderHistory( user1.getUsername(), order1 );

        // confirms the object is made
        assertNotNull( orderHistory );

        // confirms it has the correct id
        assertEquals( order1.getId(), orderHistory.getId() );

        // confirms it has the correct total cost
        double expectedTotal = 50 * 4 + 100 * 3;
        expectedTotal += taxService.calcTax( expectedTotal );
        assertEquals( expectedTotal, orderHistory.getTotal() );

        // confirms the recipes in the order are correct
        final String expectedRecipes = "Coffee: 4, Latte: 3";
        assertEquals( expectedRecipes, orderHistory.getRecipesInOrder(),
                "Recipes string should match expected format" );

        // confirms the ingredients in the order are correct
        final String expectedIngredients = "coffee:12, cream:34, milk:20, pumpkin spice:24, vanilla:30";
        assertEquals( expectedIngredients, orderHistory.getIngredientsUsed(),
                "Ingredients string should match expected format" );

        // confirms order is not picked up should auto set to false)
        assertFalse( orderHistory.getPickedUp() );

        // now we will assume the user picked up the order and confirm the
        // status is updated
        assertTrue( orderHistoryService.updateOrderHistoryStatus( orderHistory.getId() ) );

        // now lets get the OrderHistory from the repository and confirm the
        // update
        final OrderHistory updatedOrderHistory = orderHistoryRepository.findById( orderHistory.getId() ).orElse( null );
        // confirms it was found
        assertNotNull( updatedOrderHistory );
        // confirms it now has true for picked up
        assertTrue( updatedOrderHistory.getPickedUp() );
    }

    /**
     * Test getting all orders for multiple users
     */
    @Test
    @Transactional
    void testGetAllOrders () {
        // uses the makeOrderHistory method on order1
        final OrderHistoryDto orderHistory = orderHistoryService.makeOrderHistory( user1.getUsername(), order1 );
        // uses the makeOrderHistory method on order2
        final OrderHistoryDto orderHistory2 = orderHistoryService.makeOrderHistory( user2.getUsername(), order2 );

        // confirms the object is made and both have flag set to false
        assertNotNull( orderHistory );
        assertNotNull( orderHistory2 );
        assertFalse( orderHistory.getPickedUp() );
        assertFalse( orderHistory2.getPickedUp() );

        // now we will assume one of the orders was picked up
        assertTrue( orderHistoryService.updateOrderHistoryStatus( orderHistory.getId() ) );

        // now lets get the OrderHistory from the repository and confirm the
        // update
        final OrderHistory updatedOrderHistory = orderHistoryRepository.findById( orderHistory.getId() ).orElse( null );
        // confirms it was found
        assertNotNull( updatedOrderHistory );
        // confirms it now has true for picked up
        assertTrue( updatedOrderHistory.getPickedUp() );

        // now lets check that getAllOrders returns only it (other order is not
        // picked up yet)
        final List<OrderHistory> historyOne = orderHistoryService.getOrderHistory();
        // should only contain 1 order
        assertEquals( historyOne.size(), 1 );
        assertEquals( historyOne.getFirst(), updatedOrderHistory );
        // now we can update the other order to confirm it now gets added
        assertTrue( orderHistoryService.updateOrderHistoryStatus( orderHistory2.getId() ) );
        final OrderHistory updatedOrderHistory2 = orderHistoryRepository.findById( orderHistory2.getId() )
                .orElse( null );
        assertNotNull( updatedOrderHistory2 );
        assertTrue( updatedOrderHistory.getPickedUp() );
        assertTrue( updatedOrderHistory2.getPickedUp() );
        assertEquals( orderHistoryRepository.findAll().size(), 2 );
        // now that both orders have been picked up, we should get both
        final List<OrderHistory> historyTwo = orderHistoryService.getOrderHistory();
        assertEquals( historyTwo.size(), 2 );
        assertTrue( historyTwo.contains( updatedOrderHistory ) );
        assertTrue( historyTwo.contains( updatedOrderHistory2 ) );
    }

    /**
     * Test getting all orders for multiple users
     */
    @Test
    @Transactional
    void testGetUserOrders () {
        // uses the makeOrderHistory method on order1
        final OrderHistoryDto orderHistory = orderHistoryService.makeOrderHistory( user1.getUsername(), order1 );
        // uses the makeOrderHistory method on order2
        final OrderHistoryDto orderHistory2 = orderHistoryService.makeOrderHistory( user2.getUsername(), order2 );

        // confirms the object is made and both have flag set to false
        assertNotNull( orderHistory );
        assertNotNull( orderHistory2 );
        assertFalse( orderHistory.getPickedUp() );
        assertFalse( orderHistory2.getPickedUp() );

        // now we will assume one of the orders was picked up
        assertTrue( orderHistoryService.updateOrderHistoryStatus( orderHistory.getId() ) );

        // now lets get the OrderHistory from the repository and confirm the
        // update
        final OrderHistory updatedOrderHistory = orderHistoryRepository.findById( orderHistory.getId() ).orElse( null );
        // confirms it was found
        assertNotNull( updatedOrderHistory );
        // confirms it now has true for picked up
        assertTrue( updatedOrderHistory.getPickedUp() );

        // now lets check that getAllOrders returns only it (other order is not
        // picked up yet)
        final List<OrderHistory> historyOne = orderHistoryService.getOrderHistory();
        // should only contain 1 order
        assertEquals( historyOne.size(), 1 );
        assertEquals( historyOne.getFirst(), updatedOrderHistory );
        // now we can update the other order to confirm it now gets added
        assertTrue( orderHistoryService.updateOrderHistoryStatus( orderHistory2.getId() ) );
        final OrderHistory updatedOrderHistory2 = orderHistoryRepository.findById( orderHistory2.getId() )
                .orElse( null );
        assertNotNull( updatedOrderHistory2 );
        assertTrue( updatedOrderHistory.getPickedUp() );
        assertTrue( updatedOrderHistory2.getPickedUp() );
        assertEquals( orderHistoryRepository.findAll().size(), 2 );
        // now that both orders have been picked up, we should get both
        final List<OrderHistory> historyTwo = orderHistoryService.getOrderHistory();
        assertEquals( historyTwo.size(), 2 );
        assertTrue( historyTwo.contains( updatedOrderHistory ) );
        assertTrue( historyTwo.contains( updatedOrderHistory2 ) );
        // now that we know we can get both orders, lets build off this previous
        // test to make sure
        // it can also correctly get the order that belongs to the user
        final List<OrderHistory> userOneList = orderHistoryService.getUserHistory( user1.getUsername() );
        assertEquals( userOneList.size(), 1 );
        assertEquals( userOneList.getFirst(), updatedOrderHistory );
        final List<OrderHistory> userTwoList = orderHistoryService.getUserHistory( user2.getUsername() );
        assertEquals( userTwoList.size(), 1 );
        assertEquals( userTwoList.getFirst(), updatedOrderHistory2 );
    }

    /**
     * Test for getHistoryById method
     */
    @Test
    @Transactional
    void testGetHistoryById () {
        // creates order history for a user
        final OrderHistoryDto orderHistory = orderHistoryService.makeOrderHistory( user1.getUsername(), order1 );

        // confirms the order history object is created
        assertNotNull( orderHistory );
        assertFalse( orderHistory.getPickedUp() );

        // gets with valid id
        final OrderHistory retrievedOrderHistory = orderHistoryService.getHistoryById( orderHistory.getId() );
        assertNotNull( retrievedOrderHistory );
        assertEquals( orderHistory.getId(), retrievedOrderHistory.getId() );

        // test for a fake id
        final Long nonExistentId = 999L; // Assuming this ID does not exist
        final OrderHistory nullOrderHistory = orderHistoryService.getHistoryById( nonExistentId );
        assertNull( nullOrderHistory );
    }

}
