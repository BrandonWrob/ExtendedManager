import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ListRecipesComponent from '../MakeRecipeComponent';
import { listRecipes } from '../../services/RecipesService';
import { makeRecipe } from '../../services/MakeRecipeService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

vi.mock('../../services/RecipesService');
vi.mock('../../services/MakeRecipeService');

describe('ListRecipesComponent', () => {
  beforeEach(() => {
    listRecipes.mockResolvedValue({
      data: [
        { id: 1, name: 'Coffee', price: 2.5 },
        { id: 2, name: 'Tea', price: 1.5 },
      ],
    });
    makeRecipe.mockResolvedValue({ data: 0.5 }); // Mock change response
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test('renders the list of recipes and amount paid input', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('List of Recipes')).toBeInTheDocument();
      expect(screen.getByText('Coffee')).toBeInTheDocument();
      expect(screen.getByText('Tea')).toBeInTheDocument();
      expect(screen.getByPlaceholderText('How much are you paying?')).toBeInTheDocument();
    });
  });

  test('displays validation error for empty amount paid', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    const makeRecipeButton = screen.getAllByText('Make Recipe')[0];
    fireEvent.click(makeRecipeButton);

    await waitFor(() => {
      expect(screen.getByText('Please enter valid payment.')).toBeInTheDocument();
    });
  });

  test('displays validation error for negative amount paid', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('How much are you paying?'), {
      target: { value: '-5' },
    });

    const makeRecipeButton = screen.getAllByText('Make Recipe')[0];
    fireEvent.click(makeRecipeButton);

    await waitFor(() => {
      expect(screen.getByText('Amount paid must be a positive integer.')).toBeInTheDocument();
    });
  });

  test('successfully crafts a recipe and displays change', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('How much are you paying?'), {
      target: { value: '3' },
    });

    const makeRecipeButton = screen.getAllByText('Make Recipe')[0];
    fireEvent.click(makeRecipeButton);

    await waitFor(() => {
      expect(screen.getByText('Your order is ready!')).toBeInTheDocument();
      expect(screen.getByText('Change: 0.5')).toBeInTheDocument();
    });
  });

  test('displays error for insufficient funds', async () => {
    makeRecipe.mockRejectedValueOnce({ response: { status: 409 } });

    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('How much are you paying?'), {
      target: { value: '1' },
    });

    const makeRecipeButton = screen.getAllByText('Make Recipe')[0];
    fireEvent.click(makeRecipeButton);

    await waitFor(() => {
      expect(screen.getByText('Insufficient funds to pay.')).toBeInTheDocument();
      expect(screen.getByText('Change: 1')).toBeInTheDocument();
    });
  });

  test('displays error for insufficient inventory', async () => {
    makeRecipe.mockRejectedValueOnce({ response: { status: 400 } });

    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    fireEvent.change(screen.getByPlaceholderText('How much are you paying?'), {
      target: { value: '3' },
    });

    const makeRecipeButton = screen.getAllByText('Make Recipe')[0];
    fireEvent.click(makeRecipeButton);

    await waitFor(() => {
      expect(screen.getByText('Insufficient inventory.')).toBeInTheDocument();
      expect(screen.getByText('Change: 3')).toBeInTheDocument();
    });
  });
});
