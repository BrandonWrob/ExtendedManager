package wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

import wolfcafe.entity.Ingredient;
import wolfcafe.entity.MultiRecipe;
import wolfcafe.entity.Order;

/**
 * tests OrderRepository
 */
@DataJpaTest
@ActiveProfiles("localtest")
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class OrderRepositoryTest {

    /** reference to the repository being tested (OrderRepository) */
    @Autowired
    private OrderRepository orderRepository;

    /** an order to be tested */
    private Order           order1;

    /** an order to be tested */
    private Order           order2;

    private void checkEquals ( final MultiRecipe r1, final MultiRecipe r2 ) {
        assertEquals( r1.getPrice(), r2.getPrice() );
        assertEquals( r1.getName(), r2.getName() );

        assertEquals( r1.getAmount(), r2.getAmount() );

        final List<Ingredient> l1 = r1.getIngredients();
        final List<Ingredient> l2 = r2.getIngredients();
        assertEquals( l1.size(), l2.size() );
        for ( int i = 0; i < l1.size(); i++ ) {
            final Ingredient i1 = l1.get( i );
            final Ingredient i2 = l2.get( i );
            assertEquals( i1.getName(), i2.getName() );
            assertEquals( i1.getAmount(), i2.getAmount() );
        }
    }

    private void checkEquals ( final Order o1, final Order o2 ) {
        assertEquals( o1.getFulfilled(), o2.getFulfilled() );
        for ( int i = 0; i < o1.getRecipes().size(); i++ ) {
            checkEquals( o1.getRecipes().get( i ), o2.getRecipes().get( i ) );
        }
    }

    @BeforeEach
    void setUp () throws Exception {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "coffee", 3 ) );
        ingredients.add( new Ingredient( "sugar", 1 ) );

        List<MultiRecipe> recipes = new ArrayList<MultiRecipe>();

        recipes.add( new MultiRecipe( 0L, "coffee", 50, ingredients, 2 ) );

        ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "coffee", 2 ) );
        ingredients.add( new Ingredient( "WhippedCream", 1 ) );

        recipes.add( new MultiRecipe( 0L, "Latte", 50, ingredients, 1 ) );

        order1 = new Order( 0L, false, recipes );

        ingredients = new ArrayList<Ingredient>();
        ingredients.add( new Ingredient( "water", 10 ) );
        recipes = new ArrayList<MultiRecipe>();
        recipes.add( new MultiRecipe( 0L, "water", 1, ingredients, 4 ) );

        order2 = new Order( 0L, false, recipes );
    }

    @Transactional
    @Test
    void testSave () {
        final Order savedOrder1 = orderRepository.save( order1 );

        final Order savedOrder2 = orderRepository.save( order2 );

        assertTrue( savedOrder1.getId() > 0L );
        assertTrue( savedOrder2.getId() > 0L );

        checkEquals( order1, savedOrder1 );
        checkEquals( order2, savedOrder2 );

        final Optional<Order> actual1 = orderRepository.findById( savedOrder1.getId() );
        final Optional<Order> actual2 = orderRepository.findById( savedOrder2.getId() );

        // checking we can get it back
        assertTrue( actual1.isPresent() );
        assertTrue( actual2.isPresent() );

        // checking the values equal
        checkEquals( actual1.get(), savedOrder1 );
        checkEquals( actual2.get(), savedOrder2 );

        // check findAll works
        final List<Order> orders = orderRepository.findAll();

        assertEquals( 2, orders.size() );

        checkEquals( orders.get( 0 ), savedOrder1 );
        checkEquals( orders.get( 1 ), savedOrder2 );

    }

}
