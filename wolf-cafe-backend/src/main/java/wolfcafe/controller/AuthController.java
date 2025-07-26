package wolfcafe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wolfcafe.dto.JwtAuthResponse;
import wolfcafe.dto.LoginDto;
import wolfcafe.dto.RegisterDto;
import wolfcafe.entity.User;
import wolfcafe.exception.ResourceNotFoundException;
import wolfcafe.exception.WolfCafeAPIException;
import wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Controller for authentication functionality.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/auth" )
@AllArgsConstructor
public class AuthController {

    /** Link to AuthService */
    private final AuthService authService;

    /**
     * Returns all users. Requires the ADMIN role.
     *
     * @return a list of all users
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @GetMapping ( "/user" )
    public ResponseEntity<List<User>> getAllUsers () {
        final List<User> items = authService.getAllUsers();
        return ResponseEntity.ok( items );
    }

    /**
     * Registers a new customer user with the system.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PostMapping ( "/register" )
    public ResponseEntity<String> register ( @RequestBody final RegisterDto registerDto ) {
        String response = null;
        try {
            response = authService.register( registerDto, false );
        }
        catch ( final WolfCafeAPIException e ) {
            return new ResponseEntity<>( e.getMessage(), e.getStatus() );
        }
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Registers a new user with the system with the given role. Requires the
     * ADMIN role.
     *
     * @param registerDto
     *            object with registration info
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/adminRegister" )
    public ResponseEntity<String> adminRegister ( @RequestBody final RegisterDto registerDto ) {
        String response = null;
        try {
            response = authService.register( registerDto, true );
        }
        catch ( final WolfCafeAPIException e ) {
            return new ResponseEntity<>( e.getMessage(), e.getStatus() );
        }
        return new ResponseEntity<>( response, HttpStatus.CREATED );
    }

    /**
     * Logs in the given user
     *
     * @param loginDto
     *            user information for login
     * @return object representing the logged in user
     */
    @PostMapping ( "/login" )
    public ResponseEntity<JwtAuthResponse> login ( @RequestBody final LoginDto loginDto ) {
        final JwtAuthResponse jwtAuthResponse = authService.login( loginDto );
        return new ResponseEntity<>( jwtAuthResponse, HttpStatus.OK );
    }

    /**
     * Deletes the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to delete
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @DeleteMapping ( "/user/{id}" )
    public ResponseEntity<String> deleteUser ( @PathVariable ( "id" ) final Long id ) {
        try {
            authService.deleteUserById( id );
        }
        catch ( final ResourceNotFoundException e ) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.BAD_REQUEST );
        }
        return ResponseEntity.ok( "User deleted successfully." );
    }

    /**
     * Edits the given user. Requires the ADMIN role.
     *
     * @param id
     *            id of user to edit
     * @param registerDto
     *            represents the user being registered
     * @return response indicating success or failure
     */
    @PreAuthorize ( "hasRole('ADMIN')" )
    @PostMapping ( "/user/{id}" )
    public ResponseEntity<String> editUser ( @PathVariable ( "id" ) final Long id,
            @RequestBody final RegisterDto registerDto ) {
        String response = null;
        try {
            response = authService.editUser( id, registerDto );
        }
        catch ( final WolfCafeAPIException e ) {
            return new ResponseEntity<>( e.getMessage(), e.getStatus() );
        }
        return new ResponseEntity<>( response, HttpStatus.OK );
    }
}
