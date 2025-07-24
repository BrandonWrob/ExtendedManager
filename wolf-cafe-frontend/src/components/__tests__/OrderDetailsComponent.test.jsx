// OrderDetailsComponent.test.jsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import OrderDetailsComponent from '../OrderDetailsComponent';
import { getOrderById } from '../../services/ViewOrdersService';
import { MemoryRouter, Routes, Route, useNavigate } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// Mock the getOrderById function
vi.mock('../../services/ViewOrdersService', () => ({
  getOrderById: vi.fn(),
}));

// Mock useNavigate
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

describe('OrderDetailsComponent', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  test('displays loading message while fetching order details', () => {
    // Mock getOrderById to return a pending Promise
    getOrderById.mockReturnValue(new Promise(() => {}));

    render(
      <MemoryRouter initialEntries={['/orders/1']}>
        <Routes>
          <Route path="/orders/:id" element={<OrderDetailsComponent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Loading order details...')).toBeInTheDocument();
  });

  test('displays order details when fetch is successful', async () => {
    const mockOrder = {
      id: 1,
      fulfilled: true,
      recipes: [
        { name: 'Recipe 1', amount: 2, price: 10.0 },
        { name: 'Recipe 2', amount: 1, price: 15.0 },
      ],
      tip: 5.0,
    };

    getOrderById.mockResolvedValueOnce({ data: mockOrder });

    render(
      <MemoryRouter initialEntries={['/orders/1']}>
        <Routes>
          <Route path="/orders/:id" element={<OrderDetailsComponent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(
      await screen.findByText(/Order Details for Order ID: 1/i)
    ).toBeInTheDocument();

    expect(screen.getByText(/Fulfilled:/i)).toBeInTheDocument();
    expect(screen.getByText('Yes')).toBeInTheDocument();

    expect(screen.getByText('Recipe 1')).toBeInTheDocument();
    expect(screen.getByText('Recipe 2')).toBeInTheDocument();

    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('$10.00')).toBeInTheDocument();
    expect(screen.getByText('1')).toBeInTheDocument();
    expect(screen.getByText('$15.00')).toBeInTheDocument();

    // Adjusted assertion for the Tip
    expect(
      screen.getByText((content, element) => {
        const text = element.textContent.replace(/\s+/g, ' ').trim();
        return text === 'Tip: $5.00';
      })
    ).toBeInTheDocument();
  });


  test('displays error message when fetch fails', async () => {
    getOrderById.mockRejectedValueOnce(new Error('Network error'));

    render(
      <MemoryRouter initialEntries={['/orders/1']}>
        <Routes>
          <Route path="/orders/:id" element={<OrderDetailsComponent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(
      await screen.findByText(
        /Order not found or failed to fetch order details./i
      )
    ).toBeInTheDocument();
  });

  test('navigates back when "Go Back" button is clicked', async () => {
    const mockOrder = {
      id: 1,
      fulfilled: false,
      recipes: [],
      tip: 0,
    };

    getOrderById.mockResolvedValueOnce({ data: mockOrder });
    const navigate = vi.fn();
    useNavigate.mockReturnValue(navigate);

    render(
      <MemoryRouter initialEntries={['/orders/1']}>
        <Routes>
          <Route path="/orders/:id" element={<OrderDetailsComponent />} />
        </Routes>
      </MemoryRouter>
    );

    await screen.findByText(/Order Details for Order ID: 1/i);

    const goBackButton = screen.getByRole('button', { name: /Go Back/i });
    const user = userEvent.setup();
    await user.click(goBackButton);

    expect(navigate).toHaveBeenCalledWith(-1);
  });
});
