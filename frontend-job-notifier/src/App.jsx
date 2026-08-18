import React, { useContext } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './components/AuthContext';
import AdminLoginPage from './pages/AdminLoginPage';
import UserLoginPage from './pages/UserLoginPage';
import AdminDashboard from './pages/AdminDashboard';
import ProfilePage from './pages/ProfilePage';
import HomePage from './pages/HomePage';
import FosmisNotificationPage from './pages/FosmisNotificationPage';
import AdminWebsitesPage from './pages/AdminWebsitesPage';
import NotFoundPage from './pages/NotFoundPage';
import UnauthorizedPage from './pages/UnauthorizedPage';

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
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<UserLoginPage />} />
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
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
