import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LoginComponent from '../LoginComponent';
import { loginAPICall } from '../../services/AuthService';
import { BrowserRouter as Router } from 'react-router-dom';
import { vi } from 'vitest';

// Mock AuthService
vi.mock('../../services/AuthService');

describe('LoginComponent', () => {
  beforeEach(() => {
    loginAPICall.mockClear();
  });

  test('renders the login form', () => {
    render(
      <Router>
        <LoginComponent />
      </Router>
    );

    expect(screen.getByText('Login Form')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter username or email')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter password')).toBeInTheDocument();
    expect(screen.getByText('Submit')).toBeInTheDocument();
  });

  test('displays validation error for empty fields', async () => {
    render(
      <Router>
        <LoginComponent />
      </Router>
    );

    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => {
      expect(screen.getByText('Please fill in all fields.')).toBeInTheDocument();
    });
  });

  test('displays error for unsuccessful login', async () => {
    loginAPICall.mockRejectedValue(new Error('Invalid credentials'));

    render(
      <Router>
        <LoginComponent />
      </Router>
    );

    fireEvent.change(screen.getByPlaceholderText('Enter username or email'), {
      target: { value: 'testuser' },
    });
    fireEvent.change(screen.getByPlaceholderText('Enter password'), {
      target: { value: 'wrongpassword' },
    });
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => {
      expect(screen.getByText('Failed to log in.')).toBeInTheDocument();
    });
    expect(loginAPICall).toHaveBeenCalledWith('testuser', 'wrongpassword');
  });

  test('successfully logs in and redirects', async () => {
    const mockResponse = {
      data: {
        accessToken: 'mockToken',
        role: 'admin',
      },
    };
    loginAPICall.mockResolvedValue(mockResponse);

    render(
      <Router>
        <LoginComponent />
      </Router>
    );

    fireEvent.change(screen.getByPlaceholderText('Enter username or email'), {
      target: { value: 'testuser' },
    });
    fireEvent.change(screen.getByPlaceholderText('Enter password'), {
      target: { value: 'password' },
    });
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => {
      expect(screen.getByText('Login successful.')).toBeInTheDocument();
    });
    expect(loginAPICall).toHaveBeenCalledWith('testuser', 'password');
  });

  test('resets form fields after submission', async () => {
    const mockResponse = {
      data: {
        accessToken: 'mockToken',
        role: 'admin',
      },
    };
    loginAPICall.mockResolvedValue(mockResponse);

    render(
      <Router>
        <LoginComponent />
      </Router>
    );

    const usernameInput = screen.getByPlaceholderText('Enter username or email');
    const passwordInput = screen.getByPlaceholderText('Enter password');

    fireEvent.change(usernameInput, { target: { value: 'testuser' } });
    fireEvent.change(passwordInput, { target: { value: 'password' } });
    fireEvent.click(screen.getByText('Submit'));

    await waitFor(() => {
      expect(screen.getByText('Login successful.')).toBeInTheDocument();
    });

    expect(usernameInput.value).toBe('');
    expect(passwordInput.value).toBe('');
  });
});
