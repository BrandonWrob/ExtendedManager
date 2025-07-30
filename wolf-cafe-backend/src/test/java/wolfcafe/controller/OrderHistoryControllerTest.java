package wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import wolfcafe.TestUtils;
import wolfcafe.dto.IngredientDto;
import wolfcafe.dto.LoginDto;
import wolfcafe.dto.OrderDto;
import wolfcafe.dto.RecipeDto;
import wolfcafe.dto.RegisterDto;
import wolfcafe.entity.Ingredient;
import wolfcafe.entity.MultiRecipe;
import wolfcafe.entity.OrderHistory;
import wolfcafe.entity.Tax;
import wolfcafe.entity.User;
import wolfcafe.repository.OrderHistoryRepository;
import wolfcafe.repository.RecipeRepository;
import wolfcafe.repository.TaxRepository;
import wolfcafe.repository.UserRepository;
import wolfcafe.service.AuthService;
import wolfcafe.service.IngredientService;
import wolfcafe.service.OrderHistoryService;
import wolfcafe.service.RecipeService;
import jakarta.persistence.EntityManager;

/**
 * tests OrderController
 */
@SpringBootTest
@ActiveProfiles("localtest")
@AutoConfigureMockMvc
class OrderHistoryControllerTest {

    /**
     * the password of the admin
     */
    @Value ( "${app.admin-user-password}" )
    private String                 adminUserPassword;

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc                mvc;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager          entityManager;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository       recipeRepository;

    /** reference to user repository */
    @Autowired
    private UserRepository         userRepository;

    /** reference to OrderHistoryRepository */
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    /** Reference to RecipeService (and RecipeServiceImpl). */
    @Autowired
    private RecipeService          recipeService;

    /** Reference to OrderHistoryService */
    @Autowired
    private OrderHistoryService    orderHistoryService;

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService      ingredientService;

    /** Reference to tax repository */
    @Autowired
    private TaxRepository          taxRepository;

    /** reference to AuthService */
    @Autowired
    private AuthService            authService;

    /** an valid order to be tested */
    private OrderDto               order1;

    /** an valid order to be tested */
    private OrderDto               order2;

    /** an invalid order to be tested */
    private OrderDto               invalid1;
    /** an invalid order to be tested */
    private OrderDto               invalid2;
    /** an invalid order to be tested */
    private OrderDto               invalid3;
    /** an invalid order to be tested */
    private OrderDto               invalid4;
    /** an invalid order to be tested */
    private OrderDto               invalid5;
    /** an invalid order to be tested */
    private OrderDto               invalid6;
    /** an invalid order to be tested */
    private OrderDto               invalid7;
    /** an invalid order to be tested */
    private OrderDto               invalid8;
    /** an invalid order to be tested */
    private OrderDto               invalid9;
    /** an invalid order to be tested */
    private OrderDto               invalid10;
    /** an invalid order to be tested */
    private OrderDto               invalid11;
    /** an invalid order to be tested */
    private OrderDto               invalid12;
    /** an invalid order to be tested */
    private OrderDto               invalid13;
    /** an invalid order to be tested */
    private OrderDto               invalid14;
    /** an invalid order to be tested */
    private OrderDto               invalid15;
    /** an invalid order to be tested */
    private OrderDto               invalid16;

    /**
     * sets up tests with example data
     *
     * @throws Exception
     *             if something goes wrong
     */
    @BeforeEach
    void setUp () throws Exception {

        taxRepository.deleteAll();
        final Tax initialTax = new Tax();
        initialTax.setRate( 0.02 ); // 2%
        taxRepository.save( initialTax );

        // gets rid of exception for testing
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();
        // clears repositories out
        ingredientService.deleteAllIngredients();
        recipeRepository.deleteAll();
        final List<User> users = userRepository.findAll();
        for ( int i = users.size() - 1; i > 0; i-- ) {
            userRepository.deleteById( users.get( i ).getId() );
        }
        orderHistoryRepository.deleteAll();

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

        final MultiRecipe recipe1 = new MultiRecipe( 0L, "Coffee", 50, ingredientsList, 4 );
        final MultiRecipe recipe2 = new MultiRecipe( 1L, "Latte", 100, ingredientsList2, 3 );

        recipes1.add( recipe1 );
        recipes1.add( recipe2 );
        recipeService.createRecipe( new RecipeDto( 0L, "Coffee", 50, ingredientsList ) );
        recipeService.createRecipe( new RecipeDto( 1L, "Latte", 100, ingredientsList2 ) );

        order1 = new OrderDto( 0L, false, recipes1 );
        invalid1 = new OrderDto( 0L, false, new ArrayList<MultiRecipe>() );
        invalid2 = new OrderDto( 0L, false, null );
        invalid3 = new OrderDto( 0L, false, Arrays.asList( recipe1, null ) );
        final MultiRecipe invalidRecipe4 = new MultiRecipe( 2L, null, 50, ingredientsList, 4 );
        final MultiRecipe invalidRecipe5 = new MultiRecipe( 3L, "hello", 50, ingredientsList, 4 );
        final MultiRecipe invalidRecipe6 = new MultiRecipe( 4L, "Coffee", 6, ingredientsList, 4 );
        final MultiRecipe invalidRecipe7 = new MultiRecipe( 5L, "Coffee", 50, null, 4 );
        final MultiRecipe invalidRecipe8 = new MultiRecipe( 6L, "Coffee", 6, new ArrayList<Ingredient>(), 4 );
        final MultiRecipe invalidRecipe9 = new MultiRecipe( 7L, "Coffee", null, ingredientsList, 4 );
        invalid4 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe4, recipe2 ) );
        invalid5 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe5, recipe2 ) );
        invalid6 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe6, recipe2 ) );
        invalid7 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe7, recipe2 ) );
        invalid8 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe8, recipe2 ) );
        invalid9 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe9, recipe2 ) );

        final MultiRecipe invalidRecipe10 = new MultiRecipe( 8L, "Coffee", 50, ingredientsList, -4 );
        final MultiRecipe invalidRecipe11 = new MultiRecipe( 9L, "Coffee", 50,
                Arrays.asList( new Ingredient( "coffee", 3 ), new Ingredient( "milk", 5 ) ), 4 );
        final MultiRecipe invalidRecipe12 = new MultiRecipe( 10L, "Coffee", 50,
                Arrays.asList( new Ingredient( "not", 3 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ),
                4 );
        final MultiRecipe invalidRecipe13 = new MultiRecipe( 11L, "Coffee", 50,
                Arrays.asList( new Ingredient( null, 3 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ),
                4 );
        final MultiRecipe invalidRecipe14 = new MultiRecipe( 12L, "Coffee", 50, Arrays.asList(
                new Ingredient( "coffee", 2 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ), 4 );
        final MultiRecipe invalidRecipe15 = new MultiRecipe( 13L, "Coffee", 50, Arrays.asList(
                new Ingredient( "coffee", null ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ), 4 );
        final MultiRecipe invalidRecipe16 = new MultiRecipe( 14L, "Coffee", 50, ingredientsList, null );
        invalid10 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe10, recipe2 ) );
        invalid11 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe11, recipe2 ) );
        invalid12 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe12, recipe2 ) );
        invalid13 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe13, recipe2 ) );
        invalid14 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe14, recipe2 ) );
        invalid15 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe15, recipe2 ) );
        invalid16 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe16, recipe2 ) );

        final List<Ingredient> ingredientsList3 = new ArrayList<Ingredient>();
        ingredientsList3.add( new Ingredient( "coffee", 9 ) );

        final List<MultiRecipe> recipes2 = new ArrayList<MultiRecipe>();
        recipes2.add( new MultiRecipe( 15L, "Just Coffee", 100, ingredientsList3, 2 ) );
        recipeService.createRecipe( new RecipeDto( 15L, "Just Coffee", 100, ingredientsList3 ) );

        order2 = new OrderDto( 2L, false, recipes2 );

    }

    /**
     * tests updating an order status
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testUpdateOrderStatus () throws Exception {
        try {
            // goes through process of verifying the order is correct and making
            // it
            final List<String> role = new ArrayList<>();
            role.add( "ROLE_STAFF" );
            final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
            final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
            authService.register( register1, false );
            authService.register( register2, false );
            System.out.println( "User registration successful." + register1 );
            final String token1 = "Bearer " + authService
                    .login( new LoginDto( register1.getUsername(), register1.getPassword() ) ).getAccessToken();
            final String token2 = "Bearer " + authService
                    .login( new LoginDto( register2.getUsername(), register2.getPassword() ) ).getAccessToken();
            System.out.println( "Login successful, tokens generated." );

            // valid cases making an order for user 1 and 2
            mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( order1 ) ).accept( MediaType.APPLICATION_JSON )
                    .header( "Authorization", token1 ));
            mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                    .header( "Authorization", token2 ) ).andExpect( status().isOk() );
            System.out.println( "Orders placed successfully." );

            // confirms the order history was made successfully for both users
            // orders and currently it is set to fault
            final List<OrderHistory> orderHistory = orderHistoryRepository.findAll();
            assertEquals( orderHistory.size(), 2 );
            System.out.println( "Order history contains both orders." );
            for ( final OrderHistory history : orderHistory ) {
                assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
                assertTrue( history.getUsername().equals( register1.getUsername() )
                        || history.getUsername().equals( register2.getUsername() ) );
                assertFalse( history.getPickedUp() );
            }
            System.out.println( "orders are not picked up." );
            // updates the orders using the API
            mvc.perform( put( "/api/orders/history/status/{id}", order1.getId() ).header( "Authorization", token1 ) )
                    .andExpect( status().isOk() );
            mvc.perform( put( "/api/orders/history/status/{id}", order2.getId() ).header( "Authorization", token2 ) )
                    .andExpect( status().isOk() );
            System.out.println( "orders are picked up." );
            final List<OrderHistory> orderHistoryUpdated = orderHistoryRepository.findAll();
            for ( final OrderHistory history : orderHistoryUpdated ) {
                assertTrue( history.getPickedUp() );
            }
            System.err.println( "success" );
        }
        catch ( final Exception e ) {
            System.err.println( "Test failed: " + e.getMessage() );
            throw e;
        }

    }

    /**
     * tests making an order history
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testMakeOrderHistory () throws Exception {
        // goes through process of verifying the order is correct and making it
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );
        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        authService.register( register1, false );
        authService.register( register2, false );
        final String token1 = "Bearer " + authService
                .login( new LoginDto( register1.getUsername(), register1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer " + authService
                .login( new LoginDto( register2.getUsername(), register2.getPassword() ) ).getAccessToken();

        // Rigorous testing of invalid input is necessary because any customer
        // (which anyone can be) can do this, so we don't want them to be able
        // to muck our systems (although even privileged users shouldn't, this
        // is our most visible plane for attackers). Especially if they can fool
        // our system
        // to
        // believe a recipe exists in a way that it doesn't
        // so we must test every bad format option in the object given.
        // creating badly formed objects and mismatches with recorded data.
        // explained what each catches:
        // 1: no recipes
        // 2: null recipes
        // 3: null object in recipes list
        // 4: null recipe name
        // 5: not found recipe name
        // 6: different price
        // 7: null ingredient list
        // 8: empty ingredient list
        // 9: null price
        // 10: negative amount
        // 11: different size ingredient
        // 12: different named ingredeint
        // 13: null named ingredient
        // 14: different amount ingredient
        // 15: null amount ingredient
        // 16: null amount
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid3 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid4 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid5 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid6 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid7 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid8 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid9 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid10 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid11 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid12 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid13 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid14 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid15 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid16 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );

        // valid cases making an order for user 1 and 2
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token1 ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isOk() );

        // confirms the order history was made successfully for both users
        // orders
        final List<OrderHistory> orderHistory = orderHistoryRepository.findAll();
        assertEquals( orderHistory.size(), 2 );
        for ( final OrderHistory history : orderHistory ) {
            assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
            assertTrue( history.getUsername().equals( register1.getUsername() )
                    || history.getUsername().equals( register2.getUsername() ) );
        }

    }

    /**
     * tests getting the order history
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testGetOrderHistory () throws Exception {
        // goes through process of verifying the order is correct and making it
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );
        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        authService.register( register1, false );
        authService.register( register2, false );
        final String token1 = "Bearer " + authService
                .login( new LoginDto( register1.getUsername(), register1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer " + authService
                .login( new LoginDto( register2.getUsername(), register2.getPassword() ) ).getAccessToken();

        // valid cases making an order for user 1 and 2
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token1 ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isOk() );

        // confirms the order history was made successfully for both users
        // orders and currently it is set to fault
        final List<OrderHistory> orderHistory = orderHistoryRepository.findAll();
        assertEquals( orderHistory.size(), 2 );
        for ( final OrderHistory history : orderHistory ) {
            assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
            assertTrue( history.getUsername().equals( register1.getUsername() )
                    || history.getUsername().equals( register2.getUsername() ) );
            assertFalse( history.getPickedUp() );
        }
        // gets the order history when both are NOT picked up
        mvc.perform( get( "/api/orders/history" ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 0 ) );
        // makes the orders true
        assertTrue( orderHistoryService.updateOrderHistoryStatus( order1.getId() ) );
        assertTrue( orderHistoryService.updateOrderHistoryStatus( order2.getId() ) );
        final List<OrderHistory> orderHistoryUpdated = orderHistoryRepository.findAll();
        for ( final OrderHistory history : orderHistoryUpdated ) {
            assertTrue( history.getPickedUp() );
        }
        // gets the order history when both are picked up
        mvc.perform( get( "/api/orders/history" ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 2 ) );
        final List<OrderHistory> updatedOrderHistory = orderHistoryRepository.findAll();
        for ( final OrderHistory history : updatedOrderHistory ) {
            assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
            assertTrue( history.getUsername().equals( register1.getUsername() )
                    || history.getUsername().equals( register2.getUsername() ) );
            assertTrue( history.getPickedUp() );
        }

        final Long nonExistentOrderId = 999L;
        // tries updating the order status for a fake order
        mvc.perform( put( "/api/orders/history/status/{id}", nonExistentOrderId ).header( "Authorization", token1 ) )
                .andExpect( status().isBadRequest() ).andExpect( content().string( "false" ) );
    }

    /**
     * Test getting a history by id
     *
     * @throws Exception
     *             if invalid id
     */
    @Test
    @Transactional
    void testGetHistoryById () throws Exception {
        // registers a user and creates a token
        final List<String> role = List.of( "ROLE_CUSTOMER" );
        final RegisterDto register = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        authService.register( register, false );
        final String token = "Bearer "
                + authService.login( new LoginDto( register.getUsername(), register.getPassword() ) ).getAccessToken();

        // creates an order history
        assertDoesNotThrow(() -> 
            mvc.perform(post("/api/orders/history")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtils.asJsonString(order1))
                    .header("Authorization", token))
                    .andExpect(status().isOk())
        );

        // gets the order history by ID
        assertDoesNotThrow(() -> 
            mvc.perform(get("/api/orders/history/user/{id}", order1.getId())
                    .header("Authorization", token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(order1.getId()))
                    .andDo(result -> assertNotNull(result.getResponse(), "Response for fetching order history should not be null"))
        );
        // test non-existent order ID
        assertDoesNotThrow(() -> 
            mvc.perform(get("/api/orders/history/user/{id}", 999L)
                    .header("Authorization", token))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("999"))
                    .andDo(result -> assertNotNull(result.getResponse(), "Response for non-existent order history should not be null"))
        );
    }

    /**
     * tests getting the order history of a user
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testGetUserHistory () throws Exception {
        // goes through process of verifying the order is correct and making it
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );
        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        authService.register( register1, false );
        authService.register( register2, false );
        final String token1 = "Bearer " + authService
                .login( new LoginDto( register1.getUsername(), register1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer " + authService
                .login( new LoginDto( register2.getUsername(), register2.getPassword() ) ).getAccessToken();

        // valid cases making an order for user 1 and 2
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token1 ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/orders/history" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isOk() );

        // confirms the order history was made successfully for both users
        // orders and currently it is set to fault
        final List<OrderHistory> orderHistory = orderHistoryRepository.findAll();
        assertEquals( orderHistory.size(), 2 );
        for ( final OrderHistory history : orderHistory ) {
            assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
            assertTrue( history.getUsername().equals( register1.getUsername() )
                    || history.getUsername().equals( register2.getUsername() ) );
            assertFalse( history.getPickedUp() );
        }
        // gets the order history when both are NOT picked up
        mvc.perform( get( "/api/orders/history" ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 0 ) );
        // makes the orders true
        assertTrue( orderHistoryService.updateOrderHistoryStatus( order1.getId() ) );
        assertTrue( orderHistoryService.updateOrderHistoryStatus( order2.getId() ) );
        final List<OrderHistory> orderHistoryUpdated = orderHistoryRepository.findAll();
        for ( final OrderHistory history : orderHistoryUpdated ) {
            assertTrue( history.getPickedUp() );
        }
        // gets the order history when both are picked up
        mvc.perform( get( "/api/orders/history" ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 2 ) );
        final List<OrderHistory> updatedOrderHistory = orderHistoryRepository.findAll();
        for ( final OrderHistory history : updatedOrderHistory ) {
            assertTrue( history.getId() == order1.getId() || history.getId() == order2.getId() );
            assertTrue( history.getUsername().equals( register1.getUsername() )
                    || history.getUsername().equals( register2.getUsername() ) );
            assertTrue( history.getPickedUp() );
        }
        // API call for user1 order history
        mvc.perform( get( "/api/orders/history/{username}", register1.getUsername() ).header( "Authorization", token1 )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 1 ) )
                .andExpect( jsonPath( "$[0].id" ).value( order1.getId() ) )
                .andExpect( jsonPath( "$[0].username" ).value( register1.getUsername() ) )
                .andExpect( jsonPath( "$[0].pickedUp" ).value( true ) );

        // API call for user2 order history
        mvc.perform( get( "/api/orders/history/{username}", register2.getUsername() ).header( "Authorization", token2 )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.length()" ).value( 1 ) )
                .andExpect( jsonPath( "$[0].id" ).value( order2.getId() ) )
                .andExpect( jsonPath( "$[0].username" ).value( register2.getUsername() ) )
                .andExpect( jsonPath( "$[0].pickedUp" ).value( true ) );

        // tries to get the order history for a non-existent user
        final String nonExistentUsername = "nonexistentUser";

        mvc.perform( get( "/api/orders/history/{username}", nonExistentUsername ).header( "Authorization", token1 )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$" ).value( "User not found: " + nonExistentUsername ) );
    }

}