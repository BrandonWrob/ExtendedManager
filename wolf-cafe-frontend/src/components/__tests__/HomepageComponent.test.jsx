// HomepageComponent.test.jsx
import React from 'react';
import { render, screen } from '@testing-library/react';
import HomepageComponent from '../HomepageComponent';
import { MemoryRouter, useNavigate } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// Mock useNavigate
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: vi.fn(),
  };
});

describe('HomepageComponent', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  test('renders the component with welcome message and button', () => {
    render(
      <MemoryRouter>
        <HomepageComponent />
      </MemoryRouter>
    );

    // Check that the welcome message is displayed
    expect(screen.getByText('Welcome to Our Cafe')).toBeInTheDocument();

    // Check that the button is displayed
    const orderButton = screen.getByRole('button', { name: 'Click here to order' });
    expect(orderButton).toBeInTheDocument();
  });

  test('navigates to /create-order when the button is clicked', async () => {
    const navigate = vi.fn();
    useNavigate.mockReturnValue(navigate);

    render(
      <MemoryRouter>
        <HomepageComponent />
      </MemoryRouter>
    );

    const orderButton = screen.getByRole('button', { name: 'Click here to order' });
    await userEvent.click(orderButton);

    // Check that navigate was called with the correct path
    expect(navigate).toHaveBeenCalledWith('/create-order');
  });
});
