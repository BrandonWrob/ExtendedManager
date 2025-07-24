package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information to login a user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    /** username or email of the user */
    private String usernameOrEmail;
    /** password of the user */
    private String password;

}
