import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom'; // Provides additional matchers
import AddIngredientComponent from '../AddIngredientComponent';
import * as IngredientService from '../../services/IngredientService';
import { vi } from 'vitest';

vi.mock('../../services/IngredientService', () => ({
  addIngredient: vi.fn(),
}));

describe('AddIngredientComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the form correctly', () => {
    render(<AddIngredientComponent />);
    expect(screen.getByLabelText(/Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Amount/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Add Ingredient/i })).toBeInTheDocument();
  });

  it('shows validation message when fields are empty', async () => {
    render(<AddIngredientComponent />);
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));

    expect(screen.getByText('Both fields are required.')).toBeInTheDocument();
  });

  it('shows validation message for negative or non-numeric amount', async () => {
    render(<AddIngredientComponent />);
    
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Sugar' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '-10' } });
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));
    
    expect(screen.getByText('Amount must be a positive number.')).toBeInTheDocument();
  });

  it('displays success message on successful ingredient addition', async () => {
    IngredientService.addIngredient.mockResolvedValue({ data: {} });

    render(<AddIngredientComponent />);
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Sugar' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '100' } });
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));

    expect(await screen.findByText('Ingredient added successfully!')).toBeInTheDocument();
  });

  it('displays error message for duplicate ingredient', async () => {
    IngredientService.addIngredient.mockRejectedValue({ response: { status: 409 } });

    render(<AddIngredientComponent />);
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Sugar' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '100' } });
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));

    expect(await screen.findByText('Ingredient already exists.')).toBeInTheDocument();
  });

  it('displays error message for unsupported media type (negative amount)', async () => {
    IngredientService.addIngredient.mockRejectedValue({ response: { status: 415 } });

    render(<AddIngredientComponent />);
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Sugar' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '100' } });
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));

    expect(await screen.findByText('Amount must be a positive number.')).toBeInTheDocument();
  });

  it('displays generic error message for other API errors', async () => {
    IngredientService.addIngredient.mockRejectedValue(new Error('Network Error'));

    render(<AddIngredientComponent />);
    fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Sugar' } });
    fireEvent.change(screen.getByLabelText(/Amount/i), { target: { value: '100' } });
    fireEvent.click(screen.getByRole('button', { name: /Add Ingredient/i }));

    expect(await screen.findByText('Failed to add ingredient. Please try again.')).toBeInTheDocument();
  });
});
