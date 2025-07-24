package edu.ncsu.csc326.wolfcafe.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides details on errors.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails {

    /** timestamp of when error happened */
    private LocalDateTime timeStamp;
    /** message of what the error was */
    private String        message;
    /** more details about the error */
    private String        details;
}
