// src/components/AccessibilitySettingsComponent.jsx
import React, { useContext } from 'react';
import { Modal, Button, ButtonGroup } from 'react-bootstrap';
import { FaSun, FaMoon, FaAdjust } from 'react-icons/fa'; // Icons for modes
import './AccessibilitySettingsComponent.css'; // Import custom CSS
import { ThemeContext } from '../components/ThemeContext'; // Import ThemeContext

const AccessibilitySettingsComponent = ({ show, handleClose }) => {
    const { 
        theme, 
        colorblindMode, 
        changeVisualMode, 
        changeColorblindMode, 
        resetModes 
    } = useContext(ThemeContext);

    const visualModes = ['Normal', 'Dark Mode', 'High Contrast Mode'];
    const colorblindModes = [
        'Red-Green (Red Deficiency)',
        'Red-Green (Green Deficiency)',
        'Blue-Yellow',
    ];

    const handleVisualModeChange = (e) => {
        const value = parseInt(e.target.value);
        const mode = visualModes[value];
        changeVisualMode(mode);
    };

    const handleColorblindModeChange = (mode) => {
        changeColorblindMode(mode);
    };

    const handleReset = () => {
        resetModes();
    };

    const visualModeIndex = visualModes.indexOf(theme);

    return (
        <Modal show={show} onHide={handleClose} centered>
            <Modal.Header className="modal-header-center">
                <Modal.Title className="w-100 text-center">Accessibility Settings</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                {/* Colorblind Modes Section */}
                <div className="mb-4">
                    <h5 className="text-center">Colorblind Modes</h5>
                    <ButtonGroup vertical className="d-flex justify-content-center">
                        {colorblindModes.map((mode, idx) => (
                            <Button
                                key={idx}
                                variant={colorblindMode === mode ? 'primary' : 'outline-primary'}
                                onClick={() => handleColorblindModeChange(mode)}
                                className="mb-2 w-100 colorblind-button"
                            >
                                {mode}
                            </Button>
                        ))}
                    </ButtonGroup>
                </div>
                <hr />
                {/* Visual Modes Slider Section */}
                <div className="mb-4">
                    <h5 className="text-center">Visual Modes</h5>
                    <div className="d-flex flex-column align-items-center">
                        <div className="slider-container position-relative w-100">
                            <input
                                type="range"
                                min="0"
                                max="2"
                                step="1"
                                value={visualModeIndex}
                                onChange={handleVisualModeChange}
                                className="visual-mode-slider w-100"
                            />
                            <div
                                className="slider-thumb-icon position-absolute"
                                style={{ left: `calc(${(visualModeIndex / 2) * 100}% - 20px)` }} // Adjust based on thumb size
                            >
                                {visualModeIndex === 0 && <FaSun size={20} />}
                                {visualModeIndex === 1 && <FaMoon size={20} />}
                                {visualModeIndex === 2 && <FaAdjust size={20} />}
                            </div>
                        </div>
                        <div className="slider-labels d-flex justify-content-between w-100 mt-2">
                            {visualModes.map((mode, idx) => (
                                <div
                                    key={idx}
                                    className={`text-center ${
                                        visualModeIndex === idx ? 'active-label' : ''
                                    }`}
                                    style={{ width: '33%' }}
                                >
                                    <div className="slider-label-text">{mode}</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </Modal.Body>
            <Modal.Footer className="d-flex justify-content-between">
                <Button variant="primary" onClick={handleReset} className="modal-button">
                    Reset
                </Button>
                <Button variant="danger" onClick={handleClose} className="modal-button">
                    Exit
                </Button>
            </Modal.Footer>
        </Modal>
    );
};

export default AccessibilitySettingsComponent;
