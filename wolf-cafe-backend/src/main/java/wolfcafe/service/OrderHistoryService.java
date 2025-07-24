package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderHistoryDto;
import edu.ncsu.csc326.wolfcafe.entity.OrderHistory;

/**
 * Interface for the Order History Service
 */
public interface OrderHistoryService {

    /**
     * Method to make an Order History Dto
     *
     * @param usernameOrEmail
     *            represents the username or email of user the orderHistory
     *            belongs to Depends if they login with email or username
     * @param orderDto
     *            takes a input of an orderDto it wants to add to history
     * @return the OrderHistoryDto for the input order dto or null if invalid
     */
    OrderHistoryDto makeOrderHistory ( final String usernameOrEmail, final OrderDto orderDto );

    /**
     * Method to update a order status so that it is recognizable as picked up
     *
     * @param id
     *            represents the id of the order
     * @return false if not found, true if found and updated
     */
    boolean updateOrderHistoryStatus ( Long id );

    /**
     * Method that gets the history of all orders. Since we only considered
     * picked-uped orders as apart of history this only returns orders that meet
     * that requirement
     *
     * @return the history of all picked up orders
     */
    List<OrderHistory> getOrderHistory ();

    /**
     * Method that gets the history of all orders for a specified user. Since we
     * only considered picked-uped orders as apart of history this only returns
     * orders that meet that requirement
     *
     * @param username
     *            the username of user we want the orders of
     * @return the history of all picked up orders
     */
    List<OrderHistory> getUserHistory ( String username );

    /**
     * Method that gets the history of an order by an id
     *
     * @param id
     *            the id of the order it is getting
     *
     * @return the history of all picked up orders
     */
    OrderHistory getHistoryById ( Long id );

}
