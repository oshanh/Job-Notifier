import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

export default function Alert({ message, type, onClose, autoCloseTime = 5000 }) {
    if (!message) return null;

    useEffect(() => {
        if (autoCloseTime && onClose) {
            const timer = setTimeout(onClose, autoCloseTime);
            return () => clearTimeout(timer);
        }
    }, [message, type, autoCloseTime, onClose]);

    const isError = type === 'error';

    return createPortal(
        <div className="fixed top-6 left-1/2 transform -translate-x-1/2 z-[99999]">
            <div className={`flex items-center justify-between shadow-2xl border rounded-2xl px-6 py-4 min-w-[320px] max-w-lg ${isError
                ? 'bg-red-950/95 border-red-500/50 text-red-100 shadow-red-900/50'
                : 'bg-emerald-950/95 border-emerald-500/50 text-emerald-100 shadow-emerald-900/50'
                } backdrop-blur-xl transition-all duration-300`}>
                <div className="flex items-center">
                    {isError ? <AlertCircle className="w-5 h-5 mr-3 flex-shrink-0 text-red-500" /> : <CheckCircle2 className="w-5 h-5 mr-3 flex-shrink-0 text-emerald-500" />}
                    <p className="text-sm font-medium tracking-wide">{message}</p>
                </div>
                {onClose && (
                    <button onClick={onClose} className={`ml-4 p-1.5 rounded-full transition-colors flex-shrink-0 ${isError ? 'hover:bg-red-500/30 text-red-300 hover:text-white' : 'hover:bg-emerald-500/30 text-emerald-300 hover:text-white'
                        }`}>
                        <X className="w-4 h-4" />
                    </button>
                )}
            </div>
        </div>,
        document.body
    );
}
