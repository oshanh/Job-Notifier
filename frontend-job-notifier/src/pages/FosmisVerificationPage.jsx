import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CheckCircle2 } from 'lucide-react';
import { fosmisPublicApi } from '../services/apiClient';
import Alert from '../components/Alert';

export default function FosmisVerificationPage() {
    const location = useLocation();
    const navigate = useNavigate();

    // The subscription page will pass { username, email } in route state
    const [username] = useState(location.state?.username || '');
    const [email] = useState(location.state?.email || '');

    const [otpCode, setOtpCode] = useState('');
    const [status, setStatus] = useState('idle'); // idle, loading, success, error
    const [message, setMessage] = useState('');

    if (!username || !email) {
        // If loaded directly without state, boot them back to the subscription index
        navigate('/fosmis-notification');
        return null;
    }

    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setStatus('loading');

        try {
            await fosmisPublicApi.verifySubscription({
                username,
                email,
                otp: otpCode
            });
            setStatus('success');
            setOtpCode('');
            setMessage("Subscription verified successfully! You will now receive notifications.");
        } catch (error) {
            setStatus('error');
            setMessage(error.response?.data?.message || "Invalid or expired OTP. Please try again.");
        }
    };

    return (
        <div className="min-h-screen bg-[#020617] text-white flex flex-col items-center justify-center p-4 relative overflow-hidden">
            {/* Background elements */}
            <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-emerald-500/10 rounded-full blur-[100px]" />
            <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-teal-500/10 rounded-full blur-[100px]" />

            <div className="w-full max-w-md relative z-10">
                {status == 'success' ? (
                    <div className="text-center mb-10">
                    <h1 className="text-4xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-emerald-400 to-teal-400 mb-4">
                        Verify Subscription
                    </h1>
                    
                </div>
                ) : (
                    <div className="text-center mb-10">
                    <h1 className="text-4xl font-extrabold bg-clip-text text-transparent bg-gradient-to-r from-emerald-400 to-teal-400 mb-4">
                        Verify Subscription
                    </h1>
                    <p className="text-sm text-emerald-200/80 leading-relaxed font-medium">
                        We sent a 6-digit verification code to <span className="font-bold text-emerald-300">{email}</span>.
                    </p>
                    
                </div>
                )}
                

                <div className="bg-white/5 border border-white/10 rounded-3xl p-8 shadow-2xl backdrop-blur-xl">
                    {status === 'success' ? (
                        <div className="text-center py-8 space-y-6 animate-in fade-in zoom-in duration-300">
                            <div className="mx-auto w-16 h-16 bg-emerald-500/20 rounded-full flex items-center justify-center">
                                <CheckCircle2 className="w-10 h-10 text-emerald-400" />
                            </div>
                            <div>
                                <h3 className="text-2xl font-bold text-white mb-2">Verified Successfully!</h3>
                                <p className="text-emerald-200/80 text-sm">
                                    You are now subscribed and will receive automated FOSMIS notifications at {email}.
                                </p>
                            </div>
                            <button
                                onClick={() => navigate('/')}
                                className="w-full py-3.5 px-4 bg-emerald-600 hover:bg-emerald-500 rounded-xl text-white font-bold transition-colors shadow-lg shadow-emerald-500/20 outline-none mt-4"
                            >
                                Return to Home
                            </button>
                        </div>
                    ) : (
                        <>
                            {status === 'error' && (
                                <Alert
                                    message={message}
                                    type="error"
                                    onClose={() => setStatus('idle')}
                                />
                            )}
                            <form onSubmit={handleVerifyOtp} className="space-y-6">
                                <div>
                                    <label className="block text-sm font-medium text-gray-300 mb-2 text-center">
                                        Enter 6-Digit Code
                                    </label>
                                    <input
                                        type="text"
                                        required
                                        maxLength={6}
                                        placeholder="000000"
                                        value={otpCode}
                                        onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                                        className="w-full text-center text-3xl tracking-[1em] px-5 py-4 bg-black/40 border border-white/10 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:border-transparent text-white outline-none placeholder-gray-700 transition-all font-mono disabled:opacity-50 disabled:cursor-not-allowed"
                                    />
                                </div>

                                <button
                                    type="submit"
                                    disabled={status === 'loading' || otpCode.length !== 6}
                                    className="w-full py-3.5 px-4 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 rounded-xl text-white font-bold tracking-wide shadow-lg hover:shadow-emerald-500/25 transition-all outline-none disabled:opacity-50 disabled:cursor-not-allowed"
                                >
                                    {status === 'loading' ? 'Verifying...' : 'Verify Email'}
                                </button>
                            </form>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
