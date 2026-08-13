import React, { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { prefApi } from '../services/apiClient';

export default function PreferenceModal({ email, onClose }) {
    const [pref, setPref] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [newKeyword, setNewKeyword] = useState("");

    useEffect(() => {
        const fetchPref = async () => {
            try {
                const { data } = await prefApi.getByEmail(email);
                setPref(data);
            } catch (err) {
                // If 404, the preference might not exist yet. Initialize empty.
                setPref({
                    email: email,
                    keyword: [],
                    whatsapp_num: "",
                    telegram_id: "",
                    whatsapp_enabled: false,
                    telegram_enabled: false,
                    email_enabled: true
                });
            } finally {
                setIsLoading(false);
            }
        };
        fetchPref();
    }, [email]);

    const handleSave = async () => {
        setIsSaving(true);
        try {
            // Determine if to POST (create) or PUT (update) based on whether uid exists
            if (pref.uid) {
                await prefApi.update(pref);
            } else {
                await prefApi.create(pref);
            }
            onClose();
        } catch (err) {
            console.error(err);
            alert("Failed to save preferences");
        } finally {
            setIsSaving(false);
        }
    };

    const addKeyword = () => {
        if (!newKeyword.trim()) return;
        setPref(p => ({ ...p, keyword: [...p.keyword, newKeyword.trim()] }));
        setNewKeyword("");
    };

    const removeKeyword = (kw) => {
        setPref(p => ({ ...p, keyword: p.keyword.filter(k => k !== kw) }));
    };

    if (isLoading) {
        return createPortal(
            <div className="fixed inset-0 z-[100] flex items-center justify-center bg-black/60 backdrop-blur-sm">
                <span className="text-white">Loading Preferences...</span>
            </div>,
            document.body
        );
    }

    return createPortal(
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose}></div>
            <div className="bg-slate-900 border border-white/10 w-full max-w-md rounded-2xl shadow-2xl relative z-10 max-h-[90vh] flex flex-col transform scale-100 transition-all">
                <div className="p-4 border-b border-white/10">
                    <h3 className="text-xl font-bold text-white">Notification Preferences</h3>
                    <p className="text-sm text-gray-400 mt-1">{email}</p>
                </div>

                <div className="p-4 space-y-2 overflow-y-auto flex-1">
                    {/* Communication Channels */}
                    <div className="space-y-2">
                        <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider">Channels</h4>

                        <div className="bg-white/5 border border-white/10 rounded-xl px-3 py-2 flex items-center justify-between">
                            <label className="flex items-center space-x-2 text-white text-sm">
                                <input type="checkbox" checked={pref.email_enabled} onChange={e => setPref({ ...pref, email_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                                <span>Email Alerts</span>
                            </label>
                        </div>

                        <div className="bg-white/5 border border-white/10 rounded-xl px-3 py-2">
                            <label className="flex items-center space-x-2 text-white mb-2 text-sm">
                                <input type="checkbox" checked={pref.whatsapp_enabled} onChange={e => setPref({ ...pref, whatsapp_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                                <span>WhatsApp Alerts</span>
                            </label>
                            {pref.whatsapp_enabled && (
                                <div className="ml-6">
                                    <input type="text" placeholder="WhatsApp Number" value={pref.whatsapp_num || ""} onChange={e => setPref({ ...pref, whatsapp_num: e.target.value })} className="w-full px-3 py-1 bg-black/30 border border-white/10 rounded-lg focus:ring-1 focus:ring-emerald-500 text-white outline-none text-xs" />
                                </div>
                            )}
                        </div>

                        <div className="bg-white/5 border border-white/10 rounded-xl px-3 py-2">
                            <label className="flex items-center space-x-2 text-white mb-2 text-sm">
                                <input type="checkbox" checked={pref.telegram_enabled} onChange={e => setPref({ ...pref, telegram_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                                <span>Telegram Alerts</span>
                            </label>
                            {pref.telegram_enabled && (
                                <div className="ml-6">
                                    <input type="text" placeholder="Telegram ID" value={pref.telegram_id || ""} onChange={e => setPref({ ...pref, telegram_id: e.target.value })} className="w-full px-3 py-1 bg-black/30 border border-white/10 rounded-lg focus:ring-1 focus:ring-emerald-500 text-white outline-none text-xs" />
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Keywords List */}
                    <div className="pt-3 border-t border-white/10">
                        <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider mb-2">Job Keywords</h4>
                        <div className="flex space-x-2 mb-3">
                            <input
                                type="text"
                                value={newKeyword}
                                onChange={e => setNewKeyword(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && addKeyword()}
                                placeholder="e.g. software engineer, python"
                                className="flex-1 px-3 py-1.5 bg-black/30 border border-white/10 rounded-lg focus:ring-1 focus:ring-emerald-500 text-white outline-none text-xs"
                            />
                            <button onClick={addKeyword} className="px-3 py-1.5 bg-emerald-600/30 hover:bg-emerald-600 border border-emerald-500/50 rounded-lg text-emerald-200 hover:text-white transition-colors text-xs">+</button>
                        </div>
                        <div className="flex flex-wrap gap-1.5">
                            {pref.keyword.map((kw, i) => (
                                <span key={i} className="flex items-center space-x-1 px-2.5 py-0.5 bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 rounded-full text-[10px]">
                                    <span>{kw}</span>
                                    <button onClick={() => removeKeyword(kw)} className="text-emerald-400 hover:text-white ml-2">&times;</button>
                                </span>
                            ))}
                            {pref.keyword.length === 0 && <p className="text-xs text-gray-500 italic">No keywords added.</p>}
                        </div>
                    </div>
                </div>

                <div className="p-3 border-t border-white/10 flex justify-end space-x-2">
                    <button onClick={onClose} className="px-3 py-1.5 bg-white/5 hover:bg-white/10 border border-white/10 rounded-lg text-white transition-colors text-sm">Cancel</button>
                    <button onClick={handleSave} disabled={isSaving} className="px-3 py-1.5 bg-gradient-to-r from-emerald-500 to-teal-600 hover:to-teal-500 rounded-lg text-white font-medium transition-colors disabled:opacity-50 text-sm">
                        {isSaving ? 'Saving...' : 'Save Preferences'}
                    </button>
                </div>
            </div>
        </div>,
        document.body
    );
}
