// src/components/InventoryComponent.jsx
import { useEffect, useState } from 'react';
import { getInventory, updateInventory } from '../services/InventoryService';
import { Link } from 'react-router-dom';

const InventoryComponent = () => {
    const [ingredients, setIngredients] = useState([]);
    const [addAmounts, setAddAmounts] = useState({});
    const [validationMessage, setValidationMessage] = useState('');
    const [validationColor, setValidationColor] = useState('');

    // Fetch inventory on component mount
    useEffect(() => {
        getInventory()
            .then((response) => {
                const fetchedIngredients = response.data.ingredients;
                setIngredients(fetchedIngredients);

                // Initialize addAmounts with 0 for each ingredient
                const initialAddAmounts = {};
                fetchedIngredients.forEach(ingredient => {
                    initialAddAmounts[ingredient.name] = 0;
                });
                setAddAmounts(initialAddAmounts);
            })
            .catch(error => {
                console.error(error);
                setValidationMessage('Failed to fetch inventory.');
                setValidationColor('text-danger');
            });
    }, []);

	// saves inputs to the add field to addAmounts
	const saveAmountChange = (e, name) => {
	    setAddAmounts({
			// copy over the current addAmounts, then change the value stored for that specific ingredient
	        ...addAmounts,
	        [name]: e.target.value
	    })
	}

	// handles updating the inventory and resetting the page
	const modifyInventory = (e) => {
	    e.preventDefault()

		// if the amounts to add are valid, proceed
	    if (validateForm()) {
			// create a new inventory object
			const updatedInventory = {
				// fill the ingredients field of the new inventory without modifying the existing ingredients
			    ingredients: ingredients.map(ingredient => {
					// parse the additional amount for the current ingredient
			        const addedAmount = parseInt(addAmounts[ingredient.name] || 0)
			        return {
						// copy over the current ingredient, then set the amount to be the addition
			            ...ingredient,
			            amount: addedAmount
			        }
			    })
			}

			// update the amounts in the backend
	        updateInventory(updatedInventory).then(response => {
				// if successful, show the success message
	            console.log(response.data)
	            setValidationMessage('Inventory updated successfully!')
	            setValidationColor('text-success')

				// update the page with the new ingredient values from the returned response
	            setIngredients(response.data.ingredients)
	        }).catch(error => {
				// if unsuccessful, show the failure message
	            console.error(error)
	            setValidationMessage('Failed to update inventory.')
	            setValidationColor('text-danger')
	        })
	    }
		
		// set the amounts in addAmounts back to 0 to reset the form
		const resetAddAmounts = {}
		ingredients.forEach(ingredient => {
		    resetAddAmounts[ingredient.name] = 0
		})
		setAddAmounts(resetAddAmounts)
	}

	// validate that the values inputted by the user are positive integers
	const validateForm = () => {
	    let valid = true
		// iterate through addAmounts (contains user given values) to determine validity
	    Object.values(addAmounts).forEach(amount => {
	        if (parseInt(amount) < 0) {
	            valid = false
	        }
	    })

		// if not valid, show invalid input message
	    if (!valid) {
	        setValidationMessage('Negative amounts cannot be added.')
	        setValidationColor('text-danger')
	    }
	    return valid
	}

    return (
        <div className="container inventory-container">
            <br /><br />
            <div className="row">
                <div className="card col-md-8 offset-md-2 inventory-order-form1">
                    <h2 className="text-center">Inventory</h2>

                    <div className="card-body">
                        <form onSubmit={modifyInventory}>
                            {/* Inventory Table */}
                            <div className="table-responsive">
                                <table className="custom-inventory-table">
                                    <thead>
                                        <tr>
                                            <th>Ingredient</th>
                                            <th>Current Amount</th>
                                            <th>Add Amount</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {ingredients.map(ingredient => (
                                            <tr key={ingredient.id}>
                                                <td>{ingredient.name}</td>
                                                <td>
                                                    <input
                                                        type="text"
                                                        readOnly
                                                        value={ingredient.amount}
                                                        className="form-control"
                                                    />
                                                </td>
                                                <td>
                                                    <input
                                                        type="number"
                                                        placeholder="0"
                                                        value={addAmounts[ingredient.name]}
                                                        onChange={(e) => saveAmountChange(e, ingredient.name)}
                                                        className="form-control"
                                                    />
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>

                            {/* Navigation and Submit Button */}
                            <div className="d-flex justify-content-between align-items-center mt-3">
                                <Link to="/recipes" className="btn btn-secondary">Home</Link>
                                <button type="submit" className="btn btn-primary">Update Inventory</button>
                            </div>

                            {/* Validation Message */}
                            {validationMessage && (
                                <div className={`form-text ${validationColor} mt-2`}>
                                    {validationMessage}
                                </div>
                            )}
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default InventoryComponent;
