// src/contexts/ThemeContext.jsx
import React, { createContext, useState, useEffect } from 'react';

// Create the Context
export const ThemeContext = createContext();

// Create the Provider Component
export const ThemeProvider = ({ children }) => {
    // Initialize colorblindMode from localStorage or default to null
    const [colorblindMode, setColorblindMode] = useState(() => {
        return localStorage.getItem('colorblindMode') || null;
    });

    // Initialize theme state
    const [theme, setTheme] = useState(() => {
        // If colorblindMode is set, theme should be 'Normal'
        return colorblindMode ? 'Normal' : (localStorage.getItem('visualMode') || 'Normal');
    });

    // Effect to update localStorage and apply the theme class to <body>
    useEffect(() => {
        localStorage.setItem('visualMode', theme);
        if (colorblindMode) {
            localStorage.setItem('colorblindMode', colorblindMode);
        } else {
            localStorage.removeItem('colorblindMode');
        }

        // Remove all theme-related classes from body
        document.body.classList.remove(
            'normal-mode',
            'dark-mode',
            'high-contrast-mode',
            'colorblind-red-green',
            'colorblind-green-red',
            'colorblind-blue-yellow'
        );

        // Apply Visual Mode class
        if (theme === 'Normal') {
            document.body.classList.add('normal-mode');
        } else if (theme === 'Dark Mode') {
            document.body.classList.add('dark-mode');
        } else if (theme === 'High Contrast Mode') {
            document.body.classList.add('high-contrast-mode');
        }

        // Apply Colorblind Mode class if applicable
        if (colorblindMode) {
            if (colorblindMode === 'Red-Green (Red Deficiency)') {
                document.body.classList.add('colorblind-red-green');
            } else if (colorblindMode === 'Red-Green (Green Deficiency)') {
                document.body.classList.add('colorblind-green-red');
            } else if (colorblindMode === 'Blue-Yellow') {
                document.body.classList.add('colorblind-blue-yellow');
            }
        }

        console.log(`Theme updated to: ${theme}`);
        if (colorblindMode) {
            console.log(`Colorblind mode updated to: ${colorblindMode}`);
        }
    }, [theme, colorblindMode]);

    // Function to change Visual Mode
    const changeVisualMode = (newTheme) => {
        setTheme(newTheme);
        // Reset colorblind mode when visual mode changes
        setColorblindMode(null);
    };

    // Function to change Colorblind Mode
    const changeColorblindMode = (newColorblindMode) => {
        if (colorblindMode === newColorblindMode) {
            // Deselect if already selected
            setColorblindMode(null);
        } else {
            // Automatically set Visual Mode to Normal when colorblind mode is set
            setTheme('Normal');
            setColorblindMode(newColorblindMode);
        }
    };

    // Function to reset all modes
    const resetModes = () => {
        setTheme('Normal');
        setColorblindMode(null);
    };

    return (
        <ThemeContext.Provider value={{
            theme,
            colorblindMode,
            changeVisualMode,
            changeColorblindMode,
            resetModes
        }}>
            {children}
        </ThemeContext.Provider>
    );
};
