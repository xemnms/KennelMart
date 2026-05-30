import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { KennelMartLogo } from '../../components/KennelMartBrand';
import './auth.css';

interface LoginFormData {
  email: string;
  password: string;
}

export const LoginPage = () => {
  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>();
  const login = useAuthStore((state) => state.login);
  const isLoading = useAuthStore((state) => state.isLoading);
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (data: LoginFormData) => {
    try {
      await login(data);
      navigate('/');
    } catch (err: unknown) {
      // Type-safe error handling
      let errorMessage = 'Login failed';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setError(errorMessage);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-brand">
          <KennelMartLogo />
        </div>
        <h2>Login</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" {...register('email', { required: 'Email required' })} />
            {errors.email && <span className="error">{errors.email.message}</span>}
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" {...register('password', { required: 'Password required' })} />
            {errors.password && <span className="error">{errors.password.message}</span>}
          </div>
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <p>
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
};