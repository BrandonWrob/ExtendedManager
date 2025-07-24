package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.entity.Tax;
import edu.ncsu.csc326.wolfcafe.repository.TaxRepository;

/**
 * Tests the TaxController endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaxControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc       mockMvc;

    /** Reference to TaxRepository */
    @Autowired
    private TaxRepository taxRepository;

    /** ObjectMapper for JSON serialization/deserialization */
    @Autowired
    private ObjectMapper  objectMapper;

    /** Variable to hold the Tax entity's ID */
    private Long          taxId;

    /**
     * Sets up the test case.
     *
     * @throws Exception
     *             if error occurs during setup
     */
    @BeforeEach
    public void setUp () throws Exception {
        // Clear the tax repository before each test
        taxRepository.deleteAll();

        // Initialize a default tax rate as a decimal (e.g., 0.02 for 2%)
        final Tax defaultTax = new Tax();
        defaultTax.setRate( 0.02 ); // Correct representation for 2%
        final Tax savedTax = taxRepository.save( defaultTax );
        taxId = savedTax.getId();
    }

    /**
     * Tests the GET /api/tax endpoint as an authenticated ADMIN user.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testGetTaxAsAuthenticatedUser () throws Exception {
        final MvcResult result = mockMvc.perform( get( "/api/tax" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.id" ).value( taxId ) ).andExpect( jsonPath( "$.rate" ).value( 0.02 ) )
                .andReturn();

        // Deserialize response to TaxDto
        final String responseContent = result.getResponse().getContentAsString();
        final TaxDto taxDto = objectMapper.readValue( responseContent, TaxDto.class );

        // Assert the deserialized object
        assertNotNull( taxDto, "Response TaxDto should not be null." );
        assertEquals( taxId, taxDto.getId(), "TaxDto ID should match the saved tax ID." );
        assertEquals( 0.02, taxDto.getRate(), "TaxDto rate should be 0.02%" );
    }

    /**
     * Tests the GET /api/tax endpoint as an authenticated STAFF user.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staffUser", roles = "STAFF" )
    void testGetTaxAsStaffUser () throws Exception {
        final MvcResult result = mockMvc.perform( get( "/api/tax" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.id" ).value( taxId ) ).andExpect( jsonPath( "$.rate" ).value( 0.02 ) )
                .andReturn();

        // Deserialize response to TaxDto
        final String responseContent = result.getResponse().getContentAsString();
        final TaxDto taxDto = objectMapper.readValue( responseContent, TaxDto.class );

        // Assert the deserialized object
        assertNotNull( taxDto, "Response TaxDto should not be null." );
        assertEquals( taxId, taxDto.getId(), "TaxDto ID should match the saved tax ID." );
        assertEquals( 0.02, taxDto.getRate(), "TaxDto rate should be 0.02%" );
    }

    /**
     * Tests the GET /api/tax endpoint as an unauthenticated user. Expects 401
     * Unauthorized.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    void testGetTaxUnauthenticated () throws Exception {
        // Perform the GET request twice: once to expect Unauthorized, and again
        // to assert response content
        mockMvc.perform( get( "/api/tax" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnauthorized() );

        // Perform the GET request again to capture the response content
        final MvcResult result = mockMvc.perform( get( "/api/tax" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnauthorized() ).andReturn();

        final String responseContent = result.getResponse().getContentAsString();
        assertEquals( 0, responseContent.length(), "Response content should be empty for unauthorized access." );
    }

    /**
     * Tests the PUT /api/tax endpoint as an ADMIN user. Expects successful
     * update of tax rate.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testSetTaxAsAdminSuccess () throws Exception {
        final Double newRate = 0.04; // 4%
        final TaxDto updatedTaxDto = new TaxDto( taxId, newRate );

        final MvcResult result = mockMvc
                .perform( put( "/api/tax" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( newRate ) ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.id" ).value( updatedTaxDto.getId() ) )
                .andExpect( jsonPath( "$.rate" ).value( updatedTaxDto.getRate() ) ).andReturn();

        // Deserialize response to TaxDto
        final String responseContent = result.getResponse().getContentAsString();
        final TaxDto responseTaxDto = objectMapper.readValue( responseContent, TaxDto.class );

        // Assert the deserialized object
        assertNotNull( responseTaxDto, "Response TaxDto should not be null." );
        assertEquals( updatedTaxDto.getId(), responseTaxDto.getId(), "TaxDto ID should match." );
        assertEquals( updatedTaxDto.getRate(), responseTaxDto.getRate(), "TaxDto rate should be updated to 0.04%." );

        // Verify that the tax rate has been updated in the repository
        final Tax currentTax = taxRepository.findById( taxId ).orElse( null );
        assertNotNull( currentTax, "Tax entity should exist." );
        assertEquals( newRate, currentTax.getRate(), "Tax rate should be updated to 0.04%" );
    }

    /**
     * Tests the PUT /api/tax endpoint as a STAFF user. Expects successful
     * update of tax rate.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staffUser", roles = "STAFF" )
    void testSetTaxAsStaffSuccess () throws Exception {
        final Double newRate = 0.05; // 5%
        final TaxDto updatedTaxDto = new TaxDto( taxId, newRate );

        final MvcResult result = mockMvc
                .perform( put( "/api/tax" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( newRate ) ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.id" ).value( updatedTaxDto.getId() ) )
                .andExpect( jsonPath( "$.rate" ).value( updatedTaxDto.getRate() ) ).andReturn();

        // Deserialize response to TaxDto
        final String responseContent = result.getResponse().getContentAsString();
        final TaxDto responseTaxDto = objectMapper.readValue( responseContent, TaxDto.class );

        // Assert the deserialized object
        assertNotNull( responseTaxDto, "Response TaxDto should not be null." );
        assertEquals( updatedTaxDto.getId(), responseTaxDto.getId(), "TaxDto ID should match." );
        assertEquals( updatedTaxDto.getRate(), responseTaxDto.getRate(), "TaxDto rate should be updated to 0.05%." );

        // Verify that the tax rate has been updated in the repository
        final Tax currentTax = taxRepository.findById( taxId ).orElse( null );
        assertNotNull( currentTax, "Tax entity should exist." );
        assertEquals( newRate, currentTax.getRate(), "Tax rate should be updated to 0.05%" );
    }

    /**
     * Tests the PUT /api/tax endpoint as a CUSTOMER user. Expects a 403
     * Forbidden response.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "customerUser", roles = "CUSTOMER" )
    void testSetTaxAsCustomerForbidden () throws Exception {
        final Double newRate = 0.06; // 6%

        mockMvc.perform( put( "/api/tax" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( newRate ) ) ).andExpect( status().isForbidden() );

        // Verify that the tax rate remains unchanged
        final Tax currentTax = taxRepository.findById( taxId ).orElse( null );
        assertNotNull( currentTax, "Tax entity should exist." );
        assertEquals( 0.02, currentTax.getRate(), "Tax rate should remain at 0.02%" );
    }

    /**
     * Tests the PUT /api/tax endpoint with an invalid (negative) rate as ADMIN.
     * Expects a 400 Bad Request response.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testSetTaxInvalidRateNegative () throws Exception {
        final Double invalidRate = -0.01; // -1%

        final MvcResult result = mockMvc
                .perform( put( "/api/tax" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( invalidRate ) ) )
                .andExpect( status().isBadRequest() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.message" ).value( "Tax rate must be a positive value." ) ).andReturn();

        // Assert the exact response body
        final String responseContent = result.getResponse().getContentAsString();
        assertEquals( "{\"message\": \"Tax rate must be a positive value.\"}", responseContent,
                "Error message should match the expected value." );

        // Verify that the tax rate remains unchanged
        final Tax currentTax = taxRepository.findById( taxId ).orElse( null );
        assertNotNull( currentTax, "Tax entity should exist." );
        assertEquals( 0.02, currentTax.getRate(), "Tax rate should remain at 0.02%" );
    }

    /**
     * Tests the GET /api/tax/calc endpoint as an authenticated ADMIN user with
     * a valid pre-tax amount. Expects correct tax calculation.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testCalcTaxAsAuthenticatedUserValidPreTax () throws Exception {
        final Double preTax = 100.0;
        final Double expectedTax = 2.0; // 2% of 100.0

        final MvcResult result = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( content().string( expectedTax.toString() ) ).andReturn();

        // Assert the response content
        final String responseContent = result.getResponse().getContentAsString();
        final Double actualTax = objectMapper.readValue( responseContent, Double.class );
        assertEquals( expectedTax, actualTax, "Calculated tax should be 2.0%" );
    }

    /**
     * Tests the GET /api/tax/calc endpoint as an authenticated STAFF user with
     * a valid pre-tax amount. Expects correct tax calculation.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staffUser", roles = "STAFF" )
    void testCalcTaxAsStaffUserValidPreTax () throws Exception {
        final Double preTax = 200.0;
        final Double expectedTax = 4.0; // 2% of 200.0

        final MvcResult result = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( content().string( expectedTax.toString() ) ).andReturn();

        // Assert the response content
        final String responseContent = result.getResponse().getContentAsString();
        final Double actualTax = objectMapper.readValue( responseContent, Double.class );
        assertEquals( expectedTax, actualTax, "Calculated tax should be 4.0%" );
    }

    /**
     * Tests the GET /api/tax/calc endpoint as an unauthenticated user. Expects
     * 401 Unauthorized.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    void testCalcTaxUnauthenticated () throws Exception {
        final Double preTax = 150.0;

        final MvcResult result = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isUnauthorized() ).andReturn();

        // Assert that no content is returned for unauthorized access
        final String responseContent = result.getResponse().getContentAsString();
        assertEquals( 0, responseContent.length(), "Response content should be empty for unauthorized access." );
    }

    /**
     * Tests the GET /api/tax/calc endpoint with a negative pre-tax amount as
     * ADMIN user. Expects correct tax calculation (which will be negative).
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testCalcTaxAsAuthenticatedUserNegativePreTax () throws Exception {
        final Double preTax = -50.0;
        final Double expectedTax = -1.0; // 2% of -50.0

        final MvcResult result = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( content().string( expectedTax.toString() ) ).andReturn();

        // Assert the response content
        final String responseContent = result.getResponse().getContentAsString();
        final Double actualTax = objectMapper.readValue( responseContent, Double.class );
        assertEquals( expectedTax, actualTax, "Calculated tax should be -1.0%" );
    }

    /**
     * Tests the GET /api/tax/calc endpoint with a zero pre-tax amount as ADMIN
     * user. Expects tax amount to be zero.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testCalcTaxAsAuthenticatedUserZeroPreTax () throws Exception {
        final Double preTax = 0.0;
        final Double expectedTax = 0.0; // 2% of 0.0

        final MvcResult result = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( content().string( expectedTax.toString() ) ).andReturn();

        // Assert the response content
        final String responseContent = result.getResponse().getContentAsString();
        final Double actualTax = objectMapper.readValue( responseContent, Double.class );
        assertEquals( expectedTax, actualTax, "Calculated tax should be 0.0%" );
    }

    /**
     * Tests the GET /api/tax/calc endpoint after updating the tax rate as ADMIN
     * user. Expects tax calculation to reflect the updated rate.
     *
     * @throws Exception
     *             if an error occurs during the test
     */
    @Test
    @Transactional
    @WithMockUser ( username = "adminUser", roles = "ADMIN" )
    void testCalcTaxAfterTaxRateUpdateAsAdmin () throws Exception {
        // Update tax rate to 5% (0.05)
        final Double newRate = 0.05; // 5%
        final TaxDto updatedTaxDto = new TaxDto( taxId, newRate );

        final MvcResult putResult = mockMvc
                .perform( put( "/api/tax" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( newRate ) ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( jsonPath( "$.id" ).value( updatedTaxDto.getId() ) )
                .andExpect( jsonPath( "$.rate" ).value( updatedTaxDto.getRate() ) ).andReturn();

        // Deserialize PUT response to TaxDto
        final String putResponseContent = putResult.getResponse().getContentAsString();
        final TaxDto responseTaxDto = objectMapper.readValue( putResponseContent, TaxDto.class );

        // Assert the deserialized object
        assertNotNull( responseTaxDto, "Response TaxDto should not be null." );
        assertEquals( updatedTaxDto.getId(), responseTaxDto.getId(), "TaxDto ID should match." );
        assertEquals( updatedTaxDto.getRate(), responseTaxDto.getRate(), "TaxDto rate should be updated to 0.05%." );

        // Verify that the tax rate has been updated in the repository
        final Tax currentTax = taxRepository.findById( taxId ).orElse( null );
        assertNotNull( currentTax, "Tax entity should exist." );
        assertEquals( newRate, currentTax.getRate(), "Tax rate should be updated to 0.05%" );

        // Perform calcTax with new rate
        final Double preTax = 100.0;
        final Double expectedTax = 5.0; // 5% of 100.0

        final MvcResult calcResult = mockMvc
                .perform( get( "/api/tax/calc/" + preTax ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andExpect( content().contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( content().string( expectedTax.toString() ) ).andReturn();

        // Assert the calcTax response
        final String calcResponseContent = calcResult.getResponse().getContentAsString();
        final Double actualTax = objectMapper.readValue( calcResponseContent, Double.class );
        assertEquals( expectedTax, actualTax, "Calculated tax should be 5.0%" );
    }
}
