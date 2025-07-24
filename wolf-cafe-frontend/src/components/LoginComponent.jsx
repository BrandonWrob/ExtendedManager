import React, { useEffect, useState } from 'react'
import { isAdminUser, isManagerUser, isStaffUser, loginAPICall, logout, saveLoggedInUser, storeToken } from '../services/AuthService'
import { useNavigate } from 'react-router-dom'

const LoginComponent = () => {

    const [usernameOrEmail, setUsernameOrEmail] = useState('')
    const [password, setPassword] = useState('')
	const [validationMessage, setValidationMessage] = useState('')
	const [validationColor, setValidationColor] = useState('')

    const navigator = useNavigate()
	
	useEffect(() => {
		logout()
		// removes logged in users info when they logout
		localStorage.removeItem('userEmail');
		localStorage.removeItem('userName');
		localStorage.removeItem('userId');
		localStorage.removeItem('userUsername');
		localStorage.removeItem('role');
		localStorage.removeItem('token');
	}, []);

    async function handleLoginForm(e) {
        e.preventDefault()

        const loginObj = {usernameOrEmail, password}

        console.log(loginObj)
		
		if (validateForm()) {
			await loginAPICall(usernameOrEmail, password).then((response) => {
			    console.log(response.data)

			    // const token = 'Basic ' + window.btoa(usernameOrEmail + ':' + password);
			    const token = 'Bearer ' + response.data.accessToken
			    const role = response.data.role

			    storeToken(token)
			    saveLoggedInUser(usernameOrEmail, role)
				
				setValidationMessage('Login successful.')
				setValidationColor('text-success')

				/*
				if (isAdminUser() || isManagerUser()) {
					navigator('/recipes')
				} else if (isStaffUser()) {
					navigator('/make-recipe')
				} else {
					navigator('/make-recipe')
				}
				*/
				
				navigator('/home')

			    window.location.reload(false)
			}).catch(error => {
			    console.error('ERROR1' + error)
				setValidationMessage('Failed to log in.')
				setValidationColor('text-danger')
			})
		}
		resetForm()
    }
	
	// validate the form state
	function validateForm() {
	    let valid = true
		
		if (usernameOrEmail == null || usernameOrEmail == '' ||
			password == null || password == ''
		) {
			setValidationMessage('Please fill in all fields.')
			setValidationColor('text-danger')
			valid = false
		}

	    return valid
	}
	
	// reset form after successful submission
	function resetForm() {
		setUsernameOrEmail('')
		setPassword('')
	}


  return (
    <div className='container'>
        <br /><br />
        <div className='row'>
            <div className='col-md-6 offset-md-3 offset-md-3'>
                <div className='card login-form'>
                    <div className='card-header'>
                        <h2 className='text-center'>Login Form</h2>
                    </div>
                    <div className='card-body'>
                        <form>
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label'>Username</label>
                                <div className='col-md-9'>
                                    <input
                                        type='text'
                                        name='usernameOrEmail'
                                        className='form-control'
                                        placeholder='Enter username or email'
                                        value={usernameOrEmail}
                                        onChange={(e) => setUsernameOrEmail(e.target.value)}
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

                            <div className='form-group mb-3'>
                                <button className='btn btn-primary' onClick={(e) => handleLoginForm(e)}>Submit</button>
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

export default LoginComponent