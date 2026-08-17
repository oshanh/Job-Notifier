import React, { useState } from 'react';
import AdminLayout from '../components/AdminLayout';
import WebsitesTable from '../components/WebsitesTable';
import WebsiteModal from '../components/WebsiteModal';
import { Plus } from 'lucide-react';

export default function AdminWebsitesPage() {
    const [isCreatingWebsite, setIsCreatingWebsite] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <AdminLayout>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-3xl font-bold text-white tracking-tight">Website Management</h2>
                    <p className="text-emerald-200/60 mt-1">Manage target websites and their notification URLs.</p>
                </div>
                <button
                    type="button"
                    onClick={() => setIsCreatingWebsite(true)}
                    className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-xl font-medium shadow-lg transition-colors border border-emerald-400/30 flex items-center space-x-2 shrink-0"
                >
                    <Plus className="w-5 h-5" />
                    <span>New Website</span>
                </button>
            </div>

            <WebsitesTable key={`websites-${refreshKey}`} refreshKey={refreshKey} />

            {isCreatingWebsite && (
                <WebsiteModal
                    websiteData={null}
                    onClose={() => setIsCreatingWebsite(false)}
                    onRefresh={() => setRefreshKey(prev => prev + 1)}
                />
            )}
        </AdminLayout>
    );
}
