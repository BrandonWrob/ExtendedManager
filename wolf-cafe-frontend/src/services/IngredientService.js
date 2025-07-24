import axios from "axios"
import { API_BASE_URL } from '../services/APIConfig';
/** Base URL for the Ingredient API - Correspond to methods in Backend's IngredientController. */
const REST_API_BASE_URL = `${API_BASE_URL}/ingredients`

/** post Ingredients - creates a new ingredient into inventory */
export const addIngredient = (ingredient) => axios.post(REST_API_BASE_URL, ingredient)