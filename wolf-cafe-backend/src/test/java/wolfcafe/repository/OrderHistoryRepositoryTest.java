package wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import wolfcafe.entity.OrderHistory;

/**
 * tests OrderHistoryRepository
 */
@DataJpaTest
@ActiveProfiles("localtest")
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class OrderHistoryRepositoryTest {

    /** reference to the repository being tested (OrderHistoryRepository) */
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    /** an orderHistory to be tested */
    private OrderHistory           orderHistory1;

    /** an orderHistory to be tested */
    private OrderHistory           orderHistory2;

    /**
     * Creates two orders for the test cases
     *
     * @throws Exception
     *             throws exception if invalid setup
     */
    @BeforeEach
    void setUp () throws Exception {
        // string that represents the recipes in the order
        final String recipesInOrder1 = "Turkey Wrap: 1, Coffee: 2";
        final String recipesInOrder2 = "Turkey Wrap: 1";
        // string to represent the ingredients
        final String ingredientsUsed1 = "Cheese: 3, Turkey: 4, Coffee Beans: 10";
        final String ingredientsUsed2 = "Cheese: 3, Turkey: 4";
        // long to represent the total
        final double total1 = 10.43;
        final double total2 = 7.22;
        // creates the orders
        orderHistory1 = new OrderHistory( 0L, false, recipesInOrder1, ingredientsUsed1, total1, "username1" );
        orderHistory2 = new OrderHistory( 1L, false, recipesInOrder2, ingredientsUsed2, total2, "username2" );
    }

    /**
     * Helper method to confirm two order histories are equal
     *
     * @param o1
     *            represents the first of order histories being compared
     * @param o2
     *            represents the second of order histories being compared
     */
    private void checkEquals ( final OrderHistory o1, final OrderHistory o2 ) {
        assertEquals( o1.getId(), o2.getId() );
        assertEquals( o1.getPickedUp(), o2.getPickedUp() );
        assertEquals( o1.getIngredientsUsed(), o2.getIngredientsUsed() );
        assertEquals( o1.getRecipesInOrder(), o2.getRecipesInOrder() );
        assertEquals( o1.getTotal(), o2.getTotal() );
        assertEquals( o1.getUsername(), o2.getUsername() );
    }

    /**
     * Test saving the orders
     */
    @Transactional
    @Test
    void testCreateAndSave () {
        // saves the orders to the repository
        final OrderHistory savedOrder1 = orderHistoryRepository.save( orderHistory1 );
        final OrderHistory savedOrder2 = orderHistoryRepository.save( orderHistory2 );

        // confirms the order history added to the repository is equal to the
        // original
        checkEquals( orderHistory1, savedOrder1 );
        checkEquals( orderHistory2, savedOrder2 );

        // test getting the order history back from the repository with it being
        // unchanged
        final Optional<OrderHistory> actual1 = orderHistoryRepository.findById( savedOrder1.getId() );
        final Optional<OrderHistory> actual2 = orderHistoryRepository.findById( savedOrder2.getId() );
        assertTrue( actual1.isPresent() );
        assertTrue( actual2.isPresent() );
        checkEquals( actual1.get(), savedOrder1 );
        checkEquals( actual2.get(), savedOrder2 );

        // test that it can use findAll (a feature we need for our service
        // layer)
        final List<OrderHistory> orders = orderHistoryRepository.findAll();
        assertEquals( 2, orders.size() );

    }

    /**
     * Test editing the order, specifically status since it is the only feature
     * that will be modified
     */
    @Transactional
    @Test
    void testEditStatus () {
        // saves the orders to the repository
        final OrderHistory savedOrder1 = orderHistoryRepository.save( orderHistory1 );
        final OrderHistory savedOrder2 = orderHistoryRepository.save( orderHistory2 );

        // confirms the order history added to the repository is equal to the
        // original
        checkEquals( orderHistory1, savedOrder1 );
        checkEquals( orderHistory2, savedOrder2 );

        // test getting the order history back from the repository with it being
        // unchanged
        final Optional<OrderHistory> actual1 = orderHistoryRepository.findById( savedOrder1.getId() );
        final Optional<OrderHistory> actual2 = orderHistoryRepository.findById( savedOrder2.getId() );
        assertTrue( actual1.isPresent() );
        assertTrue( actual2.isPresent() );
        checkEquals( actual1.get(), savedOrder1 );
        checkEquals( actual2.get(), savedOrder2 );

        // test that it can use findAll (a feature we need for our service
        // layer)
        final List<OrderHistory> orders = orderHistoryRepository.findAll();
        assertEquals( 2, orders.size() );

        // lets edit an order to set the status is picked up and confirm the
        // change
        savedOrder1.setPickedUp( true );
        orderHistoryRepository.save( savedOrder1 );

        // now lets get it back from the repository and confirm it was changed
        final Optional<OrderHistory> modified = orderHistoryRepository.findById( savedOrder1.getId() );
        checkEquals( modified.get(), savedOrder1 );
    }

}
