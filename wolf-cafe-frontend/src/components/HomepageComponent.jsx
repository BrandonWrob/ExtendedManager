import React from 'react'
import { useNavigate } from 'react-router-dom'

const HomepageComponent = () => {

    const navigate = useNavigate()

    function handleOrder() {
        // Redirect the user to the order page (you can adjust the route as needed)
        navigate('/create-order')
    }

    return (
        <div className='container'>
            <br /> <br />
            <h2 className='text-center'>Welcome to Our Cafe</h2>
            <button className='btn btn-primary' onClick={handleOrder}>Click here to order</button>
        </div>
    )
}

export default HomepageComponent
