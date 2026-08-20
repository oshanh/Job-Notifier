import React, { useState } from 'react';
import { createPortal } from 'react-dom';
import { websiteApi } from '../services/apiClient';
import { X, Save, Loader2 } from 'lucide-react';

export default function WebsiteModal({ websiteData, onClose, onRefresh }) {
    const isNew = !websiteData;
    const originalBaseURL = websiteData?.website;

    const savedEnabled = websiteData ? (websiteData.enabled !== undefined ? websiteData.enabled : websiteData.isEnabled) : true;

    const [baseURL, setBaseURL] = useState(websiteData ? websiteData.website : '');
    const [urlsText, setUrlsText] = useState(websiteData && websiteData.url ? websiteData.url.join('\n') : '');
    const [isEnabled, setIsEnabled] = useState(savedEnabled);

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        const urlList = urlsText.split('\n').map(u => u.trim()).filter(u => u.length > 0);

        try {
            // sending both enabled and isEnabled since jackson mapping can be temperamental based on class properties
            const data = { website: baseURL, url: urlList, isEnabled, enabled: isEnabled };

            if (isNew) {
                await websiteApi.create(data);
            } else {
                await websiteApi.update(originalBaseURL, data);
            }
            onRefresh();
            onClose();
        } catch (err) {
            console.error("Operation failed", err);
            setError(`Failed to ${isNew ? 'create' : 'update'} website. ${err.response?.data?.message || 'Please try again.'}`);
            setIsLoading(false);
        }
    };

    return createPortal(
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm" onClick={onClose}></div>
            <div className="bg-slate-900 border border-white/10 w-full max-w-lg rounded-2xl shadow-2xl relative z-10 max-h-[90vh] flex flex-col transform scale-100 transition-all">
                <div className="p-6 border-b border-white/10">
                    <h3 className="text-xl font-bold text-white">{isNew ? 'New Website' : 'Edit Website'}</h3>
                    {!isNew && <p className="text-sm text-gray-400 mt-1 truncate">{originalBaseURL}</p>}
                </div>

                <div className="p-6 overflow-y-auto flex-1">
                    {error && (
                        <div className="mb-4 bg-red-500/20 text-red-200 border border-red-500/50 p-3 rounded-xl text-sm">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2">Base URL</label>
                            <input
                                type="url"
                                required
                                value={baseURL}
                                onChange={(e) => setBaseURL(e.target.value)}
                                placeholder="https://example.com"
                                className="w-full px-4 py-2 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-300 mb-2 text-justify flex justify-between">
                                <span>Tracked URLs</span>
                                <span className="text-xs text-emerald-400/50">One per line</span>
                            </label>
                            <textarea
                                rows={5}
                                value={urlsText}
                                onChange={(e) => setUrlsText(e.target.value)}
                                placeholder="https://example.com/jobs&#10;https://example.com/careers"
                                className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none text-sm font-mono whitespace-pre"
                            ></textarea>
                        </div>

                        <div className="flex items-center space-x-3 pt-2">
                            <input
                                type="checkbox"
                                id="website-enabled-switch"
                                checked={isEnabled}
                                onChange={(e) => setIsEnabled(e.target.checked)}
                                className="w-5 h-5 rounded rounded-full text-emerald-500 bg-gray-800 border-gray-600 focus:ring-emerald-500"
                            />
                            <label htmlFor="website-enabled-switch" className="text-sm font-medium text-gray-300">
                                Website Enabled (Active)
                            </label>
                        </div>

                        <div className="mt-8 flex items-center justify-end space-x-3 pt-4 border-t border-white/10">
                            <button
                                type="button"
                                onClick={onClose}
                                className="inline-flex items-center px-4 py-2 bg-white/5 hover:bg-white/10 border border-white/10 rounded-xl text-white transition-colors"
                            >
                                <X className="w-4 h-4 mr-1.5" />
                                Cancel
                            </button>
                            <button
                                type="submit"
                                disabled={isLoading}
                                className="inline-flex items-center px-4 py-2 bg-emerald-600 hover:bg-emerald-500 rounded-xl text-white font-medium transition-colors disabled:opacity-50 shadow-lg border border-emerald-500/30"
                            >
                                {isLoading ? <Loader2 className="w-4 h-4 mr-1.5 animate-spin" /> : <Save className="w-4 h-4 mr-1.5" />}
                                {isLoading ? 'Saving...' : 'Save Changes'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>,
        document.body
    );
}
