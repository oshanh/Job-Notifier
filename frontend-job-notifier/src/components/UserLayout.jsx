import React, { useContext, useState } from 'react';
import { AuthContext } from './AuthContext';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { X, LogOut, Menu, Sliders, Settings } from 'lucide-react';

export default function UserLayout({ children }) {
    const { logout } = useContext(AuthContext);
    const navigate = useNavigate();
    const location = useLocation();
    const [isSidebarOpen, setIsSidebarOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="flex h-screen bg-slate-900 text-white font-sans overflow-hidden bg-gradient-to-br from-gray-900 to-emerald-950">
            {/* Mobile Overlay */}
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 bg-black/60 backdrop-blur-sm z-40 md:hidden"
                    onClick={() => setIsSidebarOpen(false)}
                ></div>
            )}

            {/* Glass Sidebar */}
            <aside className={`fixed md:relative inset-y-0 left-0 z-50 w-64 flex-shrink-0 border-r border-white/10 bg-slate-900/95 md:bg-white/5 backdrop-blur-2xl flex flex-col justify-between transform transition-transform duration-300 ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'} md:translate-x-0 shadow-2xl md:shadow-none`}>
                <div className="p-6">
                    <div className="flex items-center justify-between">
                        <div>
                            <h1 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-emerald-400 to-teal-400 tracking-tight">
                                JobNotifier
                            </h1>
                            <p className="text-xs text-emerald-300/60 uppercase tracking-widest mt-2 font-semibold">User Dashboard</p>
                        </div>
                        <button onClick={() => setIsSidebarOpen(false)} className="md:hidden p-2 text-white/50 hover:text-white rounded-lg hover:bg-white/10 transition-colors">
                            <X className="w-5 h-5" />
                        </button>
                    </div>

                    <nav className="mt-10 space-y-2">
                        <Link to="/profile/preferences" onClick={() => setIsSidebarOpen(false)} className={`flex items-center space-x-3 border rounded-xl p-3 text-sm font-medium transition-colors shadow-inner ${location.pathname.includes('/profile/preferences') ? 'bg-white/20 border-white/20 text-white' : 'bg-white/5 border-white/5 text-emerald-100 hover:bg-white/10'}`}>
                            <Sliders className="w-5 h-5 opacity-70" />
                            <span>Preferences</span>
                        </Link>
                        <Link to="/profile/settings" onClick={() => setIsSidebarOpen(false)} className={`flex items-center space-x-3 border rounded-xl p-3 text-sm font-medium transition-colors shadow-inner ${location.pathname.includes('/profile/settings') ? 'bg-white/20 border-white/20 text-white' : 'bg-white/5 border-white/5 text-emerald-100 hover:bg-white/10'}`}>
                            <Settings className="w-5 h-5 opacity-70" />
                            <span>Account Settings</span>
                        </Link>
                    </nav>
                </div>

                <div className="p-6">
                    <button
                        onClick={handleLogout}
                        className="flex w-full items-center justify-center space-x-2 bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/20 rounded-xl p-3 text-sm transition-colors"
                    >
                        <LogOut className="w-5 h-5" />
                        <span>Sign Out</span>
                    </button>
                </div>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 flex flex-col h-full overflow-hidden relative w-full">
                {/* Top decorative gradient bar */}
                <div className="h-1 w-full bg-gradient-to-r from-emerald-500 via-teal-500 to-emerald-500 absolute top-0 left-0 z-50"></div>

                {/* Mobile Header Nav */}
                <div className="md:hidden flex items-center px-4 py-4 border-b border-white/5 bg-black/20 backdrop-blur-lg relative z-40 mt-1">
                    <button onClick={() => setIsSidebarOpen(true)} className="p-2 -ml-2 text-white/70 hover:text-white rounded-lg hover:bg-white/5 transition-colors">
                        <Menu className="w-6 h-6" />
                    </button>
                    <span className="ml-3 font-bold text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-teal-400 tracking-tight">User Dashboard</span>
                </div>

                <div className="flex-1 overflow-y-auto p-4 md:p-8 relative z-10 w-full overflow-x-hidden">
                    <div className="w-full max-w-4xl mx-auto backdrop-blur-lg bg-black/20 rounded-3xl border border-white/5 p-4 md:p-8 shadow-2xl min-h-full">
                        {children}
                    </div>
                </div>
            </main>
        </div>
    );
}
