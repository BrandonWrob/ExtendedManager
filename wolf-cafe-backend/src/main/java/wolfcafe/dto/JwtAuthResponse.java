package wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response for authenticated and authorized user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {

    /** the token used to access */
    private String accessToken;
    /** the type of token */
    private String tokenType = "Bearer";
    /** the role of the user with the token */
    private String role;
}
