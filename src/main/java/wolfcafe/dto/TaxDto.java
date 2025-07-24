package edu.ncsu.csc326.wolfcafe.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** represents tax as a data type for api calls */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaxDto {
	/** tax id */
	@Id
    private Long id;
    
	/** The rate of tax to store */
    private Double rate;
}
