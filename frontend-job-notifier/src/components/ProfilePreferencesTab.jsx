import React, { useState, useEffect } from 'react';
import { prefApi, websiteApi } from '../services/apiClient';
import { Plus, X, Save, Loader2, BellRing } from 'lucide-react';
import { commonKeywords } from '../data/commonKeywords';

export default function ProfilePreferencesTab({ email }) {
    const [pref, setPref] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);
    const [newKeyword, setNewKeyword] = useState("");
    const [availableWebsites, setAvailableWebsites] = useState([]);
    const [saveSuccess, setSaveSuccess] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [prefRes, sitesRes] = await Promise.allSettled([
                    prefApi.getByEmail(email),
                    websiteApi.getAll()
                ]);

                if (sitesRes.status === 'fulfilled') {
                    setAvailableWebsites(sitesRes.value.data || []);
                }

                if (prefRes.status === 'fulfilled' && prefRes.value.data) {
                    setPref({ ...prefRes.value.data, websites: prefRes.value.data.websites || [] });
                } else {
                    // Initialize empty
                    setPref({
                        email: email,
                        keyword: [],
                        websites: [],
                        whatsapp_num: "",
                        telegram_id: "",
                        whatsapp_enabled: false,
                        telegram_enabled: false,
                        email_enabled: true
                    });
                }
            } catch (err) {
                console.error("Failed to load generic preferences");
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [email]);

    const handleSave = async () => {
        setIsSaving(true);
        setSaveSuccess(false);
        try {
            if (pref.uid) {
                await prefApi.update(pref);
            } else {
                await prefApi.create(pref);
            }
            setSaveSuccess(true);
            setTimeout(() => setSaveSuccess(false), 3000);
        } catch (err) {
            console.error(err);
            alert("Failed to save preferences");
        } finally {
            setIsSaving(false);
        }
    };

    const addSpecificKeyword = (val) => {
        const trimmed = (val || "").trim();
        if (!trimmed) return;
        setPref(p => {
            const exists = p.keyword.some(k => k.toLowerCase() === trimmed.toLowerCase());
            if (exists) return p;
            return { ...p, keyword: [...p.keyword, trimmed] };
        });
    };

    const addKeyword = () => {
        addSpecificKeyword(newKeyword);
        setNewKeyword("");
    };

    const removeKeyword = (val) => {
        setPref(p => ({ ...p, keyword: p.keyword.filter(k => k.toLowerCase() !== val.toLowerCase()) }));
    };

    const toggleCategory = (keywords) => {
        setPref(p => {
            const currentLower = (p.keyword || []).map(k => k.toLowerCase());
            const allSelected = keywords.every(kw => currentLower.includes(kw.toLowerCase()));

            if (allSelected) {
                const categoryLower = keywords.map(kw => kw.toLowerCase());
                return { ...p, keyword: p.keyword.filter(k => !categoryLower.includes(k.toLowerCase())) };
            } else {
                const toAdd = keywords.filter(kw => !currentLower.includes(kw.toLowerCase()));
                return { ...p, keyword: [...p.keyword, ...toAdd] };
            }
        });
    };

    const toggleWebsite = (websiteDomain) => {
        setPref(p => {
            const wlist = p.websites || [];
            if (wlist.includes(websiteDomain)) return { ...p, websites: wlist.filter(w => w !== websiteDomain) };
            return { ...p, websites: [...wlist, websiteDomain] };
        });
    };

    if (isLoading) {
        return (
            <div className="flex items-center justify-center h-64">
                <Loader2 className="w-8 h-8 text-emerald-500 animate-spin" />
            </div>
        );
    }

    return (
        <div className="bg-white/5 border border-white/10 rounded-2xl shadow-xl p-6">
            <div className="flex items-center space-x-3 mb-6 pb-4 border-b border-white/10">
                <div className="w-12 h-12 bg-emerald-500/20 rounded-xl flex items-center justify-center border border-emerald-500/30">
                    <BellRing className="w-6 h-6 text-emerald-400" />
                </div>
                <div>
                    <h3 className="text-xl font-bold text-white">Notification Routing</h3>
                    <p className="text-sm text-gray-400">Configure your target keywords and bridging services.</p>
                </div>
            </div>

            {saveSuccess && (
                <div className="mb-6 p-3 rounded-xl border bg-emerald-500/20 text-emerald-200 border-emerald-500/50 text-sm">
                    Preferences successfully synchronized!
                </div>
            )}

            <div className="space-y-6">
                {/* Communication Channels */}
                <div className="bg-black/20 p-4 rounded-xl border border-white/5 space-y-3">
                    <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider">Broadcasting Channels</h4>

                    <div className="bg-white/5 border border-white/10 rounded-xl px-4 py-3 flex items-center justify-between">
                        <label className="flex items-center space-x-3 text-white text-sm cursor-pointer w-full">
                            <input type="checkbox" checked={pref.email_enabled} onChange={e => setPref({ ...pref, email_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                            <span>Email Delivery </span>
                        </label>
                    </div>

                    <div className="bg-white/5 border border-white/10 rounded-xl px-4 py-3">
                        <label className="flex items-center space-x-3 text-white mb-2 text-sm cursor-pointer w-full">
                            <input type="checkbox" checked={pref.whatsapp_enabled} onChange={e => setPref({ ...pref, whatsapp_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                            <span>WhatsApp </span>
                        </label>
                        {pref.whatsapp_enabled && (
                            <div className="ml-7 mt-2">
                                <input type="text" placeholder="International WhatsApp Number" value={pref.whatsapp_num || ""} onChange={e => setPref({ ...pref, whatsapp_num: e.target.value })} className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-lg focus:ring-1 focus:ring-emerald-500 text-white outline-none text-sm transition-all shadow-inner" />
                            </div>
                        )}
                    </div>

                    <div className="bg-white/5 border border-white/10 rounded-xl px-4 py-3">
                        <label className="flex items-center space-x-3 text-white mb-2 text-sm cursor-pointer w-full">
                            <input type="checkbox" checked={pref.telegram_enabled} onChange={e => setPref({ ...pref, telegram_enabled: e.target.checked })} className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600" />
                            <span>Telegram</span>
                        </label>
                        {pref.telegram_enabled && (
                            <div className="ml-7 mt-2">
                                <input type="text" placeholder="Internal Telegram Target ID" value={pref.telegram_id || ""} onChange={e => setPref({ ...pref, telegram_id: e.target.value })} className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-lg focus:ring-1 focus:ring-emerald-500 text-white outline-none text-sm transition-all shadow-inner" />
                            </div>
                        )}
                    </div>
                </div>

                {/* Website Sources */}
                {availableWebsites.length > 0 && (
                    <div className="bg-black/20 p-4 rounded-xl border border-white/5">
                        <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider mb-3">Website Sources Filter</h4>
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                            {availableWebsites.map((site) => (
                                <label key={site.website} className="flex items-center space-x-3 text-white text-sm bg-white/5 border border-white/10 rounded-xl px-4 py-3 cursor-pointer hover:bg-white/10 transition-colors shadow-inner">
                                    <input
                                        type="checkbox"
                                        checked={(pref.websites || []).includes(site.website)}
                                        onChange={() => toggleWebsite(site.website)}
                                        className="w-4 h-4 rounded text-emerald-500 focus:ring-emerald-500 bg-black border-gray-600"
                                    />
                                    <span className="truncate font-medium" title={site.website}>{(site.website || "").replace(/^https?:\/\//, '')}</span>
                                </label>
                            ))}
                        </div>
                    </div>
                )}

                {/* Keywords List */}
                <div className="space-y-4">
                    <div className="bg-black/20 p-4 rounded-xl border border-white/5">
                        <div className="flex items-center justify-between mb-3">
                            <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider">Target Interception Keywords</h4>
                            <button onClick={handleSave} disabled={isSaving} className="inline-flex items-center px-3 py-1.5 bg-emerald-600 hover:bg-emerald-500 rounded-lg text-white font-medium transition-all disabled:opacity-50 text-xs shadow-md border border-emerald-400/30">
                                {isSaving ? <Loader2 className="w-3.5 h-3.5 mr-1.5 animate-spin" /> : <Save className="w-3.5 h-3.5 mr-1.5" />}
                                Save Keywords
                            </button>
                        </div>
                        <div className="flex space-x-3 mb-4">
                            <input
                                type="text"
                                value={newKeyword}
                                onChange={e => setNewKeyword(e.target.value)}
                                onKeyDown={e => e.key === 'Enter' && addKeyword()}
                                placeholder="e.g. Fullstack, python, DevOps"
                                className="flex-1 px-4 py-2 bg-black/30 border border-emerald-500/30 rounded-lg focus:ring-2 focus:ring-emerald-500 text-white outline-none text-sm shadow-inner transition-all block w-full"
                            />
                            <button onClick={addKeyword} className="flex items-center justify-center px-4 py-2 bg-emerald-600 hover:bg-emerald-500 rounded-lg text-white font-medium transition-colors shadow-md border border-emerald-400/30">
                                <Plus className="w-5 h-5 mr-1" /> Add
                            </button>
                        </div>
                        <div className="flex flex-wrap gap-2 p-3 bg-white/5 rounded-xl border border-white/5 min-h-[60px] items-center text-sm">
                            {pref.keyword.map((kw, i) => (
                                <span key={i} className="flex items-center space-x-1.5 pl-3 pr-1.5 py-1 bg-gradient-to-r from-emerald-600/30 to-teal-800/30 border border-emerald-500/50 rounded-full text-emerald-200">
                                    <span className="font-medium tracking-wide">{kw}</span>
                                    <button onClick={() => removeKeyword(kw)} className="p-1 hover:bg-emerald-500/30 rounded-full transition-colors text-emerald-400 hover:text-white">
                                        <X className="w-3.5 h-3.5" />
                                    </button>
                                </span>
                            ))}
                            {pref.keyword.length === 0 && <p className="px-2 text-gray-500 italic">No job keywords currently targeting...</p>}
                        </div>
                    </div>

                    <div className="bg-black/20 p-4 rounded-xl border border-white/5">
                        <h4 className="text-xs font-semibold text-emerald-300 uppercase tracking-wider mb-3">Quick Add Categories</h4>
                        <div className="space-y-4">
                            {commonKeywords.map(categoryGrp => {
                                const isAllSelected = categoryGrp.keywords.every(kw => pref.keyword.some(k => k.toLowerCase() === kw.toLowerCase()));
                                return (
                                    <div key={categoryGrp.category}>
                                        <div className="flex items-center justify-between mb-2">
                                            <h5 className="text-xs font-medium text-white/50 uppercase tracking-wide">{categoryGrp.category}</h5>
                                            <button
                                                onClick={() => toggleCategory(categoryGrp.keywords)}
                                                className="text-[10px] uppercase font-bold tracking-wider px-2 py-1 bg-white/5 hover:bg-white/10 text-emerald-400 hover:text-emerald-300 rounded transition-colors"
                                            >
                                                {isAllSelected ? "Clear All" : "Select All"}
                                            </button>
                                        </div>
                                        <div className="flex flex-wrap gap-2">
                                            {categoryGrp.keywords.map(kw => {
                                                const isAdded = pref.keyword.some(k => k.toLowerCase() === kw.toLowerCase());
                                                return (
                                                    <button
                                                        key={kw}
                                                        onClick={() => !isAdded ? addSpecificKeyword(kw) : removeKeyword(kw)}
                                                        className={`px-3 py-1.5 rounded-full text-xs font-medium transition-colors border ${isAdded ? 'bg-emerald-600/30 border-emerald-500/50 text-emerald-200' : 'bg-white/5 border-white/10 text-gray-400 hover:bg-white/10 hover:text-white'}`}
                                                    >
                                                        {kw} {isAdded && <span className="ml-1 opacity-70">✓</span>}
                                                    </button>
                                                );
                                            })}
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </div>
                </div>
            </div>

            <div className="pt-6 mt-6 border-t border-white/10 flex justify-end">
                <button onClick={handleSave} disabled={isSaving} className="inline-flex items-center px-6 py-3 bg-emerald-600 hover:bg-emerald-500 rounded-xl text-white font-semibold transition-all disabled:opacity-50 text-sm shadow-emerald-900/30 shadow-lg border border-emerald-500/30 hover:scale-[1.02]">
                    {isSaving ? <Loader2 className="w-5 h-5 mr-2 animate-spin" /> : <Save className="w-5 h-5 mr-2" />}
                    {isSaving ? 'Synchronizing...' : 'Save Preferences'}
                </button>
            </div>
        </div>
    );
}
