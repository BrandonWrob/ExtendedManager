package wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import wolfcafe.TestUtils;
import wolfcafe.dto.LoginDto;
import wolfcafe.dto.RegisterDto;
import wolfcafe.entity.User;
import wolfcafe.repository.RoleRepository;
import wolfcafe.repository.UserRepository;

/**
 * Tests AuthController.
 */
@SpringBootTest
@ActiveProfiles("localtest")
@AutoConfigureMockMvc
public class AuthControllerTest {

	
    /** Mock MVC for testing controller */
	@Autowired
	private MockMvc mvc;
	
    /** Reference to role repository */
	@Autowired
	private RoleRepository roleRepository;
    /** Reference to user repository */
	@Autowired
	private UserRepository userRepository;
	
    /** Reference to password encoder */
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	
    /**
     * Tests the POST /api/auth/login endpoint with a non-existent user and role.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
	public void testLoginInvalid() throws Exception {
		LoginDto loginDto = new LoginDto("fake", "NotARealPassword");
		
		// attempt to log in non-existent user
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
    /**
     * Tests the POST /api/auth/register endpoint with creating a standard customer account.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
	public void testCreateCustomerAndLogin() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		
		LoginDto loginDto = new LoginDto("jestes", "JXB16TBD4LC");
		
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
		
		RegisterDto registerDto2 = new RegisterDto("Holly Berry", "jestes", "diffemail@ncsu.edu", "NewPWPlease", roles);

		// attempt to register user with same username
		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto2))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Username already exists."));
		
		RegisterDto registerDto3 = new RegisterDto("Holly Berry", "hberry101", "vitae.erat@yahoo.edu", "NewPWPlease", roles);

		// attempt to register user with same email
		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto3))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Email already exists."));
	}
	
    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating a staff account as an admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateStaffAndLogin() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_STAFF");
        
		RegisterDto registerDto = new RegisterDto("Maria Wilcox", "mwilcox", "mwilcox@ncsu.edu", "TmpPassword", roles);
		
		// create new staff user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		LoginDto loginDto = new LoginDto("mwilcox", "TmpPassword");
		
		// login new staff user
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.role").value("ROLE_STAFF"));
	}

    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating a manager account as an admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateManagerAndLogin() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_MANAGER");
        
		RegisterDto registerDto = new RegisterDto("Henry Mason", "hmason", "hmason@ncsu.edu", "123password456", roles);
		
		// create new manager user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		LoginDto loginDto = new LoginDto("hmason", "123password456");
		
		// login new manager user
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.role").value("ROLE_MANAGER"));
	}
	
    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating an admin account as an admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateAdminAndLogin() throws Exception {	
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_ADMIN");
        
		RegisterDto registerDto3 = new RegisterDto("Jennifer Neary", "jenneary", "jenneary@ncsu.edu", "ANewPassword", roles);
		
		// create new admin user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto3))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		LoginDto loginDto = new LoginDto("jenneary", "ANewPassword");
		
		// login new admin user
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
	}
	
    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating a customer account as an admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateCustomerAsAdminAndLogin() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Richard Michaels", "richym101", "rmichael@ncsu.edu", "VeryCoolGuy", roles);
		
		// create new customer user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		LoginDto loginDto = new LoginDto("richym101", "VeryCoolGuy");
		
		// login new customer user
		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(loginDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.role").value("ROLE_CUSTOMER"));
	}
	
    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating invalid accounts.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreateInvalidAccount() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		// register initial user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		RegisterDto registerDto2 = new RegisterDto("Holly Berry", "jestes", "diffemail@ncsu.edu", "NewPWPlease", roles);

		// attempt to register user with same username
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto2))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Username already exists."));
		
		RegisterDto registerDto3 = new RegisterDto("Holly Berry", "hberry101", "vitae.erat@yahoo.edu", "NewPWPlease", roles);

		// attempt to register user with same email
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto3))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Email already exists."));
		
		roles.clear();
		roles.add("BAD_ROLE");
		RegisterDto registerDto4 = new RegisterDto("Holly Berry", "hberry101", "diffemail@ncsu.edu", "NewPWPlease", roles);

		// attempt to register user with a non-existent role
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto4))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Role not found with name BAD_ROLE"));
	}
	
    /**
     * Tests the POST /api/auth/adminRegister endpoint with creating an account as a non-admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
	public void testCreateInvalidPermissions() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_STAFF");
        
		RegisterDto registerDto = new RegisterDto("Fake Account", "phoney", "phoney@ncsu.edu", "NotRealPassword", roles);
		
		// attempt to create a new staff user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
	}
	
    /**
     * Tests the DELETE /api/auth/user/{id} endpoint with deleting an account as an admin, 
     * and attempting with a non-existent user.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testDeleteUser() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_STAFF");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		// register initial user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		assertTrue(userRepository.existsByUsername(registerDto.getUsername()));
		User user = userRepository.findByUsername(registerDto.getUsername()).get();
		Long userId = user.getId();
		
		// delete newly registered user
		mvc.perform(delete("/api/auth/user/" + userId))
				.andExpect(status().isOk())
				.andExpect(content().string("User deleted successfully."));
		
		assertFalse(userRepository.existsByUsername(registerDto.getUsername()));
		
		Long fakeId = userId + 1L;
		
		// attempt to delete non-existent user
		mvc.perform(delete("/api/auth/user/" + fakeId))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("User not found with id " + fakeId));
	}
	
    /**
     * Tests the DELETE /api/auth/user/{id} endpoint with deleting an account as a non-admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
	public void testDeleteInvalidPermissions() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		// register initial user
		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		assertTrue(userRepository.existsByUsername(registerDto.getUsername()));
		assertTrue(userRepository.existsByEmail(registerDto.getEmail()));

		User user = userRepository.findByUsername(registerDto.getUsername()).get();
		Long userId = user.getId();
		
		// attempt to delete newly registered user
		mvc.perform(delete("/api/auth/user/" + userId))
				.andExpect(status().isUnauthorized());
		
		// verify that user wasn't deleted
		assertTrue(userRepository.existsByUsername(registerDto.getUsername()));
		assertTrue(userRepository.existsByEmail(registerDto.getEmail()));
	}
	
    /**
     * Tests the POST /api/auth/user/{id} endpoint with editing an account as an admin,
     * and attempting with a non-existent user and role.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testEditUser() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		// register initial user
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		// verify user now exists
		assertTrue(userRepository.existsByUsername(registerDto.getUsername()));
		assertTrue(userRepository.existsByEmail(registerDto.getEmail()));

		User user = userRepository.findByUsername(registerDto.getUsername()).get();
		Long userId = user.getId();
		
		// verify user's current information
		assertEquals("Jordan Estes", user.getName());
		assertEquals("jestes", user.getUsername());
		assertEquals("vitae.erat@yahoo.edu", user.getEmail());
		assertTrue(passwordEncoder.matches("JXB16TBD4LC", user.getPassword()));
		assertTrue(user.getRoles().contains(roleRepository.findByName("ROLE_CUSTOMER")));

		roles.clear();
		roles.add("ROLE_STAFF");
		RegisterDto updatedDto = new RegisterDto("Jordan Marco", "jmarco", "jmarco@yahoo.edu", "NewStaffPassword", roles);
		
		// update initial user with DTO
		mvc.perform(post("/api/auth/user/" + userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(updatedDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string("User updated successfully."));
		
		// verify old user info doesn't exist
		assertFalse(userRepository.existsByUsername(registerDto.getUsername()));
		assertFalse(userRepository.existsByEmail(registerDto.getEmail()));
		
		// verify new user info does exist
		assertTrue(userRepository.existsByUsername(updatedDto.getUsername()));
		assertTrue(userRepository.existsByEmail(updatedDto.getEmail()));
		
		User updatedUser = userRepository.findByUsername(updatedDto.getUsername()).get();
		Long updatedUserId = updatedUser.getId();
		
		// verify user's updated information
		assertEquals(userId, updatedUserId); // should stay the same
		assertEquals("Jordan Marco", updatedUser.getName());
		assertEquals("jmarco", updatedUser.getUsername());
		assertEquals("jmarco@yahoo.edu", updatedUser.getEmail());
		assertTrue(passwordEncoder.matches("NewStaffPassword", updatedUser.getPassword()));
		assertTrue(updatedUser.getRoles().contains(roleRepository.findByName("ROLE_STAFF")));
		
		Long fakeId = userId + 1L;
		
		// attempt to edit a non-existent user
		mvc.perform(post("/api/auth/user/" + fakeId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(updatedDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("User not found with id " + fakeId));
		
		roles.clear();
		roles.add("BAD_ROLE");

		RegisterDto invalidDto = new RegisterDto("Holly Berry", "hberry101", "vitae.erat@yahoo.edu", "NewPWPlease", roles);
		
		// attempt to update a user with invalid roles
		mvc.perform(post("/api/auth/user/" + userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(invalidDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Role not found with name BAD_ROLE"));
	}
	
    /**
     * Tests the POST /api/auth/user/{id} endpoint with editing an account as a non-admin.
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
	public void testEditInvalidPermissions() throws Exception {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_CUSTOMER");
        
		RegisterDto registerDto = new RegisterDto("Jordan Estes", "jestes", "vitae.erat@yahoo.edu", "JXB16TBD4LC", roles);
		
		// register initial user
		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		// verify user now exists
		assertTrue(userRepository.existsByUsername(registerDto.getUsername()));
		assertTrue(userRepository.existsByEmail(registerDto.getEmail()));

		User user = userRepository.findByUsername(registerDto.getUsername()).get();
		Long userId = user.getId();
		
		// verify user's current information
		assertEquals("Jordan Estes", user.getName());
		assertEquals("jestes", user.getUsername());
		assertEquals("vitae.erat@yahoo.edu", user.getEmail());
		assertTrue(passwordEncoder.matches("JXB16TBD4LC", user.getPassword()));
		assertTrue(user.getRoles().contains(roleRepository.findByName("ROLE_CUSTOMER")));

		roles.clear();
		roles.add("ROLE_STAFF");
		RegisterDto updatedDto = new RegisterDto("Jordan Marco", "jmarco", "jmarco@yahoo.edu", "NewStaffPassword", roles);
		
		// update initial user with DTO
		mvc.perform(post("/api/auth/user/" + userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(updatedDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized());
		
		// verify user's information did not change
		assertEquals("Jordan Estes", user.getName());
		assertEquals("jestes", user.getUsername());
		assertEquals("vitae.erat@yahoo.edu", user.getEmail());
		assertTrue(passwordEncoder.matches("JXB16TBD4LC", user.getPassword()));
		assertTrue(user.getRoles().contains(roleRepository.findByName("ROLE_CUSTOMER")));
	}
	
    /**
     * Tests the GET /api/auth/user endpoint with retreiving all accounts as an admin. 
     *
     * @throws Exception
     *             if issue when running the test.
     */
	@Test
	@Transactional
    @WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAllUsers() throws Exception {
		
        List<String> roles = new ArrayList<>();		
		roles.add("ROLE_STAFF");
		RegisterDto registerDto = new RegisterDto("Holly Berry", "hberry", "hberry@ncsu.edu", "NewPassword", roles);

		// register another user besides admin
		mvc.perform(post("/api/auth/adminRegister")
				.contentType(MediaType.APPLICATION_JSON)
				.content(TestUtils.asJsonString(registerDto))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully."));
		
		String users = mvc.perform(get("/api/auth/user"))
						.andExpect(status().isOk())
						.andReturn().getResponse().getContentAsString();
		
		User user = userRepository.findByUsername(registerDto.getUsername()).get();
		String userId = "" + user.getId();
		
		assertTrue(users.contains("1"));
		assertTrue(users.contains("Admin User"));
		assertTrue(users.contains("admin"));
		assertTrue(users.contains("admin@admin.edu"));
		assertTrue(users.contains("ROLE_ADMIN"));

		assertTrue(users.contains(userId));
		assertTrue(users.contains("Holly Berry"));
		assertTrue(users.contains("hberry"));
		assertTrue(users.contains("hberry@ncsu.edu"));
		assertTrue(users.contains("ROLE_STAFF"));
	}
}
