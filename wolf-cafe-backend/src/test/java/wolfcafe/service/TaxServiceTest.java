package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.TaxDto;
import edu.ncsu.csc326.wolfcafe.entity.Tax;
import edu.ncsu.csc326.wolfcafe.mapper.TaxMapper;
import edu.ncsu.csc326.wolfcafe.repository.TaxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaxServiceTest {

	/** Reference to tax service and the impl */
    @Autowired
    private TaxService taxService;
    
    /** Reference to tax repository */
    @Autowired
    private TaxRepository taxRepository;
    
    /** Reference to mapper class */
    private final TaxMapper taxMapper = new TaxMapper();

    /** Reference to the first TaxDto */
    private TaxDto initialTaxDto;

    @BeforeEach
    void setUp() {
        // Clear repository and initialize with default tax rate of 2%
        taxRepository.deleteAll();
        Tax initialTax = new Tax();
        initialTax.setRate(0.02); // 2%
        Tax savedTax = taxRepository.save(initialTax);
        initialTaxDto = taxMapper.mapToTaxDto(savedTax);
    }

    /**
     * Tests retrieving the existing tax rate.
     */
    @Test
    void testGetTaxDefaultRate() {
        TaxDto taxDto = taxService.getTax();
        assertNotNull(taxDto, "TaxDto should not be null");
        assertEquals(initialTaxDto.getId(), taxDto.getId(), "Tax ID should match");
        assertEquals(initialTaxDto.getRate(), taxDto.getRate(), "Tax rate should be 2%");
    }

    /**
     * Tests setting a valid new tax rate.
     */
    @Test
    void testSetTaxSuccess() {
        Double newRate = 0.04; // 4%
        TaxDto updatedTaxDto = taxService.setTax(newRate);

        assertNotNull(updatedTaxDto, "Updated TaxDto should not be null");
        assertEquals(initialTaxDto.getId(), updatedTaxDto.getId(), "Tax ID should remain unchanged");
        assertEquals(newRate, updatedTaxDto.getRate(), "Tax rate should be updated to 4%");

        // Verify repository reflects the updated rate
        Tax updatedTax = taxRepository.findById(updatedTaxDto.getId()).orElse(null);
        assertNotNull(updatedTax, "Updated Tax should exist in repository");
        assertEquals(newRate, updatedTax.getRate(), "Repository should have updated tax rate");
    }

    /**
     * Tests setting a tax rate with a negative value.
     * Expects IllegalArgumentException.
     */
    @Test
    void testSetTaxInvalidRateNegative() {
        Double invalidRate = -0.01; // -1%

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taxService.setTax(invalidRate);
        }, "Setting a negative tax rate should throw IllegalArgumentException");

        assertEquals("Tax rate must be a positive value.", exception.getMessage(), "Exception message should match");
    }

    /**
     * Tests setting a tax rate with a null value.
     * Expects IllegalArgumentException.
     */
    @Test
    void testSetTaxInvalidRateNull() {
        Double invalidRate = null;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taxService.setTax(invalidRate);
        }, "Setting a null tax rate should throw IllegalArgumentException");

        assertEquals("Tax rate must be a positive value.", exception.getMessage(), "Exception message should match");
    }

    /**
     * Tests setting a tax rate when no existing tax record is present.
     * Expects creation of a new tax record.
     */
    @Test
    void testSetTaxCreateNewTax() {
        // Remove existing tax to simulate no existing tax record
        taxRepository.deleteAll();

        Double newRate = 0.05; // 5%
        TaxDto updatedTaxDto = taxService.setTax(newRate);

        assertNotNull(updatedTaxDto, "Updated TaxDto should not be null");
        assertNotNull(updatedTaxDto.getId(), "New Tax ID should be generated");
        assertEquals(newRate, updatedTaxDto.getRate(), "Tax rate should be updated to 5%");

        // Verify repository has exactly one tax record with the new rate
        assertEquals(1, taxRepository.count(), "Repository should have exactly one Tax record");
        Tax savedTax = taxRepository.findById(updatedTaxDto.getId()).orElse(null);
        assertNotNull(savedTax, "New Tax should exist in repository");
        assertEquals(newRate, savedTax.getRate(), "Repository should have updated tax rate");
    }

    /**
     * Tests retrieving tax rate when no existing tax record is present.
     * Expects default rate to be used.
     */
    @Test
    void testGetTaxNoExistingTax() {
        // Remove existing tax to simulate no existing tax record
        taxRepository.deleteAll();

        TaxDto taxDto = taxService.getTax();
        assertNotNull(taxDto, "TaxDto should not be null even if no tax exists");
        assertNotNull(taxDto.getId(), "New Tax ID should be generated");
        assertEquals(0.02, taxDto.getRate(), "Default tax rate should be 2%");

        // Verify repository has exactly one tax record with the default rate
        assertEquals(1, taxRepository.count(), "Repository should have exactly one Tax record");
        Tax savedTax = taxRepository.findFirstByOrderByIdAsc().orElse(null);
        assertNotNull(savedTax, "New Tax should exist in repository");
        assertEquals(0.02, savedTax.getRate(), "Repository should have default tax rate of 2%");
    }

    /**
     * Tests calculating tax with a valid pre-tax amount.
     */
    @Test
    void testCalcTaxValidPreTax() {
        Double preTax = 100.0;
        Double expectedTax = 2.0; // 2% of 100.0

        Double calculatedTax = taxService.calcTax(preTax);
        assertNotNull(calculatedTax, "Calculated tax should not be null");
        assertEquals(expectedTax, calculatedTax, "Calculated tax should be 2.0");
    }

    /**
     * Tests calculating tax with a zero pre-tax amount.
     */
    @Test
    void testCalcTaxZeroPreTax() {
        Double preTax = 0.0;
        Double expectedTax = 0.0; // 2% of 0.0

        Double calculatedTax = taxService.calcTax(preTax);
        assertNotNull(calculatedTax, "Calculated tax should not be null");
        assertEquals(expectedTax, calculatedTax, "Calculated tax should be 0.0");
    }

    /**
     * Tests calculating tax with a negative pre-tax amount.
     * Expects a negative tax amount.
     */
    @Test
    void testCalcTaxNegativePreTax() {
        Double preTax = -50.0;
        Double expectedTax = -1.0; // 2% of -50.0

        Double calculatedTax = taxService.calcTax(preTax);
        assertNotNull(calculatedTax, "Calculated tax should not be null");
        assertEquals(expectedTax, calculatedTax, "Calculated tax should be -1.0");
    }

    /**
     * Tests calculating tax after updating the tax rate.
     */
    @Test
    void testCalcTaxAfterTaxRateUpdate() {
        // Update tax rate to 5%
        Double newRate = 0.05; // 5%
        taxService.setTax(newRate);

        // Perform tax calculation with the new rate
        Double preTax = 200.0;
        Double expectedTax = 10.0; // 5% of 200.0

        Double calculatedTax = taxService.calcTax(preTax);
        assertNotNull(calculatedTax, "Calculated tax should not be null");
        assertEquals(expectedTax, calculatedTax, "Calculated tax should be 10.0");
    }

    /**
     * Tests calculating tax with a null pre-tax amount.
     * Expects NullPointerException.
     * Note: Depending on service implementation, handling of null may vary.
     */
    @Test
    void testCalcTaxNullPreTax() {
        Double preTax = null;

        assertThrows(NullPointerException.class, () -> {
            taxService.calcTax(preTax);
        }, "Calculating tax with null pre-tax should throw NullPointerException");
    }
}
