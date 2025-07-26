package wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.TaxDto;
import wolfcafe.service.TaxService;

/**
 * TaxController provides endpoints for managing taxes.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/tax" )
public class TaxController {

    /** Reference to TaxService */
    @Autowired
    private TaxService taxService;

    /**
     * Calculates the tax based on the stored rate.
     *
     * @param preTax
     *            the total amount before tax
     * @return ResponseEntity containing the calculated tax amount
     */
    @GetMapping ( "calc/{preTax}" )
    public ResponseEntity<Double> calcTax ( @PathVariable ( "preTax" ) final Double preTax ) {
        final Double taxAmount = taxService.calcTax( preTax );
        return new ResponseEntity<>( taxAmount, HttpStatus.OK );
    }

    /**
     * GET /api/tax Retrieves the current tax rate.
     *
     * @return ResponseEntity containing the current TaxDto.
     */
    @GetMapping
    public ResponseEntity<TaxDto> getTax () {
        final TaxDto taxDto = taxService.getTax();
        return ResponseEntity.ok( taxDto );
    }

    /**
     * PUT /api/tax Sets a new tax rate. Accessible by ADMIN and STAFF users.
     *
     * @param rate
     *            the new tax rate to set
     * @return ResponseEntity containing the updated TaxDto or an error response
     */
    @PutMapping
    @PreAuthorize ( "hasAnyRole('ADMIN','STAFF','MANAGER')" )
    public ResponseEntity<?> setTax ( @RequestBody final Double rate ) {
        try {
            final TaxDto updatedTax = taxService.setTax( rate );
            return ResponseEntity.ok( updatedTax );
        }
        catch ( final IllegalArgumentException e ) {
            // Return a JSON error message directly
            return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                    .contentType( org.springframework.http.MediaType.APPLICATION_JSON )
                    .body( "{\"message\": \"" + e.getMessage() + "\"}" );
        }
    }
}
