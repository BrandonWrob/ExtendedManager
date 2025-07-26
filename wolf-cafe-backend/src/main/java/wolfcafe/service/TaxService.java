package wolfcafe.service;

import wolfcafe.dto.TaxDto;

/**
 * interface for TaxService deals with taxes
 */
public interface TaxService {
    /**
     * calculates the taxes with rate from repository
     *
     * @param preTax
     *            the total before tax and tip
     *
     * @return Double the amount due in tax
     */
    Double calcTax ( Double preTax );
    
    /**
     * sets the taxes with rate in the repository
     *
     * @param rate
     *            the rate of taxes
     *
     * @return taxDto the updated tax returned as a Dto
     */
    TaxDto setTax(Double rate);
    
    /**
     * gets the taxes from the repository
     *
     * @return taxDto the stored tax returned as a Dto
     */
    TaxDto getTax();
}
