package wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import wolfcafe.entity.Order;

/**
 * OrderRepository for working with DB through JpaRepository
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

}
