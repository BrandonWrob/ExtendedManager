package wolfcafe.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wolfcafe.dto.TaxDto;
import wolfcafe.entity.Tax;
import wolfcafe.mapper.TaxMapper;
import wolfcafe.repository.TaxRepository;
import wolfcafe.service.TaxService;
import jakarta.annotation.PostConstruct;


/** Implements TaxService to handle tax-related operations */
@Service
public class TaxServiceImpl implements TaxService {

    /** Reference to TaxRepository */
    @Autowired
    private TaxRepository taxRepository;

    /** Mapper to convert between Tax and TaxDto */
    private final TaxMapper taxMapper = new TaxMapper();
    
    /** default tax rate*/
    public static final Double DEFAULT_RATE = 0.02; // Default rate
    
    /**
     * Initializes the value of the tax rate as 
     * 2% after the dependency injection is done for the service.
     */
    @PostConstruct
    public void init() {
        // Check if a Tax entry already exists
        if (!taxRepository.findFirstByOrderByIdAsc().isPresent()) {
            // Create and save a new Tax entity with the default rate
            Tax defaultTax = new Tax();
            defaultTax.setRate(DEFAULT_RATE);
            taxRepository.save(defaultTax);
        }
    }
    
    /**
     * Calculates the taxes based on the stored rate.
     *
     * @param preTax the total amount before tax
     * @return the calculated tax amount
     */
    @Override
    public Double calcTax(final Double preTax) {
        final Optional<Tax> optionalTax = taxRepository.findFirstByOrderByIdAsc();
        Double rate = DEFAULT_RATE; // Default rate
            rate = optionalTax.get().getRate();

        return ((double) Math.round(preTax * rate * 100)) / 100;
    }

    /**
     * Sets a new tax rate.
     *
     * @param rate the new tax rate to set
     * @return the updated TaxDto
     * @throws IllegalArgumentException if the provided rate is null or non-positive
     */
    @Override
    public TaxDto setTax(Double rate) {
        // Validate the tax rate
        if (rate == null || rate < 0) {
            throw new IllegalArgumentException("Tax rate must be a positive value.");
        }

        // Fetch the existing Tax entity if present
        Optional<Tax> optionalTax = taxRepository.findFirstByOrderByIdAsc();
        Tax tax;
        if (optionalTax.isPresent()) {
            tax = optionalTax.get();
            tax.setRate(rate);
        } else {
            // Create a new Tax entity without setting the ID
            tax = new Tax();
            tax.setRate(rate);
        }

        // Save the updated or new Tax entity
        Tax updatedTax = taxRepository.save(tax);

        // Convert to TaxDto and return
        TaxDto taxDto = taxMapper.mapToTaxDto(updatedTax);
        updatedTax = taxMapper.mapToTax(taxDto);
        
        return taxDto;
    }

    /**
     * Retrieves the current tax rate.
     *
     * @return the current TaxDto
     */
    @Override
    public TaxDto getTax() {
        // Attempt to retrieve the first Tax entity
        Optional<Tax> optionalTax = taxRepository.findFirstByOrderByIdAsc();

        Tax tax;

        if (optionalTax.isPresent()) {
            tax = optionalTax.get();
        } else {
            // If no Tax entity exists, create one with the default rate
            tax = new Tax();
            tax.setRate(DEFAULT_RATE); // Default rate
            tax = taxRepository.save(tax);
        }

        // Convert to TaxDto and return
        return taxMapper.mapToTaxDto(tax);
    }
}
