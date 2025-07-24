import React, { useEffect, useState } from 'react';
import { getOrderHistory, getUserHistory } from '../services/HistoryService';
import { isAdminUser, getLoggedInUser } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';

/**
 * ViewOrdersComponent fetches and displays all orders with Order IDs and allows
 * the user to view, fulfill, or pick up orders.
 */
const OrderHistoryComponent = () => {
	const [filteredOrders, setFilteredOrders] = useState([])
	const [error, setError] = useState('')
	
	const navigate = useNavigate()
	const isAdmin = isAdminUser()
	const currentUser = getLoggedInUser()
	
	// done upon page load
	useEffect(() => {
		
		if (isAdmin) {
			// get total order history
			getOrderHistory().then((response) => {
					// fetch the orders and set them
			        const fetchedOrders = response.data
					setFilteredOrders(fetchedOrders)
			    })
			    .catch(error => {
					setError('Error fetching total history.')
			        console.error(error)
			    })
		} else {
			// get user order history
			getUserHistory(currentUser).then((response) => {
					// fetch the orders and set them
			        const fetchedOrders = response.data
					setFilteredOrders(fetchedOrders)
			    })
			    .catch(error => {
					setError('Error fetching total history.')
			        console.error(error)
			    })
		}
	}, [])
	
	// navigates to the relevant order details page based on order ID
	const viewOrderById = (id) => {
	    setError('')
		navigate(`/history/${id}`)
	}
	
	return (
	    <div className="container">
		<div className='order-history-container'>
	        <h2 className="text-center">Total Order History</h2>
	        {error && <div className="alert alert-danger">{error}</div>}

	        {filteredOrders.length === 0 ? (
	            <div style={{ fontSize: '2rem', fontWeight: 'bold', textAlign: 'center', marginTop: '20px' }}>
	                No Past Orders
	            </div>
	        ) : (
				/*makes the table look a bit more proportional*/
	            <table className="table table-striped table-bordered" style={{ width: '75%', margin: '0 auto'}}>
	                <thead>
	                    <tr>
							{/* make the columns equal width */}
							<th style={{ width: '50%' }}>Order ID</th>
							<th style={{ width: '50%' }}>View Order Details</th>
							
	                    </tr>
	                </thead>
	                <tbody>
						{filteredOrders.map((order) => (
	                        <tr key={order.id}>
	                            <td>{order.id}</td>
	                            <td>
	                                {/* View Order */}
	                                <button
	                                    className="btn btn-info"
	                                    onClick={() => viewOrderById(order.id)}
	                                    style={{ marginRight: '10px' }}
	                                >
	                                    View
	                                </button>
	                            </td>
	                        </tr>
	                    ))}
	                </tbody>
	            </table>
	        )}
	    </div>
		</div>
	);
	
}
export default OrderHistoryComponent;