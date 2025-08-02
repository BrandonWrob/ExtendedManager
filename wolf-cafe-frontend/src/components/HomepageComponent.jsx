import React from 'react'
import { useNavigate } from 'react-router-dom'
import './HomepageComponent.css';

const HomepageComponent = () => {
    const navigate = useNavigate();
	const carouselImages = ["lavender.png", "late.png", "coffee_day.png"];
	const [currentImage, setCurrentImage] = React.useState(0);

	function handleSlide(direction) {
	    setCurrentImage((prevIndex) =>
	        (prevIndex + direction + carouselImages.length) % carouselImages.length
	    );
	}

	function handleOrder() {
	    navigate('/create-order');
	}

    return (
        <div className='homepage-container'>
		
            <h2 className='homepage-title'>Welcome to Our Cafe</h2>
		
			
			<div className="hero-image">
			    <img src="/images/beans.png" alt="Cafe Background" className="hero-background" />

			    {/* Left Arrow - OUTSIDE carousel image block */}
			    <button className="carousel-arrow absolute left" onClick={() => handleSlide(-1)}>‹</button>

			    <div className="carousel-overlay-advanced">
			        {/* Left preview */}
			        <img
			            src={`/images/${carouselImages[(currentImage - 1 + carouselImages.length) % carouselImages.length]}`}
			            alt="Previous"
			            className="carousel-preview"
			            onClick={() => handleSlide(-1)}
			        />

			        {/* Main image */}
			        <img
			            src={`/images/${carouselImages[currentImage]}`}
			            alt="Main"
			            className="carousel-main"
			        />

			        {/* Right preview */}
			        <img
			            src={`/images/${carouselImages[(currentImage + 1) % carouselImages.length]}`}
			            alt="Next"
			            className="carousel-preview"
			            onClick={() => handleSlide(1)}
			        />
			    </div>

			    {/* Right Arrow - OUTSIDE carousel image block */}
			    <button className="carousel-arrow absolute right" onClick={() => handleSlide(1)}>›</button>
			</div>

            {/* Feature Sections */}
			<div className="features-grid">
			    <div className="feature-card">
			        <div className="feature-text">
			            <h3>Fresh Local Ingredients</h3>
			            <p>We source our ingredients from trusted local farms and markets to bring you the best quality.</p>
			        </div>
			        <div className="feature-image">
			            <img src="/images/farm.png" alt="Fresh Ingredients" />
			        </div>
			    </div>

			    <div className="feature-card reverse">
			        <div className="feature-text">
			            <h3>Beverages and Food Provided</h3>
			            <p>Delicious coffee, pastries, and meals — all in one cozy spot.</p>
			        </div>
			        <div className="feature-image">
			            <img src="/images/sandwich.png" alt="Food and Drinks" />
			        </div>
			    </div>

			    <div className="feature-card">
			        <div className="feature-text">
			            <h3>Fast and Friendly Services</h3>
			            <p>Enjoy a quick and welcoming experience every time you visit.</p>
			        </div>
			        <div className="feature-image">
			            <img src="/images/staff.png" alt="Friendly Staff" />
			        </div>
			    </div>

			    <div className="feature-card reverse">
			        <div className="feature-text">
			            <h3>Cozy Atmosphere for Everyone</h3>
			            <p>Whether you're catching up with friends or working solo, our warm ambiance makes you feel right at home.</p>
			        </div>
			        <div className="feature-image">
			            <img src="/images/talking.png" alt="Cozy Cafe" />
			        </div>
			    </div>
			</div>

            <button className='order-button' onClick={handleOrder}>Click here to order</button>
        </div>
    );
}

export default HomepageComponent;
