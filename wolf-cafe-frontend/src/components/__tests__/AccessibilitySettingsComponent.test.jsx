// src/components/__tests__/AccessibilitySettingsComponent.test.jsx

import React, { useState } from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AccessibilitySettingsComponent from '../AccessibilitySettingsComponent';
import { ThemeContext } from '../ThemeContext';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';

// Mock functions
const mockChangeVisualMode = vi.fn();
const mockChangeColorblindMode = vi.fn();
const mockResetModes = vi.fn();
const mockHandleClose = vi.fn();

// TestProvider Component
const TestProvider = ({ children, initialTheme = 'Normal', initialColorblindMode = null }) => {
  const [theme, setTheme] = useState(initialTheme);
  const [colorblindMode, setColorblindMode] = useState(initialColorblindMode);

  const changeVisualMode = (newMode) => {
    setTheme(newMode);
    mockChangeVisualMode(newMode);
  };

  const changeColorblindMode = (newMode) => {
    setColorblindMode(newMode);
    mockChangeColorblindMode(newMode);
  };

  const resetModes = () => {
    setTheme('Normal');
    setColorblindMode(null);
    mockResetModes();
  };

  return (
    <ThemeContext.Provider
      value={{
        theme,
        colorblindMode,
        changeVisualMode,
        changeColorblindMode,
        resetModes,
      }}
    >
      {children}
    </ThemeContext.Provider>
  );
};

// Helper function to render with TestProvider
const renderWithTestProvider = (
  ui,
  { initialTheme, initialColorblindMode, ...renderOptions } = {}
) => {
  return render(
    <TestProvider initialTheme={initialTheme} initialColorblindMode={initialColorblindMode}>
      {ui}
    </TestProvider>,
    renderOptions
  );
};

describe('AccessibilitySettingsComponent', () => {
  beforeEach(() => {
    // Clear all mock function calls before each test
    vi.clearAllMocks();
  });

  it('renders the modal when show is true', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    // Check if the modal title is in the document
    expect(screen.getByText(/accessibility settings/i)).toBeInTheDocument();
  });

  it('does not render the modal when show is false', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={false} handleClose={mockHandleClose} />
    );

    // The modal title should not be in the document
    expect(screen.queryByText(/accessibility settings/i)).not.toBeInTheDocument();
  });

  it('renders all colorblind mode buttons', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    // Check for each colorblind mode button by their accessible names
    expect(
      screen.getByRole('button', { name: /red-green \(red deficiency\)/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: /red-green \(green deficiency\)/i })
    ).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: /blue-yellow/i })
    ).toBeInTheDocument();
  });

  it('clicking a colorblind mode button calls changeColorblindMode with correct mode', async () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    // Example: Click on "Red-Green (Red Deficiency)" button
    const redGreenRedButton = screen.getByRole('button', { name: /red-green \(red deficiency\)/i });
    await userEvent.click(redGreenRedButton);

    expect(mockChangeColorblindMode).toHaveBeenCalledWith('Red-Green (Red Deficiency)');
  });

  it('renders all visual mode slider labels', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    // Check for each visual mode label
    expect(screen.getByText(/normal/i)).toBeInTheDocument();
    expect(screen.getByText(/dark mode/i)).toBeInTheDocument();
    expect(screen.getByText(/high contrast mode/i)).toBeInTheDocument();
  });

  it('adjusting the slider calls changeVisualMode with correct mode', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    const slider = screen.getByRole('slider');

    // Move slider to 'Dark Mode' (value index 1)
    fireEvent.change(slider, { target: { value: '1' } });
    expect(mockChangeVisualMode).toHaveBeenCalledWith('Dark Mode');

    // Move slider to 'High Contrast Mode' (value index 2)
    fireEvent.change(slider, { target: { value: '2' } });
    expect(mockChangeVisualMode).toHaveBeenCalledWith('High Contrast Mode');
  });

  it('active colorblind mode button has primary variant', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />,
      { initialColorblindMode: 'Red-Green (Red Deficiency)' }
    );

    // "Red-Green (Red Deficiency)" button should have 'btn-primary'
    const activeButton = screen.getByRole('button', { name: /red-green \(red deficiency\)/i });
    expect(activeButton).toHaveClass('btn-primary');

    // "Blue-Yellow" button should have 'btn-outline-primary'
    const inactiveButton = screen.getByRole('button', { name: /blue-yellow/i });
    expect(inactiveButton).toHaveClass('btn-outline-primary');
  });

  it('active visual mode slider position corresponds to the theme', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />,
      { initialTheme: 'Dark Mode' }
    );

    const slider = screen.getByRole('slider');
    expect(slider).toHaveValue('1'); // 'Dark Mode' is index 1
  });

  it('clicking the reset button calls resetModes', async () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    const resetButton = screen.getByRole('button', { name: /reset/i });
    await userEvent.click(resetButton);

    expect(mockResetModes).toHaveBeenCalled();
  });

  it('clicking the exit button calls handleClose', async () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />
    );

    const exitButton = screen.getByRole('button', { name: /exit/i });
    await userEvent.click(exitButton);

    expect(mockHandleClose).toHaveBeenCalled();
  });

  it('displays the correct icon based on the selected visual mode', async () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />,
      { initialTheme: 'Normal' } // Start with 'Normal' to observe the change
    );

    const slider = screen.getByRole('slider');
    fireEvent.change(slider, { target: { value: '2' } }); // Set to 'High Contrast Mode'

    // Use waitFor to wait for the SVG to appear in the entire document
    await waitFor(() => {
      const highContrastIcon = document.querySelector('.slider-thumb-icon svg');
      expect(highContrastIcon).toBeInTheDocument();
    });
  });

  it('handles no colorblind mode selected correctly', () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />,
      { initialColorblindMode: null }
    );

    // Ensure all colorblind mode buttons have 'btn-outline-primary' class
    const colorblindButtons = screen.getAllByRole('button', {
      name: /red-green \(red deficiency\)|red-green \(green deficiency\)|blue-yellow/i,
    });

    colorblindButtons.forEach((button) => {
      expect(button).toHaveClass('btn-outline-primary');
    });
  });

  it('triggers reset modes and updates UI accordingly', async () => {
    renderWithTestProvider(
      <AccessibilitySettingsComponent show={true} handleClose={mockHandleClose} />,
      { initialTheme: 'Dark Mode', initialColorblindMode: 'Blue-Yellow' }
    );

    // Ensure initial state
    const slider = screen.getByRole('slider');
    expect(slider).toHaveValue('1'); // 'Dark Mode' is index 1

    const blueYellowButton = screen.getByRole('button', { name: /blue-yellow/i });
    expect(blueYellowButton).toHaveClass('btn-primary');

    // Click reset
    const resetButton = screen.getByRole('button', { name: /reset/i });
    await userEvent.click(resetButton);

    // Verify resetModes was called
    expect(mockResetModes).toHaveBeenCalled();

    // After reset, all colorblind mode buttons should be inactive
    const colorblindButtons = screen.getAllByRole('button', {
      name: /red-green \(red deficiency\)|red-green \(green deficiency\)|blue-yellow/i,
    });

    colorblindButtons.forEach((button) => {
      expect(button).toHaveClass('btn-outline-primary');
    });

    // Slider should reset to 'Normal' mode
    expect(slider).toHaveValue('0'); // 'Normal' is index 0
  });
});
