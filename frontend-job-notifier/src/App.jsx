import { useContext, lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './components/AuthContext';
import AdminLoginPage from './pages/AdminLoginPage';
import UserLoginPage from './pages/UserLoginPage';
import UserRegisterPage from './pages/UserRegisterPage';
import ProfilePage from './pages/ProfilePage';
import HomePage from './pages/HomePage';
import FosmisNotificationPage from './pages/FosmisNotificationPage';
import NotFoundPage from './pages/NotFoundPage';
import UnauthorizedPage from './pages/UnauthorizedPage';

// Lazy loaded admin routes (Code Split to prevent unauthorized payload downloads)
const AdminDashboard = lazy(() => import('./pages/AdminDashboard'));
const AdminWebsitesPage = lazy(() => import('./pages/AdminWebsitesPage'));

const ProtectedRoute = ({ children, requiredRole }) => {
  const { isAuthenticated, identity } = useContext(AuthContext);
  if (!isAuthenticated) return <Navigate to="/login" replace />;

  if (requiredRole && identity && !identity.roles?.includes(requiredRole)) {
    // If they are an active USER but not an ADMIN and try to access /admin
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Suspense fallback={
          <div className="min-h-screen bg-[#050505] flex items-center justify-center">
            <div className="text-emerald-500 text-sm font-medium tracking-widest uppercase flex items-center space-x-3">
              <svg className="animate-spin h-5 w-5 text-emerald-500" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>Loading Scope...</span>
            </div>
          </div>
        }>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<UserLoginPage />} />
            <Route path="/register" element={<UserRegisterPage />} />
            <Route path="/admin/login" element={<AdminLoginPage />} />
            <Route path="/fosmis-notification" element={<FosmisNotificationPage />} />

            <Route
              path="/profile/*"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/websites"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminWebsitesPage />
                </ProtectedRoute>
              }
            />

            <Route path="/unauthorized" element={<UnauthorizedPage />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </Suspense>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
