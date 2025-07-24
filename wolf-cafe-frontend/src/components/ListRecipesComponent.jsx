import React, { useEffect, useState } from 'react';
import { listRecipes, deleteRecipe } from '../services/RecipesService';
import { isAdminUser } from '../services/AuthService'
import { useNavigate } from 'react-router-dom';
/** Lists all the recipes and provides the option to create a new recipe
 * and delete an existing recipe.
 */
const ListRecipesComponent = () => {
    const [recipes, setRecipes] = useState([]);
    const navigator = useNavigate();

    useEffect(() => {
        getAllRecipes();
    }, []);

    function getAllRecipes() {
        listRecipes()
            .then((response) => {
                setRecipes(response.data);
            })
            .catch((error) => {
                console.error(error);
            });
    }

    function addNewRecipe() {
        navigator('/add-recipe');
    }

    function editRecipe(name) {
        console.log(name);
        navigator(`/edit-recipe/${encodeURIComponent(name)}`);
    }
    function removeRecipe(id) {
        console.log(id);
        deleteRecipe(id)
            .then((response) => {
                getAllRecipes();
            })
            .catch((error) => {
                console.error(error);
            });
    }

    return (
        <div className="container recipes-container">
            <h2 className="text-center">List of Recipes</h2>
            <button className="btn btn-primary mb-2" onClick={addNewRecipe}>
                Add Recipe
            </button>
			<div className="table-responsive">
            <table className="table table-striped table-bordered">
                <thead>
                    <tr>
                        <th>Recipe Name</th>
                        <th>Recipe Price</th>
                        <th>Ingredients</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {recipes.map((recipe) => (
                        <tr key={recipe.id}>
                            <td>{recipe.name}</td>
                            <td>{recipe.price}</td>
                            <td>
                                {recipe.ingredients && recipe.ingredients.length > 0 ? (
                                    recipe.ingredients
                                        .map((ingredient) => {
                                            return `${ingredient.name}: ${ingredient.amount || "N/A"}`;
                                        })
                                        .join(', ')
                                ) : (
                                    <span>No ingredients listed</span>
                                )}
                            </td>
                            <td>
                                <button className='btn btn-info' onClick={() => editRecipe(recipe.name)}>
                                    Edit
                                </button>
                                <button
                                    className="btn btn-danger"
                                    onClick={() => removeRecipe(recipe.id)}
                                    style={{ marginLeft: '10px' }}
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
			{isAdminUser() && (
			  <button className="btn btn-primary mb-2" onClick={() => navigator('/tax')}>
			    Modify Tax
			  </button>
			)}
        </div>
		</div>
    );
};

export default ListRecipesComponent;