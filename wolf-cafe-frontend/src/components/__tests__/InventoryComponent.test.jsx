// src/components/__tests__/InventoryComponent.test.jsx
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import InventoryComponent from '../InventoryComponent';
import * as InventoryService from '../../services/InventoryService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

vi.mock('../../services/InventoryService');

describe('InventoryComponent', () => {
    const mockInventory = {
        ingredients: [
            { id: 1, name: 'Water', amount: 500 },
            { id: 2, name: 'Coffee Beans', amount: 300 }
        ]
    };

    beforeEach(() => {
        InventoryService.getInventory.mockResolvedValue({ data: mockInventory });
        InventoryService.updateInventory.mockResolvedValue({ data: mockInventory });
    });

    afterEach(() => {
        vi.clearAllMocks();
    });

    test('renders the component and loads inventory data', async () => {
        render(
            <Router>
                <InventoryComponent />
            </Router>
        );

        await waitFor(() => {
            expect(screen.getByText('Inventory')).toBeInTheDocument();
            expect(screen.getByText('Water')).toBeInTheDocument();
            expect(screen.getByText('Coffee Beans')).toBeInTheDocument();
            expect(screen.getByDisplayValue('500')).toBeInTheDocument();
            expect(screen.getByDisplayValue('300')).toBeInTheDocument();
        });
    });

    test('validates positive integers and prevents negative inputs', async () => {
        render(
            <Router>
                <InventoryComponent />
            </Router>
        );

        await waitFor(() => {
            expect(screen.getByText('Water')).toBeInTheDocument();
        });

        // Updated placeholder from 'Add Amount' to '0'
        const addAmountInputs = screen.getAllByPlaceholderText('0');
        const waterAddInput = addAmountInputs[0]; // Assuming first input is for "Water"

        // Try entering a negative number
        fireEvent.change(waterAddInput, { target: { value: '-10' } });

        fireEvent.click(screen.getByText('Update Inventory'));

        // Updated expected validation message
        const validationMessage = await screen.findByText('Negative amounts cannot be added.');
        expect(validationMessage).toBeInTheDocument();
    });

    test('displays success message when inventory updates successfully', async () => {
        render(
            <Router>
                <InventoryComponent />
            </Router>
        );

        await waitFor(() => {
            expect(screen.getByText('Water')).toBeInTheDocument();
        });

        const addAmountInputs = screen.getAllByPlaceholderText('0');
        const waterAddInput = addAmountInputs[0];

        fireEvent.change(waterAddInput, { target: { value: '10' } });

        fireEvent.click(screen.getByText('Update Inventory'));

        await waitFor(() => {
            expect(screen.getByText('Inventory updated successfully!')).toBeInTheDocument();
        });
    });

    test('displays error message when inventory update fails', async () => {
        InventoryService.updateInventory.mockRejectedValueOnce(new Error('Failed to update inventory'));

        render(
            <Router>
                <InventoryComponent />
            </Router>
        );

        await waitFor(() => {
            expect(screen.getByText('Water')).toBeInTheDocument();
        });

        const addAmountInputs = screen.getAllByPlaceholderText('0');
        const waterAddInput = addAmountInputs[0];

        fireEvent.change(waterAddInput, { target: { value: '10' } });

        fireEvent.click(screen.getByText('Update Inventory'));

        const errorMessage = await screen.findByText('Failed to update inventory.');
        expect(errorMessage).toBeInTheDocument();
    });
});
