package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.OrderHistory;

/**
 * OrderRepository for working with DB through JpaRepository
 */
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

}
