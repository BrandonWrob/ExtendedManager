import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const OrderConfirmationComponent = () => {
    const { id } = useParams();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setTimeout(() => {
            setLoading(false);
        }, 1000);
    }, []);

    if (loading) {
        return <div>Loading...</div>; 
    }

    return (
        <div style={{ padding: '20px', textAlign: 'center' }}>
            <h1 style={{ color: 'green' }}>Order Created Successfully!</h1>
            <div
                style={{
					marginTop: '20px', 
					padding: '15px', 
					display: 'inline-block', 
					fontWeight: 'bold', 
					textAlign: 'center', 
					fontSize: '18px', 
					borderRadius: '5px',
					backgroundColor: 'var(--recipes-background)',
				}}> <div>Your Order Number: {id}</div> 
            </div>
        </div>
    );
};

export default OrderConfirmationComponent;