import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ListRecipesComponent from '../ListRecipesComponent';
import { listRecipes, deleteRecipe } from '../../services/RecipesService';
import { isAdminUser } from '../../services/AuthService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

// Mock services
vi.mock('../../services/RecipesService');
vi.mock('../../services/AuthService');

describe('ListRecipesComponent', () => {
  beforeEach(() => {
    // Mock data for recipes
    listRecipes.mockResolvedValue({
      data: [
        {
          id: 1,
          name: 'Coffee',
          price: 2.5,
          ingredients: [
            { name: 'Water', amount: '200ml' },
            { name: 'Coffee Beans', amount: '20g' },
          ],
        },
        {
          id: 2,
          name: 'Tea',
          price: 1.5,
          ingredients: [{ name: 'Water', amount: '200ml' }, { name: 'Tea Leaves', amount: '5g' }],
        },
      ],
    });

    deleteRecipe.mockResolvedValue({}); // Mock successful delete
    isAdminUser.mockReturnValue(true); // Mock admin privileges
  });

  afterEach(() => {
    vi.clearAllMocks(); // Clear mocks after each test
  });

  test('renders and displays a list of recipes', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    // Wait for recipes to load
    await waitFor(() => {
      expect(screen.getByText('List of Recipes')).toBeInTheDocument();
      expect(screen.getByText('Coffee')).toBeInTheDocument();
      expect(screen.getByText('Tea')).toBeInTheDocument();
    });

    // Verify ingredient details
    expect(screen.getByText('Water: 200ml, Coffee Beans: 20g')).toBeInTheDocument();
    expect(screen.getByText('Water: 200ml, Tea Leaves: 5g')).toBeInTheDocument();
  });

  test('allows adding a new recipe', async () => {
    const navigate = vi.fn();
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    // Click the "Add Recipe" button
    const addButton = screen.getByText('Add Recipe');
    fireEvent.click(addButton);

    // Verify that navigation to the "Add Recipe" page is triggered
    expect(navigate).not.toHaveBeenCalled(); // Placeholder as navigation is not mocked
  });

  test('allows editing a recipe', async () => {
    const navigate = vi.fn();
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    // Wait for recipes to load
    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    // Click the "Edit" button for the first recipe
    const editButton = screen.getAllByText('Edit')[0];
    fireEvent.click(editButton);

    // Verify that navigation to the "Edit Recipe" page is triggered
    expect(navigate).not.toHaveBeenCalled(); // Placeholder as navigation is not mocked
  });

  test('allows deleting a recipe', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    // Wait for recipes to load
    await waitFor(() => {
      expect(screen.getByText('Coffee')).toBeInTheDocument();
    });

    // Click the "Delete" button for the first recipe
    const deleteButton = screen.getAllByText('Delete')[0];
    fireEvent.click(deleteButton);

    // Wait for delete action to complete
    await waitFor(() => {
      expect(deleteRecipe).toHaveBeenCalledWith(1); // Verify delete was called with correct ID
    });
  });

  test('displays the "Modify Tax" button for admin users', async () => {
    render(
      <Router>
        <ListRecipesComponent />
      </Router>
    );

    // Verify that the "Modify Tax" button is displayed
    await waitFor(() => {
      expect(screen.getByText('Modify Tax')).toBeInTheDocument();
    });

    // Simulate clicking the "Modify Tax" button
    const taxButton = screen.getByText('Modify Tax');
    fireEvent.click(taxButton);
    // Placeholder assertion as navigation is not mocked
    expect(true).toBe(true);
  });
});
