import React, { useState, useEffect, useContext } from 'react';
import { userApi } from '../services/apiClient';
import { UserCog, Loader2, Save, Edit2, X } from 'lucide-react';
import { AuthContext } from './AuthContext';

export default function ProfileSettingsTab({ email: initialEmail }) {
    const { updateToken } = useContext(AuthContext);
    const [statusMessage, setStatusMessage] = useState(null);

    // Context states
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');

    // Passwords
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    // Editing flags
    const [isEditingEmail, setIsEditingEmail] = useState(false);
    const [isEditingName, setIsEditingName] = useState(false);
    const [isOtpMode, setIsOtpMode] = useState(false);
    const [otpCode, setOtpCode] = useState('');

    // Loading states
    const [isEmailLoading, setIsEmailLoading] = useState(false);
    const [isNameLoading, setIsNameLoading] = useState(false);
    const [isPasswordLoading, setIsPasswordLoading] = useState(false);

    const [isFetching, setIsFetching] = useState(true);

    useEffect(() => {
        const fetchProfile = async () => {
            setIsFetching(true);
            try {
                const { data } = await userApi.getProfile();
                setName(data.name || '');
                setEmail(data.email || initialEmail || '');
            } catch (error) {
                console.error("Failed to load profile context");
            } finally {
                setIsFetching(false);
            }
        };
        fetchProfile();
    }, [initialEmail]);

    const showMessage = (type, text) => {
        setStatusMessage({ type, text });
        setTimeout(() => setStatusMessage(null), 3500);
    };

    const handleSaveEmail = async (e) => {
        e.preventDefault();
        setIsEmailLoading(true);
        setStatusMessage(null);
        try {
            await userApi.requestEmailChange({ newEmail: email });
            setIsOtpMode(true);
            showMessage('success', 'OTP sent to your new email. Please verify to continue.');
        } catch (err) {
            showMessage('error', 'Failed to request email change. It may be taken.');
        } finally {
            setIsEmailLoading(false);
        }
    };

    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setIsEmailLoading(true);
        setStatusMessage(null);
        try {
            const res = await userApi.verifyEmailChange({ newEmail: email, otp: otpCode });
            if (res.data && res.data.token) {
                updateToken(res.data.token);
            }
            setIsOtpMode(false);
            setIsEditingEmail(false);
            setOtpCode('');
            showMessage('success', 'Email successfully verified and updated!');
        } catch (err) {
            showMessage('error', 'Invalid or expired OTP. Please try again.');
        } finally {
            setIsEmailLoading(false);
        }
    };

    const handleSaveName = async (e) => {
        e.preventDefault();
        setIsNameLoading(true);
        setStatusMessage(null);
        try {
            await userApi.updateProfile({ name });
            setIsEditingName(false);
            showMessage('success', 'Name updated successfully.');
        } catch (err) {
            showMessage('error', 'Failed to update name.');
        } finally {
            setIsNameLoading(false);
        }
    };

    const handleSavePassword = async (e) => {
        e.preventDefault();
        if (newPassword !== confirmPassword) {
            showMessage('error', 'New passwords do not match.');
            return;
        }
        if (!oldPassword || !newPassword) {
            showMessage('error', 'Please fill in all password fields.');
            return;
        }

        setIsPasswordLoading(true);
        setStatusMessage(null);
        try {
            await userApi.updateProfile({ oldPassword, password: newPassword });
            setOldPassword('');
            setNewPassword('');
            setConfirmPassword('');
            showMessage('success', 'Password changed successfully.');
        } catch (err) {
            showMessage('error', 'Failed to change password. Old password may be incorrect.');
        } finally {
            setIsPasswordLoading(false);
        }
    };

    if (isFetching) {
        return (
            <div className="flex items-center justify-center p-20">
                <Loader2 className="w-8 h-8 text-emerald-500 animate-spin" />
            </div>
        );
    }

    return (
        <div className="bg-white/5 border border-white/10 rounded-2xl shadow-xl w-full max-w-2xl mx-auto overflow-hidden">
            <div className="bg-black/30 p-6 border-b border-white/10 flex items-center space-x-4">
                <div className="p-3 bg-emerald-500/20 rounded-2xl border border-emerald-500/30">
                    <UserCog className="w-8 h-8 text-emerald-400" />
                </div>
                <div>
                    <h3 className="text-xl font-bold text-white tracking-tight">Account Settings</h3>
                    <p className="text-sm text-emerald-200/50 mt-0.5">Manage your core identity, password, and context mappings.</p>
                </div>
            </div>

            <div className="p-6">
                {statusMessage && (
                    <div className={`mb-6 p-4 rounded-xl border text-sm font-medium flex items-center space-x-2 ${statusMessage.type === 'success' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' : 'bg-red-500/10 text-red-400 border-red-500/30'}`}>
                        <span>{statusMessage.text}</span>
                    </div>
                )}

                <div className="space-y-8">
                    {/* 1. Email Section */}
                    <div>
                        <div className="flex justify-between items-end mb-2">
                            <label className="block text-sm font-medium text-gray-300 ml-1">Email Address</label>
                            {!isEditingEmail && (
                                <button type="button" onClick={() => setIsEditingEmail(true)} className="text-xs text-emerald-400 hover:text-emerald-300 flex items-center transition-colors">
                                    <Edit2 className="w-3 h-3 mr-1" /> Edit Email
                                </button>
                            )}
                        </div>
                        {isEditingEmail ? (
                            isOtpMode ? (
                                <form onSubmit={handleVerifyOtp} className="flex flex-col space-y-3">
                                    <p className="text-xs text-emerald-300">Enter the 6-digit verification code sent to {email}</p>
                                    <div className="flex space-x-3">
                                        <input
                                            type="text"
                                            required
                                            maxLength="6"
                                            placeholder="XXXXXX"
                                            value={otpCode}
                                            onChange={e => setOtpCode(e.target.value)}
                                            className="w-32 px-4 py-2.5 bg-black/40 border border-emerald-500/50 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none shadow-inner transition-all text-sm tracking-widest text-center"
                                        />
                                        <button type="button" onClick={() => { setIsOtpMode(false); setIsEditingEmail(false); setOtpCode(''); }} className="px-3 text-gray-400 hover:text-white transition-colors"><X className="w-5 h-5" /></button>
                                        <button type="submit" disabled={isEmailLoading} className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-medium rounded-xl transition-all disabled:opacity-50 inline-flex items-center text-sm shadow-lg border border-emerald-500/30">
                                            {isEmailLoading ? <Loader2 className="w-4 h-4 animate-spin mr-1.5" /> : null} Verify OTP
                                        </button>
                                    </div>
                                </form>
                            ) : (
                                <form onSubmit={handleSaveEmail} className="flex space-x-3">
                                    <input
                                        type="email"
                                        required
                                        value={email}
                                        onChange={e => setEmail(e.target.value)}
                                        className="flex-1 px-4 py-2.5 bg-black/40 border border-emerald-500/50 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none shadow-inner transition-all text-sm"
                                    />
                                    <button type="button" onClick={() => setIsEditingEmail(false)} className="px-3 text-gray-400 hover:text-white transition-colors"><X className="w-5 h-5" /></button>
                                    <button type="submit" disabled={isEmailLoading} className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-medium rounded-xl transition-all disabled:opacity-50 inline-flex items-center text-sm shadow-lg border border-emerald-500/30">
                                        {isEmailLoading ? <Loader2 className="w-4 h-4 animate-spin mr-1.5" /> : null} Send OTP
                                    </button>
                                </form>
                            )
                        ) : (
                            <div className="px-4 py-3 bg-black/20 border border-white/5 rounded-xl text-gray-400 font-mono text-sm shadow-inner">
                                {email}
                            </div>
                        )}
                    </div>

                    {/* 2. Name Section */}
                    <div>
                        <div className="flex justify-between items-end mb-2">
                            <label className="block text-sm font-medium text-gray-300 ml-1">Display Name</label>
                            {!isEditingName && (
                                <button type="button" onClick={() => setIsEditingName(true)} className="text-xs text-emerald-400 hover:text-emerald-300 flex items-center transition-colors">
                                    <Edit2 className="w-3 h-3 mr-1" /> Edit Name
                                </button>
                            )}
                        </div>
                        {isEditingName ? (
                            <form onSubmit={handleSaveName} className="flex space-x-3">
                                <input
                                    type="text"
                                    required
                                    value={name}
                                    onChange={e => setName(e.target.value)}
                                    className="flex-1 px-4 py-2.5 bg-black/40 border border-emerald-500/50 rounded-xl focus:ring-2 focus:ring-emerald-500 text-white outline-none shadow-inner transition-all text-sm"
                                />
                                <button type="button" onClick={() => setIsEditingName(false)} className="px-3 text-gray-400 hover:text-white transition-colors"><X className="w-5 h-5" /></button>
                                <button type="submit" disabled={isNameLoading} className="px-5 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white font-medium rounded-xl transition-all disabled:opacity-50 inline-flex items-center text-sm shadow-lg border border-emerald-500/30">
                                    {isNameLoading ? <Loader2 className="w-4 h-4 animate-spin mr-1.5" /> : <Save className="w-4 h-4 mr-1.5" />} Save
                                </button>
                            </form>
                        ) : (
                            <div className="px-4 py-3 bg-black/20 border border-white/5 rounded-xl text-white text-sm shadow-inner">
                                {name || "Name not set"}
                            </div>
                        )}
                    </div>

                    {/* 3. Password Section */}
                    <div className="pt-4 border-t border-white/5">
                        <label className="block text-sm font-medium text-gray-300 mb-4 ml-1">Password Change</label>
                        <form onSubmit={handleSavePassword} className="space-y-4 p-5 bg-black/20 border border-white/5 rounded-xl">
                            <div>
                                <label className="block text-xs text-gray-400 mb-1.5">Old Password</label>
                                <input
                                    type="password"
                                    required
                                    value={oldPassword}
                                    onChange={e => setOldPassword(e.target.value)}
                                    className="w-full px-4 py-2.5 bg-white/5 border border-white/10 rounded-xl focus:bg-black/40 focus:ring-2 focus:ring-emerald-500 text-white outline-none transition-all shadow-inner text-sm"
                                />
                            </div>
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-xs text-gray-400 mb-1.5">New Password</label>
                                    <input
                                        type="password"
                                        required
                                        value={newPassword}
                                        onChange={e => setNewPassword(e.target.value)}
                                        className="w-full px-4 py-2.5 bg-white/5 border border-white/10 rounded-xl focus:bg-black/40 focus:ring-2 focus:ring-emerald-500 text-white outline-none transition-all shadow-inner text-sm"
                                    />
                                </div>
                                <div>
                                    <label className="block text-xs text-gray-400 mb-1.5">Confirm New Password</label>
                                    <input
                                        type="password"
                                        required
                                        value={confirmPassword}
                                        onChange={e => setConfirmPassword(e.target.value)}
                                        className="w-full px-4 py-2.5 bg-white/5 border border-white/10 rounded-xl focus:bg-black/40 focus:ring-2 focus:ring-emerald-500 text-white outline-none transition-all shadow-inner text-sm"
                                    />
                                </div>
                            </div>
                            <div className="flex justify-end pt-3">
                                <button
                                    type="submit"
                                    disabled={isPasswordLoading}
                                    className="px-6 py-2.5 bg-emerald-600 hover:bg-emerald-500 text-white text-sm font-medium rounded-xl transition-all disabled:opacity-50 inline-flex items-center border border-emerald-500/30 shadow-lg shadow-emerald-900/40 hover:scale-[1.02] disabled:hover:scale-100"
                                >
                                    {isPasswordLoading ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : <Save className="w-4 h-4 mr-2" />}
                                    Change Password
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
