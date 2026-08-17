import axios from "axios";

const BASE_URL = "http://localhost:8080";

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

export const authApi = {
    login: (data) => apiClient.post('/auth/login', data),
    register: (data) => apiClient.post('/auth/register', data)
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
    register: (data) => apiClient.post('/auth/register', data)
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
