import axios from 'axios'
import { getToken } from './AuthService';
import { API_BASE_URL } from '../services/APIConfig';

const REST_API_BASE_URL = `${API_BASE_URL}/orders/history`;

/** Configure Axios to include the Authorization token in each request. */
axios.interceptors.request.use(
  (config) => {
	config.headers['Authorization'] = 'Bearer ' + getToken();
    return config
  },
  (error) => {
    // Handle request error
    return Promise.reject(error)
  }
)

export const createOrderHistory = (orderObj) => axios.post(REST_API_BASE_URL, orderObj)

export const updateHistoryStatus = (id) => axios.put(REST_API_BASE_URL + "/status/" + id)

export const getOrderHistory = () => axios.get(REST_API_BASE_URL)

export const getUserHistory = (usernameOrEmail) => axios.get(REST_API_BASE_URL + "/" + usernameOrEmail)

export const getHistoryById = (id) => axios.get(REST_API_BASE_URL + "/user/" + id)