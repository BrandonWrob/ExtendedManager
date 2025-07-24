import axios from 'axios';
import { getToken } from './AuthService';
import { API_BASE_URL } from '../services/APIConfig';

const TAX_API_BASE_URL = `${API_BASE_URL}/tax`

axios.interceptors.request.use(function (config) {
  const token = getToken()
  if (token) {
    config.headers['Authorization'] = token
  }
  config.headers['Content-Type'] = 'application/json'  // Ensure Content-Type is JSON
  return config
}, function (error) {
  return Promise.reject(error)
})

export const getTaxRate = () => axios.get(TAX_API_BASE_URL);

export const setTaxRate = (rate) => axios.put(TAX_API_BASE_URL, rate, {
  headers: {
    'Content-Type': 'application/json',
  }
});
export const calculateTax = (preTaxAmount) =>
  axios.get(`${TAX_API_BASE_URL}/calc/${preTaxAmount}`);