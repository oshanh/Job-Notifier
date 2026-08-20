import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ShieldAlert, ArrowLeft, Home } from 'lucide-react';

export default function UnauthorizedPage() {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-[#050505] text-amber-50 flex flex-col items-center justify-center relative overflow-hidden font-sans">
            {/* Ambient Background Lights - Adjusted to Amber/Orange theme for warnings */}
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[800px] bg-red-500/10 rounded-full blur-[120px] pointer-events-none" />
            <div className="absolute top-0 right-0 w-96 h-96 bg-amber-500/10 rounded-full blur-[100px] pointer-events-none" />
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-orange-600/10 rounded-full blur-[100px] pointer-events-none" />

            {/* Main Glass Panel */}
            <div className="relative z-10 text-center space-y-6 max-w-lg w-full mx-4 p-10 bg-white/5 border border-white/10 rounded-3xl backdrop-blur-xl shadow-2xl">

                <div className="flex justify-center mb-2">
                    <ShieldAlert className="w-20 h-20 text-red-400 drop-shadow-md" />
                </div>

                <h1 className="text-6xl font-black text-transparent bg-clip-text bg-gradient-to-br from-red-400 via-orange-400 to-amber-500 tracking-tighter drop-shadow-sm">
                    403
                </h1>

                <div className="space-y-2">
                    <h2 className="text-2xl font-bold tracking-tight text-white/95">
                        Access Denied
                    </h2>
                    <p className="text-amber-100/60 text-sm leading-relaxed max-w-sm mx-auto">
                        You don't have the necessary permissions to view this area. This route is highly restricted.
                    </p>
                </div>

                <div className="pt-6 flex flex-col sm:flex-row items-center justify-center gap-3">
                    <button
                        onClick={() => navigate(-1)}
                        className="w-full sm:w-auto flex items-center justify-center space-x-2 px-6 py-2.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl font-medium transition-all duration-200 text-sm text-amber-100/90 hover:text-white group"
                    >
                        <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
                        <span>Go Back</span>
                    </button>
                    <button
                        onClick={() => navigate('/')}
                        className="w-full sm:w-auto flex items-center justify-center space-x-2 px-6 py-2.5 bg-gradient-to-r from-red-500 to-orange-600 hover:from-red-400 hover:to-orange-500 shadow-lg shadow-red-500/20 rounded-xl font-medium transition-all duration-200 text-sm text-white border border-red-400/20 hover:scale-[1.02]"
                    >
                        <Home className="w-4 h-4" />
                        <span>Return Home</span>
                    </button>
                </div>

            </div>

        </div>
    );
}
