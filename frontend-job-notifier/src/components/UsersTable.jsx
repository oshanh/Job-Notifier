import React, { useEffect, useState } from 'react';
import { userApi, adminApi } from '../services/apiClient';
import UserModal from './UserModal';
import PreferenceModal from './PreferenceModal';
import { Loader2, Settings, Edit } from 'lucide-react';

export default function UsersTable() {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [editingUser, setEditingUser] = useState(null);
    const [viewingPrefEmail, setViewingPrefEmail] = useState(null);

    const fetchUsers = async () => {
        setIsLoading(true);
        try {
            const { data } = await adminApi.getAllUsers();
            setUsers(data);
        } catch (error) {
            console.error("Failed to fetch users", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    if (isLoading) {
        return (
            <div className="w-full bg-white/5 border border-white/10 rounded-2xl flex items-center justify-center py-20 mt-8">
                <span className="flex items-center space-x-3 text-emerald-300">
                    <Loader2 className="animate-spin h-6 w-6 text-emerald-500" />
                    <span>Loading users matrix...</span>
                </span>
            </div>
        );
    }

    return (
        <div className="w-full bg-white/5 border border-white/10 rounded-2xl overflow-hidden shadow-inner backdrop-blur-md">
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm text-gray-300">
                    <thead className="bg-white/10 text-xs uppercase text-gray-200">
                        <tr>
                            <th className="px-6 py-4 font-medium tracking-wider">User Identity</th>
                            <th className="px-6 py-4 font-medium tracking-wider">Role</th>
                            <th className="px-6 py-4 font-medium tracking-wider">Status</th>
                            <th className="px-6 py-4 font-medium tracking-wider text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-white/5">
                        {users.map((u, i) => (
                            <tr key={u.email} className="hover:bg-white/5 transition-colors">
                                <td className="px-6 py-4">
                                    <div className="font-semibold text-white">{u.name}</div>
                                    <div className="text-xs text-gray-400 mt-1">{u.email}</div>
                                </td>
                                <td className="px-6 py-4">
                                    <span className="px-2.5 py-1 bg-emerald-500/20 text-emerald-300 text-[10px] uppercase font-bold tracking-widest rounded-full border border-emerald-500/30">
                                        {u.role || 'USER'}
                                    </span>
                                </td>
                                <td className="px-6 py-4">
                                    {u.enabled ? (
                                        <span className="flex items-center space-x-2 text-emerald-400">
                                            <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 shadow-[0_0_8px_rgba(16,185,129,0.8)]"></span>
                                            <span className="text-xs">Active</span>
                                        </span>
                                    ) : (
                                        <span className="flex items-center space-x-2 text-rose-400">
                                            <span className="w-1.5 h-1.5 rounded-full bg-rose-500 shadow-[0_0_8px_rgba(244,63,94,0.8)]"></span>
                                            <span className="text-xs">Disabled</span>
                                        </span>
                                    )}
                                </td>
                                <td className="px-6 py-4 text-right space-x-4">
                                    <button onClick={() => setViewingPrefEmail(u.email)} className="inline-flex items-center text-teal-400 hover:text-teal-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none">
                                        <Settings className="w-4 h-4 mr-1" />
                                        Prefs
                                    </button>
                                    <button onClick={() => setEditingUser(u)} className="inline-flex items-center text-emerald-400 hover:text-emerald-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none">
                                        <Edit className="w-4 h-4 mr-1" />
                                        Edit
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {users.length === 0 && (
                            <tr>
                                <td colSpan="4" className="text-center py-10 text-gray-400 text-sm italic">
                                    No users found in the system.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            {editingUser && <UserModal user={editingUser} onClose={() => setEditingUser(null)} onRefresh={fetchUsers} />}
            {viewingPrefEmail && <PreferenceModal email={viewingPrefEmail} onClose={() => setViewingPrefEmail(null)} />}
        </div>
    );
}
