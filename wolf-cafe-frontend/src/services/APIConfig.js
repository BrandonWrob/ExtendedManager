// Function to dynamically determine the base URL
const getBaseUrl = () => {
  // Check if the app is running in a local development environment
  if (window.location.hostname === 'localhost') {
    return 'http://localhost:8080/api'; // Use localhost for development
  }

  // Fallback to using the machine's IP address for external devices
  const machineIp = '<YOUR_MACHINE_IP>'; // Replace with your machine's IP
  return `http://${machineIp}:8080/api`;
};

export const API_BASE_URL = getBaseUrl();