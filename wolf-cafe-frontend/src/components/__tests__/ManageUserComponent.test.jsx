import React from 'react';
import { render, screen, waitFor} from '@testing-library/react';
import ManageUserComponent from '../ManageUserComponent';
import {
  editUserAPICall,
  deleteUserAPICall,
  getAllUsersAPICall,
  isAdminUser,
  getLoggedInUser,
} from '../../services/AuthService';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';

// Mock services
vi.mock('../../services/AuthService');

describe('ManageUserComponent', () => {
  const mockUsers = [
    {
      id: 1,
      name: 'John Doe',
      username: 'johndoe',
      email: 'john@example.com',
      roles: ['ROLE_CUSTOMER'],
    },
    {
      id: 2,
      name: 'Jane Smith',
      username: 'janesmith',
      email: 'jane@example.com',
      roles: ['ROLE_MANAGER'],
    },
  ];

  beforeEach(() => {
    vi.clearAllMocks();

    // Mock service calls
    getAllUsersAPICall.mockResolvedValue({ data: mockUsers });
    isAdminUser.mockReturnValue(true);

    // Mock getLoggedInUser to return the email of the logged-in user
    getLoggedInUser.mockReturnValue('john@example.com');
  });
  test('renders and fetches user list on mount', async () => {
    render(<ManageUserComponent />);

    // Check that user list is fetched and rendered
    await waitFor(() => {
      expect(screen.getByText('John Doe')).toBeInTheDocument();
      expect(screen.getByText('Jane Smith')).toBeInTheDocument();
    });
  });

  test('displays validation error if no user is selected on edit', async () => {
    render(<ManageUserComponent />);

    const user = userEvent.setup();

    // Attempt to submit the edit form without selecting a user
    await user.click(screen.getByText('Edit User'));

    // Expect validation error
    expect(screen.getByText('Please select a user.')).toBeInTheDocument();
  });

  test('updates user details successfully', async () => {
    const { container } = render(<ManageUserComponent />);

    const user = userEvent.setup();

    // Wait for the "Select User" options to be rendered
    const selectElements = await screen.findAllByRole('combobox');
    const selectUserElement = selectElements[0]; // Assuming first combobox is 'Select User'

    // Wait for the options to be present
    await waitFor(() => {
      expect(selectUserElement.options.length).toBeGreaterThan(1);
    });

    // Now select the user with id '2' (Jane Smith)
    await user.selectOptions(selectUserElement, '2');

    // Wait for the form fields to populate
    await waitFor(() => {
      const nameInput = container.querySelector('input[name="userName"]');
      expect(nameInput).toHaveValue('Jane Smith');
    });

    // Modify user details
    const nameInput = container.querySelector('input[name="userName"]');
    const usernameInput = container.querySelector('input[name="userUsername"]');
    const emailInput = container.querySelector('input[name="userEmail"]');
    const passwordInput = container.querySelector('input[name="password"]');

    await user.clear(nameInput);
    await user.type(nameInput, 'Jane Doe');
    await user.clear(usernameInput);
    await user.type(usernameInput, 'janedoe');
    await user.clear(emailInput);
    await user.type(emailInput, 'jane.doe@example.com');
    await user.type(passwordInput, 'newpassword');

    // Role select
    const roleSelectElement = selectElements[1]; // Assuming second combobox is 'Role'

    // Wait for the role options to be loaded (if necessary)
    await waitFor(() => {
      expect(roleSelectElement.options.length).toBeGreaterThan(1);
    });

    await user.selectOptions(roleSelectElement, 'ROLE_ADMIN');

    // Mock edit API call
    editUserAPICall.mockResolvedValueOnce({ data: 'User updated successfully.' });

    // Submit the edit form
    await user.click(screen.getByText('Edit User'));

    // Expect success message
    await waitFor(() => {
      expect(screen.getByText('User updated successfully.')).toBeInTheDocument();
    });

    // Verify API call
    expect(editUserAPICall).toHaveBeenCalledWith(2, {
      name: 'Jane Doe',
      username: 'janedoe',
      email: 'jane.doe@example.com',
      password: 'newpassword',
      roles: ['ROLE_ADMIN'],
    });
  });

  test('deletes user successfully', async () => {
    const { container } = render(<ManageUserComponent />);

    const user = userEvent.setup();

    // Wait for the "Select User" options to be rendered
    const selectElements = await screen.findAllByRole('combobox');
    const selectUserElement = selectElements[0];

    // Wait for the options to be present
    await waitFor(() => {
      expect(selectUserElement.options.length).toBeGreaterThan(1);
    });

    // Select a user with id '2' (Jane Smith)
    await user.selectOptions(selectUserElement, '2');

    // Wait for the form fields to populate
    await waitFor(() => {
      const nameInput = container.querySelector('input[name="userName"]');
      expect(nameInput).toHaveValue('Jane Smith');
    });

    // Mock delete API call
    deleteUserAPICall.mockResolvedValueOnce({ data: 'User deleted successfully.' });

    // Submit the delete form
    await user.click(screen.getByText('Delete User'));

    // Expect success message
    await waitFor(() => {
      expect(screen.getByText('User deleted successfully.')).toBeInTheDocument();
    });

    // Verify API call
    expect(deleteUserAPICall).toHaveBeenCalledWith(2);
  });

  test('prevents editing or deleting the logged-in user', async () => {
    render(<ManageUserComponent />);

    const user = userEvent.setup();

    // Wait for the "Select User" options to be rendered
    const selectElements = await screen.findAllByRole('combobox');
    const selectUserElement = selectElements[0];

    // Select a user with id '1' (logged-in user)
    await user.selectOptions(selectUserElement, '1');

    // Wait for selectedUser to be updated
    await waitFor(() => {
      expect(selectUserElement.value).toBe('1');
    });

    // Attempt to submit the edit form
    await user.click(screen.getByText('Edit User'));

    // Assert that editUserAPICall was not called
    expect(editUserAPICall).not.toHaveBeenCalled();

    // Attempt to submit the delete form
    await user.click(screen.getByText('Delete User'));

    // Assert that deleteUserAPICall was not called
    expect(deleteUserAPICall).not.toHaveBeenCalled();
  });
});
