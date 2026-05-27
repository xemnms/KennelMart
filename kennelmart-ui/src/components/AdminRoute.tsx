import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

export const AdminRoute = ({ children }: { children: React.ReactNode }) => {
  const user = useAuthStore((state) => state.user);
  return user?.role === 'ADMIN' ? <>{children}</> : <Navigate to="/" replace />;
};