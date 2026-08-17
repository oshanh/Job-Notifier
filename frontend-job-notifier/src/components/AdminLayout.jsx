import React, { useContext, useState } from 'react';
import { AuthContext } from './AuthContext';
import { useNavigate } from 'react-router-dom';

export default function AdminLayout({ children }) {
    const { logout } = useContext(AuthContext);
    const navigate = useNavigate();
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
                            <p className="text-xs text-emerald-300/60 uppercase tracking-widest mt-2 font-semibold">Admin Panel</p>
                        </div>
                        <button onClick={() => setIsSidebarOpen(false)} className="md:hidden p-2 text-white/50 hover:text-white rounded-lg hover:bg-white/10 transition-colors">
                            <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" /></svg>
                        </button>
                    </div>

                    <nav className="mt-10 space-y-2">
                        <a href="/admin/dashboard" className="flex items-center space-x-3 bg-white/10 border border-white/5 rounded-xl p-3 text-sm font-medium hover:bg-white/20 transition-colors shadow-inner text-emerald-100">
                            <svg className="w-5 h-5 opacity-70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z"></path></svg>
                            <span>Users Management</span>
                        </a>
                        <a href="/admin/websites" className="flex items-center space-x-3 bg-white/10 border border-white/5 rounded-xl p-3 text-sm font-medium hover:bg-white/20 transition-colors shadow-inner text-emerald-100">
                            <svg className="w-5 h-5 opacity-70" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9"></path></svg>
                            <span>Websites Management</span>
                        </a>
                    </nav>
                </div>

                <div className="p-6">
                    <button
                        onClick={handleLogout}
                        className="flex w-full items-center justify-center space-x-2 bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/20 rounded-xl p-3 text-sm transition-colors"
                    >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path></svg>
                        <span>Sign Out</span>
                    </button>
                </div>
            </aside>

            {/* Main Content Area */}
            <main className="flex-1 flex flex-col h-full overflow-hidden relative">
                {/* Top decorative gradient bar */}
                <div className="h-1 w-full bg-gradient-to-r from-emerald-500 via-teal-500 to-green-500 absolute top-0 left-0 z-50"></div>

                {/* Mobile Header Nav */}
                <div className="md:hidden flex items-center px-4 py-4 border-b border-white/5 bg-black/20 backdrop-blur-lg relative z-40 mt-1">
                    <button onClick={() => setIsSidebarOpen(true)} className="p-2 -ml-2 text-white/70 hover:text-white rounded-lg hover:bg-white/5 transition-colors">
                        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 6h16M4 12h16M4 18h16" /></svg>
                    </button>
                    <span className="ml-3 font-bold text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 to-teal-400 tracking-tight">Admin Panel</span>
                </div>

                <div className="flex-1 overflow-y-auto p-4 md:p-8 relative z-10 w-full overflow-x-hidden">
                    <div className="w-full max-w-7xl mx-auto backdrop-blur-lg bg-black/20 rounded-3xl border border-white/5 p-4 md:p-8 shadow-2xl min-h-full">
                        {children}
                    </div>
                </div>
            </main>
        </div>
    );
}
