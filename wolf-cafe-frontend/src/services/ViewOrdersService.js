import axios from "axios";
import { getToken } from './AuthService';
import { API_BASE_URL } from '../services/APIConfig';

/** Base URL for the Order API - Corresponds to methods in Backend's Order Controller. */
const REST_API_BASE_URL = `${API_BASE_URL}/orders`;

/** Configure Axios to include the Authorization token in each request. */
axios.interceptors.request.use(
  (config) => {
    config.headers['Authorization'] = getToken();
    return config;
  },
  (error) => {
    // Handle request error
    return Promise.reject(error);
  }
);

/** GET Orders - Lists all orders. */
export const listOrders = () => axios.get(REST_API_BASE_URL);

/** PUT Order - Updates an existing order to be fulfilled. */
export const fulfillOrder = (id) => {
  return axios.put(`${REST_API_BASE_URL}/${id}`);
};

/** GET Order - Retrieves a single order by ID. */
export const getOrderById = (id) => axios.get(`${REST_API_BASE_URL}/${id}`);

/** DELETE order - Removes an order when it is picked up */
export const pickupOrder = (id) => axios.delete(REST_API_BASE_URL + "/" + id)

/** GET Orders by Customer - Shows orders for the authenticated user. */
export const viewOrdersStatus = () => {
  return axios.get(`${REST_API_BASE_URL}/user`);
};