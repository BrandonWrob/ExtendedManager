import axios from 'axios'

import { API_BASE_URL } from '../services/APIConfig';

// Base URL for all authentication-related API calls
const AUTH_REST_API_BASE_URL = `${API_BASE_URL}/auth`;

export const getAllUsersAPICall = () => axios.get(AUTH_REST_API_BASE_URL + '/user')

export const registerAPICall = (registerObj) => axios.post(AUTH_REST_API_BASE_URL + '/register', registerObj)

//export const adminRegisterAPICall = (registerObj) => axios.post(AUTH_REST_API_BASE_URL + '/adminRegister', registerObj)

export const adminRegisterAPICall = (registerObj) => {
  const token = localStorage.getItem('token')

  return axios.post(
    AUTH_REST_API_BASE_URL + '/adminRegister',
    registerObj,
    {
		headers: {
		  'Authorization': 'Bearer ' + token 
		}
    }
  )
}

//export const editUserAPICall = (id, registerObj) => axios.post(AUTH_REST_API_BASE_URL + '/user/' + id, registerObj)

export const editUserAPICall = (id, registerObj) => {
  const token = localStorage.getItem('token')

  return axios.post(
    AUTH_REST_API_BASE_URL + '/user/' + id,
    registerObj,
    {
		headers: {
		  'Authorization': 'Bearer ' + token 
		}
    }
  )
}

export const deleteUserAPICall = (id) => {
  const token = localStorage.getItem('token')

  return axios.delete(
    AUTH_REST_API_BASE_URL + '/user/' + id,
    {
		headers: {
		  'Authorization': 'Bearer ' + token 
		}
    }
  )
}

export const loginAPICall = (usernameOrEmail, password) => axios.post(AUTH_REST_API_BASE_URL + '/login', { usernameOrEmail, password })

export const storeToken = (token) => localStorage.setItem('token', token)

export const getToken = () => localStorage.getItem('token')

export const saveLoggedInUser = (username, role) => {
    sessionStorage.setItem('authenticatedUser', username)
    sessionStorage.setItem('role', role)
}

export const isUserLoggedIn = () => {
    const username = sessionStorage.getItem('authenticatedUser')

    if (username == null) return false
    else return true
}

export const getLoggedInUser = () => {
    const username = sessionStorage.getItem('authenticatedUser')
    return username
}

export const logout = () => {
    localStorage.clear()
    sessionStorage.clear()
}

export const isAdminUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_ADMIN'
}

export const isManagerUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_MANAGER'
}

export const isStaffUser = () => {
    let role = sessionStorage.getItem('role')
    return role != null && role == 'ROLE_STAFF'
}