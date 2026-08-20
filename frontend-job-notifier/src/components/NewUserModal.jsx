import React, { useState } from 'react';
import { createPortal } from 'react-dom';
import { adminApi } from '../services/apiClient';

export default function NewUserModal({ onClose, onRefresh }) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER');
    const [enabled, setEnabled] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleCreate = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await adminApi.createUser({
                name,
                email,
                password,
                role,
                enabled
            });
            onRefresh();
            onClose();
        } catch (err) {
            console.error("Creation failed", err);
            // If the error response has a message, we could grab it here
            setError("Failed to create user. Please ensure the email is unique.");
            setIsLoading(false);
        }
    };

    return createPortal(
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose}></div>
            <div className="bg-slate-900 border border-white/10 w-full max-w-md rounded-2xl shadow-2xl relative z-10 max-h-[90vh] flex flex-col transform scale-100 transition-all">
                <div className="p-6 border-b border-white/10">
                    <h3 className="text-xl font-bold text-white">Register New User</h3>
                    <p className="text-sm text-gray-400 mt-1">Create an account for a new member.</p>
                </div>

                <div className="p-6 overflow-y-auto flex-1">
                    {error && (
                        <div className="mb-4 bg-red-500/20 text-red-200 border border-red-500/50 p-3 rounded-xl text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleCreate} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Full Name</label>
                            <input
                                type="text"
                                required
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Email Address</label>
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Temporary Password</label>
                            <input
                                type="password"
                                required
                                minLength={6}
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Role</label>
                            <select
                                value={role}
                                onChange={(e) => setRole(e.target.value)}
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none appearance-none"
                            >
                                <option value="USER" className="bg-slate-800">USER</option>
                                <option value="ADMIN" className="bg-slate-800">ADMIN</option>
                            </select>
                        </div>

                        <div className="flex items-center space-x-3 pt-2">
                            <input
                                type="checkbox"
                                id="enabled-switch"
                                checked={enabled}
                                onChange={(e) => setEnabled(e.target.checked)}
                                className="w-5 h-5 rounded rounded-full text-emerald-500 bg-gray-800 border-gray-600 focus:ring-emerald-500"
                            />
                            <label htmlFor="enabled-switch" className="text-sm font-medium text-gray-300">
                                Account Enabled (Active)
                            </label>
                        </div>

                        <div className="mt-8 flex items-center justify-end space-x-3 pt-4 border-t border-white/10">
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
                                {isLoading ? 'Creating...' : 'Create User'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>,
        document.body
    );
}
