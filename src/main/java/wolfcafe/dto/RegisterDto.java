package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Information needed to register a new customer.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    /** name of the user registering */
    private String       name;
    /** username of the user registering */
    private String       username;
    /** email of the user registering */
    private String       email;
    /** password of the user registering */
    private String       password;
    /** represents the roles the user has */
    private List<String> roles;
}
