package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;

/**
 * Tests IngredientController.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class IngredientControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc              mvc;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
    }

    /**
     * Tests the POST /api/ingredients endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    void testCreateIngredient () throws Exception {
        IngredientDto ingredient1 = new IngredientDto( 1L, "Coffee", 5 );

        mvc.perform( post( "/api/ingredients" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.amount" ).value( "5" ) )
                .andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

        mvc.perform( post( "/api/ingredients" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        ingredient1 = new IngredientDto( 1L, "Coffee", -2 );

        mvc.perform( post( "/api/ingredients" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnsupportedMediaType() );
    }

    /**
     * Tests the GET /api/ingredients endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser(username = "staff", roles = "STAFF")
    void testGetIngredientsAndGetIngredientById () throws Exception {
        final IngredientDto ingredient1 = new IngredientDto( 1L, "Coffee", 5 );
        final IngredientDto ingredient2 = new IngredientDto( 2L, "Apple Cider", 6 );
        final IngredientDto ingredient3 = new IngredientDto( 3L, "Milk", 10 );

        final String result = mvc
                .perform( post( "/api/ingredients" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( ingredient2 ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                        .andExpect( status().isOk() )
                        .andExpect( jsonPath( "$.amount" ).value( "6" ) )
                        .andExpect( jsonPath( "$.name" ).value( "Apple Cider" ) )
                        .andExpect( status().isOk() )
                        .andReturn().getResponse().getContentAsString();

        mvc.perform( post( "/api/ingredients" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient3 ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.amount" ).value( "10" ) )
                .andExpect( jsonPath( "$.name" ).value( "Milk" ) );

        mvc.perform( post( "/api/ingredients" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredient1 ) )
                .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.amount" ).value( "5" ) )
                .andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

        final String ingredients = mvc.perform( get( "/api/ingredients" ) )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        assertTrue( ingredients.contains( "Apple Cider" ) );
        assertTrue( ingredients.contains( "Milk" ) );
        assertTrue( ingredients.contains( "Coffee" ) );

        final JsonObject json = JsonParser.parseString( result ).getAsJsonObject();
        final Long actualId = json.get( "id" ).getAsLong();

        // Use the actualId in subsequent GET requests
        final String appleCider = mvc.perform( get( "/api/ingredients/" + actualId ) )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.name" ).value( "Apple Cider" ) )
                .andReturn().getResponse().getContentAsString();

        assertTrue( appleCider.contains( "Apple Cider" ) );
    }

}
