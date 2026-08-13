import AdminLayout from '../components/AdminLayout';
import UsersTable from '../components/UsersTable';
import NewUserModal from '../components/NewUserModal';
import { useState } from 'react';

export default function AdminDashboard() {
    const [isCreating, setIsCreating] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <AdminLayout>
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h2 className="text-3xl font-bold text-white tracking-tight">Registered Users</h2>
                    <p className="text-emerald-200/60 mt-1">Manage system members, roles, and configure their preferences.</p>
                </div>
                <button
                    onClick={() => setIsCreating(true)}
                    className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-xl font-medium shadow-lg transition-colors border border-emerald-400/30 flex items-center space-x-2"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
                    <span>New User</span>
                </button>
            </div>

            <UsersTable key={refreshKey} />
            {isCreating && (
                <NewUserModal
                    onClose={() => setIsCreating(false)}
                    onRefresh={() => setRefreshKey(prev => prev + 1)}
                />
            )}
        </AdminLayout>
    );
}
