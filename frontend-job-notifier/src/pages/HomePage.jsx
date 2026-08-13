import React from 'react';
import { useNavigate } from 'react-router-dom';

export default function HomePage() {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen flex flex-col items-center justify-center bg-slate-900 text-white p-4 relative overflow-hidden font-sans">
            {/* Ambient Background Glows */}
            <div className="absolute top-1/4 -left-1/4 w-[800px] h-[800px] bg-emerald-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>
            <div className="absolute top-3/4 -right-1/4 w-[800px] h-[800px] bg-indigo-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse animation-delay-2000"></div>

            <div className="relative z-10 max-w-3xl text-center space-y-8">
                <div className="inline-block mb-4 px-4 py-1.5 rounded-full bg-white/5 border border-white/10 text-emerald-300 text-sm font-semibold tracking-wider uppercase mb-6 backdrop-blur-md shadow-xl">
                    Automated Career Intelligence
                </div>

                <h1 className="text-6xl md:text-8xl font-extrabold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-emerald-400 via-teal-300 to-indigo-400">
                    Job Notifier.
                </h1>

                <p className="text-xl md:text-2xl text-gray-300 font-light max-w-2xl mx-auto leading-relaxed">
                    Instantly receive real-time alerts across WhatsApp, Email, or Telegram whenever target companies post your dream role.
                </p>

                <div className="pt-8">
                    <button
                        onClick={() => navigate('/login')}
                        className="group relative inline-flex items-center justify-center px-10 py-5 text-xl font-bold text-white transition-all duration-200 bg-emerald-600 font-bold rounded-2xl hover:bg-emerald-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-emerald-600 shadow-[0_0_40px_rgba(16,185,129,0.3)] hover:shadow-[0_0_60px_rgba(16,185,129,0.5)] transform hover:-translate-y-1"
                    >
                        <span>Get Notified Now</span>
                        <svg className="w-6 h-6 ml-3 transform group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 7l5 5m0 0l-5 5m5-5H6"></path></svg>
                    </button>
                </div>

                <div className="mt-12 pt-8 flex items-center justify-center space-x-12 opacity-60">
                    <div className="text-center">
                        <div className="text-3xl font-bold text-white mb-1">500+</div>
                        <div className="text-xs uppercase tracking-widest text-emerald-400">Companies</div>
                    </div>
                    <div className="w-px h-12 bg-white/10"></div>
                    <div className="text-center">
                        <div className="text-3xl font-bold text-white mb-1">24/7</div>
                        <div className="text-xs uppercase tracking-widest text-indigo-400">Monitoring</div>
                    </div>
                </div>
            </div>

            <div className="absolute bottom-8 text-center w-full z-10 opacity-50">
                <a href="/admin/login" className="text-xs text-gray-400 hover:text-white transition-colors cursor-pointer border-b border-transparent hover:border-white pb-1">
                    Administrator Login
                </a>
            </div>
        </div>
    );
}