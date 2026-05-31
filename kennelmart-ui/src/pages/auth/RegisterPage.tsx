import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { KennelMartLogo } from '../../components/KennelMartBrand';
import './auth.css';

interface RegisterFormData {
  name: string;
  studentOrFacultyId: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export const RegisterPage = () => {
  const { register, handleSubmit, watch, formState: { errors } } = useForm<RegisterFormData>();
  const registerUser = useAuthStore((state) => state.register);
  const isLoading = useAuthStore((state) => state.isLoading);
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  // eslint-disable-next-line react-hooks/incompatible-library
  const password = watch('password'); // Safe to ignore for MVP

  const onSubmit = async (data: RegisterFormData) => {
    try {
      await registerUser(data);
      navigate('/');
    } catch (err: unknown) {
      let errorMessage = 'Registration failed';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setError(errorMessage);
    }
  };

  return (
    <div className="auth-stage">
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-brand">
          <KennelMartLogo />
        </div>
        <h2>Register</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label>Full Name</label>
            <input {...register('name', { required: 'Name required' })} />
            {errors.name && <span className="error">{errors.name.message}</span>}
          </div>
          <div className="form-group">
            <label>Student/Faculty ID</label>
            <input {...register('studentOrFacultyId', { required: 'ID required' })} />
            {errors.studentOrFacultyId && <span className="error">{errors.studentOrFacultyId.message}</span>}
          </div>
          <div className="form-group">
            <label>Email (NU email only)</label>
            <input
              type="email"
              {...register('email', {
                required: 'Email required',
                pattern: {
                  value: /@students\.nu-laguna\.edu\.ph$/,
                  message: 'Only @students.nu-laguna.edu.ph emails allowed',
                },
              })}
            />
            {errors.email && <span className="error">{errors.email.message}</span>}
          </div>
          <div className="form-group">
            <label>Password (min 8 characters)</label>
            <input type="password" {...register('password', { required: 'Password required', minLength: 8 })} />
            {errors.password && <span className="error">{errors.password.message}</span>}
          </div>
          <div className="form-group">
            <label>Confirm Password</label>
            <input
              type="password"
              {...register('confirmPassword', {
                required: 'Confirm password',
                validate: (value) => value === password || 'Passwords do not match',
              })}
            />
            {errors.confirmPassword && <span className="error">{errors.confirmPassword.message}</span>}
          </div>
          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Registering...' : 'Register'}
          </button>
        </form>
        <p>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
    </div>
  );
};