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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import wolfcafe.dto.IngredientDto;
import wolfcafe.dto.OrderDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.entity.MultiRecipe;
import wolfcafe.entity.Order;
import wolfcafe.entity.User;
import wolfcafe.exception.ResourceNotFoundException;
import wolfcafe.exception.WolfCafeAPIException;
import wolfcafe.repository.OrderRepository;
import wolfcafe.repository.UserRepository;
import jakarta.persistence.EntityManager;

/**
 * tests OrderService
 */
@ActiveProfiles("localtest")
@SpringBootTest
class OrderServiceTest {
    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;

    /** reference to user repository */
    @Autowired
    private UserRepository    userRepository;

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService  inventoryService;

    /** Reference to MakeRecipeService (and MakeRecipeServiceImpl). */
    @Autowired
    private OrderService      orderService;

    /** reference to OrderRepository */
    @Autowired
    private OrderRepository   orderRepository;

    /** a valid order to be tested */
    private OrderDto          order1;

    /** a valid order to be tested */
    private OrderDto          order2;

    /** users used to test making an order */
    private User              user1;
    /** users used to test making an order */
    private User              user2;
    /** users used to test making an order */
    private User              user3;

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

    private void checkEquals ( final OrderDto o1, final Order o2 ) {
        assertEquals( o1.getFulfilled(), o2.getFulfilled() );
        for ( int i = 0; i < o1.getRecipes().size(); i++ ) {
            checkEquals( o1.getRecipes().get( i ), o2.getRecipes().get( i ) );
        }
    }

    private void checkEquals ( final OrderDto o1, final OrderDto o2 ) {
        assertEquals( o1.getFulfilled(), o2.getFulfilled() );
        for ( int i = 0; i < o1.getRecipes().size(); i++ ) {
            checkEquals( o1.getRecipes().get( i ), o2.getRecipes().get( i ) );
        }
    }

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

        order2 = new OrderDto( 0L, false, recipes2 );

        user1 = new User( 0L, "alex", "user1", "user1@gmail.com", "pass", null, new ArrayList<Order>() );
        user2 = new User( 0L, "maddy", "user2", "user2@gmail.com", "012345", null, new ArrayList<Order>() );
        user3 = new User( 0L, "maddy", "user3", "user3@gmail.com", "012345", null, new ArrayList<Order>() );

        userRepository.save( user1 );
        userRepository.save( user2 );
        userRepository.save( user3 );
    }

    @Test
    @Transactional
    void testMakeOrder () {

        orderService.makeOrder( user1.getUsername(), order1 );

        final List<Ingredient> ingredients = inventoryService.getInventory().getIngredients();

        assertAll( "testing updated inventory correctly", () -> assertEquals( 21, ingredients.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients.get( 5 ).getAmount() ) );

        assertThrows( ResourceNotFoundException.class, () -> orderService.makeOrder( "turkey", order2 ) );

        orderService.makeOrder( user1.getUsername(), order2 );

        // check that the order is still the same
        checkEquals( order1, userRepository.findByUsername( user1.getUsername() ).get().getOrders().get( 0 ) );

        checkEquals( order2, userRepository.findByUsername( user1.getUsername() ).get().getOrders().get( 1 ) );

        final List<Ingredient> ingredients2 = inventoryService.getInventory().getIngredients();

        assertAll( "testing updated inventory correctly", () -> assertEquals( 3, ingredients2.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients2.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients2.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients2.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients2.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients2.get( 5 ).getAmount() ) );

        for ( final Ingredient ingredient : ingredients2 ) {
            ingredientService.updateIngredient( ingredient.getId(),
                    new IngredientDto( 0L, ingredient.getName(), 100 ) );
        }
        ingredientService.updateIngredient( ingredients2.get( 0 ).getId(),
                new IngredientDto( 0L, ingredients2.get( 0 ).getName(), 18 ) );

        // checking another user making an order
        orderService.makeOrder( user2.getUsername(), order2 );

        final List<Ingredient> ingredients3 = inventoryService.getInventory().getIngredients();
        assertAll( "testing updated inventory correctly", () -> assertEquals( 0, ingredients3.get( 0 ).getAmount() ),
                () -> assertEquals( 100, ingredients3.get( 1 ).getAmount() ),
                () -> assertEquals( 100, ingredients3.get( 2 ).getAmount() ),
                () -> assertEquals( 100, ingredients3.get( 3 ).getAmount() ),
                () -> assertEquals( 100, ingredients3.get( 4 ).getAmount() ),
                () -> assertEquals( 100, ingredients3.get( 5 ).getAmount() ) );

        // check that the order is still the same
        checkEquals( order1, userRepository.findByUsername( user1.getUsername() ).get().getOrders().get( 0 ) );
        checkEquals( order2, userRepository.findByUsername( user1.getUsername() ).get().getOrders().get( 1 ) );
        checkEquals( order2, userRepository.findByUsername( user2.getUsername() ).get().getOrders().get( 0 ) );

        assertThrows( IllegalArgumentException.class, () -> orderService.makeOrder( user3.getUsername(), order2 ) );

    }

    @Test
    @Transactional
    void testGetOrders () {

        assertEquals( 0, orderService.getOrders().size() );

        orderService.makeOrder( user1.getUsername(), order1 );

        List<OrderDto> orders = orderService.getOrders();
        assertEquals( 1, orders.size() );
        checkEquals( order1, orders.get( 0 ) );

        orderService.makeOrder( user2.getUsername(), order2 );

        orders = orderService.getOrders();
        assertEquals( 2, orders.size() );
        checkEquals( order1, orders.get( 0 ) );
        checkEquals( order2, orders.get( 1 ) );

    }

    @Test
    @Transactional
    void testFulfillOrder () {

        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( -1L ) );
        // scan for orders
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 1L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 2L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 3L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 4L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 5L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 6L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 7L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 8L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 9L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( 10L ) );

        final Long id1 = orderService.makeOrder( user1.getUsername(), order1 ).getId();

        final Long id2 = orderService.makeOrder( user2.getUsername(), order2 ).getId();

        assertFalse( orderService.getOrderById( id1 ).getFulfilled() );
        assertFalse( orderService.getOrderById( id2 ).getFulfilled() );

        // testing unique id when orders exist
        assertThrows( ResourceNotFoundException.class, () -> orderService.fulfillOrder( id1 + id2 ) );

        orderService.fulfillOrder( id2 );
        assertFalse( orderService.getOrderById( id1 ).getFulfilled() );
        assertTrue( orderService.getOrderById( id2 ).getFulfilled() );

        assertThrows( IllegalStateException.class, () -> orderService.fulfillOrder( id2 ) );

        assertFalse( orderService.getOrderById( id1 ).getFulfilled() );
        assertTrue( orderService.getOrderById( id2 ).getFulfilled() );

    }

    @Test
    @Transactional
    void testGetOrderById () {

        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( -1L ) );
        // sweep for orders
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 1L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 2L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 3L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 4L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 5L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 6L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 7L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 8L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 9L ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( 10L ) );

        final Long id1 = orderService.makeOrder( user1.getUsername(), order1 ).getId();

        final Long id2 = orderService.makeOrder( user2.getUsername(), order2 ).getId();

        checkEquals( order1, orderService.getOrderById( id1 ) );
        checkEquals( order2, orderService.getOrderById( id2 ) );

        // testing unique id when orders exist
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id1 + id2 ) );

    }

    @Test
    @Transactional
    void testPickupOrder () {

        for ( final Ingredient ingredient : inventoryService.getInventory().getIngredients() ) {
            ingredientService.updateIngredient( ingredient.getId(),
                    new IngredientDto( 0L, ingredient.getName(), 100 ) );
        }

        final Long id1 = orderService.makeOrder( user1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( user1.getUsername(), order2 ).getId();
        final Long id3 = orderService.makeOrder( user2.getUsername(), order1 ).getId();

        // should not be able to pickup new orders that are unfulfilled
        assertEquals( HttpStatus.BAD_REQUEST,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user1.getUsername(), id1 ) )
                        .getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user1.getUsername(), id2 ) )
                        .getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user2.getUsername(), id3 ) )
                        .getStatus() );

        // should not be able to pickup (or detect) other users' orders
        assertEquals( HttpStatus.GONE,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user2.getUsername(), id1 ) )
                        .getStatus() );
        assertEquals( HttpStatus.GONE,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user2.getUsername(), id2 ) )
                        .getStatus() );
        assertEquals( HttpStatus.GONE,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user1.getUsername(), id3 ) )
                        .getStatus() );
        // trying to get an order that doesn't have id
        assertEquals( HttpStatus.GONE, assertThrows( WolfCafeAPIException.class,
                () -> orderService.pickupOrder( user2.getUsername(), id1 + id2 + id3 ) ).getStatus() );

        // pickup order with id2
        orderService.fulfillOrder( id2 );
        orderService.pickupOrder( user1.getUsername(), id2 );
        assertEquals( HttpStatus.BAD_REQUEST,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user1.getUsername(), id1 ) )
                        .getStatus() );
        assertEquals( HttpStatus.GONE,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user1.getUsername(), id2 ) )
                        .getStatus() );
        assertEquals( HttpStatus.BAD_REQUEST,
                assertThrows( WolfCafeAPIException.class, () -> orderService.pickupOrder( user2.getUsername(), id3 ) )
                        .getStatus() );

        // test that it is not in order repository
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id2 ) );
        // test that it is not in user/order join table
        assertEquals( 1, orderService.getOrdersByCustomer( user1.getUsername() ).size() );
        checkEquals( orderService.getOrdersByCustomer( user1.getUsername() ).get( 0 ), order1 );

        orderService.fulfillOrder( id1 );
        orderService.fulfillOrder( id3 );

        orderService.pickupOrder( user1.getUsername(), id1 );

        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id1 ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id2 ) );
        orderService.getOrderById( id3 );

        orderService.pickupOrder( user2.getUsername(), id3 );

        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id1 ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id2 ) );
        assertThrows( ResourceNotFoundException.class, () -> orderService.getOrderById( id3 ) );

    }

}
