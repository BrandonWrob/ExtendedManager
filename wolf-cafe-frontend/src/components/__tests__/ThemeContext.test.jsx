// src/components/__tests__/ThemeContext.test.jsx

import React, { useContext } from 'react';
import { render, screen, fireEvent, act } from '@testing-library/react';
import { ThemeProvider, ThemeContext } from '../ThemeContext';
import { vi } from 'vitest';

// Define a TestComponent to consume ThemeContext
const TestComponent = () => {
    const { theme, colorblindMode, changeVisualMode, changeColorblindMode, resetModes } = useContext(ThemeContext);

    return (
        <div>
            <span data-testid="current-theme">{theme}</span>
            <span data-testid="current-colorblindMode">{colorblindMode || 'None'}</span>
            <button onClick={() => changeVisualMode('Dark Mode')}>Set Dark Mode</button>
            <button onClick={() => changeColorblindMode('Red-Green (Red Deficiency)')}>Set Red-Green (Red Deficiency)</button>
            <button onClick={() => changeColorblindMode('Red-Green (Green Deficiency)')}>Set Red-Green (Green Deficiency)</button>
            <button onClick={() => changeColorblindMode('Blue-Yellow')}>Set Blue-Yellow</button>
            <button onClick={() => resetModes()}>Reset Modes</button>
        </div>
    );
};

describe('ThemeContext', () => {
    let originalLocalStorage;
    let addSpy, removeSpy;
    let consoleLogSpy;

    beforeEach(() => {
        // Mock localStorage
        originalLocalStorage = { ...localStorage };
        const localStorageMock = {
            getItem: vi.fn(),
            setItem: vi.fn(),
            removeItem: vi.fn(),
            clear: vi.fn(),
        };
        Object.defineProperty(global, 'localStorage', {
            value: localStorageMock,
        });

        // Mock document.body.classList methods
        addSpy = vi.spyOn(document.body.classList, 'add').mockImplementation(() => {});
        removeSpy = vi.spyOn(document.body.classList, 'remove').mockImplementation(() => {});

        // Mock console.log
        consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
    });

    afterEach(() => {
        // Restore mocks
        vi.restoreAllMocks();
        Object.defineProperty(global, 'localStorage', {
            value: originalLocalStorage,
        });
    });

    test('initializes with default theme "Normal" and no colorblind mode when localStorage is empty', () => {
        // Mock localStorage to return null for both keys
        localStorage.getItem.mockReturnValue(null);

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Verify initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Verify that 'normal-mode' class is added to body
        expect(addSpy).toHaveBeenCalledWith('normal-mode');

        // Ensure no colorblind classes are added
        expect(addSpy).not.toHaveBeenCalledWith(
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );

        // Verify localStorage was accessed
        expect(localStorage.getItem).toHaveBeenCalledWith('colorblindMode');
        expect(localStorage.getItem).toHaveBeenCalledWith('visualMode');
    });

    test('initializes with theme from localStorage and no colorblind mode', () => {
        // Mock localStorage to return 'High Contrast Mode' for visualMode and null for colorblindMode
        localStorage.getItem.mockImplementation((key) => {
            if (key === 'visualMode') return 'High Contrast Mode';
            if (key === 'colorblindMode') return null;
            return null;
        });

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Verify initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('High Contrast Mode');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Verify that 'high-contrast-mode' class is added to body
        expect(addSpy).toHaveBeenCalledWith('high-contrast-mode');

        // Ensure no colorblind classes are added
        expect(addSpy).not.toHaveBeenCalledWith(
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );

        // Verify localStorage was accessed
        expect(localStorage.getItem).toHaveBeenCalledWith('colorblindMode');
        expect(localStorage.getItem).toHaveBeenCalledWith('visualMode');
    });

    test('initializes with colorblind mode from localStorage and sets theme to "Normal"', () => {
        // Mock localStorage to return 'Dark Mode' for visualMode and 'Red-Green (Red Deficiency)' for colorblindMode
        localStorage.getItem.mockImplementation((key) => {
            if (key === 'visualMode') return 'Dark Mode';
            if (key === 'colorblindMode') return 'Red-Green (Red Deficiency)';
            return null;
        });

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Verify initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal'); // Should reset to 'Normal'
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Red-Green (Red Deficiency)');

        // Verify that 'colorblind-red-green' class is added to body
        expect(addSpy).toHaveBeenCalledWith('colorblind-red-green');

        // Ensure that all theme-related classes are removed before adding the new one
        expect(removeSpy).toHaveBeenCalledWith(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );

        // Verify localStorage was accessed
        expect(localStorage.getItem).toHaveBeenCalledWith('colorblindMode');
    });

    test('changes visual mode correctly', async () => {
        // Initialize with default values
        localStorage.getItem.mockReturnValue(null);

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Click to set Dark Mode
        await act(async () => {
            fireEvent.click(screen.getByText('Set Dark Mode'));
        });

        // Verify updated state
        expect(screen.getByTestId('current-theme').textContent).toBe('Dark Mode');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Verify that 'dark-mode' class is added and other classes are removed
        expect(removeSpy).toHaveBeenCalledWith(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );
        expect(addSpy).toHaveBeenCalledWith('dark-mode');

        // Verify that 'visualMode' is set in localStorage and 'colorblindMode' is removed
        expect(localStorage.setItem).toHaveBeenCalledWith('visualMode', 'Dark Mode');
        expect(localStorage.removeItem).toHaveBeenCalledWith('colorblindMode');

        // Verify console logs
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Dark Mode');
    });

    test('changes colorblind mode correctly and resets visual mode to "Normal"', async () => {
        // Initialize with default values
        localStorage.getItem.mockReturnValue(null);

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Click to set Red-Green (Red Deficiency) Colorblind Mode
        await act(async () => {
            fireEvent.click(screen.getByText('Set Red-Green (Red Deficiency)'));
        });

        // Verify updated state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal'); // Should remain 'Normal'
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Red-Green (Red Deficiency)');

        // Verify that 'colorblind-red-green' class is added and other classes are removed
        expect(removeSpy).toHaveBeenCalledWith(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );
        expect(addSpy).toHaveBeenCalledWith('colorblind-red-green');

        // Verify that 'visualMode' is set to 'Normal' and 'colorblindMode' is set in localStorage
        expect(localStorage.setItem).toHaveBeenCalledWith('visualMode', 'Normal');
        expect(localStorage.setItem).toHaveBeenCalledWith('colorblindMode', 'Red-Green (Red Deficiency)');

        // Verify console logs
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
        expect(consoleLogSpy).toHaveBeenCalledWith('Colorblind mode updated to: Red-Green (Red Deficiency)');
    });

    test('toggles colorblind mode off when the same mode is selected again', async () => {
        // Initialize with a colorblind mode
        localStorage.getItem.mockImplementation((key) => {
            if (key === 'visualMode') return 'Normal';
            if (key === 'colorblindMode') return 'Red-Green (Red Deficiency)';
            return null;
        });

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Red-Green (Red Deficiency)');

        // Reset the spies to ignore initial class additions
        addSpy.mockClear();
        removeSpy.mockClear();

        // Click to toggle off Red-Green (Red Deficiency) Colorblind Mode
        await act(async () => {
            fireEvent.click(screen.getByText('Set Red-Green (Red Deficiency)'));
        });

        // Verify updated state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal'); // Remains 'Normal'
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None'); // Toggled off

        // Verify that 'normal-mode' class is added
        expect(addSpy).toHaveBeenCalledWith('normal-mode');

        // Ensure that 'colorblind-red-green' was not added again
        expect(addSpy).not.toHaveBeenCalledWith('colorblind-red-green');

        // Verify that 'colorblindMode' is removed from localStorage
        expect(localStorage.removeItem).toHaveBeenCalledWith('colorblindMode');

        // Verify console logs
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
    });

    test('changes multiple colorblind modes correctly', async () => {
        // Initialize with default values
        localStorage.getItem.mockReturnValue(null);

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Set Red-Green (Red Deficiency)
        await act(async () => {
            fireEvent.click(screen.getByText('Set Red-Green (Red Deficiency)'));
        });
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Red-Green (Red Deficiency)');
        expect(addSpy).toHaveBeenCalledWith('colorblind-red-green');

        // Change to Red-Green (Green Deficiency)
        await act(async () => {
            fireEvent.click(screen.getByText('Set Red-Green (Green Deficiency)'));
        });
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Red-Green (Green Deficiency)');
        expect(addSpy).toHaveBeenCalledWith('colorblind-green-red');

        // Change to Blue-Yellow
        await act(async () => {
            fireEvent.click(screen.getByText('Set Blue-Yellow'));
        });
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Blue-Yellow');
        expect(addSpy).toHaveBeenCalledWith('colorblind-blue-yellow');

        // Ensure previous colorblind classes are removed
        expect(removeSpy).toHaveBeenCalledWith(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );

        // Verify that 'colorblindMode' is set correctly in localStorage each time
        expect(localStorage.setItem).toHaveBeenCalledWith('visualMode', 'Normal');
        expect(localStorage.setItem).toHaveBeenCalledWith('colorblindMode', 'Red-Green (Red Deficiency)');
        expect(localStorage.setItem).toHaveBeenCalledWith('colorblindMode', 'Red-Green (Green Deficiency)');
        expect(localStorage.setItem).toHaveBeenCalledWith('colorblindMode', 'Blue-Yellow');

        // Verify console logs
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
        expect(consoleLogSpy).toHaveBeenCalledWith('Colorblind mode updated to: Red-Green (Red Deficiency)');
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
        expect(consoleLogSpy).toHaveBeenCalledWith('Colorblind mode updated to: Red-Green (Green Deficiency)');
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
        expect(consoleLogSpy).toHaveBeenCalledWith('Colorblind mode updated to: Blue-Yellow');
    });

    test('resets modes correctly', async () => {
        // Initialize with a theme and colorblind mode
        localStorage.getItem.mockImplementation((key) => {
            if (key === 'visualMode') return 'Dark Mode';
            if (key === 'colorblindMode') return 'Blue-Yellow';
            return null;
        });

        render(
            <ThemeProvider>
                <TestComponent />
            </ThemeProvider>
        );

        // Initial state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal'); // Should reset to 'Normal'
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('Blue-Yellow');

        // Click to reset modes
        await act(async () => {
            fireEvent.click(screen.getByText('Reset Modes'));
        });

        // Verify updated state
        expect(screen.getByTestId('current-theme').textContent).toBe('Normal');
        expect(screen.getByTestId('current-colorblindMode').textContent).toBe('None');

        // Verify that 'normal-mode' class is added and colorblind classes are removed
        expect(removeSpy).toHaveBeenCalledWith(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );
        expect(addSpy).toHaveBeenCalledWith('normal-mode');

        // Verify that 'colorblindMode' is removed from localStorage
        expect(localStorage.removeItem).toHaveBeenCalledWith('colorblindMode');

        // Verify console logs
        expect(consoleLogSpy).toHaveBeenCalledWith('Theme updated to: Normal');
    });
});
