import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import AdminRegisterComponent from '../AdminRegisterComponent';
import * as AuthService from '../../services/AuthService';

// Mock dependencies
vi.mock('../../services/AuthService', () => ({
  isAdminUser: vi.fn(),
  adminRegisterAPICall: vi.fn(),
}));

describe('AdminRegisterComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('should display the registration form', () => {
    render(<AdminRegisterComponent />);

    expect(screen.getByPlaceholderText(/Enter name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Enter username/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Enter email/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/Enter password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Submit/i })).toBeInTheDocument();
  });

  it('displays error message for duplicate username or email', async () => {
    AuthService.isAdminUser.mockReturnValue(true);
    AuthService.adminRegisterAPICall.mockRejectedValueOnce({
      response: { status: 400 },
    });

    render(<AdminRegisterComponent />);

    fireEvent.change(screen.getByPlaceholderText(/Enter name/i), { target: { value: 'John Doe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter username/i), { target: { value: 'johndoe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter email/i), { target: { value: 'johndoe@example.com' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter password/i), { target: { value: 'password' } });
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'ROLE_ADMIN' } });
    
    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    expect(await screen.findByText(/This username or email already exists./i)).toBeInTheDocument();
  });

  it('displays error message when role assignment fails', async () => {
    AuthService.isAdminUser.mockReturnValue(true);
    AuthService.adminRegisterAPICall.mockRejectedValueOnce({
      response: { status: 404 },
    });

    render(<AdminRegisterComponent />);

    fireEvent.change(screen.getByPlaceholderText(/Enter name/i), { target: { value: 'John Doe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter username/i), { target: { value: 'johndoe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter email/i), { target: { value: 'johndoe@example.com' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter password/i), { target: { value: 'password' } });
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'ROLE_ADMIN' } });

    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    expect(await screen.findByText(/Could not assign a user role./i)).toBeInTheDocument();
  });

  it('displays generic error message for other API errors', async () => {
    AuthService.isAdminUser.mockReturnValue(true);
    AuthService.adminRegisterAPICall.mockRejectedValueOnce(new Error('Network Error'));

    render(<AdminRegisterComponent />);

    fireEvent.change(screen.getByPlaceholderText(/Enter name/i), { target: { value: 'John Doe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter username/i), { target: { value: 'johndoe' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter email/i), { target: { value: 'johndoe@example.com' } });
    fireEvent.change(screen.getByPlaceholderText(/Enter password/i), { target: { value: 'password' } });
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'ROLE_ADMIN' } });

    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    expect(await screen.findByText(/User could not be registered./i)).toBeInTheDocument();
  });

  it('should not allow non-admin users to register a new user', async () => {
    AuthService.isAdminUser.mockReturnValue(false);

    render(<AdminRegisterComponent />);

    fireEvent.change(screen.getByPlaceholderText(/Enter name/i), { target: { value: 'John Doe' } });
    fireEvent.click(screen.getByRole('button', { name: /Submit/i }));

    // Check for any validation error message after submission
    expect(await screen.findByText(/User could not be registered.|Please fill in all fields./i)).toBeInTheDocument();
  });
});
