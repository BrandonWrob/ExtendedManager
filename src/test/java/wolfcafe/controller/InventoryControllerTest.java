/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.dto.InventoryDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.mapper.IngredientMapper;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;
import jakarta.persistence.EntityManager;

/**
 * Tests InventoryController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc           mvc;

    /** Reference to EntityManager */
    @Autowired
    private EntityManager     entityManager;

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
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 0" ).executeUpdate();
        entityManager.createNativeQuery( "TRUNCATE TABLE inventory" ).executeUpdate();
        entityManager.createNativeQuery( "SET FOREIGN_KEY_CHECKS = 1" ).executeUpdate();

        // Query query = entityManager.createNativeQuery("TRUNCATE TABLE
        // inventory");
        // query.executeUpdate();
    }

    /**
     * Tests the POST /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testCreateInventory () throws Exception {
        final List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( new Ingredient( "coffee", 2 ) );
        ingredientsList.add( new Ingredient( "milk", 3 ) );
        ingredientsList.add( new Ingredient( "cream", 3 ) );

        final InventoryDto initialInventory = new InventoryDto( 1L, ingredientsList );

        mvc.perform( post( "/api/inventory" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( initialInventory ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id" ).value( 1L ) )
                .andExpect( jsonPath( "$.ingredients" ).isArray() )
                .andExpect( jsonPath( "$.ingredients.length()" ).value( ingredientsList.size() ) )
                .andExpect( jsonPath( "$.ingredients[0].name" ).value( ingredientsList.get( 0 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( ingredientsList.get( 0 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( ingredientsList.get( 1 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( ingredientsList.get( 1 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( ingredientsList.get( 2 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( ingredientsList.get( 2 ).getAmount() ) );
    }

    /**
     * Tests the POST /api/inventory endpoint with invalid ingredients.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testCreateInventoryInvalid () throws Exception {
        final List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( new Ingredient( "coffee", 1 ) );
        ingredientsList.add( new Ingredient( "milk", 1 ) );
        ingredientsList.add( new Ingredient( "cream", -1 ) );

        final InventoryDto initialInventory = new InventoryDto( 1L, ingredientsList );

        mvc.perform( post( "/api/inventory" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( initialInventory ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnsupportedMediaType() )
                .andExpect( jsonPath( "$.id" ).value( 1L ) )
                .andExpect( jsonPath( "$.ingredients" ).isArray() )
                .andExpect( jsonPath( "$.ingredients.length()" ).value( ingredientsList.size() ) )
                .andExpect( jsonPath( "$.ingredients[0].name" ).value( ingredientsList.get( 0 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( ingredientsList.get( 0 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( ingredientsList.get( 1 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( ingredientsList.get( 1 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( ingredientsList.get( 2 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( ingredientsList.get( 2 ).getAmount() ) );
    }

    /**
     * Tests the GET /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testGetInventory () throws Exception {
        final InventoryDto expectedInventory = new InventoryDto( 1L, new ArrayList<Ingredient>() );

        mvc.perform( get( "/api/inventory" ) )
                .andExpect( content().string( TestUtils.asJsonString( expectedInventory ) ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id" ).value( 1L ) )
                .andExpect( jsonPath( "$.ingredients" ).isArray() )
                .andExpect( jsonPath( "$.ingredients.length()" ).value( 0 ) );
    }

    /**
     * Tests the PUT /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    public void testUpdateInventory () throws Exception {

        final InventoryDto expectedInventory = new InventoryDto( 1L, new ArrayList<Ingredient>() );

        mvc.perform( get( "/api/inventory" ) )
                .andExpect( content().string( TestUtils.asJsonString( expectedInventory ) ) )
                .andExpect( status().isOk() );

        // create ingredients, which are added to the inventory
        final IngredientDto ing1 = ingredientService.createIngredient( new IngredientDto( null, "coffee", 0 ) );
        final IngredientDto ing2 = ingredientService.createIngredient( new IngredientDto( null, "milk", 0 ) );
        final IngredientDto ing3 = ingredientService.createIngredient( new IngredientDto( null, "cream", 0 ) );

        final Ingredient i1 = IngredientMapper.mapToIngredient( ing1 );
        final Ingredient i2 = IngredientMapper.mapToIngredient( ing2 );
        final Ingredient i3 = IngredientMapper.mapToIngredient( ing3 );

        i1.setAmount( 10 );
        i2.setAmount( 10 );
        i3.setAmount( 10 );

        final List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
        ingredientsList.add( i1 );
        ingredientsList.add( i2 );
        ingredientsList.add( i3 );

        final InventoryDto updatedInventory = new InventoryDto( 1L, ingredientsList );

        mvc.perform( put( "/api/inventory" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( updatedInventory ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.id" ).value( 1L ) )
                .andExpect( jsonPath( "$.ingredients" ).isArray() )
                .andExpect( jsonPath( "$.ingredients.length()" ).value( ingredientsList.size() ) )
                .andExpect( jsonPath( "$.ingredients[0].name" ).value( ingredientsList.get( 0 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( ingredientsList.get( 0 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( ingredientsList.get( 1 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( ingredientsList.get( 1 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( ingredientsList.get( 2 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( ingredientsList.get( 2 ).getAmount() ) );

        i1.setAmount( 1 );
        i2.setAmount( -1 );
        i3.setAmount( 1 );

        final List<Ingredient> ingredientsList2 = new ArrayList<Ingredient>();
        ingredientsList2.add( i1 );
        ingredientsList2.add( i2 );
        ingredientsList2.add( i3 );

        final InventoryDto invalidInventory = new InventoryDto( 1L, ingredientsList2 );
        mvc.perform( put( "/api/inventory" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( invalidInventory ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnsupportedMediaType() )
                .andExpect( jsonPath( "$.id" ).value( 1L ) )
                .andExpect( jsonPath( "$.ingredients" ).isArray() )
                .andExpect( jsonPath( "$.ingredients.length()" ).value( ingredientsList2.size() ) )
                .andExpect( jsonPath( "$.ingredients[0].name" ).value( ingredientsList2.get( 0 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[0].amount" ).value( ingredientsList2.get( 0 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[1].name" ).value( ingredientsList2.get( 1 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[1].amount" ).value( ingredientsList2.get( 1 ).getAmount() ) )
                .andExpect( jsonPath( "$.ingredients[2].name" ).value( ingredientsList2.get( 2 ).getName() ) )
                .andExpect( jsonPath( "$.ingredients[2].amount" ).value( ingredientsList2.get( 2 ).getAmount() ) );
    }

}
