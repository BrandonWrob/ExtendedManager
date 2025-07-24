import React, { useEffect, useState } from 'react';
import { listOrders, fulfillOrder, pickupOrder, viewOrdersStatus } from '../services/ViewOrdersService';
import { isAdminUser, isManagerUser, isStaffUser, getLoggedInUser } from '../services/AuthService';
import { updateHistoryStatus } from '../services/HistoryService';
import { useNavigate } from 'react-router-dom';

/**
 * ViewOrdersComponent fetches and displays all orders with Order IDs and allows
 * the user to view, fulfill, or pick up orders.
 */
const ViewOrdersComponent = () => {
    const [orders, setOrders] = useState([]);
    const [userOrdersStatus, setUserOrdersStatus] = useState([]);
    const [fulfilledOrders, setFulfilledOrders] = useState({});
    const [pickedUpOrders, setPickedUpOrders] = useState({});
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const isAdmin = isAdminUser();
    const isManager = isManagerUser();
    const isStaff = isStaffUser();
    const loggedInUser = getLoggedInUser();

    useEffect(() => {
        getAllOrders();
        getUserOrdersStatus();  
    }, []);

    /** Fetches all orders */
    const getAllOrders = () => {
        console.log("Fetching all orders...");
        listOrders()
            .then((response) => {
                console.log("Received response: ", response);
                setOrders(response.data);
                setFulfilledOrders(
                    response.data.reduce((acc, order) => {
                        acc[order.id] = order.fulfilled;
                        return acc;
                    }, {})
                );
            })
            .catch((error) => {
                console.error('Error fetching orders:', error);
            });
    };

    /** Fetches the orders for the logged-in user */
    const getUserOrdersStatus = () => {
        console.log("Fetching orders for the logged-in user...");
        viewOrdersStatus(loggedInUser)
            .then((response) => {
                console.log("User orders status: ", response);
                setUserOrdersStatus(response.data);
            })
            .catch((error) => {
                console.error('Error fetching user orders status:', error);
            });
    };

    /** Marks an order as fulfilled */
    const fulfillOrderById = (id) => {
        setError('');
        if (fulfilledOrders[id]) {
            setMessage(`Order ${id} has already been fulfilled.`);
            return;
        }

        fulfillOrder(id)
            .then(() => {
                setFulfilledOrders((prev) => ({ ...prev, [id]: true }));
                setMessage(`Order ${id} has been successfully fulfilled.`);
            })
            .catch((error) => {
                console.error('Error fulfilling order:', error);
            });
    };

    /** After pickup, the order is removed */
    const handlePickupOrder = (id) => {
        setError('');

        if (!fulfilledOrders[id]) {
            setError(`Order ${id} is not fulfilled and cannot be picked up.`);
            return;
        }

        pickupOrder(id)
            .then(() => {				
                updateHistoryStatus(id)
                  .then(() => {
                    setPickedUpOrders((prev) => ({ ...prev, [id]: true }));
                    setMessage(`Order ${id} has been successfully picked up.`);
                  })
                  .catch((error) => {
                      console.error('Error recording order pickup:', error);
                  });
            })
            .catch((error) => {
                console.error('Error picking up order:', error);
                setError('Order does not belong to you or Order has been picked up.');
            });
    };

    /** Fetches and navigates to the order details by ID */
    const viewOrderById = (id) => {
        setError('');
        navigate(`/orders/${id}`);
    };

    return (
        <div className="container view-order-container">
            <h2 className="text-center">All Orders</h2>
            {message && <div className="alert alert-info">{message}</div>}
            {error && <div className="alert alert-danger">{error}</div>}

            {orders.length === 0 ? (
                <div style={{ fontSize: '2rem', fontWeight: 'bold', textAlign: 'center', marginTop: '20px' }}>
                    No Current Orders
                </div>
            ) : (
                <table className="table table-striped table-bordered">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((order) => (
                            <tr key={order.id}>
                                <td>{order.id}</td>
                                <td>
                                    {/* View Order */}
                                    {(isAdmin || isManager || isStaff) && (
                                        <button
                                            className="btn btn-info"
                                            onClick={() => viewOrderById(order.id)}
                                            style={{ marginRight: '10px' }}
                                        >
                                            View Order
                                        </button>
                                    )}
                                    {/* Fulfill Order */}
                                    {(isAdmin || isManager || isStaff) && (
                                        <button
                                            className={`btn ${fulfilledOrders[order.id] ? 'btn-success' : 'btn-secondary'}`}
                                            onClick={() => fulfillOrderById(order.id)}
                                            style={{ marginLeft: '10px' }}
                                            disabled={fulfilledOrders[order.id]}
                                        >
                                            {fulfilledOrders[order.id] ? 'Fulfilled' : 'Fulfill'}
                                        </button>
                                    )}

                                    {/* Pick Up Order */}
                                    <button
                                        className={`btn ${fulfilledOrders[order.id] && !pickedUpOrders[order.id] ? 'btn-success' : (pickedUpOrders[order.id] ? 'btn-dark' : 'btn-warning')}`}
                                        onClick={() => handlePickupOrder(order.id)}
                                        style={{ marginLeft: '10px' }}
                                        disabled={pickedUpOrders[order.id]}
                                    >
                                        {pickedUpOrders[order.id] ? 'Picked Up' : 'Pick Up'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}

            {userOrdersStatus.length === 0 ? (
                <div style={{ fontSize: '2rem', fontWeight: 'bold', textAlign: 'center', marginTop: '20px' }}>
                    No Orders for You
                </div>
            ) : (
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', textAlign: 'center' }}>
                    Your Order IDs: {userOrdersStatus.map((order, index) => (
                        <span key={order.id}>
                            {order.id}{index < userOrdersStatus.length - 1 ? ', ' : ''}
                        </span>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ViewOrdersComponent;