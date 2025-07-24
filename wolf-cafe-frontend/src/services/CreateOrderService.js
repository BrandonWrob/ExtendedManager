import axios from "axios"

import { API_BASE_URL } from '../services/APIConfig';

/** Base URL for the MakeRecipe API - Correspond to methods in Backend's MakeRecipeController. */
const REST_API_BASE_URL = `${API_BASE_URL}/orders`

/** POST MakeOrder - makes the given order with the given payment. Returns the saved order. */
export const makeOrder = (orderDto) => axios.post(REST_API_BASE_URL, orderDto, {
    headers: { 
        'Content-Type' : 'application/json' 
    }
})

/** 
 * GET Order - retrieves the details of an order by its ID.
 * Replace 'orderId' with the specific order ID or implement user-based order retrieval as needed.
 */
export const getOrder = (orderId) => axios.get(`${REST_API_BASE_URL}/${orderId}`, {
    headers: { 
        'Content-Type': 'application/json' 
    }
});
