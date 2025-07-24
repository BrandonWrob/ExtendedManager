package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** used to store the tax rate in the repository */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "Tax" )
public class Tax {
    /** the id of the tax. should be singleton */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long   id;

    /** the tax rate. 2% tax: 0.02. */
    private Double rate;
}
