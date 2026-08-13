import React, { useState, useContext } from 'react';
import { AuthContext } from '../components/AuthContext';
import { userApi } from '../services/apiClient';
import { useNavigate, Link } from 'react-router-dom';

export default function UserLoginPage() {
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const [isRegistering, setIsRegistering] = useState(false);
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            if (isRegistering) {
                // Register Flow
                await userApi.register({ name, email, password });
                // We auto-login immediately after register
                const loginResult = await login(email, password);
                if (loginResult.success) {
                    navigate('/profile');
                } else {
                    setError("Registered successfully, but failed to auto-Login.");
                }
            } else {
                // Login Flow
                const loginResult = await login(email, password);
                if (loginResult.success) {
                    navigate('/profile');
                } else {
                    setError(loginResult.message);
                }
            }
        } catch (err) {
            console.error("Auth process failed", err);
            setError(err.response?.data?.message || err.response?.data || "Authentication error occurred");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-900 via-slate-900 to-black p-4 relative overflow-hidden">
            <div className="absolute top-1/4 right-1/4 w-[600px] h-[600px] bg-teal-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>

            <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 shadow-2xl relative z-10">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-extrabold text-white tracking-tight">
                        {isRegistering ? 'Create Account' : 'Welcome Back'}
                    </h2>
                    <p className="text-sm text-emerald-200 mt-2">
                        {isRegistering ? 'Sign up for instant automated career alerts' : 'Sign in to monitor your active job signals'}
                    </p>
                </div>

                {error && (
                    <div className="bg-red-500/20 border border-red-500/50 text-red-200 px-4 py-3 rounded-lg mb-6 text-sm">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-5">
                    {isRegistering && (
                        <div>
                            <label className="block text-sm font-medium text-gray-200 mb-2">Display Name</label>
                            <input
                                type="text"
                                required
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                placeholder="Your Name"
                            />
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">Email Address</label>
                        <input
                            type="email"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                            placeholder="you@domain.com"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-200 mb-2">Password</label>
                        <input
                            type="password"
                            required
                            minLength={6}
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                            placeholder="••••••••"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full py-3 px-4 mt-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold rounded-xl shadow-lg transform transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isLoading ? 'Processing...' : isRegistering ? 'Sign Up' : 'Sign In'}
                    </button>

                    <div className="text-center mt-6 text-sm">
                        <span className="text-gray-400">
                            {isRegistering ? 'Already have an account?' : 'Need to join?'}
                        </span>
                        <button
                            type="button"
                            onClick={() => setIsRegistering(!isRegistering)}
                            className="ml-2 font-medium text-emerald-400 hover:text-emerald-300 transition-colors cursor-pointer"
                        >
                            {isRegistering ? 'Sign In' : 'Register'}
                        </button>
                    </div>

                    <div className="text-center mt-2 border-t border-white/10 pt-4">
                        <Link to="/" className="text-xs text-gray-500 hover:text-gray-300 transition-colors">
                            &larr; Back to Home
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}
