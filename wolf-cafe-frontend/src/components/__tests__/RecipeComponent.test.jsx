// RecipeComponent.test.jsx
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import RecipeComponent from '../RecipeComponent';
import { createRecipe } from '../../services/RecipesService';
import { getInventory } from '../../services/InventoryService';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// Mock the createRecipe function
vi.mock('../../services/RecipesService', () => ({
  createRecipe: vi.fn(),
}));

// Mock the getInventory function
vi.mock('../../services/InventoryService', () => ({
  getInventory: vi.fn(),
}));

// Mock useNavigate
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

describe('RecipeComponent', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  test('renders the component and fetches inventory', async () => {
    const mockInventory = {
      data: {
        ingredients: [
          { name: 'Sugar' },
          { name: 'Flour' },
          { name: 'Eggs' },
        ],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Check that the heading is displayed
    expect(screen.getByText('Add Recipe')).toBeInTheDocument();

    // Wait for the inventory ingredients to be fetched and rendered
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Get the first select (ingredient select)
    const ingredientSelect = screen.getAllByRole('combobox')[0];

    // Get the options from the select
    const options = Array.from(ingredientSelect.options).map(
      (option) => option.textContent
    );

    expect(options).toEqual(
      expect.arrayContaining(['Sugar', 'Flour', 'Eggs'])
    );
  });


  test('validates form inputs and displays errors', async () => {
    const mockInventory = {
      data: {
        ingredients: [
          { name: 'Sugar' },
          { name: 'Flour' },
          { name: 'Eggs' },
        ],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Click on the Submit button without filling any fields
    const submitButton = screen.getByRole('button', { name: /Submit/i });
    userEvent.click(submitButton);

    // Check for validation errors
    expect(await screen.findByText('Name is required.')).toBeInTheDocument();
    expect(
      screen.getByText('Enter a positive number for the price.')
    ).toBeInTheDocument();
    expect(
      screen.getByText('Please select valid ingredients and enter amounts.')
    ).toBeInTheDocument();
  });

  test('adds an ingredient field when "Add Ingredient" button is clicked', async () => {
    const mockInventory = {
      data: {
        ingredients: [{ name: 'Sugar' }],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // There should be one ingredient field by default
    expect(screen.getAllByRole('combobox').length).toBe(1);

    // Click on "Add Ingredient" button
    const addIngredientButton = screen.getByRole('button', {
      name: /Add Ingredient/i,
    });
    await userEvent.click(addIngredientButton);

    // Wait for the new ingredient field to be added
    await waitFor(() => {
      expect(screen.getAllByRole('combobox').length).toBe(2);
    });
  });

  test('removes an ingredient field when "Remove" button is clicked', async () => {
    const mockInventory = {
      data: {
        ingredients: [{ name: 'Sugar' }],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Add a second ingredient field
    const addIngredientButton = screen.getByRole('button', {
      name: /Add Ingredient/i,
    });
    await userEvent.click(addIngredientButton);

    // Wait for the new ingredient field to be added
    await waitFor(() => {
      expect(screen.getAllByRole('combobox').length).toBe(2);
    });

    // Click the "Remove" button on the second ingredient
    const removeButtons = screen.getAllByRole('button', { name: /Remove/i });
    await userEvent.click(removeButtons[0]); // Adjust index if necessary

    // Wait for the ingredient field to be removed
    await waitFor(() => {
      expect(screen.getAllByRole('combobox').length).toBe(1);
    });
  });

  test('submits the form with valid data', async () => {
    const mockInventory = {
      data: {
        ingredients: [{ name: 'Sugar' }, { name: 'Flour' }],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);
    createRecipe.mockResolvedValueOnce({
      data: { message: 'Recipe created successfully' },
    });
    const navigate = vi.fn();
    useNavigate.mockReturnValue(navigate);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Fill in the recipe name
    const nameInput = screen.getByPlaceholderText('Enter Recipe Name');
    await userEvent.type(nameInput, 'Chocolate Cake');

    // Fill in the recipe price
    const priceInput = screen.getByPlaceholderText('Enter Recipe Price');
    await userEvent.type(priceInput, '15.99');

    // Select an ingredient
    const ingredientSelect = screen.getAllByRole('combobox')[0];
    await userEvent.selectOptions(ingredientSelect, 'Sugar');

    // Enter the amount
    const amountInput = screen.getAllByPlaceholderText('Amount')[0];
    await userEvent.type(amountInput, '2');

    // Click on the Submit button
    const submitButton = screen.getByRole('button', { name: /Submit/i });
    userEvent.click(submitButton);

    // Wait for createRecipe to be called
    await waitFor(() => {
      expect(createRecipe).toHaveBeenCalledTimes(1);
    });

    // Check that createRecipe was called with the correct data
    expect(createRecipe).toHaveBeenCalledWith({
      name: 'Chocolate Cake',
      price: '15.99',
      ingredients: [{ name: 'Sugar', amount: '2' }],
    });

    // Check that navigate was called to redirect
    expect(navigate).toHaveBeenCalledWith('/recipes');
  });

  test('displays error message when createRecipe fails', async () => {
    const mockInventory = {
      data: {
        ingredients: [{ name: 'Sugar' }],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);
    const errorResponse = {
      response: {
        status: 409, // Conflict error for duplicate recipe name
      },
    };
    createRecipe.mockRejectedValueOnce(errorResponse);
    const navigate = vi.fn();
    useNavigate.mockReturnValue(navigate);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Fill in the recipe name
    const nameInput = screen.getByPlaceholderText('Enter Recipe Name');
    await userEvent.type(nameInput, 'Chocolate Cake');

    // Fill in the recipe price
    const priceInput = screen.getByPlaceholderText('Enter Recipe Price');
    await userEvent.type(priceInput, '15.99');

    // Select an ingredient
    const ingredientSelect = screen.getAllByRole('combobox')[0];
    await userEvent.selectOptions(ingredientSelect, 'Sugar');

    // Enter the amount
    const amountInput = screen.getAllByPlaceholderText('Amount')[0];
    await userEvent.type(amountInput, '2');

    // Click on the Submit button
    const submitButton = screen.getByRole('button', { name: /Submit/i });
    userEvent.click(submitButton);

    // Wait for error message to appear
    expect(
      await screen.findByText('Duplicate recipe name.')
    ).toBeInTheDocument();
  });

  test('validates ingredient amounts', async () => {
    const mockInventory = {
      data: {
        ingredients: [{ name: 'Sugar' }],
      },
    };

    getInventory.mockResolvedValueOnce(mockInventory);

    render(
      <MemoryRouter>
        <RecipeComponent />
      </MemoryRouter>
    );

    // Wait for inventory to be fetched
    await waitFor(() => {
      expect(getInventory).toHaveBeenCalledTimes(1);
    });

    // Fill in the recipe name
    const nameInput = screen.getByPlaceholderText('Enter Recipe Name');
    await userEvent.type(nameInput, 'Chocolate Cake');

    // Fill in the recipe price
    const priceInput = screen.getByPlaceholderText('Enter Recipe Price');
    await userEvent.type(priceInput, '15.99');

    // Select an ingredient
    const ingredientSelect = screen.getAllByRole('combobox')[0];
    await userEvent.selectOptions(ingredientSelect, 'Sugar');

    // Enter an invalid amount
    const amountInput = screen.getAllByPlaceholderText('Amount')[0];
    await userEvent.type(amountInput, '-2');

    // Click on the Submit button
    const submitButton = screen.getByRole('button', { name: /Submit/i });
    userEvent.click(submitButton);

    // Check for validation error
    expect(
      await screen.findByText(
        'Please select valid ingredients and enter amounts.'
      )
    ).toBeInTheDocument();
  });
});
