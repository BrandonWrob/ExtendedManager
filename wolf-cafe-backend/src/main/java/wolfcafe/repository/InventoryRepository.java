package wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wolfcafe.entity.Inventory;

/**
 * InventoryRepository for working with the DB through the JpaRepository.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
