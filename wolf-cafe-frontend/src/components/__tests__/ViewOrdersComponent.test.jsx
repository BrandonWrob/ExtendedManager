// src/components/__tests__/ViewOrdersComponent.test.jsx

import React from 'react';
import { vi } from 'vitest';

// **1. Mock the service functions before importing the component**
vi.mock('../../services/ViewOrdersService', () => ({
  listOrders: vi.fn(),
  fulfillOrder: vi.fn(),
  pickupOrder: vi.fn(),
  viewOrdersStatus: vi.fn(),
}));

// **2. Mock useNavigate before importing the component**
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// **3. Now import the component after setting up mocks**
import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import * as AuthService from '../../services/AuthService';
import ViewOrdersComponent from '../ViewOrdersComponent';
import { listOrders, viewOrdersStatus, fulfillOrder } from '../../services/ViewOrdersService'; // **Imported fulfillOrder**

describe('ViewOrdersComponent', () => {
  beforeEach(() => {
    // **4. Mock AuthService to return true for roles and a logged-in user**
    vi.spyOn(AuthService, 'isAdminUser').mockReturnValue(true);
    vi.spyOn(AuthService, 'isManagerUser').mockReturnValue(true);
    vi.spyOn(AuthService, 'isStaffUser').mockReturnValue(true);
    vi.spyOn(AuthService, 'getLoggedInUser').mockReturnValue({ id: 1, username: 'testuser' }); // Mock logged-in user
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  beforeAll(() => {
    // **5. Suppress React Router Future Flag Warnings**
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

  test('renders the component and fetches orders', async () => {
    const mockOrders = [
      { id: 1, fulfilled: false },
      { id: 2, fulfilled: true },
    ];
    const mockUserOrdersStatus = [
      { id: 1, fulfilled: false },
      { id: 2, fulfilled: true },
    ];

    // **6. Mock the responses**
    listOrders.mockResolvedValueOnce({ data: mockOrders });
    viewOrdersStatus.mockResolvedValueOnce({ data: mockUserOrdersStatus });

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **7. Wait for the orders to be fetched and displayed**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledWith({ id: 1, username: 'testuser' });
    });

    // **8. Check that the orders are displayed**
    expect(screen.getByText('All Orders')).toBeInTheDocument();
    expect(screen.getByText('Order ID')).toBeInTheDocument();

    // **9. Locate all table rows (including header)**
    const rows = screen.getAllByRole('row');
    // There should be header row + 2 order rows
    expect(rows.length).toBe(3);

    // **10. Check content of each order row**

    // **Order 1**
    const order1Row = rows[1]; // first data row
    expect(within(order1Row).getByText('1')).toBeInTheDocument();
    expect(within(order1Row).getByRole('button', { name: /View Order/i })).toBeInTheDocument();
    expect(within(order1Row).getByRole('button', { name: 'Fulfill' })).toBeInTheDocument();
    expect(within(order1Row).getByRole('button', { name: 'Pick Up' })).toBeInTheDocument();

    // **Order 2**
    const order2Row = rows[2]; // second data row
    expect(within(order2Row).getByText('2')).toBeInTheDocument();
    expect(within(order2Row).getByRole('button', { name: /View Order/i })).toBeInTheDocument();
    const fulfilledButton = within(order2Row).getByRole('button', { name: 'Fulfilled' });
    expect(fulfilledButton).toBeDisabled();
    expect(within(order2Row).getByRole('button', { name: 'Pick Up' })).toBeInTheDocument();

    // **11. Check user orders status display**
    const userOrdersText = screen.getByText('Your Order IDs:');
    const userOrdersDiv = userOrdersText.closest('div');
    expect(userOrdersDiv).toBeInTheDocument();
    expect(within(userOrdersDiv).getByText('1,')).toBeInTheDocument();
    expect(within(userOrdersDiv).getByText('2')).toBeInTheDocument();
  });

  test('handles clicking "View Order" and navigates correctly', async () => {
    // **a. Mock orders data**
    const mockOrders = [{ id: 1, fulfilled: false }];
    const mockUserOrdersStatus = [{ id: 1, fulfilled: false }];

    // **b. Mock service calls**
    listOrders.mockResolvedValueOnce({ data: mockOrders });
    viewOrdersStatus.mockResolvedValueOnce({ data: mockUserOrdersStatus });

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **c. Wait for orders to be fetched and rendered**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledTimes(1);
    });

    // **d. Locate all table rows (excluding header)**
    const rows = screen.getAllByRole('row');
    expect(rows.length).toBe(2); // header + 1 data row

    const orderRow = rows[1]; // first data row
    expect(within(orderRow).getByText('1')).toBeInTheDocument();

    // **e. Find the "View Order" button within the row**
    const viewButton = within(orderRow).getByRole('button', { name: /View Order/i });
    expect(viewButton).toBeInTheDocument();

    // **f. Simulate clicking the "View Order" button**
    await userEvent.click(viewButton);

    // **g. Check that navigation occurred correctly**
    expect(mockNavigate).toHaveBeenCalledWith('/orders/1');
  });

  test('handles fulfilling an order', async () => {
    // **a. Mock orders data**
    const mockOrders = [{ id: 1, fulfilled: false }];
    const mockUserOrdersStatus = [{ id: 1, fulfilled: false }];

    // **b. Mock service calls**
    listOrders.mockResolvedValueOnce({ data: mockOrders });
    viewOrdersStatus.mockResolvedValueOnce({ data: mockUserOrdersStatus });
    fulfillOrder.mockResolvedValueOnce({}); // Assume API returns empty object on success

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **c. Wait for orders to be fetched and rendered**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledTimes(1);
    });

    // **d. Locate the order row**
    const rows = screen.getAllByRole('row');
    expect(rows.length).toBe(2); // header + 1 data row

    const orderRow = rows[1]; // first data row
    expect(within(orderRow).getByText('1')).toBeInTheDocument();

    // **e. Find the "Fulfill" button within the row**
    const fulfillButton = within(orderRow).getByRole('button', { name: 'Fulfill' });
    expect(fulfillButton).toBeInTheDocument();

    // **f. Simulate clicking the "Fulfill" button**
    await userEvent.click(fulfillButton);

    // **g. Check that fulfillOrder was called with the correct order ID**
    expect(fulfillOrder).toHaveBeenCalledWith(1);

    // **h. Optionally, verify UI changes (e.g., button disabled or label changed)**
    // This depends on how the component updates after fulfilling
  });

  test('does not allow fulfilling an already fulfilled order', async () => {
    // **a. Mock orders data**
    const mockOrders = [{ id: 1, fulfilled: true }];
    const mockUserOrdersStatus = [{ id: 1, fulfilled: true }];

    // **b. Mock service calls**
    listOrders.mockResolvedValueOnce({ data: mockOrders });
    viewOrdersStatus.mockResolvedValueOnce({ data: mockUserOrdersStatus });

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **c. Wait for orders to be fetched and rendered**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledWith({ id: 1, username: 'testuser' });
    });

    // **d. Locate the order row**
    const rows = screen.getAllByRole('row');
    expect(rows.length).toBe(2); // header + 1 data row

    const orderRow = rows[1]; // first data row
    expect(within(orderRow).getByText('1')).toBeInTheDocument();

    // **e. Find the "Fulfilled" button within the row**
    const fulfilledButton = within(orderRow).getByRole('button', { name: 'Fulfilled' });
    expect(fulfilledButton).toBeInTheDocument();

    // **f. Check that the "Fulfilled" button is disabled**
    expect(fulfilledButton).toBeDisabled();

    // **g. Attempt to click the disabled "Fulfilled" button**
    await userEvent.click(fulfilledButton);

    // **h. Verify that fulfillOrder was not called**
    expect(fulfillOrder).not.toHaveBeenCalled();
  });

  test('handles errors when fulfilling an order', async () => {
    // **a. Mock orders data**
    const mockOrders = [{ id: 1, fulfilled: false }];
    const mockUserOrdersStatus = [{ id: 1, fulfilled: false }];

    // **b. Mock service calls**
    listOrders.mockResolvedValueOnce({ data: mockOrders });
    viewOrdersStatus.mockResolvedValueOnce({ data: mockUserOrdersStatus });
    fulfillOrder.mockRejectedValueOnce(new Error('Network error'));

    // **c. Spy on console.error to suppress error logs and assert on them if needed**
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **d. Wait for orders to be fetched and rendered**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledTimes(1);
    });

    // **e. Locate the order row**
    const rows = screen.getAllByRole('row');
    expect(rows.length).toBe(2); // header + 1 data row

    const orderRow = rows[1]; // first data row
    expect(within(orderRow).getByText('1')).toBeInTheDocument();

    // **f. Find the "Fulfill" button within the row**
    const fulfillButton = within(orderRow).getByRole('button', { name: 'Fulfill' });
    expect(fulfillButton).toBeInTheDocument();

    // **g. Simulate clicking the "Fulfill" button**
    await userEvent.click(fulfillButton);

    // **h. Wait for fulfillOrder to be called and rejected**
    await waitFor(() => {
      expect(fulfillOrder).toHaveBeenCalledWith(1);
    });

    // **i. Check that an error was logged**
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error fulfilling order:', expect.any(Error));

    // **j. Optionally, verify UI remains unchanged (e.g., button enabled)**
    expect(fulfillButton).not.toBeDisabled();

    // **k. Restore console.error**
    consoleErrorSpy.mockRestore();
  });

  test('handles errors when fetching orders', async () => {
    // **a. Spy on console.error to suppress error logs and assert on them if needed**
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    // **b. Mock service calls to reject listOrders**
    listOrders.mockRejectedValueOnce(new Error('Network error'));
    viewOrdersStatus.mockResolvedValueOnce({ data: [] }); // Even if viewOrdersStatus resolves

    render(
      <MemoryRouter>
        <ViewOrdersComponent />
      </MemoryRouter>
    );

    // **c. Wait for listOrders to be called and rejected**
    await waitFor(() => {
      expect(listOrders).toHaveBeenCalledTimes(1);
      expect(viewOrdersStatus).toHaveBeenCalledWith({ id: 1, username: 'testuser' });
    });

    // **d. Check that an error was logged**
    expect(consoleErrorSpy).toHaveBeenCalledWith('Error fetching orders:', expect.any(Error));

    // **e. Check that "No Current Orders" message is displayed**
    expect(screen.getByText('No Current Orders')).toBeInTheDocument();

    // **f. Restore console.error**
    consoleErrorSpy.mockRestore();
  });
});
