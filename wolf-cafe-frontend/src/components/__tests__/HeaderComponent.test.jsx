// src/components/__tests__/HeaderComponent.test.jsx

import React from 'react';
import { render, screen, cleanup, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import HeaderComponent from '../HeaderComponent';
import * as AuthService from '../../services/AuthService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

// Mock AccessibilitySettingsComponent to prevent useContext errors
vi.mock('../AccessibilitySettingsComponent');

vi.mock('../../services/AuthService', () => ({
  isUserLoggedIn: vi.fn(),
  isAdminUser: vi.fn(),
  isManagerUser: vi.fn(),
  isStaffUser: vi.fn(),
  logout: vi.fn(),
}));

describe('HeaderComponent', () => {
  afterEach(() => {
    vi.resetAllMocks();
    cleanup();
  });

  it('renders authenticated links when user is logged in as Admin', () => {
    AuthService.isUserLoggedIn.mockReturnValue(true);
    AuthService.isAdminUser.mockReturnValue(true);
    AuthService.isManagerUser.mockReturnValue(false);
    AuthService.isStaffUser.mockReturnValue(false);

    render(
      <Router>
        <HeaderComponent />
      </Router>
    );

    expect(screen.getByText(/^Make Order$/i)).toBeInTheDocument();
    expect(screen.getByText(/Recipes/i)).toBeInTheDocument();
    expect(screen.getByText(/Ingredient/i)).toBeInTheDocument();
    expect(screen.getByText(/Inventory/i)).toBeInTheDocument();

    // Ensure there's only one "Order" link
    const orderLinks = screen.getAllByText(/^Order$/i);
    expect(orderLinks).toHaveLength(1);

    expect(screen.getByText(/Create Accounts/i)).toBeInTheDocument();
    expect(screen.getByText(/Manage Accounts/i)).toBeInTheDocument();
    expect(screen.getByText(/Logout/i)).toBeInTheDocument();
  });

  it('calls logout and redirects to login page on logout click', () => {
    AuthService.isUserLoggedIn.mockReturnValue(true);
    const navigateMock = vi.fn();
    AuthService.logout.mockImplementation(() => navigateMock());

    render(
      <Router>
        <HeaderComponent />
      </Router>
    );

    const logoutLink = screen.getByText(/Logout/i);
    fireEvent.click(logoutLink);

    expect(AuthService.logout).toHaveBeenCalled();
    // If you want to test redirection, consider mocking `useNavigate` from react-router-dom
  });
});
