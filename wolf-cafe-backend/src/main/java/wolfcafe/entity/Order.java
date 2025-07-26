package wolfcafe.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** represents an order that a user has */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "orders" )
public class Order {
    /** Order id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long              id;

    /** whether or not the recipe was fulfilled */
    private Boolean           fulfilled;

    /** List of recipes with amounts */
    @OneToMany ( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<MultiRecipe> recipes;

}
