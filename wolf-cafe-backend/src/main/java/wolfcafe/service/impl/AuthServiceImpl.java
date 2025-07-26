package wolfcafe.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import wolfcafe.dto.JwtAuthResponse;
import wolfcafe.dto.LoginDto;
import wolfcafe.dto.RegisterDto;
import wolfcafe.entity.Role;
import wolfcafe.entity.User;
import wolfcafe.exception.ResourceNotFoundException;
import wolfcafe.exception.WolfCafeAPIException;
import wolfcafe.repository.RoleRepository;
import wolfcafe.repository.UserRepository;
import wolfcafe.security.JwtTokenProvider;
import wolfcafe.service.AuthService;
import lombok.AllArgsConstructor;

/**
 * Implemented AuthService
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * reference to userRepository
     */
    private final UserRepository        userRepository;
    /**
     * reference to RoleRepository
     */
    private final RoleRepository        roleRepository;
    /**
     * reference to PasswordEncoder
     */
    private final PasswordEncoder       passwordEncoder;
    /**
     * reference to AuthenticationMangager
     */
    private final AuthenticationManager authenticationManager;
    /** reference to JwtTokenProvider */
    private final JwtTokenProvider      jwtTokenProvider;

    /**
     * Returns all users
     *
     * @return all users
     */
    @Override
    public List<User> getAllUsers () {
        return userRepository.findAll();
    }

    /**
     * Registers the given user
     *
     * @param registerDto
     *            new user information
     * @param authorized
     *            of whether a user can be registered as a non-customer
     * @return message for success or failure
     */
    @Override
    public String register ( final RegisterDto registerDto, final Boolean authorized ) {
        // Check for duplicates - username
        if ( userRepository.existsByUsername( registerDto.getUsername() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Username already exists." );
        }
        // Check for duplicates - email
        if ( userRepository.existsByEmail( registerDto.getEmail() ) ) {
            throw new WolfCafeAPIException( HttpStatus.BAD_REQUEST, "Email already exists." );
        }

        final User user = new User();
        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();

        // only authorized users can create non-customer accounts
        if ( authorized ) {
            for ( final String role : registerDto.getRoles() ) {
                final Role fetchedRole = roleRepository.findByName( role );
                if ( fetchedRole == null ) {
                    throw new WolfCafeAPIException( HttpStatus.NOT_FOUND, "Role not found with name " + role );
                }
                roles.add( fetchedRole );
            }
        }
        else {
            final Role userRole = roleRepository.findByName( "ROLE_CUSTOMER" );
            roles.add( userRole );
        }
        user.setRoles( roles );

        userRepository.save( user );

        return "User registered successfully.";
    }

    /**
     * Logins in the given user
     *
     * @param loginDto
     *            username/email and password
     * @return response with authenticated user
     */
    @Override
    public JwtAuthResponse login ( final LoginDto loginDto ) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( loginDto.getUsernameOrEmail(), loginDto.getPassword() ) );

        SecurityContextHolder.getContext().setAuthentication( authentication );

        final String token = jwtTokenProvider.generateToken( authentication );

        final Optional<User> userOptional = userRepository.findByUsernameOrEmail( loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail() );

        String role = null;
        if ( userOptional.isPresent() ) {
            final User loggedInUser = userOptional.get();
            final Optional<Role> optionalRole = loggedInUser.getRoles().stream().findFirst();

            if ( optionalRole.isPresent() ) {
                final Role userRole = optionalRole.get();
                role = userRole.getName();
            }
        }

        final JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setRole( role );
        jwtAuthResponse.setAccessToken( token );

        return jwtAuthResponse;
    }

    /**
     * Deletes the given user by id
     *
     * @param id
     *            id of user to delete
     */
    @Override
    public void deleteUserById ( final Long id ) {
        userRepository.findById( id )
                .orElseThrow( () -> new ResourceNotFoundException( "User not found with id " + id ) );
        userRepository.deleteById( id );
    }

    /**
     * Updates the given user by id with the information in the given DTO.
     *
     * @param id
     *            id of user to edit
     * @param registerDto
     *            user information to update with
     */
    @Override
    public String editUser ( final Long id, final RegisterDto registerDto ) {
        final User user = userRepository.findById( id )
                .orElseThrow( () -> new WolfCafeAPIException( HttpStatus.NOT_FOUND, "User not found with id " + id ) );

        user.setName( registerDto.getName() );
        user.setUsername( registerDto.getUsername() );
        user.setEmail( registerDto.getEmail() );
        user.setPassword( passwordEncoder.encode( registerDto.getPassword() ) );

        final Set<Role> roles = new HashSet<>();
        for ( final String role : registerDto.getRoles() ) {
            final Role fetchedRole = roleRepository.findByName( role );
            if ( fetchedRole == null ) {
                throw new WolfCafeAPIException( HttpStatus.NOT_FOUND, "Role not found with name " + role );
            }
            roles.add( fetchedRole );
        }
        user.setRoles( roles );

        userRepository.save( user );

        return "User updated successfully.";
    }

    /**
     * finds the username from a given session token
     *
     * @param token
     *            the token of the user
     * @return String the username of the user
     */
    @Override
    public String getUsername ( final String token ) {
        return jwtTokenProvider.getUsername( token );
    }
}
