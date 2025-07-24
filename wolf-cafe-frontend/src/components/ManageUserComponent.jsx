import React, { useState, useEffect } from 'react'
import { editUserAPICall, deleteUserAPICall, getAllUsersAPICall, isAdminUser, getLoggedInUser } from '../services/AuthService'

const ManageUserComponent = () => {
  const [users, setUsers] = useState([])
  const [selectedUser, setSelectedUser] = useState(null)
  const [name, setName] = useState('')
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState('')
  const [validationMessage, setValidationMessage] = useState('')
  const [validationColor, setValidationColor] = useState('')
  
  const currentUser = getLoggedInUser()
  
  // list of pre-set roles
  const roles = [
    'ROLE_CUSTOMER', 
    'ROLE_ADMIN', 
    'ROLE_STAFF', 
    'ROLE_MANAGER'
  ]

  // fetch users initially
  useEffect(() => {
    fetchUsers();
  }, []);
  
  // fetch and store all users
  async function fetchUsers() {
    try {
      // fetch and set all users
      const response = await getAllUsersAPICall();
      setUsers(response.data);
    } catch (error) {
      console.error(error);
    }
  }

  // select user from dropdown menu, update all fields with their info
  const handleUserSelect = (e) => {

	// convert to int (value from "select" will always be a string)
	const selectedUserId = parseInt(e.target.value, 10)

	// find selected user from list of users via id and store them
    const user = users.find((user) => user.id === selectedUserId)
    setSelectedUser(user)
    
	// if the user instance isn't invalid, update form
    if (user) {
      setName(user.name)
      setUsername(user.username)
      setEmail(user.email)
	  setPassword('')
      setRole('')
    }
  }

    // handle upon edit form submission
    async function handleEditForm(e) {
		e.preventDefault()
		
		// make sure the form is in a valid state for editing
		if (validateForm(false)) {
			// make sure only an admin can update
			if (isAdminUser()) {
				
				// make register object from updated user info
				const updatedUser = {name, username, email, password, roles: role ? [role] : []}
								
				// make api call to edit, send user id and the updated info
				await editUserAPICall(selectedUser.id, updatedUser).then((response) => {
					
					console.log(response.data)
					setValidationMessage(response.data)
					setValidationColor('text-success')
				}).catch(error => {
					
				    console.error(error)
					setValidationMessage(response.data)
					setValidationColor('text-danger')
				})		
			}
		}
		// reset form and make sure list of users is updated
		resetForm()
		await fetchUsers()
    }
  
    // handle upon delete form submission
    async function handleDeleteForm(e) {
		e.preventDefault()

		// make sure the form is in a valid state for deleting
		if (validateForm(true)) {
			// make sure only an admin can update
			if (isAdminUser()) {
								
				// make api call to delete, send user id
				await deleteUserAPICall(selectedUser.id).then((response) => {
					
					console.log(response.data)
					setValidationMessage(response.data)
					setValidationColor('text-success')
				
				}).catch(error => {
				    console.error(error)
					setValidationMessage(response.data)
					setValidationColor('text-danger')
				})
			}
		}
		// reset form and make sure list of users is updated
		resetForm()
		await fetchUsers()
    }
	
	// validate the form state
	function validateForm(deleting) {
	    let valid = true
		
		// cannot edit or delete without a user selected
		if (selectedUser == null || selectedUser == '') {
			setValidationMessage('Please select a user.')
			setValidationColor('text-danger')
			valid = false
			return
		}
		
		// cannot edit or delete current user
		if (selectedUser.username === currentUser || selectedUser.email === currentUser) {
			setValidationMessage('Action cannot be performed on a user that is logged in.')
			setValidationColor('text-danger')
			valid = false
			return
		}
		
		// only check when editing a user
		if (!deleting) {
			// make sure all fields are filled
			if (name == null || name == '' ||
				username == null || username == '' ||
				email == null || email == '' ||
				password == null || password == '' ||
				role == null || role == ''
			) {
				setValidationMessage('Please fill in all fields.')
				setValidationColor('text-danger')
				valid = false
			}
			
			// ensure no duplicate usernames or emails
			users.forEach((listUser) => {
				// if the new username matches a username in the list of users, but isn't the current user
				if (listUser.username === username && selectedUser.username !== username) {
					setValidationMessage('This username already exists.')
					setValidationColor('text-danger')
					valid = false
				}
				// if the new email matches an email in the list of users, but isn't the current user
				if (listUser.email === email && selectedUser.email !== email) {
					setValidationMessage('This email already exists.')
					setValidationColor('text-danger')
					valid = false
				}
			})
		}

	    return valid
	}
	
	// reset form after successful submission
	function resetForm() {
		setName('')
		setUsername('')
		setEmail('')
		setPassword('')
		setRole('')
		setSelectedUser(null)
	}

  return (
    <div className='container'>
      <br /><br />
      <div className='row'>
        <div className='col-md-6 offset-md-3'>
          <div className='card manage-user-form'>
            <div className='card-header'>
              <h2 className='text-center'>Manage User Accounts</h2>
            </div>
            <div className='card-body'>
              <form>
                {/* User Dropdown */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Select User</label>
                  <div className='col-md-9'>
                    <select
                      className='form-control select'
                      onChange={handleUserSelect}
					  // if selected user is value, set value to their id, otherwise clear the field
                      value={selectedUser ? selectedUser.id : ''}
                    >
                      <option value=''>Select a user</option>
                      {users.map((user) => (
                        <option key={user.id} value={user.id}>
                          {user.name}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

                {/* Name Field */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Name</label>
                  <div className='col-md-9'>
                    <input
                      type='text'
                      name='userName'
                      className='form-control'
					  placeholder='Enter name'
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                    />
                  </div>
                </div>

                {/* Username Field */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Username</label>
                  <div className='col-md-9'>
                    <input
                      type='text'
                      name='userUsername'
                      className='form-control'
					  placeholder='Enter username'
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                    />
                  </div>
                </div>

                {/* Email Field */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Email</label>
                  <div className='col-md-9'>
                    <input
                      type='email'
                      name='userEmail'
                      className='form-control'
					  placeholder='Enter email'
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                  </div>
                </div>

                {/* Password Field */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Password</label>
                  <div className='col-md-9'>
                    <input
                      type='password'
                      name='password'
                      className='form-control'
					  placeholder='Enter password'
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                    />
                  </div>
                </div>

                {/* Role Dropdown */}
                <div className='row mb-3'>
                  <label className='col-md-3 control-label'>Role</label>
                  <div className='col-md-9'>
                    <select
                      className='form-control select'
                      value={role}  // Set role value from state
                      onChange={(e) => setRole(e.target.value)}
                    >
                      <option value=''>Select Role</option>
                      {roles.map((roleOption, index) => (
                        <option key={index} value={roleOption}>
                          {roleOption.replace('ROLE_', '')}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>

				<div className="form-group mb-3 d-flex justify-content-center">
				  {/* Edit Button */}
				  <div className="me-2">
				    <button className="btn btn-info" onClick={handleEditForm}>
				      Edit User
				    </button>
				  </div>

				  {/* Delete Button */}
				  <div>
				    <button className="btn btn-danger" onClick={handleDeleteForm}>
				      Delete User
				    </button>
				  </div>
				</div>
				
				<div className="d-flex justify-content-between align-items-center">
				  <div className="p-3">
				    <a href="/recipes" className="text-decoration-none">Home</a>
				  </div>
				</div>
				
				{validationMessage && (
				    <div className={`form-text ${validationColor}`}>
				        {validationMessage}
				    </div>
				)}
				
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default ManageUserComponent
