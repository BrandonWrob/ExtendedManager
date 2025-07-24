// src/components/__tests__/CreateOrderComponent.test.jsx

import React from 'react';
import { render, screen, waitFor, within, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';

// **1. Mock the Service Modules Before Importing the Component**
vi.mock('../../services/RecipesService', () => ({
  listRecipes: vi.fn(),
}));

vi.mock('../../services/CreateOrderService', () => ({
  makeOrder: vi.fn(),
}));

vi.mock('../../services/TaxService', () => ({
  getTaxRate: vi.fn(),
  calculateTax: vi.fn(),
}));

vi.mock('../../services/HistoryService', () => ({
  createOrderHistory: vi.fn(),
}));

// **2. Mock useNavigate from react-router-dom before importing the component**
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// **3. Now import the component and other necessary modules**
import CreateOrderComponent from '../CreateOrderComponent';
import { listRecipes } from '../../services/RecipesService';
import { makeOrder } from '../../services/CreateOrderService';
import { getTaxRate, calculateTax } from '../../services/TaxService';
import { createOrderHistory } from '../../services/HistoryService';

describe('CreateOrderComponent', () => {
  beforeEach(() => {
    // **4. Mock listRecipes to Return Sample Data**
    listRecipes.mockResolvedValue({
      data: [
        { id: 1, name: 'Espresso', price: 3.5 },
        { id: 2, name: 'Latte', price: 4.0 },
      ],
    });

    // **5. Mock getTaxRate to Return a Sample Tax Rate**
    getTaxRate.mockResolvedValue({
      data: { rate: 0.08 }, // 8% tax
    });

    // **6. Mock calculateTax to Return a Calculated Tax Amount**
    calculateTax.mockImplementation((subtotal) => {
      return Promise.resolve(subtotal * 0.08); // 8% tax
    });

    // **7. Mock createOrderHistory to Resolve Successfully**
    createOrderHistory.mockResolvedValue({
      data: { success: true },
    });
  });

  afterEach(() => {
    // **8. Reset All Mocks After Each Test**
    vi.resetAllMocks();
  });

  beforeAll(() => {
    // **9. Suppress Specific React Router Warnings**
    vi.spyOn(console, 'warn').mockImplementation((msg) => {
      if (
        !msg.includes('React Router Future Flag Warning') &&
        !msg.includes('Relative route resolution within Splat routes is changing')
      ) {
        console.warn(msg);
      }
    });
  });

  afterAll(() => {
    console.warn.mockRestore();
  });

  test('renders the create order form', async () => {
    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **10. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **11. Check for Presence of "Available Recipes" and Recipes**
    expect(screen.getByText('Available Recipes')).toBeInTheDocument();
    expect(screen.getByText('Espresso')).toBeInTheDocument();
    expect(screen.getByText('Latte')).toBeInTheDocument();

    // **12. Check for "Create Order" Button**
    expect(screen.getByRole('button', { name: /Create Order/i })).toBeInTheDocument();
  });

  test('successfully creates an order and redirects', async () => {
    const mockOrder = { id: 1 };
    makeOrder.mockResolvedValueOnce({ data: mockOrder });
	createOrderHistory.mockResolvedValueOnce();

    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **13. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **14. Find the "Espresso" Row**
    const espressoRow = screen.getByText('Espresso').closest('tr');
    expect(espressoRow).toBeInTheDocument();

    // **15. Find and Update Quantity Input**
    const quantityInput = within(espressoRow).getByRole('spinbutton');

    // **16. Set Quantity to '2' Using fireEvent.change**
    fireEvent.change(quantityInput, { target: { value: '2' } });
    expect(quantityInput).toHaveValue(2);

    // **17. Click "Add to Cart" Button**
    const addToCartButton = within(espressoRow).getByRole('button', { name: /Add to Cart/i });
    await userEvent.click(addToCartButton);

    // **18. Select a Tip Option (e.g., 15%)**
    const tipButton = screen.getByRole('button', { name: /15%/i });
    await userEvent.click(tipButton);

    // **19. Click "Create Order" Button in Cart**
    const createOrderButton = screen.getByRole('button', { name: /Create Order/i });
    await userEvent.click(createOrderButton);

    // **20. Assert makeOrder was Called with Correct Data**
    expect(makeOrder).toHaveBeenCalledWith({
      fulfilled: false,
      recipes: [
        { id: 1, name: 'Espresso', price: 3.5, amount: 2 },
      ],
    });
	
	// **20. Wait for createOrderHistory to be Called**
	await waitFor(() => expect(createOrderHistory).toHaveBeenCalledTimes(1));

    // **21. Assert createOrderHistory was Called with Correct Data**
    expect(createOrderHistory).toHaveBeenCalledWith(mockOrder);

    // **22. Assert Navigation to Confirmation Page**
    expect(mockNavigate).toHaveBeenCalledWith(`/order-confirmation/${mockOrder.id}`);
  });

  test('displays error message when order creation fails', async () => {
    makeOrder.mockRejectedValueOnce(new Error('API Error'));

    // **A. Spy on console.error Before Rendering**
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **B. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **C. Find the "Espresso" Row**
    const espressoRow = screen.getByText('Espresso').closest('tr');
    expect(espressoRow).toBeInTheDocument();

    // **D. Find and Update Quantity Input**
    const quantityInput = within(espressoRow).getByRole('spinbutton');

    // **E. Set Quantity to '2' Using fireEvent.change**
    fireEvent.change(quantityInput, { target: { value: '2' } });
    expect(quantityInput).toHaveValue(2);

    // **F. Click "Add to Cart" Button**
    const addToCartButton = within(espressoRow).getByRole('button', { name: /Add to Cart/i });
    await userEvent.click(addToCartButton);

    // **G. Select a Tip Option (e.g., 15%)**
    const tipButton = screen.getByRole('button', { name: /15%/i });
    await userEvent.click(tipButton);

    // **H. Click "Create Order" Button in Cart**
    const createOrderButton = screen.getByRole('button', { name: /Create Order/i });
    await userEvent.click(createOrderButton);

    // **I. Assert Error Message is Displayed**
    await waitFor(() => {
      expect(screen.getByText('Error creating order. Please try again.')).toBeInTheDocument();
    });

    // **J. Assert makeOrder was Called with Correct Data**
    expect(makeOrder).toHaveBeenCalledWith({
      fulfilled: false,
      recipes: [
        { id: 1, name: 'Espresso', price: 3.5, amount: 2 },
      ],
    });

    // **K. Restore console.error**
    consoleErrorSpy.mockRestore();
  });

  test('displays validation errors when required fields are empty', async () => {
    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **A. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **B. Click "Create Order" Button Without Adding Items**
    const createOrderButton = screen.getByRole('button', { name: /Create Order/i });
    await userEvent.click(createOrderButton);

    // **C. Assert Validation Error Messages**
    expect(screen.getByText('No item selected to order!')).toBeInTheDocument();
  });

  test('displays validation error when quantity is zero', async () => {
    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **A. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **B. Find the "Espresso" Row**
    const espressoRow = screen.getByText('Espresso').closest('tr');
    expect(espressoRow).toBeInTheDocument();

    // **C. Find and Set Quantity to Zero Using fireEvent.change**
    const quantityInput = within(espressoRow).getByRole('spinbutton');
    fireEvent.change(quantityInput, { target: { value: '0' } });
    expect(quantityInput).toHaveValue(0);

    // **D. Click "Add to Cart" Button**
    const addToCartButton = within(espressoRow).getByRole('button', { name: /Add to Cart/i });
    await userEvent.click(addToCartButton);

    // **E. Click "Create Order" Button in Cart**
    const createOrderButton = screen.getByRole('button', { name: /Create Order/i });
    await userEvent.click(createOrderButton);

    // **F. Assert Validation Error Message**
    expect(screen.getByText('Invalid tip option!')).toBeInTheDocument();
  });

  test('resets form fields after successful submission', async () => {
    const mockOrder = { id: 1 };
    makeOrder.mockResolvedValueOnce({ data: mockOrder });
	createOrderHistory.mockResolvedValueOnce();

    render(
      <MemoryRouter>
        <CreateOrderComponent />
      </MemoryRouter>
    );

    // **A. Wait for listRecipes and getTaxRate to be Called**
    await waitFor(() => {
      expect(listRecipes).toHaveBeenCalledTimes(1);
      expect(getTaxRate).toHaveBeenCalledTimes(1);
    });

    // **B. Find the "Espresso" Row**
    const espressoRow = screen.getByText('Espresso').closest('tr');
    expect(espressoRow).toBeInTheDocument();

    // **C. Find and Update Quantity Input**
    const quantityInput = within(espressoRow).getByRole('spinbutton');

    // **D. Set Quantity to '2' Using fireEvent.change**
    fireEvent.change(quantityInput, { target: { value: '2' } });
    expect(quantityInput).toHaveValue(2);

    // **E. Click "Add to Cart" Button**
    const addToCartButton = within(espressoRow).getByRole('button', { name: /Add to Cart/i });
    await userEvent.click(addToCartButton);

    // **F. Select a Tip Option (e.g., 15%)**
    const tipButton = screen.getByRole('button', { name: /15%/i });
    await userEvent.click(tipButton);

    // **G. Click "Create Order" Button in Cart**
    const createOrderButton = screen.getByRole('button', { name: /Create Order/i });
    await userEvent.click(createOrderButton);

    // **H. Assert makeOrder was Called with Correct Data**
    expect(makeOrder).toHaveBeenCalledWith({
      fulfilled: false,
      recipes: [
        { id: 1, name: 'Espresso', price: 3.5, amount: 2 },
      ],
    });
	
	// **46. Wait for createOrderHistory to be Called**
	await waitFor(() => expect(createOrderHistory).toHaveBeenCalledTimes(1));

    // **I. Assert createOrderHistory was Called with Correct Data**
    expect(createOrderHistory).toHaveBeenCalledWith(mockOrder);

    // **J. Assert Navigation to Confirmation Page**
    expect(mockNavigate).toHaveBeenCalledWith(`/order-confirmation/${mockOrder.id}`);

    // **K. Optionally, Check if Cart is Reset**
    // Since the component navigates away after order creation, the cart should be cleared.
    // However, in tests, since we navigate within the same render, you might not see the reset.
    // To verify, you can check if the cart is empty by expecting "No items added to cart." to be in the document.
    await waitFor(() => {
      expect(screen.queryByText('No items added to cart.')).toBeInTheDocument();
    });
  });
});
