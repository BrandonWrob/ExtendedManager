// src/components/__tests__/HistoryDetailsComponent.test.jsx

import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import HistoryDetailsComponent from '../HistoryDetailsComponent';
import { getHistoryById } from '../../services/HistoryService';
import { isAdminUser } from '../../services/AuthService';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// **1. Mock the service functions**
vi.mock('../../services/HistoryService', () => ({
  getHistoryById: vi.fn(),
}));

vi.mock('../../services/AuthService', () => ({
  isAdminUser: vi.fn(),
}));

// **2. Mock useNavigate from react-router-dom**
const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('HistoryDetailsComponent', () => {
  beforeEach(() => {
    // **3. Reset mocks before each test**
    vi.resetAllMocks();
  });

  // **4. Helper function to render the component with necessary providers**
  const renderComponent = (route) => {
    render(
      <MemoryRouter initialEntries={[route]}>
        <Routes>
          <Route path="/history/:id" element={<HistoryDetailsComponent />} />
        </Routes>
      </MemoryRouter>
    );
  };

  test('renders correctly for admin user with single recipe', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getHistoryById.mockResolvedValueOnce({
      data: {
        recipesInOrder: 'Espresso: 2', // String format
        ingredientsUsed: 'Water: 200, Coffee Beans: 20', // String format
        total: 5.00,
      },
    });

    // **B. Render the component with a specific route**
    renderComponent('/history/123');

    // **C. Wait for data fetching to complete**
    await waitFor(() => {
      expect(getHistoryById).toHaveBeenCalledWith('123');
    });

    // **D. Assert that the order ID is displayed**
    expect(screen.getByText('Order ID: 123')).toBeInTheDocument();

    // **E. Assert that the recipe name is displayed without a dropdown (single recipe)**
    expect(screen.getByText('Espresso')).toBeInTheDocument();

    // **F. Assert that the amount ordered is displayed correctly**
    expect(screen.getByText('2')).toBeInTheDocument();

    // **G. Assert that the ingredients are displayed correctly**
    expect(screen.getByText('Water: 200, Coffee Beans: 20')).toBeInTheDocument();

    // **H. Assert that the total cost is displayed correctly**
    expect(screen.getByText('Total: $5.00')).toBeInTheDocument();

    // **I. Assert that the "Go Back" button is present**
    expect(screen.getByRole('button', { name: /Go Back/i })).toBeInTheDocument();
  });

  test('renders correctly for admin user with multiple recipes and interacts with dropdown', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getHistoryById.mockResolvedValueOnce({
      data: {
        recipesInOrder: 'Latte: 3, Cappuccino: 1', // String format
        ingredientsUsed: 'Milk: 300, Coffee Beans: 50', // String format
        total: 10.50,
      },
    });

    // **B. Render the component with a specific route**
    renderComponent('/history/456');

    // **C. Wait for data fetching to complete**
    await waitFor(() => {
      expect(getHistoryById).toHaveBeenCalledWith('456');
    });

    // **D. Assert that the order ID is displayed**
    expect(screen.getByText('Order ID: 456')).toBeInTheDocument();

    // **E. Find the dropdown (select element) using the accessible name**
    const dropdown = screen.getByRole('combobox', { name: /Select Recipe/i });
    expect(dropdown).toBeInTheDocument();

    // **F. Assert that all recipes are present in the dropdown**
    expect(within(dropdown).getByRole('option', { name: 'Select' })).toBeInTheDocument();
    expect(within(dropdown).getByRole('option', { name: 'Latte' })).toBeInTheDocument();
    expect(within(dropdown).getByRole('option', { name: 'Cappuccino' })).toBeInTheDocument();

    // **G. Initially, no amount or ingredients should be displayed**
    expect(screen.queryByText('3')).not.toBeInTheDocument();
    expect(screen.queryByText('Milk: 300, Coffee Beans: 50')).not.toBeInTheDocument();

    // **H. Select the first recipe from the dropdown (Latte)**
    await userEvent.selectOptions(dropdown, 'Latte');

    // **I. Assert that the amount and ingredients are displayed correctly for the selected recipe**
    expect(screen.getByText('3')).toBeInTheDocument();
    expect(screen.getByText('Milk: 300, Coffee Beans: 50')).toBeInTheDocument();

    // **J. Assert that the total cost remains the same**
    expect(screen.getByText('Total: $10.50')).toBeInTheDocument();
  });

  test('renders correctly for regular user with multiple recipes', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(false);
    getHistoryById.mockResolvedValueOnce({
      data: {
        recipesInOrder: 'Americano: 2, Mocha: 1', // String format
        ingredientsUsed: 'Water: 400, Coffee Beans: 60', // String format
        total: 8.75,
      },
    });

    // **B. Render the component with a specific route**
    renderComponent('/history/789');

    // **C. Wait for data fetching to complete**
    await waitFor(() => {
      expect(getHistoryById).toHaveBeenCalledWith('789');
    });

    // **D. Assert that the order ID is displayed**
    expect(screen.getByText('Order ID: 789')).toBeInTheDocument();

    // **E. Assert that all recipes are displayed without a dropdown**
    expect(screen.getByText('Americano')).toBeInTheDocument();
    expect(screen.getByText('Mocha')).toBeInTheDocument();

    // **F. Assert that the amounts ordered are displayed correctly**
    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('1')).toBeInTheDocument();

    // **G. Assert that the ingredients are NOT displayed for regular users**
    expect(screen.queryByText('Water: 400, Coffee Beans: 60')).not.toBeInTheDocument();

    // **H. Assert that the total cost is displayed correctly (Adjusted based on user role)**
    expect(screen.getByText('Total: $0.00')).toBeInTheDocument(); // Adjust this based on actual component logic

    // **I. Assert that the "Go Back" button is present**
    expect(screen.getByRole('button', { name: /Go Back/i })).toBeInTheDocument();
  });

  test('navigates back when "Go Back" button is clicked', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getHistoryById.mockResolvedValueOnce({
      data: {
        recipesInOrder: 'Espresso: 2', // String format
        ingredientsUsed: 'Water: 200, Coffee Beans: 20', // String format
        total: 5.00,
      },
    });

    // **B. Render the component with a specific route**
    renderComponent('/history/123');

    // **C. Wait for data fetching to complete**
    await waitFor(() => {
      expect(getHistoryById).toHaveBeenCalledWith('123');
    });

    // **D. Find and click the "Go Back" button**
    const goBackButton = screen.getByRole('button', { name: /Go Back/i });
    expect(goBackButton).toBeInTheDocument();

    await userEvent.click(goBackButton);

    // **E. Assert that navigate(-1) was called**
    expect(mockNavigate).toHaveBeenCalledWith(-1);
  });
});
