package edu.ncsu.csc326.wolfcafe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Repository interface for Roles.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * finds a role with the given name
     *
     * @param name
     *            the name of the role
     * @return Role with the given name
     */
    Role findByName ( String name );
}
