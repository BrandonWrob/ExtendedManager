import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import EditRecipeComponent from '../EditRecipeComponent';
import * as RecipesService from '../../services/RecipesService';
import * as InventoryService from '../../services/InventoryService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

vi.mock('../../services/RecipesService');
vi.mock('../../services/InventoryService');

describe('EditRecipeComponent', () => {
    const mockRecipe = {
        id: 1,
        name: 'Coffee',
        price: 2.5,
        ingredients: [
            { name: 'Water', amount: '200' },
            { name: 'Coffee Beans', amount: '20' }
        ],
    };

    const mockInventory = {
        ingredients: [
            { name: 'Water' },
            { name: 'Coffee Beans' },
            { name: 'Milk' }
        ]
    };

    beforeEach(() => {
        RecipesService.getRecipe.mockResolvedValue({ data: mockRecipe });
        InventoryService.getInventory.mockResolvedValue({ data: mockInventory });
    });

    afterEach(() => {
        vi.clearAllMocks();
    });

	test('renders the component and loads recipe data', async () => {
	    render(
	        <Router>
	            <EditRecipeComponent />
	        </Router>
	    );

	    await waitFor(() => {
	        expect(screen.getByDisplayValue('Coffee')).toBeInTheDocument();
	        expect(screen.getByDisplayValue('2.5')).toBeInTheDocument();

	        // Use getAllByText to handle multiple "Water" instances
	        const waterOptions = screen.getAllByText('Water');
	        expect(waterOptions.length).toBeGreaterThan(0);

	        // Verify the ingredient amount
	        expect(screen.getByDisplayValue('200')).toBeInTheDocument();
	        expect(screen.getByDisplayValue('20')).toBeInTheDocument();
	    });
	});

    test('updates ingredient amount', async () => {
        render(
            <Router>
                <EditRecipeComponent />
            </Router>
        );

        await waitFor(() => screen.getByDisplayValue('200'));

        // Simulate changing ingredient amount
        const waterAmountInput = screen.getByDisplayValue('200');
        fireEvent.change(waterAmountInput, { target: { value: '300' } });

        expect(waterAmountInput.value).toBe('300');
    });

    test('shows validation error for invalid price', async () => {
        render(
            <Router>
                <EditRecipeComponent />
            </Router>
        );

        await waitFor(() => screen.getByDisplayValue('2.5'));

        const priceInput = screen.getByPlaceholderText('Enter Recipe Price');
        fireEvent.change(priceInput, { target: { value: '-5' } });

        const saveButton = screen.getByText('Save');
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(screen.getByText('Enter a positive number for the price.')).toBeInTheDocument();
        });
    });

    test('shows validation error for empty ingredient name', async () => {
        render(
            <Router>
                <EditRecipeComponent />
            </Router>
        );

        await waitFor(() => screen.getByDisplayValue('200'));

        // Set empty ingredient name
        const ingredientSelect = screen.getAllByRole('combobox')[0];
        fireEvent.change(ingredientSelect, { target: { value: '' } });

        const saveButton = screen.getByText('Save');
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(screen.getByText('Please enter valid ingredients with positive amounts.')).toBeInTheDocument();
        });
    });

    test('calls updateRecipe on valid form submission', async () => {
        const updateMock = vi.spyOn(RecipesService, 'updateRecipe').mockResolvedValue({});

        render(
            <Router>
                <EditRecipeComponent />
            </Router>
        );

        await waitFor(() => screen.getByDisplayValue('Coffee'));

        const saveButton = screen.getByText('Save');
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(updateMock).toHaveBeenCalledWith({
                id: 1,
                name: 'Coffee',
                price: '2.5',
                ingredients: [
                    { name: 'Water', amount: '200' },
                    { name: 'Coffee Beans', amount: '20' }
                ]
            });
        });
    });

    test('displays error when update fails', async () => {
        RecipesService.updateRecipe.mockRejectedValue({
            response: { status: 409 }
        });

        render(
            <Router>
                <EditRecipeComponent />
            </Router>
        );

        await waitFor(() => screen.getByDisplayValue('Coffee'));

        const saveButton = screen.getByText('Save');
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(screen.getByText('Duplicate recipe name.')).toBeInTheDocument();
        });
    });
});
