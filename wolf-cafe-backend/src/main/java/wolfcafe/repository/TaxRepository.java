package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ncsu.csc326.wolfcafe.entity.Tax;

/**
 * Repository interface for Tax.
 */
@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    /**
     * Retrieves the first Tax entity ordered by ID in ascending order.
     *
     * @return Optional containing the first Tax entity if present.
     */
    Optional<Tax> findFirstByOrderByIdAsc();
}
