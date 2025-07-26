package wolfcafe.entity;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * System user.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "users" )
public class User {
    /** the unique id of the user */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long             id;
    /** the name of the user */
    private String           name;
    /** the unique username of the user */
    @Column ( nullable = false, unique = true )
    private String           username;
    /** the unique email of the user */
    @Column ( nullable = false, unique = true )
    private String           email;
    /** the password of the user */
    @Column ( nullable = false )
    private String           password;

    /** the roles the user has */
    @ManyToMany ( fetch = FetchType.EAGER )
    @JoinTable ( name = "users_roles", joinColumns = @JoinColumn ( name = "user_id", referencedColumnName = "id" ),
            inverseJoinColumns = @JoinColumn ( name = "role_id", referencedColumnName = "id" ) )
    private Collection<Role> roles;

    /** the orders that the user has in */
    @OneToMany ( fetch = FetchType.EAGER, cascade = CascadeType.ALL )
    private List<Order>      orders;

}
