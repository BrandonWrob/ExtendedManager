package wolfcafe.dto;

import java.util.List;

import wolfcafe.entity.MultiRecipe;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** represents an order as a data type for api calls */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    /** Order id */
    @Id
    private Long              id;

    /** Recipe name */
    private Boolean           fulfilled;

    /** List of recipes with amounts */
    private List<MultiRecipe> recipes;
}
