package wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.security.test.context.support.WithMockUser;
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
import wolfcafe.entity.Order;
import wolfcafe.entity.User;
import wolfcafe.repository.RecipeRepository;
import wolfcafe.repository.UserRepository;
import wolfcafe.service.AuthService;
import wolfcafe.service.IngredientService;
import wolfcafe.service.InventoryService;
import wolfcafe.service.OrderService;
import wolfcafe.service.RecipeService;
import jakarta.persistence.EntityManager;

/**
 * tests OrderController
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("localtest")
class OrderControllerTest {



    /**
     * the token of the admin
     */
    private String            adminToken;

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc           mvc;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository  recipeRepository;

    /** reference to user repository */
    @Autowired
    private UserRepository    userRepository;

    /** Reference to RecipeService (and RecipeServiceImpl). */
    @Autowired
    private RecipeService     recipeService;

    /** Reference to IngredientService (and IngredientServiceImpl). */
    @Autowired
    private IngredientService ingredientService;

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService  inventoryService;

    /** reference to OrderService */
    @Autowired
    private OrderService      orderService;

    /** reference to AuthService */
    @Autowired
    private AuthService       authService;

    /** an valid order to be tested */
    private OrderDto          order1;

    /** an valid order to be tested */
    private OrderDto          order2;

    /** an invalid order to be tested */
    private OrderDto          invalid1;
    /** an invalid order to be tested */
    private OrderDto          invalid2;
    /** an invalid order to be tested */
    private OrderDto          invalid3;
    /** an invalid order to be tested */
    private OrderDto          invalid4;
    /** an invalid order to be tested */
    private OrderDto          invalid5;
    /** an invalid order to be tested */
    private OrderDto          invalid6;
    /** an invalid order to be tested */
    private OrderDto          invalid7;
    /** an invalid order to be tested */
    private OrderDto          invalid8;
    /** an invalid order to be tested */
    private OrderDto          invalid9;
    /** an invalid order to be tested */
    private OrderDto          invalid10;
    /** an invalid order to be tested */
    private OrderDto          invalid11;
    /** an invalid order to be tested */
    private OrderDto          invalid12;
    /** an invalid order to be tested */
    private OrderDto          invalid13;
    /** an invalid order to be tested */
    private OrderDto          invalid14;
    /** an invalid order to be tested */
    private OrderDto          invalid15;
    /** an invalid order to be tested */
    private OrderDto          invalid16;

    /**
     * checks if two MultiRecipes are equal does not check id
     *
     * @param r1
     *            the first multirecipe to check if equal
     * @param r2
     *            the second multirecipe to check if equal
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
     * checks if two orders are equal does not check id
     *
     * @param o1
     *            the first OrderDto to check if equal
     * @param o2
     *            the second Order to check if equal
     */
    private void checkEquals ( final OrderDto o1, final Order o2 ) {
        assertEquals( o1.getFulfilled(), o2.getFulfilled() );
        for ( int i = 0; i < o1.getRecipes().size(); i++ ) {
            checkEquals( o1.getRecipes().get( i ), o2.getRecipes().get( i ) );
        }
    }

    /**
     * sets up tests with example data
     *
     * @throws Exception
     *             if something goes wrong
     */
    @BeforeEach
    void setUp () throws Exception {
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        ingredientService.deleteAllIngredients();
        recipeRepository.deleteAll();
        final List<User> users = userRepository.findAll();
        for ( int i = users.size() - 1; i > 0; i-- ) {
            userRepository.deleteById( users.get( i ).getId() );
        }

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
        final MultiRecipe recipe2 = new MultiRecipe( 0L, "Latte", 100, ingredientsList2, 3 );

        recipes1.add( recipe1 );
        recipes1.add( recipe2 );
        recipeService.createRecipe( new RecipeDto( 0L, "Coffee", 50, ingredientsList ) );
        recipeService.createRecipe( new RecipeDto( 0L, "Latte", 100, ingredientsList2 ) );

        order1 = new OrderDto( 0L, false, recipes1 );
        invalid1 = new OrderDto( 0L, false, new ArrayList<MultiRecipe>() );
        invalid2 = new OrderDto( 0L, false, null );
        invalid3 = new OrderDto( 0L, false, Arrays.asList( recipe1, null ) );
        final MultiRecipe invalidRecipe4 = new MultiRecipe( 0L, null, 50, ingredientsList, 4 );
        final MultiRecipe invalidRecipe5 = new MultiRecipe( 0L, "hello", 50, ingredientsList, 4 );
        final MultiRecipe invalidRecipe6 = new MultiRecipe( 0L, "Coffee", 6, ingredientsList, 4 );
        final MultiRecipe invalidRecipe7 = new MultiRecipe( 0L, "Coffee", 50, null, 4 );
        final MultiRecipe invalidRecipe8 = new MultiRecipe( 0L, "Coffee", 6, new ArrayList<Ingredient>(), 4 );
        final MultiRecipe invalidRecipe9 = new MultiRecipe( 0L, "Coffee", null, ingredientsList, 4 );
        invalid4 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe4, recipe2 ) );
        invalid5 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe5, recipe2 ) );
        invalid6 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe6, recipe2 ) );
        invalid7 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe7, recipe2 ) );
        invalid8 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe8, recipe2 ) );
        invalid9 = new OrderDto( 0L, false, Arrays.asList( invalidRecipe9, recipe2 ) );

        final MultiRecipe invalidRecipe10 = new MultiRecipe( 0L, "Coffee", 50, ingredientsList, -4 );
        final MultiRecipe invalidRecipe11 = new MultiRecipe( 0L, "Coffee", 50,
                Arrays.asList( new Ingredient( "coffee", 3 ), new Ingredient( "milk", 5 ) ), 4 );
        final MultiRecipe invalidRecipe12 = new MultiRecipe( 0L, "Coffee", 50,
                Arrays.asList( new Ingredient( "not", 3 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ),
                4 );
        final MultiRecipe invalidRecipe13 = new MultiRecipe( 0L, "Coffee", 50,
                Arrays.asList( new Ingredient( null, 3 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ),
                4 );
        final MultiRecipe invalidRecipe14 = new MultiRecipe( 0L, "Coffee", 50, Arrays.asList(
                new Ingredient( "coffee", 2 ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ), 4 );
        final MultiRecipe invalidRecipe15 = new MultiRecipe( 0L, "Coffee", 50, Arrays.asList(
                new Ingredient( "coffee", null ), new Ingredient( "milk", 5 ), new Ingredient( "cream", 4 ) ), 4 );
        final MultiRecipe invalidRecipe16 = new MultiRecipe( 0L, "Coffee", 50, ingredientsList, null );
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
        recipes2.add( new MultiRecipe( 0L, "Just Coffee", 100, ingredientsList3, 2 ) );
        recipeService.createRecipe( new RecipeDto( 0L, "Just Coffee", 100, ingredientsList3 ) );

        order2 = new OrderDto( 0L, false, recipes2 );

    }

    /**
     * tests making an order
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    void testMakeOrder () throws Exception {
    	/**
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );

        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        final RegisterDto register3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", role );

        authService.register( register1, false );
        authService.register( register2, false );
        authService.register( register3, false );
  

        final String token1 = "Bearer " + authService
                .login( new LoginDto( register1.getUsername(), register1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer " + authService
                .login( new LoginDto( register2.getUsername(), register2.getPassword() ) ).getAccessToken();
        final String token3 = "Bearer " + authService
                .login( new LoginDto( register3.getUsername(), register3.getPassword() ) ).getAccessToken();

        assertEquals( "user1", authService.getUsername( token1.substring( 7 ) ) );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token1 ) ).andExpect( status().isOk() );

        final List<Ingredient> ingredients = inventoryService.getInventory().getIngredients();
        assertAll( "testing updated inventory correctly", () -> assertEquals( 21, ingredients.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients.get( 2 ).getAmount  () ),
                () -> assertEquals( 34, ingredients.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients.get( 5 ).getAmount() ) );

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
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid1 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid3 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid4 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid5 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid6 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid7 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid8 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid9 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid10 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid11 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid12 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid13 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid14 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid15 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalid16 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isNotFound() );

        // these invalid tests should not affect inventory
        final List<Ingredient> ingredients2 = inventoryService.getInventory().getIngredients();
        assertAll( "testing no change in inventory", () -> assertEquals( 21, ingredients2.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients2.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients2.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients2.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients2.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients2.get( 5 ).getAmount() ) );

        // valid case after other cases.
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token2 ) ).andExpect( status().isOk() );
        final List<Ingredient> ingredients3 = inventoryService.getInventory().getIngredients();
        assertAll( "testing updated inventory correctly", () -> assertEquals( 3, ingredients3.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients3.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients3.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients3.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients3.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients3.get( 5 ).getAmount() ) );

        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token3 ) ).andExpect( status().isBadRequest() );
        final List<Ingredient> ingredients4 = inventoryService.getInventory().getIngredients();
        assertAll( "testing inventory not updated", () -> assertEquals( 3, ingredients4.get( 0 ).getAmount() ),
                () -> assertEquals( 0, ingredients4.get( 1 ).getAmount() ),
                () -> assertEquals( 66, ingredients4.get( 2 ).getAmount() ),
                () -> assertEquals( 34, ingredients4.get( 3 ).getAmount() ),
                () -> assertEquals( 22, ingredients4.get( 4 ).getAmount() ),
                () -> assertEquals( 20, ingredients4.get( 5 ).getAmount() ) );

        // editing contents of inventory
        for ( final Ingredient ingredient : ingredients4 ) {
            ingredientService.updateIngredient( ingredient.getId(),
                    new IngredientDto( 0L, ingredient.getName(), 100 ) );
        }
        ingredientService.updateIngredient( ingredients4.get( 0 ).getId(),
                new IngredientDto( 0L, ingredients4.get( 0 ).getName(), 18 ) );

        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( order2 ) ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", token1 ) ).andExpect( status().isOk() );

        final List<Ingredient> ingredients5 = inventoryService.getInventory().getIngredients();
        assertAll( "testing updated inventory correctly", () -> assertEquals( 0, ingredients5.get( 0 ).getAmount() ),
                () -> assertEquals( 100, ingredients5.get( 1 ).getAmount() ),
                () -> assertEquals( 100, ingredients5.get( 2 ).getAmount() ),
                () -> assertEquals( 100, ingredients5.get( 3 ).getAmount() ),
                () -> assertEquals( 100, ingredients5.get( 4 ).getAmount() ),
                () -> assertEquals( 100, ingredients5.get( 5 ).getAmount() ) );

        checkEquals( order1, userRepository.findByUsername( "user1" ).get().getOrders().get( 0 ) );
        checkEquals( order2, userRepository.findByUsername( "user1" ).get().getOrders().get( 1 ) );
        checkEquals( order2, userRepository.findByUsername( "user2" ).get().getOrders().get( 0 ) );
	*/
    }

    /**
     * tests getting the list of orders
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetOrders () throws Exception {
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );

        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        final RegisterDto register3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", role );

        authService.register( register1, false );
        authService.register( register2, false );
        authService.register( register3, false );

        final Long id1 = orderService.makeOrder( register1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( register2.getUsername(), order2 ).getId();
        
        
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        RegisterDto registerDto3 = new RegisterDto("Jennifer Neary", "jenneary", "jenneary@ncsu.edu", "ANewPassword", roles);
        // create new admin user and get token
     	mvc.perform(post("/api/auth/adminRegister")
     			.contentType(MediaType.APPLICATION_JSON)
     			.content(TestUtils.asJsonString(registerDto3))
     			.accept(MediaType.APPLICATION_JSON))
     			.andExpect(status().isCreated())
     			.andExpect(content().string("User registered successfully."));
        adminToken = "Bearer " + authService.login( new LoginDto( "jenneary", "ANewPassword" ) ).getAccessToken();
        
        System.out.println("admin token is:" + adminToken);
        mvc.perform( get( "/api/orders" ).contentType( MediaType.APPLICATION_JSON ).accept( MediaType.APPLICATION_JSON )
                .header( "Authorization", adminToken ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].id" ).value( "" + id1 ) )
                .andExpect( jsonPath( "$[1].id" ).value( "" + id2 ) );

    }

    /**
     * tests fulfilling an order
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testFulfillOrder () throws Exception {
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );

        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        final RegisterDto register3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", role );

        authService.register( register1, false );
        authService.register( register2, false );
        authService.register( register3, false );

        final Long id1 = orderService.makeOrder( register1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( register2.getUsername(), order2 ).getId();
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        RegisterDto registerDto3 = new RegisterDto("Jennifer Neary", "jenneary", "jenneary@ncsu.edu", "ANewPassword", roles);
        
        // create new admin user and get token
     	mvc.perform(post("/api/auth/adminRegister")
     			.contentType(MediaType.APPLICATION_JSON)
     			.content(TestUtils.asJsonString(registerDto3))
     			.accept(MediaType.APPLICATION_JSON))
     			.andExpect(status().isCreated())
     			.andExpect(content().string("User registered successfully."));
        adminToken = "Bearer " + authService.login( new LoginDto( "jenneary", "ANewPassword" ) ).getAccessToken();
        
        mvc.perform( put( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isOk() );

        assertTrue( orderService.getOrderById( id1 ).getFulfilled() );
        assertFalse( orderService.getOrderById( id2 ).getFulfilled() );

        mvc.perform( put( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isConflict() );

        mvc.perform( put( "/api/orders/" + ( id1 + id2 ) ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isGone() );

        assertTrue( orderService.getOrderById( id1 ).getFulfilled() );
        assertFalse( orderService.getOrderById( id2 ).getFulfilled() );

    }

    /**
     * tests getting an order by id
     *
     * @throws Exception
     *             if something goes wrong
     */
    @Test
    @Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testGetOrderById () throws Exception {
        final List<String> role = new ArrayList<>();
        role.add( "ROLE_CUSTOMER" );

        final RegisterDto register1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", role );
        final RegisterDto register2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", role );
        final RegisterDto register3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", role );

        authService.register( register1, false );
        authService.register( register2, false );
        authService.register( register3, false );

        final Long id1 = orderService.makeOrder( register1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( register2.getUsername(), order2 ).getId();
        
        
        
        
        
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        RegisterDto registerDto3 = new RegisterDto("Jennifer Neary", "jenneary", "jenneary@ncsu.edu", "ANewPassword", roles);
        // create new admin user and get token
     	mvc.perform(post("/api/auth/adminRegister")
     			.contentType(MediaType.APPLICATION_JSON)
     			.content(TestUtils.asJsonString(registerDto3))
     			.accept(MediaType.APPLICATION_JSON))
     			.andExpect(status().isCreated())
     			.andExpect(content().string("User registered successfully."));
        adminToken = "Bearer " + authService.login( new LoginDto( "jenneary", "ANewPassword" ) ).getAccessToken();
        
        mvc.perform( get( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.id" ).value( "" + id1 ) )
                .andExpect( jsonPath( "$.fulfilled" ).value( "" + order1.getFulfilled() ) )
                .andExpect( jsonPath( "$.recipes[0].name" ).value( order1.getRecipes().get( 0 ).getName() ) );

        mvc.perform( get( "/api/orders/" + id2 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isOk() ).andExpect( jsonPath( "$.id" ).value( "" + id2 ) )
                .andExpect( jsonPath( "$.fulfilled" ).value( "" + order2.getFulfilled() ) )
                .andExpect( jsonPath( "$.recipes[0].name" ).value( order2.getRecipes().get( 0 ).getName() ) );

        mvc.perform( get( "/api/orders/" + ( id1 + id2 ) ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", adminToken ) )
                .andExpect( status().isGone() );
	
    }

    /**
     * tests picking up an order
     *
     * @throws Exception
     *             if test goes wrong
     */
    @Test
    @Transactional
    void testPickupOrder () throws Exception {
        final RegisterDto user1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", null );
        final RegisterDto user2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", null );
        final RegisterDto user3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", null );

        authService.register( user1, false );
        authService.register( user2, false );
        authService.register( user3, false );

        for ( final Ingredient ingredient : inventoryService.getInventory().getIngredients() ) {
            ingredientService.updateIngredient( ingredient.getId(),
                    new IngredientDto( 0L, ingredient.getName(), 100 ) );
        }

        final Long id1 = orderService.makeOrder( user1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( user1.getUsername(), order2 ).getId();
        final Long id3 = orderService.makeOrder( user2.getUsername(), order1 ).getId();

        final String token1 = "Bearer "
                + authService.login( new LoginDto( user1.getUsername(), user1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer "
                + authService.login( new LoginDto( user2.getUsername(), user2.getPassword() ) ).getAccessToken();
        authService.login( new LoginDto( user3.getUsername(), user3.getPassword() ) ).getAccessToken();

        // try to pickup an order that isn't ready
        mvc.perform( delete( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isBadRequest() );
        mvc.perform( delete( "/api/orders/" + id2 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isBadRequest() );
        mvc.perform( delete( "/api/orders/" + id3 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) )
                .andExpect( status().isBadRequest() );

        // try getting someone elses' order or no order
        mvc.perform( delete( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) )
                .andExpect( status().isGone() );
        mvc.perform( delete( "/api/orders/" + id2 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) )
                .andExpect( status().isGone() );
        mvc.perform( delete( "/api/orders/" + id3 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isGone() );
        // order does not exist
        mvc.perform( delete( "/api/orders/" + ( id1 + id2 + id3 ) ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isGone() );

        orderService.fulfillOrder( id2 );

        mvc.perform( delete( "/api/orders/" + id1 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isBadRequest() );
        mvc.perform( delete( "/api/orders/" + id2 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/orders/" + id3 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) )
                .andExpect( status().isBadRequest() );
        // should not exist anymore
        mvc.perform( delete( "/api/orders/" + id2 ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) )
                .andExpect( status().isGone() );

    }

    @Test
    @Transactional
    void testviewOrdersStatus () throws Exception {
        final RegisterDto user1 = new RegisterDto( "Alex", "user1", "alex@gmail.com", "pass1", null );
        final RegisterDto user2 = new RegisterDto( "Keeth", "user2", "keeth@gmail.com", "pass2", null );
        final RegisterDto user3 = new RegisterDto( "Abby", "user3", "abby@gmail.com", "pass3", null );

        authService.register( user1, false );
        authService.register( user2, false );
        authService.register( user3, false );

        for ( final Ingredient ingredient : inventoryService.getInventory().getIngredients() ) {
            ingredientService.updateIngredient( ingredient.getId(),
                    new IngredientDto( 0L, ingredient.getName(), 100 ) );
        }

        final Long id1 = orderService.makeOrder( user1.getUsername(), order1 ).getId();
        final Long id2 = orderService.makeOrder( user1.getUsername(), order2 ).getId();
        final Long id3 = orderService.makeOrder( user2.getUsername(), order1 ).getId();

        final String token1 = "Bearer "
                + authService.login( new LoginDto( user1.getUsername(), user1.getPassword() ) ).getAccessToken();
        final String token2 = "Bearer "
                + authService.login( new LoginDto( user2.getUsername(), user2.getPassword() ) ).getAccessToken();
        authService.login( new LoginDto( user3.getUsername(), user3.getPassword() ) ).getAccessToken();

        mvc.perform( get( "/api/orders/user" ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].id" ).value( "" + id1 ) )
                .andExpect( jsonPath( "$[0].fulfilled" ).value( "" + false ) )
                .andExpect( jsonPath( "$[1].id" ).value( "" + id2 ) )
                .andExpect( jsonPath( "$[1].fulfilled" ).value( "" + false ) );

        mvc.perform( get( "/api/orders/user" ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].id" ).value( "" + id3 ) )
                .andExpect( jsonPath( "$[0].fulfilled" ).value( "" + false ) );

        orderService.fulfillOrder( id2 );

        mvc.perform( get( "/api/orders/user" ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token1 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].id" ).value( "" + id1 ) )
                .andExpect( jsonPath( "$[0].fulfilled" ).value( "" + false ) )
                .andExpect( jsonPath( "$[1].id" ).value( "" + id2 ) )
                .andExpect( jsonPath( "$[1].fulfilled" ).value( "" + true ) );

        mvc.perform( get( "/api/orders/user" ).contentType( MediaType.APPLICATION_JSON )
                .accept( MediaType.APPLICATION_JSON ).header( "Authorization", token2 ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$[0].id" ).value( "" + id3 ) )
                .andExpect( jsonPath( "$[0].fulfilled" ).value( "" + false ) );
    }

}
