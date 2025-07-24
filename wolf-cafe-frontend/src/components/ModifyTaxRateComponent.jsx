// src/components/ModifyTaxRateComponent.jsx
import React, { useState, useEffect } from 'react';
import { getTaxRate, setTaxRate } from '../services/TaxService';
import { useNavigate } from 'react-router-dom';

const ModifyTaxRateComponent = () => {
  const [currentRate, setCurrentRate] = useState(null);
  const [newRate, setNewRate] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [fieldErrors, setFieldErrors] = useState({ newRate: '' });
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchTaxRate = async () => {
      setLoading(true);
      try {
        const response = await getTaxRate();
        if (response?.data?.rate !== undefined) {
          setCurrentRate(response.data.rate);
        } else {
          throw new Error('Tax rate not found');
        }
      } catch (error) {
        console.error('Error fetching tax rate:', error);
        setFieldErrors({ newRate: 'Failed to fetch the current tax rate.' });
      } finally {
        setLoading(false);
      }
    };
    fetchTaxRate();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({ newRate: '' });
    setSuccessMessage('');

    // Validate new rate input
    if (!newRate) {
      setFieldErrors({ newRate: 'Tax rate is required.' });
      return;
    }

    const rateNumber = parseFloat(newRate);
    if (isNaN(rateNumber) || rateNumber <= 0) {
      setFieldErrors({
        newRate: 'Please enter a valid positive number for the tax rate.',
      });
      return;
    }

    setSubmitting(true);

    try {
      // Convert percentage to decimal before sending to backend
      await setTaxRate(rateNumber / 100);
      setSuccessMessage('Tax rate updated successfully!');
      setCurrentRate(rateNumber / 100); // Update currentRate in decimal
      setNewRate('');
    } catch (error) {
      setFieldErrors({ newRate: 'Failed to update the tax rate. Please try again.' });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <div>Loading tax rate...</div>;
  }

  return (
    <div className="container">
      <br />
      <div className="row">
        <div className="card col-md-6 offset-md-3 tax-form">
		<h2 className="text-center">Modify Tax Rate</h2>
          <div className="card-body">
            {successMessage && (
              <div className="alert alert-success" role="alert">
                {successMessage}
              </div>
            )}
            <form onSubmit={handleSubmit}>
              <div className="form-group mb-2">
                <label className="form-label">Current Tax Rate</label>
                {/* Convert decimal to percentage for display */}
                <input
                  type="text"
                  className="form-control"
                  value={currentRate !== null ? `${(currentRate * 100).toFixed(2)}%` : 'N/A'}
                  readOnly
                />
              </div>
              <div className="form-group mb-2">
                <label className="form-label">New Tax Rate (%)</label>
				<input
				  type="number"
				  placeholder="Enter new tax rate (e.g., 4.00)"
				  value={newRate}
				  onChange={(e) => setNewRate(e.target.value)}
				  className={`form-control ${fieldErrors.newRate ? 'is-invalid' : ''}`}
				/>
                {fieldErrors.newRate && (
                  <div className="invalid-feedback" style={{ display: 'block' }}>
                    {fieldErrors.newRate}
                  </div>
                )}
              </div>
              <div className="d-flex justify-content-between mb-2">
                <button type="submit" className="btn btn-success" disabled={submitting}>
                  {submitting ? 'Updating...' : 'Update Tax Rate'}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => navigate(-1)}
                  disabled={submitting}
                >
                  Return
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ModifyTaxRateComponent;
