import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ModifyTaxRateComponent from '../ModifyTaxRateComponent';
import { getTaxRate, setTaxRate } from '../../services/TaxService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

vi.mock('../../services/TaxService');

describe('ModifyTaxRateComponent', () => {
  beforeEach(() => {
    getTaxRate.mockResolvedValue({ data: { rate: 0.04 } }); // Initial tax rate of 4%
    setTaxRate.mockResolvedValue({}); // Mock successful update response
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test('renders and loads current tax rate on mount', async () => {
    render(
      <Router>
        <ModifyTaxRateComponent />
      </Router>
    );

    // Check that loading indicator appears initially
    expect(screen.getByText('Loading tax rate...')).toBeInTheDocument();

    // Wait for tax rate to load and display in percentage format
    await waitFor(() => {
      expect(screen.getByDisplayValue('4.00%')).toBeInTheDocument();
    });
  });

  test('displays validation error for empty new tax rate', async () => {
    render(
      <Router>
        <ModifyTaxRateComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByDisplayValue('4.00%')).toBeInTheDocument();
    });

    // Submit the form without entering a new rate
    fireEvent.click(screen.getByText('Update Tax Rate'));

    expect(screen.getByText('Tax rate is required.')).toBeInTheDocument();
  });

  test('displays validation error for invalid new tax rate input', async () => {
    render(
      <Router>
        <ModifyTaxRateComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByDisplayValue('4.00%')).toBeInTheDocument();
    });

    // Enter an invalid new tax rate
    fireEvent.change(screen.getByPlaceholderText('Enter new tax rate (e.g., 4.00)'), {
      target: { value: '-5' },
    });
    fireEvent.click(screen.getByText('Update Tax Rate'));

    expect(screen.getByText('Please enter a valid positive number for the tax rate.')).toBeInTheDocument();
  });

  test('successfully updates tax rate and displays success message', async () => {
    render(
      <Router>
        <ModifyTaxRateComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByDisplayValue('4.00%')).toBeInTheDocument();
    });

    // Enter a new valid tax rate
    fireEvent.change(screen.getByPlaceholderText('Enter new tax rate (e.g., 4.00)'), {
      target: { value: '5' },
    });
    fireEvent.click(screen.getByText('Update Tax Rate'));

    await waitFor(() => {
      expect(screen.getByText('Tax rate updated successfully!')).toBeInTheDocument();
      expect(screen.getByDisplayValue('5.00%')).toBeInTheDocument();
    });
  });

  test('displays error message when tax rate update fails', async () => {
    setTaxRate.mockRejectedValueOnce(new Error('Failed to update tax rate'));

    render(
      <Router>
        <ModifyTaxRateComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByDisplayValue('4.00%')).toBeInTheDocument();
    });

    // Enter a new valid tax rate
    fireEvent.change(screen.getByPlaceholderText('Enter new tax rate (e.g., 4.00)'), {
      target: { value: '5' },
    });
    fireEvent.click(screen.getByText('Update Tax Rate'));

    await waitFor(() => {
      expect(screen.getByText('Failed to update the tax rate. Please try again.')).toBeInTheDocument();
    });
  });
});
