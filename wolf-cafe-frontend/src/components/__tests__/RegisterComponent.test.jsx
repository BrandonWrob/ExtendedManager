// RegisterComponent.test.jsx
import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import RegisterComponent from '../RegisterComponent';
import { registerAPICall } from '../../services/AuthService';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// Mock the registerAPICall function
vi.mock('../../services/AuthService', () => ({
  registerAPICall: vi.fn(),
}));

describe('RegisterComponent', () => {
  afterEach(() => {
    vi.clearAllMocks();
  });

  test('renders the component with all input fields and submit button', () => {
    render(<RegisterComponent />);

    // Check for input fields
    expect(screen.getByPlaceholderText('Enter name')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter username')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter email')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Enter password')).toBeInTheDocument();

    // Check for submit button
    expect(screen.getByRole('button', { name: 'Submit' })).toBeInTheDocument();
  });

  test('displays validation error when fields are empty', async () => {
    render(<RegisterComponent />);

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    await userEvent.click(submitButton);

    expect(await screen.findByText('Please fill in all fields.')).toBeInTheDocument();
  });

  test('calls registerAPICall with correct data on form submission', async () => {
    const mockResponse = { data: 'Registration successful.' };
    registerAPICall.mockResolvedValueOnce(mockResponse);

    render(<RegisterComponent />);

    // Fill in the form fields
    await userEvent.type(screen.getByPlaceholderText('Enter name'), 'John Doe');
    await userEvent.type(screen.getByPlaceholderText('Enter username'), 'johndoe');
    await userEvent.type(screen.getByPlaceholderText('Enter email'), 'john@example.com');
    await userEvent.type(screen.getByPlaceholderText('Enter password'), 'password123');

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    await userEvent.click(submitButton);

    // Wait for registerAPICall to be called
    await waitFor(() => {
      expect(registerAPICall).toHaveBeenCalledWith({
        name: 'John Doe',
        username: 'johndoe',
        email: 'john@example.com',
        password: 'password123',
      });
    });

    // Check that success message is displayed
    expect(await screen.findByText('Registration successful.')).toBeInTheDocument();
    expect(screen.getByText('Registration successful.')).toHaveClass('text-success');

    // Check that the form fields are reset
    expect(screen.getByPlaceholderText('Enter name')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter username')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter email')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter password')).toHaveValue('');
  });

  test('displays error message when registration fails due to existing user', async () => {
    const errorResponse = {
      response: {
        status: 400,
      },
    };
    registerAPICall.mockRejectedValueOnce(errorResponse);

    render(<RegisterComponent />);

    // Fill in the form fields
    await userEvent.type(screen.getByPlaceholderText('Enter name'), 'Jane Doe');
    await userEvent.type(screen.getByPlaceholderText('Enter username'), 'janedoe');
    await userEvent.type(screen.getByPlaceholderText('Enter email'), 'jane@example.com');
    await userEvent.type(screen.getByPlaceholderText('Enter password'), 'password123');

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    await userEvent.click(submitButton);

    // Wait for registerAPICall to be called
    await waitFor(() => {
      expect(registerAPICall).toHaveBeenCalledWith({
        name: 'Jane Doe',
        username: 'janedoe',
        email: 'jane@example.com',
        password: 'password123',
      });
    });

    // Check that error message is displayed
    expect(
      await screen.findByText('This username or email already exists.')
    ).toBeInTheDocument();
    expect(screen.getByText('This username or email already exists.')).toHaveClass('text-danger');

    // Ensure form fields are reset
    expect(screen.getByPlaceholderText('Enter name')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter username')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter email')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter password')).toHaveValue('');
  });

  test('displays error message when registration fails due to role assignment', async () => {
    const errorResponse = {
      response: {
        status: 404,
      },
    };
    registerAPICall.mockRejectedValueOnce(errorResponse);

    render(<RegisterComponent />);

    // Fill in the form fields
    await userEvent.type(screen.getByPlaceholderText('Enter name'), 'Alice');
    await userEvent.type(screen.getByPlaceholderText('Enter username'), 'alice');
    await userEvent.type(screen.getByPlaceholderText('Enter email'), 'alice@example.com');
    await userEvent.type(screen.getByPlaceholderText('Enter password'), 'password123');

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    await userEvent.click(submitButton);

    // Wait for registerAPICall to be called
    await waitFor(() => {
      expect(registerAPICall).toHaveBeenCalledWith({
        name: 'Alice',
        username: 'alice',
        email: 'alice@example.com',
        password: 'password123',
      });
    });

    // Check that error message is displayed
    expect(await screen.findByText('Could not assign a user role.')).toBeInTheDocument();
    expect(screen.getByText('Could not assign a user role.')).toHaveClass('text-danger');

    // Ensure form fields are reset
    expect(screen.getByPlaceholderText('Enter name')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter username')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter email')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter password')).toHaveValue('');
  });

  test('displays generic error message for other registration failures', async () => {
    const errorResponse = {
      response: {
        status: 500,
      },
    };
    registerAPICall.mockRejectedValueOnce(errorResponse);

    render(<RegisterComponent />);

    // Fill in the form fields
    await userEvent.type(screen.getByPlaceholderText('Enter name'), 'Bob');
    await userEvent.type(screen.getByPlaceholderText('Enter username'), 'bob');
    await userEvent.type(screen.getByPlaceholderText('Enter email'), 'bob@example.com');
    await userEvent.type(screen.getByPlaceholderText('Enter password'), 'password123');

    const submitButton = screen.getByRole('button', { name: 'Submit' });
    await userEvent.click(submitButton);

    // Wait for registerAPICall to be called
    await waitFor(() => {
      expect(registerAPICall).toHaveBeenCalledWith({
        name: 'Bob',
        username: 'bob',
        email: 'bob@example.com',
        password: 'password123',
      });
    });

    // Check that error message is displayed
    expect(await screen.findByText('User could not be registered.')).toBeInTheDocument();
    expect(screen.getByText('User could not be registered.')).toHaveClass('text-danger');

    // Ensure form fields are reset
    expect(screen.getByPlaceholderText('Enter name')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter username')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter email')).toHaveValue('');
    expect(screen.getByPlaceholderText('Enter password')).toHaveValue('');
  });
});
