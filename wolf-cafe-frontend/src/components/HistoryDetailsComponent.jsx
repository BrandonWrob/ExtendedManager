import React, { useEffect, useState } from 'react';
import { getHistoryById } from '../services/HistoryService';
import { isAdminUser } from '../services/AuthService'
import { useParams, useNavigate } from 'react-router-dom';

const HistoryDetailsComponent = () => {
	
	const { id } = useParams();
	const [recipeData, setRecipeData] = useState([]);
	const [ingredientData, setIngredientData] = useState('');
	const [totalCost, setTotalCost] = useState('');
	const [recipeIndex, setRecipeIndex] = useState('');
	const [error, setError] = useState(null);
	const navigate = useNavigate()
	const isAdmin = isAdminUser()
	
	useEffect(() => {
		fetchOrderHistory()
	}, [id]);
		
	// fetch and store all users
	async function fetchOrderHistory() {
		// get the order history corresponding to the id
		await getHistoryById(id)
		  .then((response) => {
			
			// parse the recipes from the response
			const recipes = parseRecipeData(response.data.recipesInOrder);
			setRecipeData(recipes);
			
			// format the ingredients from the response
			const ingredients = parseIngredientData(response.data.ingredientsUsed);
			setIngredientData(ingredients);
			
			// set total cost
			setTotalCost(response.data.total.toFixed(2))
		  })
		  .catch((error) => {
		    setError('Failed to fetch order history.');
		    console.error('Error fetching order history:', error);
		  });
	}
	
	// break up the recipes by name and amount and store them together
	const parseRecipeData = (recipesInOrder) => {
	  const recipes = recipesInOrder.split(',').map(recipe => {
	    const [name, amount] = recipe.split(':').map(data => data.trim())
	    return { name, amount: parseInt(amount) }
	  })
	  return recipes
	}
	
	// format the ingredient string to look a bit nicer
	const parseIngredientData = (ingredientsUsed) => {
	  return ingredientsUsed.split(',').map(ingredient => {
	      return ingredient.replace(':', ': ')
	    })
	    .join(', ').trim()
	}

	// handle index change in recipe list from dropdown menu
	const handleRecipeChange = (e) => {
	  setRecipeIndex(e.target.value);
	};
	

	const adminTable = (
		<>
		<thead>
		  <tr>
		    <th style={{ fontSize: '1.2rem'}}>Recipe Name</th>
		    <th style={{ fontSize: '1.2rem' }}>Amount Ordered</th>
		    <th style={{ fontSize: '1.2rem' }}>Total Ingredients Used</th>
		  </tr>
		</thead>
		<tbody>
		  {recipeData.length > 0 ? (
		    <>
		      <tr>
		        <td style={{ fontSize: '1.2rem' }}>
		          {recipeData.length > 1 ? (
		            <select aria-label="Select Recipe" className= "select"
		              value={recipeIndex}
		              onChange={handleRecipeChange}
		              style={{ fontSize: '1.2rem', padding: '5px', width: '100%' }}
		            >
				  	<option value=''>Select</option>
		              {recipeData.map((recipe, index) => (
		                <option key={index} value={index}>
		                  {recipe.name}
		                </option>
		              ))}
		            </select>
		          ) : (
		            recipeData[0].name
		          )}
		        </td>
			  
			  <td style={{ fontSize: '1.2rem' }}>
			    {recipeData.length === 1
			      ? recipeData[0].amount // If only one recipe, display its amount directly
			      : recipeIndex !== '' // If a recipe is selected, show its amount
			      ? recipeData[recipeIndex].amount
			      : ''}
		        </td>
			  
			  <td style={{ fontSize: '1.2rem'}}>
			    {recipeData.length === 1
			      ? ingredientData // show ingredients initially if only 1 recipe
			      : recipeIndex !== ''
			      ? ingredientData // if multiple, only show ingredients once recipe is selected
			      : ''}
			  </td>
		      </tr>
		    </>
		  ) : (
		    <tr>
		      <td colSpan="4" style={{ textAlign: 'center', fontSize: '1.2rem'}}>
		        No recipes found for this order.
		      </td>
		    </tr>
		  )}
		</tbody>
		</>
	)
	
	const userTable = (
		<>
		<thead>
		  <tr>
		    <th style={{ fontSize: '1.2rem'}}>Recipe Name</th>
		    <th style={{ fontSize: '1.2rem'}}>Amount Ordered</th>
		  </tr>
		</thead>
		<tbody>
		  {recipeData.length > 0 ? (
		    recipeData.map((recipe, index) => (
		      <tr key={index}>
		        <td style={{ fontSize: '1.2rem' }}>{recipe.name}</td>
		        <td style={{ fontSize: '1.2rem' }}>{recipe.amount}</td>
		      </tr>
		    ))
		  ) : (
		    <tr>
		      <td colSpan="3" style={{ textAlign: 'center', fontSize: '1.2rem'}}>
		        No recipes found for this order.
		      </td>
		    </tr>
		  )}
		</tbody>
		</>
	)
	
	return (
	  <div>
	    <div>
	     <div className="container order-history-container">
		  <h2 className="text-center">Order ID: {id}</h2>
		  <div className="table-responsive">
	        <table className="table  table-bordered custom-history-table">
			  {isAdmin ? adminTable : userTable}
	        </table>
	      </div>
		 </div>
	    </div>
		
		<h4>Total: ${recipeData.length === 1
					  ? totalCost // show cost initially if only 1 recipe
					  : recipeIndex !== ''
					  ? totalCost // if multiple, only show cost once recipe is selected
					  : '0.00'}</h4>
		
		
		
		{/* Backspace Button */}
		<div className="d-flex justify-content-start mt-4">
		  <button
		    onClick={() => navigate(-1)}
		    className="btn btn-secondary"
		    style={{ fontSize: '1rem', padding: '8px 16px', borderRadius: '5px' }}
		  >
		    Go Back
		  </button>
		</div>
	  </div>
	);
	
}
export default HistoryDetailsComponent;
