import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { KennelMartMark } from '../../components/KennelMartBrand';
import './Splash.css';

export const SplashPage = () => {
  const token = useAuthStore((state) => state.token);
  const navigate = useNavigate();

  useEffect(() => {
    if (token) navigate('/', { replace: true });
  }, [token, navigate]);

  return (
    <div className="splash-stage">
      <div className="splash-container">

        {/* Ambient glows */}
        <span className="splash-glow splash-glow--blue" aria-hidden="true" />
        <span className="splash-glow splash-glow--gold" aria-hidden="true" />

        {/* Logo + name */}
        <div className="splash-brand">
          <div className="splash-logo-wrap">
            <KennelMartMark className="splash-logo-svg" role="img" ariaLabel="KennelMart" />
            <span className="splash-logo-ring" aria-hidden="true" />
          </div>
          <h1 className="splash-name">KennelMart</h1>
          <p className="splash-tagline">Your campus marketplace</p>
        </div>

        {/* CTAs */}
        <div className="splash-actions">
          <Link to="/login" className="splash-btn splash-btn--primary">Sign In</Link>
          <Link to="/register" className="splash-btn splash-btn--ghost">Create Account</Link>
        </div>

        <p className="splash-footer">For NU Laguna Community</p>
      </div>
    </div>
  );
};
