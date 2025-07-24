import axios from "axios"
import { API_BASE_URL } from '../services/APIConfig';
/** Base URL for the MakeRecipe API - Correspond to methods in Backend's MakeRecipeController. */
const REST_API_BASE_URL = `${API_BASE_URL}/makerecipe`

/** POST MakeRecipe - makes the given recipe with the given payment. Returns the change. */
export const makeRecipe = (recipeName, amtPaid) => axios.post(REST_API_BASE_URL + "/" + recipeName, amtPaid, {
    headers: { 
        'Content-Type' : 'application/json' 
    }
})