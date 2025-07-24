package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Inventory;

/**
 * InventoryRepository for working with the DB through the JpaRepository.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
