package wolfcafe.service;

import java.util.List;

import wolfcafe.dto.JwtAuthResponse;
import wolfcafe.dto.LoginDto;
import wolfcafe.dto.RegisterDto;
import wolfcafe.entity.User;

/**
 * Authorization service
 */
public interface AuthService {

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @param authorized
     *            of whether a user can be registered as a non-customer
     * @return message for success or failure
     */
    String register ( RegisterDto registerDto, Boolean authorized );

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    JwtAuthResponse login ( LoginDto loginDto );

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    void deleteUserById ( Long id );

    /**
     * Updates the given user by id with the information in the given DTO.
     *
     * @param id
     *            id of user to edit
     * @param registerDto
     *            user information to update with
     * 
     * @return a string representation of the modified user
     */
    String editUser ( Long id, RegisterDto registerDto );

    /**
     * Returns all users
     *
     * @return all users
     */
    List<User> getAllUsers ();

    /**
     * finds the username from a given session token
     *
     * @param token
     *            the token of the user
     * @return String the username of the user
     */
    String getUsername ( String token );
}
