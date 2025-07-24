import axios from "axios"
import { API_BASE_URL } from '../services/APIConfig';
/** Base URL for the Recipe API - Correspond to methods in Backend's Recipe Controller. */
const REST_API_BASE_URL = `${API_BASE_URL}/recipes`

/** GET Recipes - lists all recipes */
export const listRecipes = () => axios.get(REST_API_BASE_URL)

/** POST Recipe - creates a new recipe */
export const createRecipe = (recipe) => axios.post(REST_API_BASE_URL, recipe)

/** PUT Recipe - updates an existing recipe */
export const updateRecipe = (recipe) =>
    axios.put(REST_API_BASE_URL + "/" + recipe.id, recipe);

/** GET Recipe - gets a single recipe by id */
export const getRecipe = (id) => axios.get(REST_API_BASE_URL + "/" + id)

/** DELETE Recipe - deletes the recipe with the given id */
export const deleteRecipe = (id) => axios.delete(REST_API_BASE_URL + "/" + id)