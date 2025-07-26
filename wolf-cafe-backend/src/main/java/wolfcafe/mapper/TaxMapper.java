package wolfcafe.mapper;

import wolfcafe.dto.TaxDto;
import wolfcafe.entity.Tax;

/**
 * Mapper class to convert between Tax and TaxDto.
 */
public class TaxMapper {

    /**
     * Maps a Tax entity to a TaxDto.
     *
     * @param tax the Tax entity
     * @return the corresponding TaxDto
     */
    public TaxDto mapToTaxDto(Tax tax) {
        return new TaxDto(tax.getId(), tax.getRate());
    }

    /**
     * Maps a TaxDto to a Tax entity.
     *
     * @param taxDto the TaxDto
     * @return the corresponding Tax entity
     */
    public Tax mapToTax(TaxDto taxDto) {
        Tax tax = new Tax();
        tax.setRate(taxDto.getRate());
        // Do not set the ID manually to allow Hibernate to manage it
        return tax;
    }
}
