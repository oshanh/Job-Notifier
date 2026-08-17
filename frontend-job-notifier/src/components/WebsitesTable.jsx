import React, { useState, useEffect } from 'react';
import { websiteApi } from '../services/apiClient';
import WebsiteModal from './WebsiteModal';
import { Edit, PowerOff, Trash2 } from 'lucide-react';

export default function WebsitesTable({ refreshKey }) {
    const [websites, setWebsites] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingWebsite, setEditingWebsite] = useState(null);
    const [localRefresh, setLocalRefresh] = useState(0);

    useEffect(() => {
        const fetchWebsites = async () => {
            setIsLoading(true);
            try {
                const response = await websiteApi.getAll();
                setWebsites(response.data);
            } catch (err) {
                setError("Failed to load websites.");
            } finally {
                setIsLoading(false);
            }
        };
        fetchWebsites();
    }, [refreshKey, localRefresh]);

    const handleSoftDelete = async (website) => {
        try {
            await websiteApi.softDelete(website.website);
            setLocalRefresh(prev => prev + 1);
        } catch (err) {
            alert('Failed to disable website');
        }
    };

    const handleHardDelete = async (website) => {
        if (!confirm(`Are you sure you want to permanently delete ${website.website}? This action cannot be reversed.`)) return;
        try {
            await websiteApi.hardDelete(website.website);
            setLocalRefresh(prev => prev + 1);
        } catch (err) {
            alert('Failed to delete website');
        }
    };

    if (isLoading) return <div className="text-white mt-10 text-center animate-pulse">Loading Websites...</div>;
    if (error) return <div className="text-rose-400 mt-10 text-center">{error}</div>;

    return (
        <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden shadow-xl backdrop-blur-md">
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm whitespace-nowrap">
                    <thead className="bg-black/40 text-emerald-300/80 font-medium">
                        <tr>
                            <th className="px-6 py-4 rounded-tl-2xl">Base URL</th>
                            <th className="px-6 py-4 text-center">URLs Tracked</th>
                            <th className="px-6 py-4 text-center">Status</th>
                            <th className="px-6 py-4 text-right rounded-tr-2xl">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-white/5 text-gray-300">
                        {websites.length === 0 ? (
                            <tr>
                                <td colSpan="4" className="px-6 py-8 text-center text-gray-500">
                                    No websites registered. Get started by adding one!
                                </td>
                            </tr>
                        ) : websites.map((wb, i) => (
                            <tr key={i} className="hover:bg-white/5 transition-colors group">
                                <td className="px-6 py-4 font-mono text-emerald-100">{wb.website}</td>
                                <td className="px-6 py-4 text-center">{wb.url?.length || 0}</td>
                                <td className="px-6 py-4 text-center">
                                    {wb.enabled || wb.isEnabled ? (
                                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                                            Active
                                        </span>
                                    ) : (
                                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-500/10 text-gray-400 border border-gray-500/20">
                                            Disabled
                                        </span>
                                    )}
                                </td>
                                <td className="px-6 py-4 text-right space-x-4">
                                    <button
                                        onClick={() => setEditingWebsite(wb)}
                                        className="inline-flex items-center text-emerald-400 hover:text-emerald-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none"
                                    >
                                        <Edit className="w-4 h-4 mr-1" />
                                        Edit
                                    </button>
                                    {(wb.enabled || wb.isEnabled) && (
                                        <button
                                            onClick={() => handleSoftDelete(wb)}
                                            title="Disable this website to temporarily prevent jobs fetching"
                                            className="inline-flex items-center text-amber-400 hover:text-amber-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none"
                                        >
                                            <PowerOff className="w-4 h-4 mr-1" />
                                            Disable
                                        </button>
                                    )}
                                    <button
                                        onClick={() => handleHardDelete(wb)}
                                        className="inline-flex items-center text-rose-400 hover:text-rose-300 transition-colors text-xs font-semibold uppercase tracking-wider outline-none"
                                    >
                                        <Trash2 className="w-4 h-4 mr-1" />
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {editingWebsite && (
                <WebsiteModal
                    websiteData={editingWebsite}
                    onClose={() => setEditingWebsite(null)}
                    onRefresh={() => setLocalRefresh(prev => prev + 1)}
                />
            )}
        </div>
    );
}
