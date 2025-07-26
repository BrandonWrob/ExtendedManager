package wolfcafe.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception for WolfCafe API calls.
 */
@Getter
@AllArgsConstructor
public class WolfCafeAPIException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    /** the HttpStatus associated with the exception */
    private final HttpStatus  status;
    /** the message associated with the exception */
    private final String      message;
}
