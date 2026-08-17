import React, { useState, useEffect, useContext } from 'react';
import UserLayout from '../components/UserLayout';
import PreferenceModal from '../components/PreferenceModal';
import { AuthContext } from '../components/AuthContext';
import { userApi } from '../services/apiClient';
import { Bell, Sliders } from 'lucide-react';

export default function ProfilePage() {
    const { identity } = useContext(AuthContext);

    // Extracted identity
    const email = identity?.sub || "Unknown User";

    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [statusMessage, setStatusMessage] = useState(null);
    const [showPrefModal, setShowPrefModal] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            setIsLoading(true);
            try {
                const { data } = await userApi.getProfile();
                setName(data.name || '');
            } catch (error) {
                console.error("Failed to load profile context");
            } finally {
                setIsLoading(false);
            }
        };
        fetchProfile();
    }, []);

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setStatusMessage(null);
        try {
            await userApi.updateProfile({
                name,
                password
            });
            setStatusMessage({ type: 'success', text: 'Profile successfully updated!' });
            setPassword(''); // Clear password field after success
        } catch (err) {
            setStatusMessage({ type: 'error', text: 'Could not update profile.' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <UserLayout>
            <div className="mb-8">
                <h2 className="text-3xl font-bold text-white tracking-tight">Access Control</h2>
                <p className="text-emerald-200/60 mt-1">Manage your identity mappings and notification algorithms.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">

                {/* Profile Editor */}
                <div className="bg-white/5 border border-white/10 rounded-2xl p-6 shadow-xl">
                    <h3 className="text-lg font-bold text-white mb-6 border-b border-white/10 pb-4">Personal Details</h3>

                    {statusMessage && (
                        <div className={`mb-6 p-3 rounded-xl border text-sm ${statusMessage.type === 'success' ? 'bg-emerald-500/20 text-emerald-200 border-emerald-500/50' : 'bg-red-500/20 text-red-200 border-red-500/50'}`}>
                            {statusMessage.text}
                        </div>
                    )}

                    <form onSubmit={handleUpdateProfile} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Authenticated Email</label>
                            <input
                                type="text"
                                disabled
                                value={email}
                                className="w-full px-4 py-2 bg-black/50 border border-white/10 rounded-xl text-gray-500 outline-none cursor-not-allowed"
                            />
                            <p className="text-xs text-gray-400 mt-2 ml-1">Identity address is locked to this session.</p>
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Display Name</label>
                            <input
                                type="text"
                                value={name}
                                onChange={e => setName(e.target.value)}
                                placeholder="Update your name..."
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">New Password (Optional)</label>
                            <input
                                type="password"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                placeholder="Leave blank to skip"
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div className="pt-4">
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="w-full py-3 bg-emerald-600 hover:bg-emerald-500 text-white font-medium rounded-xl shadow-lg shadow-emerald-900/20 transition-colors disabled:opacity-50"
                            >
                                {isLoading ? 'Updating Framework...' : 'Save Profile Settings'}
                            </button>
                        </div>
                    </form>
                </div>

                {/* Notifications & Prefs Panel */}
                <div className="bg-white/5 border border-white/10 rounded-2xl p-6 shadow-xl flex flex-col items-center justify-center text-center">
                    <div className="w-20 h-20 bg-emerald-500/20 rounded-full flex items-center justify-center mb-4 border border-emerald-500/30">
                        <Bell className="w-10 h-10 text-emerald-400" />
                    </div>
                    <h3 className="text-xl font-bold text-white mb-2">Notification Routing</h3>
                    <p className="text-sm text-gray-400 max-w-sm mb-8">
                        Configure WhatsApp, Email, or Telegram bridges and assign targeting keywords so we can alert you about new software jobs automatically.
                    </p>

                    <button
                        onClick={() => setShowPrefModal(true)}
                        className="px-6 py-3 bg-emerald-600 hover:bg-emerald-500 text-white rounded-xl font-semibold shadow-lg shadow-emerald-900/30 transition-all transform hover:scale-105 active:scale-95 flex items-center space-x-2"
                    >
                        <Sliders className="w-5 h-5" />
                        <span>Tune Preferences</span>
                    </button>
                </div>
            </div>

            {showPrefModal && (
                <PreferenceModal
                    email={email}
                    onClose={() => setShowPrefModal(false)}
                />
            )}
        </UserLayout>
    );
}
