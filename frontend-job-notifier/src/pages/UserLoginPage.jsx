import React, { useState, useContext } from 'react';
import { AuthContext } from '../components/AuthContext';
import { userApi, authApi } from '../services/apiClient';
import { useNavigate, Link } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';

export default function UserLoginPage() {
    const { login, updateToken } = useContext(AuthContext);
    const navigate = useNavigate();

    const [isRegistering, setIsRegistering] = useState(false);
    const [isOtpMode, setIsOtpMode] = useState(false);
    const [isForgotPasswordMode, setIsForgotPasswordMode] = useState(false);
    const [isResetCodeSent, setIsResetCodeSent] = useState(false);
    const [otpCode, setOtpCode] = useState('');

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);

        try {
            if (isRegistering) {
                if (password !== confirmPassword) {
                    setError("Passwords do not match");
                    return;
                }

                // Register Flow - dispatches OTP
                const res = await userApi.register({ name, email, password });
                if (res.data?.message === "OTP_SENT" || res.data?.token === "OTP_SENT") {
                    setIsOtpMode(true);
                    setError(null);
                } else {
                    setIsOtpMode(true);
                }
            } else {
                // Login Flow
                const loginResult = await login(email, password);
                if (loginResult.success) {
                    navigate('/profile');
                } else {
                    setError(loginResult.message);
                }
            }
        } catch (err) {
            console.error("Auth process failed", err);
            setError(err.response?.data?.message || err.response?.data || "Authentication error occurred");
        } finally {
            setIsLoading(false);
        }
    };

    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);
        try {
            const res = await authApi.verifyRegistration({ email, password: otpCode });
            if (res.data && res.data.token) {
                updateToken(res.data.token);
                navigate('/profile');
            }
        } catch (err) {
            console.error("OTP verification failed", err);
            setError(err.response?.data?.message || err.response?.data || "Invalid or expired OTP");
        } finally {
            setIsLoading(false);
        }
    };

    const handleForgotPasswordSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            await authApi.forgotPassword({ email });
            setIsResetCodeSent(true);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to send reset code");
        } finally {
            setIsLoading(false);
        }
    };

    const handleResetPasswordSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }
        setIsLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            await authApi.resetPassword({ email, otp: otpCode, newPassword: password });
            setIsForgotPasswordMode(false);
            setIsResetCodeSent(false);
            setOtpCode('');
            setPassword('');
            setConfirmPassword('');
            setSuccessMessage("Password successfully reset! Please sign in.");
        } catch (err) {
            setError(err.response?.data?.message || "Invalid OTP or reset failed");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-emerald-900 via-slate-900 to-black p-4 relative overflow-hidden">
            <div className="absolute top-1/4 right-1/4 w-[600px] h-[600px] bg-teal-600 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-pulse"></div>

            <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 shadow-2xl relative z-10">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-extrabold text-white tracking-tight">
                        {isForgotPasswordMode
                            ? (isResetCodeSent ? 'Check Your Inbox' : 'Reset Password')
                            : isOtpMode ? 'Check Your Inbox' : (isRegistering ? 'Create Account' : 'Welcome Back')}
                    </h2>
                    <p className="text-sm text-emerald-200 mt-2">
                        {isForgotPasswordMode
                            ? (isResetCodeSent ? `We sent a reset code to ${email}` : 'Enter your email to receive a secure reset code')
                            : isOtpMode
                                ? `We sent a 6-digit code to ${email}`
                                : (isRegistering ? 'Sign up for instant automated career alerts' : 'Sign in to monitor your active job signals')}
                    </p>
                </div>

                {error && (
                    <div className="bg-red-500/20 border border-red-500/50 text-red-200 px-4 py-3 rounded-lg mb-6 text-sm">
                        {error}
                    </div>
                )}
                {successMessage && (
                    <div className="bg-emerald-500/20 border border-emerald-500/50 text-emerald-200 px-4 py-3 rounded-lg mb-6 text-sm">
                        {successMessage}
                    </div>
                )}

                {isOtpMode ? (
                    <form onSubmit={handleVerifyOtp} className="space-y-5">
                        <div>
                            <label className="block text-sm font-medium text-emerald-400 mb-4 text-center">Enter Verification Code</label>
                            <input
                                type="text"
                                required
                                maxLength={6}
                                value={otpCode}
                                onChange={(e) => setOtpCode(e.target.value)}
                                className="w-full px-4 py-4 bg-black/40 border border-emerald-500/50 rounded-xl focus:ring-2 focus:ring-emerald-400 focus:border-transparent text-white placeholder-emerald-900/50 transition-all outline-none text-center text-2xl tracking-[0.5em] font-mono shadow-inner"
                                placeholder="------"
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full py-3 px-4 mt-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold rounded-xl shadow-[0_0_20px_rgba(5,150,105,0.4)] transform transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Verifying...' : 'Complete Registration'}
                        </button>

                        <div className="text-center mt-6 text-sm">
                            <button
                                type="button"
                                onClick={() => setIsOtpMode(false)}
                                className="font-medium text-gray-400 hover:text-white transition-colors cursor-pointer"
                            >
                                &larr; Back to Registration
                            </button>
                        </div>
                    </form>
                ) : isForgotPasswordMode ? (
                    <form onSubmit={isResetCodeSent ? handleResetPasswordSubmit : handleForgotPasswordSubmit} className="space-y-5">
                        {!isResetCodeSent ? (
                            <div>
                                <label className="block text-sm font-medium text-gray-200 mb-2">Email Address</label>
                                <input
                                    type="email"
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                    placeholder="you@domain.com"
                                />
                            </div>
                        ) : (
                            <>
                                <div>
                                    <label className="block text-sm font-medium text-emerald-400 mb-4 text-center">Enter 6-Digit Reset Code</label>
                                    <input
                                        type="text"
                                        required
                                        maxLength={6}
                                        value={otpCode}
                                        onChange={(e) => setOtpCode(e.target.value)}
                                        className="w-full px-4 py-4 bg-black/40 border border-emerald-500/50 rounded-xl focus:ring-2 focus:ring-emerald-400 focus:border-transparent text-white placeholder-emerald-900/50 transition-all outline-none text-center text-2xl tracking-[0.5em] font-mono shadow-inner"
                                        placeholder="------"
                                    />
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-200 mb-2">New Password</label>
                                    <div className="relative">
                                        <input
                                            type={showPassword ? "text" : "password"}
                                            required
                                            minLength={6}
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                            placeholder="••••••••"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPassword(!showPassword)}
                                            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-emerald-400 transition-colors"
                                        >
                                            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                        </button>
                                    </div>
                                </div>
                                <div>
                                    <label className="block text-sm font-medium text-gray-200 mb-2">Confirm New Password</label>
                                    <div className="relative">
                                        <input
                                            type={showConfirmPassword ? "text" : "password"}
                                            required
                                            minLength={6}
                                            value={confirmPassword}
                                            onChange={(e) => setConfirmPassword(e.target.value)}
                                            className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                            placeholder="••••••••"
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-emerald-400 transition-colors"
                                        >
                                            {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                        </button>
                                    </div>
                                </div>
                            </>
                        )}
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full py-3 px-4 mt-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold rounded-xl shadow-[0_0_20px_rgba(5,150,105,0.4)] transform transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Processing...' : (isResetCodeSent ? 'Reset Password' : 'Send OTP')}
                        </button>
                        <div className="text-center mt-6 text-sm">
                            <button
                                type="button"
                                onClick={() => {
                                    setIsForgotPasswordMode(false);
                                    setIsResetCodeSent(false);
                                }}
                                className="font-medium text-gray-400 hover:text-white transition-colors cursor-pointer"
                            >
                                &larr; Back to Login
                            </button>
                        </div>
                    </form>
                ) : (
                    <form onSubmit={handleSubmit} className="space-y-5">
                        {isRegistering && (
                            <div>
                                <label className="block text-sm font-medium text-gray-200 mb-2">Display Name</label>
                                <input
                                    type="text"
                                    required
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                    placeholder="Your Name"
                                />
                            </div>
                        )}

                        <div>
                            <label className="block text-sm font-medium text-gray-200 mb-2">Email Address</label>
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                placeholder="you@domain.com"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-200 mb-2">Password</label>
                            <div className="relative">
                                <input
                                    type={showPassword ? "text" : "password"}
                                    required
                                    minLength={6}
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                    placeholder="••••••••"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-emerald-400 transition-colors"
                                >
                                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                </button>
                            </div>
                        </div>

                        {isRegistering && (
                            <div>
                                <label className="block text-sm font-medium text-gray-200 mb-2">Confirm Password</label>
                                <div className="relative">
                                    <input
                                        type={showConfirmPassword ? "text" : "password"}
                                        required
                                        minLength={6}
                                        value={confirmPassword}
                                        onChange={(e) => setConfirmPassword(e.target.value)}
                                        className="w-full px-4 py-3 bg-black/30 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white placeholder-gray-400 transition-all outline-none"
                                        placeholder="••••••••"
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                        className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-emerald-400 transition-colors"
                                    >
                                        {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>
                        )}

                        <button
                            type="submit"
                            disabled={isLoading}
                            className="w-full py-3 px-4 mt-2 bg-emerald-600 hover:bg-emerald-500 text-white font-bold rounded-xl shadow-lg transform transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                            {isLoading ? 'Processing...' : isRegistering ? 'Sign Up' : 'Sign In'}
                        </button>

                        {!isRegistering && (
                            <div className="text-center mt-4">
                                <button
                                    type="button"
                                    onClick={() => {
                                        setIsForgotPasswordMode(true);
                                        setIsResetCodeSent(false);
                                        setError(null);
                                        setSuccessMessage(null);
                                    }}
                                    className="text-sm font-medium text-gray-400 hover:text-emerald-400 transition-colors"
                                >
                                    Forgot your password?
                                </button>
                            </div>
                        )}

                        <div className="text-center mt-6 text-sm">
                            <span className="text-gray-400">
                                {isRegistering ? 'Already have an account?' : 'Need to join?'}
                            </span>
                            <button
                                type="button"
                                onClick={() => setIsRegistering(!isRegistering)}
                                className="ml-2 font-medium text-emerald-400 hover:text-emerald-300 transition-colors cursor-pointer"
                            >
                                {isRegistering ? 'Sign In' : 'Register'}
                            </button>
                        </div>

                        <div className="text-center mt-2 border-t border-white/10 pt-4">
                            <Link to="/" className="text-xs text-gray-500 hover:text-gray-300 transition-colors">
                                &larr; Back to Home
                            </Link>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
}
