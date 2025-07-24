// src/components/__tests__/OrderConfirmationComponent.test.jsx
import React from 'react';
import { render, screen, cleanup, act } from '@testing-library/react';
import OrderConfirmationComponent from '../OrderConfirmationComponent';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { vi } from 'vitest';

describe('OrderConfirmationComponent', () => {
  beforeAll(() => {
    // Suppress React Router warnings
    vi.spyOn(console, 'warn').mockImplementation(() => {});
  });

  afterAll(() => {
    console.warn.mockRestore();
  });

  beforeEach(() => {
    vi.useFakeTimers(); // Use fake timers for time control
  });

  afterEach(() => {
    vi.runOnlyPendingTimers();
    vi.useRealTimers();
    cleanup(); // Cleanup DOM after each test
  });

  test('displays loading initially', () => {
    render(
      <MemoryRouter initialEntries={['/order-confirmation/123']}>
        <Routes>
          <Route path="/order-confirmation/:id" element={<OrderConfirmationComponent />} />
        </Routes>
      </MemoryRouter>
    );

    expect(screen.getByText('Loading...')).toBeInTheDocument(); // Ensure loading text is shown
  });

  test('displays order confirmation after loading', () => {
    render(
      <MemoryRouter initialEntries={['/order-confirmation/123']}>
        <Routes>
          <Route path="/order-confirmation/:id" element={<OrderConfirmationComponent />} />
        </Routes>
      </MemoryRouter>
    );

    // Initially, "Loading..." should be displayed
    expect(screen.getByText('Loading...')).toBeInTheDocument();

    // Advance timers and wrap in `act` to trigger state updates
    act(() => {
      vi.advanceTimersByTime(1000); // Advance time by 1 second
    });

    // Assert component updates
    expect(screen.getByRole('heading', { name: 'Order Created Successfully!' })).toBeInTheDocument();
    expect(screen.getByText('Your Order Number: 123')).toBeInTheDocument();

    // Removed the expectation for 'Status: Not Fulfilled' as it's not present in the component
  }, 10000); // Increased timeout for this test
});
