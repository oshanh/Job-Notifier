import AdminLayout from '../components/AdminLayout';
import UsersTable from '../components/UsersTable';
import NewUserModal from '../components/NewUserModal';
import FosmisUsersTable from '../components/FosmisUsersTable';
import FosmisUserModal from '../components/FosmisUserModal';
import { useState } from 'react';
import { Plus } from 'lucide-react';

export default function AdminDashboard() {
    const [isCreatingSystemUser, setIsCreatingSystemUser] = useState(false);
    const [isCreatingFosmisUser, setIsCreatingFosmisUser] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [activeTab, setActiveTab] = useState('system'); // 'system' or 'fosmis'

    return (
        <AdminLayout>
            <div className="flex justify-between items-center mb-6">
                <div>
                    <h2 className="text-3xl font-bold text-white tracking-tight">Registered Users</h2>
                    <p className="text-emerald-200/60 mt-1">Manage system members, roles, and configure their preferences.</p>
                </div>
                <button
                    type="button"
                    onClick={() => activeTab === 'system' ? setIsCreatingSystemUser(true) : setIsCreatingFosmisUser(true)}
                    className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-xl font-medium shadow-lg transition-colors border border-emerald-400/30 flex items-center space-x-2 shrink-0"
                >
                    <Plus className="w-5 h-5" />
                    <span>{activeTab === 'system' ? 'New System User' : 'New Fosmis User'}</span>
                </button>
            </div>

            <div className="flex space-x-1 bg-white/5 p-1 rounded-xl mb-6 w-fit border border-white/10">
                <button
                    className={`px-6 py-2 rounded-lg text-sm font-medium transition-all duration-200 outline-none ${activeTab === 'system' ? 'bg-emerald-500/20 text-emerald-300 shadow-sm border border-emerald-500/30' : 'text-gray-400 hover:text-gray-200 hover:bg-white/5'}`}
                    onClick={() => setActiveTab('system')}
                >
                    System Users
                </button>
                <button
                    className={`px-6 py-2 rounded-lg text-sm font-medium transition-all duration-200 outline-none ${activeTab === 'fosmis' ? 'bg-emerald-500/20 text-emerald-300 shadow-sm border border-emerald-500/30' : 'text-gray-400 hover:text-gray-200 hover:bg-white/5'}`}
                    onClick={() => setActiveTab('fosmis')}
                >
                    Fosmis Users
                </button>
            </div>

            {activeTab === 'system' ? (
                <UsersTable key={`system-${refreshKey}`} />
            ) : (
                <FosmisUsersTable key={`fosmis-${refreshKey}`} refreshKey={refreshKey} />
            )}

            {isCreatingSystemUser && (
                <NewUserModal
                    onClose={() => setIsCreatingSystemUser(false)}
                    onRefresh={() => setRefreshKey(prev => prev + 1)}
                />
            )}

            {isCreatingFosmisUser && (
                <FosmisUserModal
                    user={null}
                    onClose={() => setIsCreatingFosmisUser(false)}
                    onRefresh={() => setRefreshKey(prev => prev + 1)}
                />
            )}
        </AdminLayout>
    );
}
