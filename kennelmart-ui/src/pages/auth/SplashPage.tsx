import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { useId } from 'react';
import './Splash.css';

const PawLogo = () => {
  const gradId = useId();
  const shimId = useId();
  return (
    <svg className="splash-logo-svg" viewBox="0 0 64 64" role="img" aria-label="KennelMart">
      <defs>
        <linearGradient id={gradId} x1="6" y1="4" x2="58" y2="60" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#0f2f6b" />
          <stop offset="45%" stopColor="#1d4ed8" />
          <stop offset="100%" stopColor="#d4a62a" />
        </linearGradient>
        <radialGradient id={shimId} cx="38%" cy="22%" r="62%">
          <stop offset="0%" stopColor="rgba(255,255,255,0.25)" />
          <stop offset="100%" stopColor="rgba(255,255,255,0)" />
        </radialGradient>
      </defs>

      {/* App tile background */}
      <rect x="2" y="2" width="60" height="60" rx="16" fill={`url(#${gradId})`} />
      <rect x="2" y="2" width="60" height="60" rx="16" fill={`url(#${shimId})`} />

      {/* ── 4 paw toe circles sitting ABOVE / ON TOP of the roof ── */}
      <circle cx="22" cy="9" r="2.4" fill="#fff" opacity="0.92" />
      <circle cx="28.5" cy="6" r="2.4" fill="#fff" opacity="0.92" />
      <circle cx="35.5" cy="6" r="2.4" fill="#fff" opacity="0.92" />
      <circle cx="42" cy="9" r="2.4" fill="#fff" opacity="0.92" />

      {/* ── Kennel roof (no chimney) ── */}
      <polygon points="10,30 32,10 54,30" fill="rgba(255,255,255,0.18)" />
      <polyline points="10,30 32,10 54,30" fill="none" stroke="#fff" strokeWidth="2.6" strokeLinejoin="round" strokeLinecap="round" />

      {/* ── Store body ── */}
      <rect x="12" y="29" width="40" height="24" rx="3" fill="rgba(255,255,255,0.12)" />
      <rect x="12" y="29" width="40" height="24" rx="3" fill="none" stroke="#fff" strokeWidth="2.4" />

      {/* Door */}
      <rect x="22" y="36" width="20" height="15" rx="2.5" fill="rgba(255,255,255,0.18)" />

      {/* ── Basket inside door ── */}
      {/* handle arc */}
      <path d="M 27 43 Q 32 37.5 37 43" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" />
      {/* body */}
      <rect x="26.5" y="42.5" width="11" height="7.5" rx="1.8" fill="rgba(255,255,255,0.15)" stroke="#fff" strokeWidth="1.8" />
      {/* weave vertical */}
      <line x1="29.5" y1="42.5" x2="29.5" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
      <line x1="32" y1="42.5" x2="32" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
      <line x1="34.5" y1="42.5" x2="34.5" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
      {/* weave horizontal */}
      <line x1="26.5" y1="46" x2="37.5" y2="46" stroke="#fff" strokeWidth="0.8" strokeOpacity="0.4" />
    </svg>
  );
};

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
            <PawLogo />
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
