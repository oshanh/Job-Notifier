import React, { useEffect, useState } from 'react';
import { fosmisApi } from '../services/apiClient';
import FosmisUserModal from './FosmisUserModal';

export default function FosmisUsersTable({ refreshKey }) {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [editingUser, setEditingUser] = useState(null);
    const [localRefresh, setLocalRefresh] = useState(0);

    const fetchUsers = async () => {
        setIsLoading(true);
        try {
            const { data } = await fosmisApi.getAllUsers();
            setUsers(data);
        } catch (error) {
            console.error("Failed to fetch fosmis users", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, [refreshKey, localRefresh]);

    if (isLoading) {
        return (
            <div className="w-full bg-white/5 border border-white/10 rounded-2xl flex items-center justify-center py-20 mt-8">
                <span className="flex items-center space-x-3 text-emerald-300">
                    <svg className="animate-spin h-6 w-6 text-emerald-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    <span>Loading fosmis users matrix...</span>
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
                            <th className="px-6 py-4 font-medium tracking-wider">Username</th>
                            <th className="px-6 py-4 font-medium tracking-wider">Email</th>
                            <th className="px-6 py-4 font-medium tracking-wider">Status</th>
                            <th className="px-6 py-4 font-medium tracking-wider text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-white/5">
                        {users.map((u, i) => (
                            console.log(u),
                            <tr key={u.username} className="hover:bg-white/5 transition-colors">
                                <td className="px-6 py-4 font-semibold text-white">
                                    {u.username}
                                </td>
                                <td className="px-6 py-4 text-gray-400">
                                    {u.email}
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
                                    <button onClick={() => setEditingUser(u)} className="text-emerald-400 hover:text-emerald-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none">Edit</button>
                                </td>
                            </tr>
                        ))}
                        {users.length === 0 && (
                            <tr>
                                <td colSpan="4" className="text-center py-10 text-gray-400 text-sm italic">
                                    No fosmis users found in the system.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            {editingUser && <FosmisUserModal user={editingUser} onClose={() => setEditingUser(null)} onRefresh={() => setLocalRefresh(prev => prev + 1)} />}
        </div>
    );
}
