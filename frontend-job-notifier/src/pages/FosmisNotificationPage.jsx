import React, { useState } from 'react';
import { fosmisPublicApi } from '../services/apiClient';

export default function FosmisNotificationPage() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [status, setStatus] = useState('idle'); // idle, loading, success, error
    const [message, setMessage] = useState('');
    const [usernameError, setUsernameError] = useState('');

    const handleUsernameChange = (e) => {
        const val = e.target.value;
        setUsername(val);

        if (status === 'error') {
            setStatus('idle');
            setMessage('');
        }

        if (!val) {
            setUsernameError('');
            return;
        }

        const lowerCaseUsername = val.toLowerCase();
        const usernameRegex = /^sc\d{5}$/i;

        if (!usernameRegex.test(lowerCaseUsername)) {
            setUsernameError("Username must be in format scXXXXX (5 digits).");
            return;
        }

        const numericPart = parseInt(lowerCaseUsername.substring(2), 10);
        if (numericPart < 10000 || numericPart > 18000) {
            setUsernameError("The username may not exist yet, or the university membership may have expired.");
            return;
        }

        setUsernameError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setStatus('loading');

        const lowerCaseUsername = username.toLowerCase();
        const usernameRegex = /^sc\d{5}$/i;

        if (!usernameRegex.test(lowerCaseUsername)) {
            setStatus('error');
            setMessage("Username must be in format scXXXXX (5 digits).");
            return;
        }

        const numericPart = parseInt(lowerCaseUsername.substring(2), 10);
        if (numericPart < 10000 || numericPart > 18000) {
            setStatus('error');
            setMessage("The username may not exist yet, or the university membership may have expired.");
            return;
        }

        try {
            await fosmisPublicApi.subscribe({ username: lowerCaseUsername, email, isEnabled: true });
            setStatus('success');
            setMessage("Successfully subscribed! You will now receive notifications.");
            setUsername('');
            setEmail('');
        } catch (error) {
            setStatus('error');
            setMessage(error.response?.data?.message || "Failed to subscribe. Please try again.");
        }
    };

    return (
        <div className="min-h-screen bg-[#020617] text-white flex flex-col items-center justify-center p-4 relative overflow-hidden">
            {/* Background elements */}
            <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-emerald-500/10 rounded-full blur-[100px]" />
            <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-teal-500/10 rounded-full blur-[100px]" />

            <div className="w-full max-w-md relative z-10">
                <div className="text-center mb-10">
                    <h1 className="text-4xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-emerald-400 to-teal-400 mb-4">
                        FOSMIS Notifications
                    </h1>
                    <p className="text-lg text-emerald-200/80 leading-relaxed font-medium">
                        Welcome to University of Ruhuna Science Faculty FOSMIS Notification Service. Subscribe to get notified when new notice is published.
                    </p>
                </div>

                <div className="bg-white/5 border border-white/10 rounded-3xl p-8 shadow-2xl backdrop-blur-xl">
                    {status === 'success' && (
                        <div className="mb-6 p-4 bg-emerald-500/20 border border-emerald-500/50 rounded-xl text-emerald-200 text-center font-medium">
                            {message}
                        </div>
                    )}
                    {status === 'error' && (
                        <div className="mb-6 p-4 bg-rose-500/20 border border-rose-500/50 rounded-xl text-rose-200 text-center font-medium">
                            {message}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">SC Number</label>
                            <input
                                type="text"
                                required
                                placeholder="sc12345"
                                value={username}
                                onChange={handleUsernameChange}
                                className={`w-full px-5 py-3.5 bg-black/40 border ${usernameError ? 'border-rose-500/50 focus:ring-rose-500' : 'border-white/10 focus:ring-emerald-500'} rounded-xl focus:ring-2 focus:border-transparent text-white outline-none placeholder-gray-600 transition-all font-mono`}
                            />
                            {usernameError && (
                                <p className="mt-2 text-sm text-rose-400 font-medium">{usernameError}</p>
                            )}
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Email Address</label>
                            <input
                                type="email"
                                required
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-5 py-3.5 bg-black/40 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white outline-none placeholder-gray-600 transition-all"
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={status === 'loading' || !!usernameError}
                            className="w-full py-3.5 px-4 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 rounded-xl text-white font-bold tracking-wide shadow-lg hover:shadow-emerald-500/25 transition-all outline-none disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {status === 'loading' ? 'Subscribing...' : 'Subscribe Now'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
