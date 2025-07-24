import React, { useState } from 'react'
import { isAdminUser, adminRegisterAPICall } from '../services/AuthService'

const AdminRegisterComponent = () => {

    const [name, setName] = useState('')
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
	const [role, setRole] = useState('')
	const [validationMessage, setValidationMessage] = useState('')
	const [validationColor, setValidationColor] = useState('')
	
	// list of pre-set roles
	const roles = [
	  'ROLE_CUSTOMER', 
	  'ROLE_ADMIN', 
	  'ROLE_STAFF', 
	  'ROLE_MANAGER'
	]

	// handle upon form submission
    async function handleRegistrationForm(e) {
        e.preventDefault()
		
		// make sure the form is in a valid state for registering
		if (validateForm()) {
			// only allowed by admin user
			if(isAdminUser()) {
				
				// make register object from user info
				const register = {name, username, email, password, roles: role ? [role] : []}

				// make api call, send user info
				await adminRegisterAPICall(register).then((response) => {
					
					console.log(response.data)
					setValidationMessage(response.data)
					setValidationColor('text-success')
				}).catch(error => {
					
					if (error.response && error.response.status === 400) {
						setValidationMessage('This username or email already exists.')
					} else if (error.response && error.response.status === 404) {
						setValidationMessage('Could not assign a user role.')
					} else {
						setValidationMessage('User could not be registered.')
					}
					
				    console.error(error)
					setValidationColor('text-danger')
				})
			}
		}
		resetForm()
    }
	
	// validate the form state
	function validateForm() {
	    let valid = true
		
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

	    return valid
	}
	
	// reset form after successful submission
	function resetForm() {
		setName('')
		setUsername('')
		setEmail('')
		setPassword('')
		setRole('')
	}

  return (
    <div className='container'>
        <br /><br />
        <div className='row'>
            <div className='col-md-6 offset-md-3 offset-md-3'>
                <div className='card manage-user-form'>
                    <div className='card-header'>
                        <h2 className='text-center'>User Registration Form</h2>
                    </div>
                    <div className='card-body'>
                        <form>
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Name</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='name'
                                        className='form-control'
                                        placeholder='Enter name'
                                        value={name}
                                        onChange={(e) => setName(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='username'
                                        className='form-control'
                                        placeholder='Enter username'
                                        value={username}
                                        onChange={(e) => setUsername(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Email</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='email'
                                        className='form-control'
                                        placeholder='Enter email'
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>

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
                                    >
                                    </input>
                                </div>
                            </div>
							
							<div className='row mb-3'>
							  <label className='col-md-3 control-label'>Role</label>
							  <div className='col-md-9'>
							    <select
							      className='form-control select'
							      value={role}
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

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleRegistrationForm(e)}>Submit</button>
                            </div>
							
							<div className="d-flex justify-content-between align-items-center">
							  {/* Home Link at Bottom Left */}
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

export default AdminRegisterComponent