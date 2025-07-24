import React, { useState, useEffect } from 'react';
import { listRecipes } from '../services/RecipesService';
import { makeOrder } from '../services/CreateOrderService';
import { createOrderHistory } from '../services/HistoryService';
import { useNavigate } from 'react-router-dom';
import { getTaxRate, calculateTax } from '../services/TaxService';

const CreateOrderComponent = () => {
    const [recipes, setRecipes] = useState([]);
    const [cart, setCart] = useState([]);
    const [tipPercent, setTipPercent] = useState(null);
    const [tax, setTax] = useState(0);
    const [customTip, setCustomTip] = useState('');
    const [quantities, setQuantities] = useState({});
    const [errorMessage, setErrorMessage] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [isCustomTipDisabled, setIsCustomTipDisabled] = useState(false);
	const [taxRate, setTaxRate] = useState(0); 

    const navigate = useNavigate();

    useEffect(() => {
		 listRecipes()
		    .then((response) => {
				setRecipes(response.data);
			})
			.catch((error) => {
				console.error("Error fetching recipes: ", error);
			});
		getTaxRate()
			.then(response => {
			    setTaxRate(response.data.rate);
			})
			.catch(error => {
			    console.error('Error fetching tax rate: ', error);
			});
    }, []);

    const handleQuantityChange = (id, value) => {
        setQuantities((prev) => ({ ...prev, [id]: value }));
    };

    const addToCart = (recipe) => {
        const quantity = parseInt(quantities[recipe.id]) || 1;
        setCart((prevCart) => {
            const existingItem = prevCart.find((item) => item.id === recipe.id);
            if (existingItem) {
                return prevCart.map((item) =>
                    item.id === recipe.id ? { ...item, amount: item.amount + quantity } : item
                );
            }
            return [...prevCart, { ...recipe, amount: quantity }];
        });
        setQuantities((prev) => ({ ...prev, [recipe.id]: 1 }));
    };

    const removeFromCart = (id) => {
        setCart((prevCart) => prevCart.filter((item) => item.id !== id));
    };

    const handleTipChange = (percent) => {
        if (percent === 0) {
            if (tipPercent === 0) {
                setTipPercent(null);
                setCustomTip('');
                setIsCustomTipDisabled(false); 
            } else {
                setTipPercent(0);
                setCustomTip('');
                setIsCustomTipDisabled(true);
            }
        } else {
            if (tipPercent === percent) {
                setTipPercent(null);
                setIsCustomTipDisabled(false);
            } else {
                setTipPercent(percent);
                setCustomTip('');
                setIsCustomTipDisabled(true);
            }
        }
        setErrorMessage('');
    };

	const handleCustomTipChange = (value) => {
	    // Allows only two points after decimal point
	    const regex = /^[0-9]*(\.[0-9]{0,2})?$/;
	    
	    if (regex.test(value) || value === '') {
	        if (parseFloat(value) < 0) {
	            setErrorMessage('Custom tip cannot be negative!');
	        } else {
	            setErrorMessage('');
	        }
	        setCustomTip(value);
	    }
	};


    const calculateSubtotal = () => {
        return cart.reduce((total, item) => total + item.price * item.amount, 0);
    };

    const calculateTip = () => {
        const subtotal = calculateSubtotal();
        return tipPercent !== null
            ? subtotal * (tipPercent / 100)
            : parseFloat(customTip) || 0;
    };
	
	const calculateTaxHigh = () => {
		const subtotal = calculateSubtotal();
    calculateTax(subtotal)
    .then((response) => {
      setTax(response.data);
    })
    .catch((error) => {
      console.error("Error fetching recipes: ", error);
    });
	};

    const calculateTotal = () => {
        const subtotal = calculateSubtotal();
        const tip = calculateTip();
        calculateTaxHigh();
        return subtotal + tip + tax;
    };

	const handleCreateOrder = () => {
	    if (cart.length === 0) {
	        setErrorMessage('No item selected to order!');
	        return;
	    }

	    if (tipPercent === null && (customTip === '' || parseFloat(customTip) <= 0)) {
	        setErrorMessage('Invalid tip option!');
	        return;
	    }

	    setErrorMessage('');
	    setSuccessMessage('');

	    const orderDto = {
	        fulfilled: false,
	        recipes: cart,
	    };

	    makeOrder(orderDto)
	        .then(response => {
              const orderId = response.data.id; // Assuming the ID is in `response.data.id`
	            setSuccessMessage('Order created successfully!');

              createOrderHistory(response.data)
                .then(() => {
                  setCart([]);
                  navigate(`/order-confirmation/${orderId}`);
                })
                .catch(error => {
                    console.error("Error recording order: ", error);
                    setErrorMessage('An error occured while recording the order.');
                });
	        })
	        .catch((error) => {
	            setErrorMessage('Error creating order. Please try again.');
	        });
	};


    return (
		<div className='create-order-container'>
        <div style={{ fontFamily: 'Arial, sans-serif', margin: '0 auto', padding: '30px', maxWidth: '1200px' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '20px' }}>Wolfpack Cafe</h1>
            {errorMessage && <div style={{ color: 'red', fontWeight: 'bold', marginBottom: '10px' }}>{errorMessage}</div>}
            {successMessage && <div style={{ color: 'green', fontWeight: 'bold', marginBottom: '10px' }}>{successMessage}</div>}

            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '20px' }} className= 'create-order-content'>
                <div style={{ flex: 1, padding: '20px', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }} className='create-order-form'>
                    <h3>Available Recipes</h3>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr>
                                <th>Recipe Name</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {recipes.map((recipe) => (
                                <tr key={recipe.id}>
                                    <td>{recipe.name}</td>
                                    <td>${recipe.price.toFixed(2)}</td>
                                    <td>
                                        <input
                                            type="number"
                                            min="1"
                                            value={quantities[recipe.id] || 1}
                                            onChange={(e) => handleQuantityChange(recipe.id, e.target.value)}
                                            style={{ padding: '8px', width: '60px', textAlign: 'center' }}
                                        />
                                    </td>
                                    <td>
                                        <button
                                            style={{
                                                backgroundColor: '#4CAF50',
                                                color: 'white',
                                                padding: '10px 20px',
                                                border: 'none',
                                                borderRadius: '5px',
                                                cursor: 'pointer',
                                            }}
                                            onClick={() => addToCart(recipe)}
                                        >
                                            Add to Cart
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                <div style={{ flex: 1, padding: '20px', borderRadius: '8px', boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)' }} className='create-order-form'>
                    <h3>Cart</h3>
                    {cart.length > 0 ? (
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Amount</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {cart.map((item) => (
                                    <tr key={item.id}>
                                        <td>{item.name}</td>
                                        <td>{item.amount}</td>
                                        <td>
                                            <button
                                                style={{
                                                    backgroundColor: '#f44336',
                                                    color: 'white',
                                                    padding: '8px 16px',
                                                    border: 'none',
                                                    borderRadius: '5px',
                                                    cursor: 'pointer',
                                                }}
                                                onClick={() => removeFromCart(item.id)}
                                            >
                                                Remove
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : (
                        <p>No items added to cart.</p>
                    )}

                    <div>
                        <h4>Tip:</h4>
                        {[15, 20, 25, 0].map((percent) => (
                            <button
                                key={percent}
                                style={{
                                    backgroundColor: tipPercent === percent ? '#355E3B' : '#4CAF50',
                                    color: 'white',
                                    padding: '8px 16px',
                                    border: 'none',
                                    borderRadius: '5px',
                                    cursor: 'pointer',
                                    marginRight: '5px',
                                }}
                                onClick={() => handleTipChange(percent)}
                            >
                                {percent === 0 ? 'No Tip' : `${percent}%`}
                            </button>
                        ))}
                        <input
                            type="text"
                            placeholder="Custom Tip"
                            value={customTip}
                            onChange={(e) => handleCustomTipChange(e.target.value)}
                            disabled={isCustomTipDisabled}
                            style={{
                                width: '100%',
                                padding: '10px',
                                marginTop: '10px',
                                borderRadius: '5px',
                                border: '1px solid',
                            }}
                        />
                    </div>

                    <div>
                        <h4>Order Summary</h4>
                        <div>
                            <p><strong>Items:</strong></p>
                            {cart.map(item => (
                                <p key={item.id}>{item.name} x {item.amount} - ${(item.price * item.amount)?.toFixed(2)}</p>
                            ))}
                        </div>
                        <div>
                            <p><strong>Tip:</strong> ${calculateTip()?.toFixed(2)}</p>
                        </div>
                        <div>
                            <p>Tax: ${tax?.toFixed(2)}</p>
                        </div>
                        <div>
                            <p><strong>Total:</strong> ${calculateTotal()?.toFixed(2)}</p>
                        </div>
                    </div>

                    <button
                        onClick={handleCreateOrder}
                        style={{
                            backgroundColor: '#28a745',
                            color: 'white',
                            padding: '10px 20px',
                            border: 'none',
                            borderRadius: '5px',
                            cursor: 'pointer',
                            width: '100%',
                            marginTop: '15px',
                        }}
                    >
                        Create Order
                    </button>
                </div>
            </div>
        </div>
		</div>
    );
};

export default CreateOrderComponent;
