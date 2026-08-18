import React, { createContext, useState } from 'react';
import { authApi } from '../services/apiClient';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const parseJwt = (token) => {
        if (!token) return null;
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    };

    const getInitialIdentity = () => {
        const t = localStorage.getItem('token');
        if (t) return parseJwt(t);
        return null;
    };

    const [token, setToken] = useState(localStorage.getItem('token') || null);
    const [identity, setIdentity] = useState(getInitialIdentity());
    const [isAuthenticated, setIsAuthenticated] = useState(!!token);

    const login = async (email, password) => {
        try {
            const response = await authApi.login({ email, password });
            const jwt = response.data.token;
            setToken(jwt);
            setIdentity(parseJwt(jwt));
            setIsAuthenticated(true);
            localStorage.setItem('token', jwt);
            return { success: true };
        } catch (error) {
            console.error("Login failed", error);
            // In case the backend returns a string or standard Spring error map
            const message = error.response?.data?.message || typeof error.response?.data === 'string' ? error.response?.data : 'Invalid credentials or access denied';
            return { success: false, message };
        }
    };

    const logout = () => {
        setToken(null);
        setIdentity(null);
        setIsAuthenticated(false);
        localStorage.removeItem('token');
    };

    const updateToken = (newToken) => {
        setToken(newToken);
        setIdentity(parseJwt(newToken));
        setIsAuthenticated(true);
        localStorage.setItem('token', newToken);
    };

    return (
        <AuthContext.Provider value={{ token, isAuthenticated, identity, login, logout, updateToken }}>
            {children}
        </AuthContext.Provider>
    );
};
