import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, roles }) {
  const { user, hasRole } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (roles && roles.length && !hasRole(...roles)) {
    return <Navigate to="/" replace />;
  }
  return children;
}
