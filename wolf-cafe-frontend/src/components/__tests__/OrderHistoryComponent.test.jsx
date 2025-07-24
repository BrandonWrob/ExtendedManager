// src/components/__tests__/OrderHistoryComponent.test.jsx

import React from 'react';
import { render, screen, waitFor, within } from '@testing-library/react';
import OrderHistoryComponent from '../OrderHistoryComponent';
import { getOrderHistory, getUserHistory } from '../../services/HistoryService';
import { isAdminUser, getLoggedInUser } from '../../services/AuthService';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// **Mock the HistoryService functions**
vi.mock('../../services/HistoryService', () => ({
  getOrderHistory: vi.fn(),
  getUserHistory: vi.fn(),
}));

// **Mock the AuthService functions**
vi.mock('../../services/AuthService', () => ({
  isAdminUser: vi.fn(),
  getLoggedInUser: vi.fn(),
}));

// **Mock useNavigate from react-router-dom**
const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('OrderHistoryComponent', () => {
  beforeEach(() => {
    // **Reset all mocks before each test**
    vi.resetAllMocks();
  });

  // **Helper function to render the component with routing**
  const renderComponent = (route = '/history') => {
    render(
      <MemoryRouter initialEntries={[route]}>
        <Routes>
          <Route path="/history/:id" element={<div>Order Details Page</div>} />
          <Route path="/history" element={<OrderHistoryComponent />} />
        </Routes>
      </MemoryRouter>
    );
  };

  test('renders correctly for admin user with orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getOrderHistory.mockResolvedValueOnce({
      data: [
        { id: 'ORD123' },
        { id: 'ORD456' },
      ],
    });

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getOrderHistory was called**
    await waitFor(() => {
      expect(getOrderHistory).toHaveBeenCalledTimes(1);
    });

    // **D. Assert that table headers are displayed**
    expect(screen.getByText('View Order Details')).toBeInTheDocument();

    // **E. Assert that Order IDs are displayed in table cells**
    expect(screen.getByText('ORD123')).toBeInTheDocument();
    expect(screen.getByText('ORD456')).toBeInTheDocument();

    // **F. Assert that "View" buttons are present**
    const viewButtons = screen.getAllByRole('button', { name: /View/i });
    expect(viewButtons).toHaveLength(2);
  });

  test('renders "No Past Orders" for admin user with no orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getOrderHistory.mockResolvedValueOnce({
      data: [],
    });

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getOrderHistory was called**
    await waitFor(() => {
      expect(getOrderHistory).toHaveBeenCalledTimes(1);
    });

    // **D. Assert that "No Past Orders" message is displayed**
    expect(screen.getByText('No Past Orders')).toBeInTheDocument();
  });

  test('renders correctly for regular user with orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(false);
    getLoggedInUser.mockReturnValue('user123');
    getUserHistory.mockResolvedValueOnce({
      data: [
        { id: 'ORD789' },
      ],
    });

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getUserHistory was called with the correct user**
    await waitFor(() => {
      expect(getUserHistory).toHaveBeenCalledWith('user123');
    });

    // **D. Assert that table headers are displayed**
    expect(screen.getByText('View Order Details')).toBeInTheDocument();

    // **E. Assert that Order ID is displayed in table cell**
    expect(screen.getByText('ORD789')).toBeInTheDocument();

    // **F. Assert that "View" button is present**
    const viewButton = screen.getByRole('button', { name: /View/i });
    expect(viewButton).toBeInTheDocument();
  });

  test('renders "No Past Orders" for regular user with no orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(false);
    getLoggedInUser.mockReturnValue('user456');
    getUserHistory.mockResolvedValueOnce({
      data: [],
    });

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getUserHistory was called with the correct user**
    await waitFor(() => {
      expect(getUserHistory).toHaveBeenCalledWith('user456');
    });

    // **D. Assert that "No Past Orders" message is displayed**
    expect(screen.getByText('No Past Orders')).toBeInTheDocument();
  });

  test('displays error message when admin user fails to fetch orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getOrderHistory.mockRejectedValueOnce(new Error('Network Error'));

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getOrderHistory was called**
    await waitFor(() => {
      expect(getOrderHistory).toHaveBeenCalledTimes(1);
    });

    // **D. Assert that error message is displayed using findByText**
    const errorMessage = await screen.findByText('Error fetching total history.');
    expect(errorMessage).toBeInTheDocument();

    // **E. Assert that "No Past Orders" message is displayed**
    expect(screen.getByText('No Past Orders')).toBeInTheDocument();
  });

  test('displays error message when regular user fails to fetch orders', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(false);
    getLoggedInUser.mockReturnValue('user789');
    getUserHistory.mockRejectedValueOnce(new Error('API Error'));

    // **B. Render the component**
    renderComponent();

    // **C. Assert that getUserHistory was called with the correct user**
    await waitFor(() => {
      expect(getUserHistory).toHaveBeenCalledWith('user789');
    });

    // **D. Assert that error message is displayed using findByText**
    const errorMessage = await screen.findByText('Error fetching total history.');
    expect(errorMessage).toBeInTheDocument();

    // **E. Assert that "No Past Orders" message is displayed**
    expect(screen.getByText('No Past Orders')).toBeInTheDocument();
  });

  test('navigates to order details page when "View" button is clicked', async () => {
    // **A. Mock AuthService and HistoryService responses**
    isAdminUser.mockReturnValue(true);
    getOrderHistory.mockResolvedValueOnce({
      data: [
        { id: 'ORD123' },
      ],
    });

    // **B. Render the component**
    renderComponent();

    // **C. Wait for data fetching**
    await waitFor(() => {
      expect(getOrderHistory).toHaveBeenCalledWith();
    });

    // **D. Find the "View" button**
    const viewButton = screen.getByRole('button', { name: /View/i });
    expect(viewButton).toBeInTheDocument();

    // **E. Click the "View" button**
    await userEvent.click(viewButton);

    // **F. Assert that navigate was called with the correct path**
    expect(mockNavigate).toHaveBeenCalledWith('/history/ORD123');
  });
});
