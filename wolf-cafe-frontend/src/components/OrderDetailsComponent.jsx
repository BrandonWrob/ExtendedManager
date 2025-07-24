import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getOrderById } from '../services/ViewOrdersService';

/**
 * OrderDetailsComponent displays detailed information about a single order
 * based on the order ID from the URL.
 */
const OrderDetailsComponent = () => {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOrderDetails();
  }, [id]);

  const fetchOrderDetails = () => {
    getOrderById(id)
      .then((response) => {
        setOrder(response.data); // Assuming response.data contains the order info
      })
      .catch((error) => {
        setError('Order not found or failed to fetch order details.');
        console.error('Error fetching order:', error);
      });
  };

  const calculateTotalAmount = () => {
    if (!order.recipes) return 0;
    
    // Calculate the sum of all recipes' prices
    const totalRecipesAmount = order.recipes.reduce((total, recipe) => {
      return total + recipe.amount * recipe.price; // Assuming 'amount' and 'price' are available
    }, 0);

    // Add the tip to the total amount
    const totalAmount = totalRecipesAmount + (order.tip || 0);
    return totalAmount;
  };

  if (error) {
    return <div className="alert alert-danger">{error}</div>;
  }

  if (!order) {
    return <div className="loading">Loading order details...</div>;
  }

  const totalAmount = calculateTotalAmount();

  return (
    <div className="container order-details-container">
      <h2 className="text-center k mb-4" style={{ fontSize: '2rem', fontWeight: 'bold' }}>
        Order Details for Order ID: {order.id}
      </h2>

      {/* Fulfilled Status */}
      <p style={{ fontSize: '1.2rem' }}>
        <strong>Fulfilled:</strong>
        <span 
          style={{
            display: 'inline-block',
            padding: '5px 10px',
            borderRadius: '5px',
            color: order.fulfilled ? 'white' : 'black',
            backgroundColor: order.fulfilled ? 'green' : 'yellow',
            fontWeight: 'bold',
            fontSize: '1.4rem'
          }}
        >
          {order.fulfilled ? 'Yes' : 'No'}
        </span>
      </p>

      {/* Recipes Table */}
      <h5 className="card-title" style={{ fontSize: '1.5rem' }}>Recipes</h5>
      <div className="table-responsive">
        <table className="table table-bordered">
          <thead>
            <tr>
              <th style={{ fontSize: '1.2rem'}}>Recipe Name</th>
              <th style={{ fontSize: '1.2rem' }}>Amount</th>
              <th style={{ fontSize: '1.2rem' }}>Price</th>
            </tr>
          </thead>
          <tbody>
            {order.recipes && order.recipes.length > 0 ? (
              order.recipes.map((recipe, index) => (
                <tr key={index}>
                  <td style={{ fontSize: '1.2rem'}}>{recipe.name}</td>
                  <td style={{ fontSize: '1.2rem' }}>{recipe.amount}</td>
                  <td style={{ fontSize: '1.2rem'}}>${recipe.price.toFixed(2)}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3" style={{ textAlign: 'center', fontSize: '1.2rem'}}>No recipes found for this order.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Tip Information */}
      {order.tip && (
        <p style={{ fontSize: '1.3rem'}}><strong>Tip:</strong> ${order.tip.toFixed(2)}</p>
      )}

      {/* Go Back Button */}
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
};

export default OrderDetailsComponent;
