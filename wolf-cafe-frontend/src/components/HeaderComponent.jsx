import React, { useState, useEffect, useRef } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { isAdminUser, isManagerUser, isStaffUser, isUserLoggedIn, logout } from '../services/AuthService';
import AccessibilitySettingsComponent from './AccessibilitySettingsComponent';
import { FaEye, FaChevronDown, FaChevronUp } from 'react-icons/fa';

const HeaderComponent = () => {
    const isAuth = isUserLoggedIn();
    const isAdmin = isAdminUser();
    const isManager = isManagerUser();
    const isStaff = isStaffUser();

    const [showAccessibilitySettings, setShowAccessibilitySettings] = useState(false);
    const [dropdownOpen, setDropdownOpen] = useState(false);

    const navigator = useNavigate();
    const dropdownRef = useRef(null); // Ref for the dropdown menu

    function handleLogout() {
        logout();
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userName');
        localStorage.removeItem('userId');
        localStorage.removeItem('userUsername');
        localStorage.removeItem('role');
        localStorage.removeItem('token');
        navigator('/login');
    }

    const toggleDropdown = () => {
        setDropdownOpen((prev) => !prev);
    };

    const closeDropdown = () => {
        setDropdownOpen(false);
    };

    // Detect clicks outside the dropdown to close it
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                closeDropdown();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    // Prevent body scroll when dropdown is open
    useEffect(() => {
        if (dropdownOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'auto';
        }
        return () => {
            document.body.style.overflow = 'auto';
        };
    }, [dropdownOpen]);

    return (
        <div>
            <header>
                <nav className="navbar navbar-expand-xl navbar-dark bg-dark">
                    <div className="container header-container" ref={dropdownRef}>
                        <NavLink to="/home" className="navbar-brand">
                            WolfCafe
                        </NavLink>
                        <button
                            className="navbar-toggler"
                            type="button"
                            onClick={toggleDropdown}
                            aria-expanded={dropdownOpen}
                            aria-label="Toggle navigation"
                        >
                            {dropdownOpen ? <FaChevronUp /> : <FaChevronDown />}
                        </button>
                        <div className={`collapse navbar-collapse ${dropdownOpen ? 'show' : ''}`}>
                            <ul className="navbar-nav me-auto">
                                {isAuth && (
                                    <li className="nav-item">
                                        <NavLink to="/home" className="nav-link" onClick={closeDropdown}>
                                            Make Order
                                        </NavLink>
                                    </li>
                                )}
                                {isAuth && (isAdmin || isManager) && (
                                    <>
                                        <li className="nav-item">
                                            <NavLink to="/recipes" className="nav-link" onClick={closeDropdown}>
                                                Recipes
                                            </NavLink>
                                        </li>
                                        <li className="nav-item">
                                            <NavLink to="/add-ingredient" className="nav-link" onClick={closeDropdown}>
                                                Ingredient
                                            </NavLink>
                                        </li>
                                        <li className="nav-item">
                                            <NavLink to="/inventory" className="nav-link" onClick={closeDropdown}>
                                                Inventory
                                            </NavLink>
                                        </li>
                                    </>
                                )}
                                {isAuth && (isAdmin || isManager || isStaff || isUserLoggedIn) && (
                                    <li className="nav-item">
                                        <NavLink to="/orders" className="nav-link" onClick={closeDropdown}>
                                            Order
                                        </NavLink>
                                    </li>
                                )}
                                {isAuth && (
                                    <li className="nav-item">
                                        <NavLink to="/history" className="nav-link" onClick={closeDropdown}>
                                            History
                                        </NavLink>
                                    </li>
                                )}
                                {isAuth && isAdmin && (
                                    <>
                                        <li className="nav-item">
                                            <NavLink to="/create-account" className="nav-link" onClick={closeDropdown}>
                                                Create Accounts
                                            </NavLink>
                                        </li>
                                        <li className="nav-item">
                                            <NavLink to="/manage-account" className="nav-link" onClick={closeDropdown}>
                                                Manage Accounts
                                            </NavLink>
                                        </li>
                                    </>
                                )}
                            </ul>
                            <ul className="navbar-nav ms-auto">
                                <li className="nav-item me-3">
                                    <button
                                        onClick={() => setShowAccessibilitySettings(true)}
                                        aria-label="Accessibility Settings"
                                        className="accessibility-button"
                                    >
                                        <FaEye size={20} />
                                    </button>
                                </li>
                                {!isAuth && (
                                    <>
                                        <li className="nav-item">
                                            <NavLink to="/register" className="nav-link">
                                                Register
                                            </NavLink>
                                        </li>
                                        <li className="nav-item">
                                            <NavLink to="/login" className="nav-link">
                                                Login
                                            </NavLink>
                                        </li>
                                    </>
                                )}
                                {isAuth && (
                                    <li className="nav-item">
                                        <NavLink to="/login" className="nav-link" onClick={handleLogout}>
                                            Logout
                                        </NavLink>
                                    </li>
                                )}
                            </ul>
                        </div>
                    </div>
                </nav>
            </header>
            <AccessibilitySettingsComponent
                show={showAccessibilitySettings}
                handleClose={() => setShowAccessibilitySettings(false)}
            />
        </div>
    );
};

export default HeaderComponent;
