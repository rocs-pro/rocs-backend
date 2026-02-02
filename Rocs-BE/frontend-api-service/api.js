/**
 * ============================================
 * SmartRetail Pro - API Service
 * ============================================
 *
 * This file handles all communication between
 * the React frontend and Spring Boot backend.
 *
 * HOW TO USE:
 * 1. Copy this file to: src/services/api.js
 * 2. Install axios: npm install axios
 * 3. Import in your components:
 *    import { productsAPI, categoriesAPI } from '../services/api';
 *
 * ============================================
 */

import axios from 'axios';

// ===========================================
// CONFIGURATION
// ===========================================

// Backend server URL - Change this if your backend runs on a different port
const API_BASE_URL = 'http://localhost:8080';

// Create axios instance with default config
const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000, // 30 seconds timeout
    headers: {
        'Content-Type': 'application/json',
    },
});

// ===========================================
// REQUEST INTERCEPTOR
// Automatically adds JWT token to all requests
// ===========================================

api.interceptors.request.use(
    (config) => {
        // Get token from localStorage
        const token = localStorage.getItem('token');

        // If token exists, add it to the Authorization header
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        // Log request for debugging (remove in production)
        console.log(`📤 ${config.method?.toUpperCase()} ${config.url}`);

        return config;
    },
    (error) => {
        console.error('Request Error:', error);
        return Promise.reject(error);
    }
);

// ===========================================
// RESPONSE INTERCEPTOR
// Handles responses and errors globally
// ===========================================

api.interceptors.response.use(
    (response) => {
        // Log successful response (remove in production)
        console.log(`📥 ${response.status} ${response.config.url}`);
        return response;
    },
    (error) => {
        // Handle different error types
        if (error.response) {
            // Server responded with error status
            const status = error.response.status;

            switch (status) {
                case 401:
                    // Unauthorized - token expired or invalid
                    console.error('❌ Unauthorized - Please login again');
                    localStorage.removeItem('token');
                    // Redirect to login page
                    window.location.href = '/login';
                    break;
                case 403:
                    console.error('❌ Forbidden - You don\'t have permission');
                    break;
                case 404:
                    console.error('❌ Not Found - Resource doesn\'t exist');
                    break;
                case 500:
                    console.error('❌ Server Error - Something went wrong on the server');
                    break;
                default:
                    console.error(`❌ Error ${status}:`, error.response.data);
            }
        } else if (error.request) {
            // No response received (network error)
            console.error('❌ Network Error - Cannot reach the server');
            console.error('Make sure backend is running on', API_BASE_URL);
        } else {
            // Error setting up the request
            console.error('❌ Error:', error.message);
        }

        return Promise.reject(error);
    }
);

// ===========================================
// AUTHENTICATION API
// ===========================================

export const authAPI = {
    /**
     * Login user
     * @param {string} username
     * @param {string} password
     * @returns {Promise} Response with token
     */
    login: async (username, password) => {
        try {
            const response = await api.post('/api/v1/auth/login', {
                username,
                password,
            });

            // Save token to localStorage
            if (response.data && response.data.token) {
                localStorage.setItem('token', response.data.token);
                console.log('✅ Login successful, token saved');
            }

            return response.data;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    },

    /**
     * Register new user
     * @param {object} userData User registration data
     * @returns {Promise} Response
     */
    register: async (userData) => {
        const response = await api.post('/api/v1/auth/register', userData);
        return response.data;
    },

    /**
     * Logout user - clears token from localStorage
     */
    logout: () => {
        localStorage.removeItem('token');
        console.log('✅ Logged out, token removed');
    },

    /**
     * Check if user is authenticated
     * @returns {boolean}
     */
    isAuthenticated: () => {
        return !!localStorage.getItem('token');
    },

    /**
     * Get current token
     * @returns {string|null}
     */
    getToken: () => {
        return localStorage.getItem('token');
    },
};

// ===========================================
// PRODUCTS API
// ===========================================

export const productsAPI = {
    /**
     * Get all products
     * @returns {Promise} List of products
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/products');
        return response.data;
    },

    /**
     * Get product by ID
     * @param {number} id Product ID
     * @returns {Promise} Product data
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/products/${id}`);
        return response.data;
    },

    /**
     * Create new product
     * @param {object} productData Product data
     * @returns {Promise} Created product
     *
     * Example productData:
     * {
     *   sku: "BEV-001",
     *   barcode: "1234567890123",
     *   name: "Coca Cola 500ml",
     *   description: "Soft drink",
     *   categoryId: 1,
     *   brandId: 1,
     *   unitId: 1,
     *   costPrice: 80.00,
     *   sellingPrice: 100.00,
     *   mrp: 110.00,
     *   reorderLevel: 50,
     *   taxRate: 10.00,
     *   isActive: true
     * }
     */
    create: async (productData) => {
        const response = await api.post('/api/inventory/products', productData);
        return response.data;
    },

    /**
     * Update product
     * @param {number} id Product ID
     * @param {object} productData Updated product data
     * @returns {Promise} Updated product
     */
    update: async (id, productData) => {
        const response = await api.put(`/api/inventory/products/${id}`, productData);
        return response.data;
    },

    /**
     * Delete product
     * @param {number} id Product ID
     * @returns {Promise} Response
     */
    delete: async (id) => {
        const response = await api.delete(`/api/inventory/products/${id}`);
        return response.data;
    },

    /**
     * Search products
     * @param {string} query Search query
     * @returns {Promise} List of matching products
     */
    search: async (query) => {
        const response = await api.get(`/api/inventory/products/search?q=${encodeURIComponent(query)}`);
        return response.data;
    },
};

// ===========================================
// CATEGORIES API
// ===========================================

export const categoriesAPI = {
    /**
     * Get all categories
     * @returns {Promise} List of categories
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/categories');
        return response.data;
    },

    /**
     * Get category by ID
     * @param {number} id Category ID
     * @returns {Promise} Category data
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/categories/${id}`);
        return response.data;
    },

    /**
     * Create new category
     * @param {object} categoryData Category data
     * @returns {Promise} Created category
     *
     * Example categoryData:
     * {
     *   name: "Beverages",
     *   description: "Drinks and beverages",
     *   isActive: true
     * }
     */
    create: async (categoryData) => {
        const response = await api.post('/api/inventory/categories', categoryData);
        return response.data;
    },

    /**
     * Update category
     * @param {number} id Category ID
     * @param {object} categoryData Updated category data
     * @returns {Promise} Updated category
     */
    update: async (id, categoryData) => {
        const response = await api.put(`/api/inventory/categories/${id}`, categoryData);
        return response.data;
    },

    /**
     * Delete category
     * @param {number} id Category ID
     * @returns {Promise} Response
     */
    delete: async (id) => {
        const response = await api.delete(`/api/inventory/categories/${id}`);
        return response.data;
    },
};

// ===========================================
// BRANDS API
// ===========================================

export const brandsAPI = {
    /**
     * Get all brands
     * @returns {Promise} List of brands
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/brands');
        return response.data;
    },

    /**
     * Get brand by ID
     * @param {number} id Brand ID
     * @returns {Promise} Brand data
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/brands/${id}`);
        return response.data;
    },

    /**
     * Create new brand
     * @param {object} brandData Brand data
     * @returns {Promise} Created brand
     *
     * Example brandData:
     * {
     *   name: "Coca Cola",
     *   description: "The Coca-Cola Company",
     *   isActive: true
     * }
     */
    create: async (brandData) => {
        const response = await api.post('/api/inventory/brands', brandData);
        return response.data;
    },

    /**
     * Update brand
     * @param {number} id Brand ID
     * @param {object} brandData Updated brand data
     * @returns {Promise} Updated brand
     */
    update: async (id, brandData) => {
        const response = await api.put(`/api/inventory/brands/${id}`, brandData);
        return response.data;
    },

    /**
     * Delete brand
     * @param {number} id Brand ID
     * @returns {Promise} Response
     */
    delete: async (id) => {
        const response = await api.delete(`/api/inventory/brands/${id}`);
        return response.data;
    },
};

// ===========================================
// SUPPLIERS API
// ===========================================

export const suppliersAPI = {
    /**
     * Get all suppliers
     * @returns {Promise} List of suppliers
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/suppliers');
        return response.data;
    },

    /**
     * Get supplier by ID
     * @param {number} id Supplier ID
     * @returns {Promise} Supplier data with contacts and branches
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/suppliers/${id}`);
        return response.data;
    },

    /**
     * Create new supplier
     * @param {object} supplierData Supplier data
     * @returns {Promise} Created supplier
     *
     * Example supplierData:
     * {
     *   code: "SUP001",
     *   name: "ABC Distributors",
     *   companyName: "ABC Distributors Pvt Ltd",
     *   contactPerson: "John Doe",
     *   phone: "0112345678",
     *   email: "john@abc.com",
     *   addressLine1: "123 Main Street",
     *   city: "Colombo",
     *   country: "Sri Lanka",
     *   creditDays: 30,
     *   creditLimit: 100000.00,
     *   isActive: true,
     *   contacts: [
     *     {
     *       name: "John Doe",
     *       designation: "Sales Manager",
     *       phone: "0771234567",
     *       email: "john@abc.com",
     *       isPrimary: true
     *     }
     *   ],
     *   branches: [
     *     {
     *       branchId: 1,
     *       isPreferred: true,
     *       discountPercentage: 5.0,
     *       notes: "Main branch supplier"
     *     }
     *   ]
     * }
     */
    create: async (supplierData) => {
        const response = await api.post('/api/inventory/suppliers', supplierData);
        return response.data;
    },

    /**
     * Update supplier
     * @param {number} id Supplier ID
     * @param {object} supplierData Updated supplier data
     * @returns {Promise} Updated supplier
     */
    update: async (id, supplierData) => {
        const response = await api.put(`/api/inventory/suppliers/${id}`, supplierData);
        return response.data;
    },

    /**
     * Delete supplier
     * @param {number} id Supplier ID
     * @returns {Promise} Response
     */
    delete: async (id) => {
        const response = await api.delete(`/api/inventory/suppliers/${id}`);
        return response.data;
    },
};

// ===========================================
// UNITS API
// ===========================================

export const unitsAPI = {
    /**
     * Get all units
     * @returns {Promise} List of units
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/units');
        return response.data;
    },

    /**
     * Get unit by ID
     * @param {number} id Unit ID
     * @returns {Promise} Unit data
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/units/${id}`);
        return response.data;
    },

    /**
     * Create new unit
     * @param {object} unitData Unit data
     * @returns {Promise} Created unit
     *
     * Example unitData:
     * {
     *   name: "Kilogram",
     *   symbol: "kg"
     * }
     */
    create: async (unitData) => {
        const response = await api.post('/api/inventory/units', unitData);
        return response.data;
    },

    /**
     * Update unit
     * @param {number} id Unit ID
     * @param {object} unitData Updated unit data
     * @returns {Promise} Updated unit
     */
    update: async (id, unitData) => {
        const response = await api.put(`/api/inventory/units/${id}`, unitData);
        return response.data;
    },

    /**
     * Delete unit
     * @param {number} id Unit ID
     * @returns {Promise} Response
     */
    delete: async (id) => {
        const response = await api.delete(`/api/inventory/units/${id}`);
        return response.data;
    },
};

// ===========================================
// STOCK API (for Stock Operations)
// ===========================================

export const stockAPI = {
    /**
     * Get stock overview
     * @param {number} branchId Optional branch ID filter
     * @returns {Promise} Stock data
     */
    getOverview: async (branchId = null) => {
        const url = branchId
            ? `/api/inventory/stock?branchId=${branchId}`
            : '/api/inventory/stock';
        const response = await api.get(url);
        return response.data;
    },

    /**
     * Get stock by product
     * @param {number} productId Product ID
     * @returns {Promise} Stock data for product
     */
    getByProduct: async (productId) => {
        const response = await api.get(`/api/inventory/stock/product/${productId}`);
        return response.data;
    },

    /**
     * Create stock adjustment
     * @param {object} adjustmentData Adjustment data
     * @returns {Promise} Created adjustment
     */
    createAdjustment: async (adjustmentData) => {
        const response = await api.post('/api/inventory/stock/adjustment', adjustmentData);
        return response.data;
    },
};

// ===========================================
// BATCHES API
// ===========================================

export const batchesAPI = {
    /**
     * Get all batches
     * @returns {Promise} List of batches
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/batches');
        return response.data;
    },

    /**
     * Get batches by product
     * @param {number} productId Product ID
     * @returns {Promise} List of batches for product
     */
    getByProduct: async (productId) => {
        const response = await api.get(`/api/inventory/batches/product/${productId}`);
        return response.data;
    },

    /**
     * Get expiring batches
     * @param {number} days Number of days to check
     * @returns {Promise} List of batches expiring within days
     */
    getExpiring: async (days = 30) => {
        const response = await api.get(`/api/inventory/batches/expiring?days=${days}`);
        return response.data;
    },
};

// ===========================================
// STOCK TRANSFERS API
// ===========================================

export const transfersAPI = {
    /**
     * Get all transfers
     * @returns {Promise} List of transfers
     */
    getAll: async () => {
        const response = await api.get('/api/inventory/transfers');
        return response.data;
    },

    /**
     * Get transfer by ID
     * @param {number} id Transfer ID
     * @returns {Promise} Transfer data
     */
    getById: async (id) => {
        const response = await api.get(`/api/inventory/transfers/${id}`);
        return response.data;
    },

    /**
     * Create stock transfer
     * @param {object} transferData Transfer data
     * @returns {Promise} Created transfer
     */
    create: async (transferData) => {
        const response = await api.post('/api/inventory/transfers', transferData);
        return response.data;
    },

    /**
     * Approve transfer
     * @param {number} id Transfer ID
     * @returns {Promise} Approved transfer
     */
    approve: async (id) => {
        const response = await api.put(`/api/inventory/transfers/${id}/approve`);
        return response.data;
    },

    /**
     * Reject transfer
     * @param {number} id Transfer ID
     * @param {string} reason Rejection reason
     * @returns {Promise} Rejected transfer
     */
    reject: async (id, reason) => {
        const response = await api.put(`/api/inventory/transfers/${id}/reject`, { reason });
        return response.data;
    },
};

// ===========================================
// HELPER FUNCTIONS
// ===========================================

/**
 * Handle API response
 * @param {Promise} apiCall The API call promise
 * @returns {Promise} Extracted data or error
 */
export const handleResponse = async (apiCall) => {
    try {
        const response = await apiCall;
        if (response.success) {
            return { success: true, data: response.data };
        } else {
            return { success: false, error: response.message || 'Operation failed' };
        }
    } catch (error) {
        return {
            success: false,
            error: error.response?.data?.message || error.message || 'An error occurred'
        };
    }
};

// ===========================================
// DEFAULT EXPORT
// ===========================================

export default api;

