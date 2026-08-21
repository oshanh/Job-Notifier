import axios from "axios";

// In production, point to the self-hosted IP on exactly port 8080. Locally, fall back to localhost.
const BASE_URL = import.meta.env.PROD ? `${window.location.protocol}//${window.location.hostname}:8080` : "http://localhost:8080";

const apiClient = axios.create({
    baseURL: BASE_URL
});

apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem('email');

            if (window.location.pathname !== '/login' && window.location.pathname !== '/admin/login') {
                if (window.location.pathname.startsWith('/admin')) {
                    window.location.href = '/admin/login';
                } else {
                    window.location.href = '/login';
                }
            }
        }
        return Promise.reject(error);
    }
);

export const authApi = {
    login: (data) => apiClient.post('/auth/login', data),
    register: (data) => apiClient.post('/auth/register', data),
    verifyRegistration: (data) => apiClient.post('/auth/verify-registration', data),
    forgotPassword: (data) => apiClient.post('/auth/forgot-password', data),
    resetPassword: (data) => apiClient.post('/auth/reset-password', data)
};
export const adminApi = {
    getAllUsers: () => apiClient.get('/admin/users/all'),
    updateUser: (data) => apiClient.put('/admin/users/update', data),
    deleteUser: (data) => apiClient.delete('/admin/users/delete', { data }),
    createUser: (data) => apiClient.post('/admin/users/add', data)
};

export const userApi = {
    getProfile: () => apiClient.get('/user/me'),
    updateProfile: (data) => apiClient.put('/user/me', data),
    register: (data) => apiClient.post('/auth/register', data), // Used generically sometimes
    requestEmailChange: (data) => apiClient.post('/user/request-email-change', data),
    verifyEmailChange: (data) => apiClient.post('/user/verify-email-change', data)
};

export const prefApi = {
    getByEmail: (email) => apiClient.get(`/pref?email=${email}`),
    create: (data) => apiClient.post('/pref', data),
    update: (data) => apiClient.put('/pref', data),
    delete: (email) => apiClient.delete(`/pref?email=${email}`)
};

export const fosmisApi = {
    getAllUsers: () => apiClient.get('/fosmis'),
    getUserByUsername: (username) => apiClient.get(`/fosmis/${username}`),
    createUser: (data) => apiClient.post('/fosmis', data),
    updateUser: (username, data) => apiClient.put(`/fosmis/${username}`, data),
    deleteUser: (username) => apiClient.delete(`/fosmis/${username}`)
};

export const websiteApi = {
    getAll: () => apiClient.get('/websites'),
    create: (data) => apiClient.post('/websites', data),
    update: (baseURL, data) => apiClient.put(`/websites?url=${encodeURIComponent(baseURL)}`, data),
    softDelete: (baseURL) => apiClient.patch(`/websites/disable?url=${encodeURIComponent(baseURL)}`),
    hardDelete: (baseURL) => apiClient.delete(`/websites?url=${encodeURIComponent(baseURL)}`)
};

export const fosmisPublicApi = {
    subscribe: (data) => apiClient.post('/fosmis-notification', data)
};

export function sendTestGmail(data) {
    return apiClient.post(`/test/gmail`, data);
}
