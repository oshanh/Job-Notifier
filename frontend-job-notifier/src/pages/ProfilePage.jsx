import React, { useContext } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import UserLayout from '../components/UserLayout';
import ProfileSettingsTab from '../components/ProfileSettingsTab';
import ProfilePreferencesTab from '../components/ProfilePreferencesTab';
import { AuthContext } from '../components/AuthContext';

export default function ProfilePage() {
    const { identity } = useContext(AuthContext);
    const email = identity?.sub || "Unknown User";

    return (
        <UserLayout>
            <div className="mb-6 border-b border-white/10 pb-4">
                <h2 className="text-3xl font-extrabold text-white tracking-tight">Access Control</h2>
                <p className="text-emerald-200/60 mt-1">Manage your identity mappings and notification algorithms.</p>
            </div>

            <div className="w-full relative z-10">
                <Routes>
                    <Route path="/" element={<Navigate to="preferences" replace />} />
                    <Route
                        path="preferences"
                        element={<ProfilePreferencesTab email={email} />}
                    />
                    <Route
                        path="settings"
                        element={<ProfileSettingsTab email={email} />}
                    />
                </Routes>
            </div>
        </UserLayout>
    );
}
