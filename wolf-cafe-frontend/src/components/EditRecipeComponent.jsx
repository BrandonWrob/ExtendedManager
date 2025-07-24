import { useState, useEffect } from 'react';
import { getRecipe, updateRecipe } from '../services/RecipesService';
import { getInventory } from '../services/InventoryService'; // Add this import to fetch inventory
import { useNavigate, useParams } from 'react-router-dom';

/** Form to edit an existing recipe. */
const EditRecipeComponent = () => {
    const { name } = useParams(); // Get the recipe name from the URL parameters
    const [id, setId] = useState(null);
    const [recipeName, setRecipeName] = useState("");
    const [price, setPrice] = useState("");
    const [ingredients, setIngredients] = useState([]);
    const [inventoryIngredients, setInventoryIngredients] = useState([]); // Store inventory ingredients

    const navigator = useNavigate();
    const [errors, setErrors] = useState({
        general: "",
        name: "",
        price: "",
        ingredients: "",
    });

    // Fetch the recipe and inventory data on component mount
    useEffect(() => {
        getRecipe(name)
            .then((response) => {
                const recipe = response.data;
                setId(recipe.id); // Store the id
                setRecipeName(recipe.name);
                setPrice(recipe.price.toString());
                setIngredients(
                    recipe.ingredients.map((ingredient) => ({
                        name: ingredient.name,
                        amount: ingredient.amount.toString(),
                    }))
                );
            })
            .catch((error) => {
                console.error(error);
                setErrors((prevErrors) => ({
                    ...prevErrors,
                    general: "Failed to load recipe data.",
                }));
            });

        // Fetch inventory data
        getInventory()
            .then((response) => {
                setInventoryIngredients(response.data.ingredients); // Set the inventory ingredients
            })
            .catch((error) => {
                console.error("Failed to fetch inventory:", error);
            });
    }, [name]);

    const addIngredient = () => {
        setIngredients([...ingredients, { name: "", amount: "" }]);
    };

    const removeIngredient = (index) => {
        const newIngredients = [...ingredients];
        newIngredients.splice(index, 1);
        setIngredients(newIngredients);
    };

    const handleIngredientChange = (index, field, value) => {
        const newIngredients = [...ingredients];
        newIngredients[index][field] = value;
        setIngredients(newIngredients);
    };

    function updateRecipeHandler(e) {
        e.preventDefault();

        if (validateForm()) {
            const recipe = { id, name: recipeName, price, ingredients };
            console.log('Updating recipe:', recipe);

            updateRecipe(recipe)
                .then((response) => {
                    console.log('Recipe updated:', response.data);
                    navigator("/recipes");  // Navigate to ListRecipes component
                })
                .catch((error) => {
                    console.error(error);
                    const errorsCopy = { ...errors };
                    if (error.response && error.response.status === 507) {
                        errorsCopy.general = "Recipe list is at capacity.";
                    }
                    if (error.response && error.response.status === 409) {
                        errorsCopy.general = "Duplicate recipe name.";
                    }
                    setErrors(errorsCopy);
                });
        }
    }

    function validateForm() {
        let valid = true;
        const errorsCopy = { general: "", name: "", price: "", ingredients: "" };

        // Validate recipe name
        if (recipeName.trim()) {
            errorsCopy.name = "";
        } else {
            errorsCopy.name = "Name is required.";
            valid = false;
        }

        // Validate price
        const parsedPrice = parseFloat(price);
        if (isNaN(parsedPrice) || parsedPrice < 0) {
            errorsCopy.price = "Enter a positive number for the price.";
            valid = false;
        } else {
            errorsCopy.price = "";
        }

        // Validate ingredients
        if (
            ingredients.length === 0 ||
            ingredients.some(
                (ingredient) =>
                    !ingredient.name.trim() ||
                    !ingredient.amount.trim() ||
                    isNaN(parseInt(ingredient.amount)) ||
                    parseInt(ingredient.amount) <= 0
            )
        ) {
            errorsCopy.ingredients = "Please enter valid ingredients with positive amounts.";
            valid = false;
        } else {
            errorsCopy.ingredients = "";
        }

        setErrors(errorsCopy);
        return valid;
    }

    function getGeneralErrors() {
        if (errors.general) {
            return <div className="p-3 mb-2 bg-danger text-white">{errors.general}</div>;
        }
    }

    return (
        <div className="container">
            <br /><br />
            <div className="row">
                <div className="card col-md-6 offset-md-3 add-recipe-form">
                    <h2 className="text-center">Edit Recipe</h2>

                    <div className="card-body">
                        {getGeneralErrors()}
                        <form>
                            <div className="form-group mb-2">
                                <label className="form-label">Recipe Name</label>
                                <input
									type="text"
									readOnly
									value={recipeName}
									className="form-control"
                                />
                                {errors.name && <div className="invalid-feedback">{errors.name}</div>}
                            </div>

                            <div className="form-group mb-2">
                                <label className="form-label">Recipe Price</label>
                                <input
                                    type="text"
                                    placeholder="Enter Recipe Price"
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    className={`form-control ${errors.price ? "is-invalid" : ""}`}
                                />
                                {errors.price && <div className="invalid-feedback">{errors.price}</div>}
                            </div>

                            {ingredients.map((ingredient, index) => (
                                <div className="form-group mb-2" key={index}>
                                    <label className="form-label">Ingredient</label>
                                    <select
                                        value={ingredient.name}
                                        onChange={(e) => handleIngredientChange(index, 'name', e.target.value)}
                                        className="form-control select"
                                    >
                                        <option value="">Select Ingredient</option>
                                        {inventoryIngredients.map((invIngredient, idx) => (
                                            <option key={idx} value={invIngredient.name}>
                                                {invIngredient.name}
                                            </option>
                                        ))}
                                    </select>

                                    <label className="form-label mt-2">Amount</label>
                                    <input
                                        type="text"
                                        placeholder="Amount"
                                        value={ingredient.amount}
                                        onChange={(e) => handleIngredientChange(index, 'amount', e.target.value)}
                                        className="form-control"
                                    />
                                    {index > 0 && (
                                        <button
                                            type="button"
                                            className="btn btn-danger mt-2"
                                            onClick={() => removeIngredient(index)}
                                        >
                                            Remove
                                        </button>
                                    )}
                                </div>
                            ))}

                            {errors.ingredients && (
                                <div className="text-danger">{errors.ingredients}</div>
                            )}

                            <div className="d-flex justify-content-between mb-2">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={addIngredient}
                                >
                                    Add Ingredient
                                </button>
                                <div>
                                    <button
                                        className="btn btn-success me-2"
                                        onClick={updateRecipeHandler}
                                    >
                                        Save
                                    </button>
                                    <button
                                        className="btn btn-danger"
                                        onClick={() => navigator("/recipes")}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EditRecipeComponent;
