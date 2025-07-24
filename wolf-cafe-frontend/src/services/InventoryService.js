import axios from "axios"
import { API_BASE_URL } from '../services/APIConfig';
/** Base URL for the Inventory API - Correspond to methods in Backend's InventoryController. */
const REST_API_BASE_URL = `${API_BASE_URL}/inventory`

/** GET Inventory - returns all inventory */
//export const getInventory = () => axios.get(REST_API_BASE_URL)

export const getInventory = () => {
  const token = localStorage.getItem('token')

  return axios.get(
    REST_API_BASE_URL,
    {
		headers: {
		  'Authorization': 'Bearer ' + token 
		}
    }
  )
}

/** PUT Inventory - updates the inventory */
//export const updateInventory = (inventory) => axios.put(REST_API_BASE_URL, inventory)

export const updateInventory = (inventory) => {
  const token = localStorage.getItem('token')

  return axios.put(
    REST_API_BASE_URL,
    inventory,
    {
		headers: {
		  'Authorization': 'Bearer ' + token 
		}
    }
  )
}