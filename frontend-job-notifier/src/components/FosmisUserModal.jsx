import React, { useState } from 'react';
import { createPortal } from 'react-dom';
import { fosmisApi } from '../services/apiClient';

export default function FosmisUserModal({ user, onClose, onRefresh }) {
    const isNew = !user;
    const [username, setUsername] = useState(user ? user.username : '');
    const [email, setEmail] = useState(user ? user.email : '');
    const [isEnabled, setIsEnabled] = useState(user ? user.enabled : true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        const lowerCaseUsername = username.toLowerCase();

        const usernameRegex = /^sc\d{5}$/i;
        if (!usernameRegex.test(lowerCaseUsername)) {
            setError("Username must be in format scXXXXX (5 digits).");
            setIsLoading(false);
            return;
        }

        const numericPart = parseInt(lowerCaseUsername.substring(2), 10);
        if (numericPart < 10000 || numericPart > 18000) {
            setError("The username may not exist yet, or the university membership may have expired.");
            setIsLoading(false);
            return;
        }

        try {
            const data = { username: lowerCaseUsername, email, isEnabled };

            if (isNew) {
                await fosmisApi.createUser(data);
            } else {
                await fosmisApi.updateUser(user.username, data);
            }
            onRefresh();
            onClose();
        } catch (err) {
            console.error("Operation failed", err);
            setError(`Failed to ${isNew ? 'create' : 'update'} user. ${err.response?.data?.message || 'Please try again.'}`);
            setIsLoading(false);
        }
    };

    const handleDelete = async () => {
        if (!confirm(`Are you sure you want to delete ${user.username}? This action cannot be reversed.`)) return;
        setIsLoading(true);
        try {
            await fosmisApi.deleteUser(user.username);
            onRefresh();
            onClose();
        } catch (err) {
            console.error("Delete failed", err);
            setError("Failed to delete user.");
            setIsLoading(false);
        }
    };

    return createPortal(
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose}></div>
            <div className="bg-slate-900 border border-white/10 w-full max-w-md rounded-2xl shadow-2xl relative z-10 max-h-[90vh] flex flex-col transform scale-100 transition-all">
                <div className="p-6 border-b border-white/10">
                    <h3 className="text-xl font-bold text-white">{isNew ? 'New Fosmis User' : 'Edit Fosmis User'}</h3>
                    {!isNew && <p className="text-sm text-gray-400 mt-1">{user.username}</p>}
                </div>

                <div className="p-6 overflow-y-auto flex-1">
                    {error && (
                        <div className="mb-4 bg-red-500/20 text-red-200 border border-red-500/50 p-3 rounded-xl text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Username</label>
                            <input
                                type="text"
                                required
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                disabled={!isNew}
                                placeholder="scxxxxx"
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none disabled:opacity-50"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Email</label>
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div className="flex items-center space-x-3 pt-2">
                            <input
                                type="checkbox"
                                id="fosmis-enabled-switch"
                                checked={isEnabled}
                                onChange={(e) => setIsEnabled(e.target.checked)}
                                className="w-5 h-5 rounded rounded-full text-emerald-500 bg-gray-800 border-gray-600 focus:ring-emerald-500"
                            />
                            <label htmlFor="fosmis-enabled-switch" className="text-sm font-medium text-gray-300">
                                Account Enabled (Active)
                            </label>
                        </div>

                        <div className="mt-8 flex items-center justify-between space-x-4 pt-4 border-t border-white/10">
                            {!isNew ? (
                                <button
                                    type="button"
                                    onClick={handleDelete}
                                    className="text-rose-400 hover:text-rose-300 text-sm font-medium transition-colors"
                                >
                                    Delete User
                                </button>
                            ) : <div></div>}

                            <div className="flex space-x-3">
                                <button
                                    type="button"
                                    onClick={onClose}
                                    className="px-4 py-2 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl text-white transition-colors"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className="px-4 py-2 bg-emerald-600 hover:bg-emerald-500 rounded-xl text-white font-medium transition-colors disabled:opacity-50"
                                >
                                    {isLoading ? 'Saving...' : 'Save Changes'}
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>,
        document.body
    );
}
