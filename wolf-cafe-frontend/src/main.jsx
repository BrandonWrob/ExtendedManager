// src/main.jsx

import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap CSS
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css'; // Import global styles
import { ThemeProvider } from './components/ThemeContext';
import 'bootstrap/dist/css/bootstrap.min.css'; // Import Bootstrap CSS
import './App.css';

ReactDOM.createRoot(document.getElementById('root')).render(
	<React.StrictMode>
	    <ThemeProvider>
	        <App />
	    </ThemeProvider>
	</React.StrictMode>,
)