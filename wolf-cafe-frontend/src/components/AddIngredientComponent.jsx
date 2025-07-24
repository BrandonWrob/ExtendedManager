import React, { useState } from 'react';
import { addIngredient } from '../services/IngredientService'

const AddIngredientComponent = () => {
  const [ingredient, setIngredient] = useState('');
  const [amount, setAmount] = useState('');
  const [feedback, setFeedback] = useState('');
  const [error, setError] = useState('');

  const handleAddIngredient = async (e) => {
    e.preventDefault();

    // Clear previous messages
    setFeedback('');
    setError('');

    // Validate the input
    if (ingredient === '' || amount === '') {
      setError('Both fields are required.');
      return;
    }

    if (isNaN(amount) || amount < 0) {
      setError('Amount must be a positive number.');
      return;
    }

    // Prepare the data to send
    const ingredientDto = {
      name: ingredient.trim(),
      amount: parseInt(amount),
    };
	
	// Send a POST API request to the backend API on port 8080
	await addIngredient(ingredientDto).then(response => {
		
		console.log(response.data)
		
		// Ingredient added successfully
		setFeedback('Ingredient added successfully!');
		setIngredient('');
		setAmount('');

    }).catch(error => {
      // if unsuccessful, show the failure message
        console.error(error)

      if (error.response && error.response.status === 409) {
        // Conflict error (ingredient already exists)
        setError('Ingredient already exists.');
      } else if (error.response && error.response.status === 415) {
        // Unsupported Media Type (negative amount)
        setError('Amount must be a positive number.');
      } else {
        // Other errors
        setError('Failed to add ingredient. Please try again.');
      }
    })
  };

  return (
    <div className="container position-relative vh-50 d-flex flex-column justify-content-between ingredient-container">
      {/* Add Ingredients Form */}
      <div className="container mt-4">
        <div className="card col-md-6 offset-md-3 manage-user-form">
			<h2 className="text-center">Add Ingredients</h2>
          <div className="card-body">
            <form onSubmit={handleAddIngredient}>
              <div className="form-group mb-3 d-flex align-items-center">
                <label htmlFor="ingredient" className="form-label me-2" style={{ width: "100px" }}>Name</label>
                <input
                  type="text"
                  id="ingredient"
                  placeholder="Enter Ingredient Name"
                  value={ingredient}
                  onChange={(e) => setIngredient(e.target.value)}
                  className="form-control"
                  style={{ flex: 1 }}
                />
              </div>

              <div className="form-group mb-3 d-flex align-items-center">
                <label htmlFor="amount" className="form-label me-2" style={{ width: "100px" }}>Amount</label>
                <input
                  type="number"
                  id="amount"
                  placeholder="Enter Amount"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="form-control"
                  style={{ flex: 1 }}
                />
              </div>

              {/* Error and Feedback Messages */}
              {error && <div className="text-danger mb-3">{error}</div>}
              {feedback && <div className="text-success mb-3">{feedback}</div>}

              {/* Submit Button */}
              <button type="submit" className="btn btn-primary mt-2">Add Ingredient</button>
			  
			  {/* Bottom Controls */}
			  <div className="d-flex justify-content-between align-items-center">
			    {/* Home Link at Bottom Left */}
			    <div className="p-3">
			      <a href="/recipes" className="text-decoration-none">Home</a>
			    </div>
			  </div>
			  
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddIngredientComponent;