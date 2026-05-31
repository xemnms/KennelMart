import { useId } from 'react';

type KennelMartLogoProps = {
  compact?: boolean;
  showTagline?: boolean;
  className?: string;
};

export type KennelMartIconName = 'home' | 'search' | 'messages' | 'notifications' | 'create' | 'cart' | 'orders' | 'profile' | 'heart' | 'comment' | 'send' | 'bookmark';

type KennelMartIconProps = {
  name: KennelMartIconName;
  className?: string;
};

const iconPaths: Record<KennelMartIconName, string> = {
  home: 'M8 18.5 18 10l10 8.5V30H8z M13.5 30V21h9v9',
  search: 'M17 8.5a8.5 8.5 0 1 0 5.15 15.3L31 32.65 32.65 31l-8.85-8.85A8.5 8.5 0 0 0 17 8.5Z',
  messages: 'M8.5 11.5h18v12h-10l-5 4.5v-4.5h-3z',
  notifications: 'M16 8.5a5.5 5.5 0 0 1 5.5 5.5v4.25c0 1.05.34 1.96 1 2.7L24 23H8l1.5-1.05c.66-.74 1-1.65 1-2.7V14a5.5 5.5 0 0 1 5.5-5.5Zm-2 16a2 2 0 0 0 4 0',
  create: 'M16 8v16M8 16h16',
  cart: 'M9 10h1.5l1.15 10h11.1l1.6-7.5H12.2M14 26.25a1.25 1.25 0 1 0 0 .01Zm9 0a1.25 1.25 0 1 0 0 .01Z',
  orders: 'M10 8.5h12v19H10zm2.5 4.5h7M12.5 18h7M12.5 22h4.5',
  profile: 'M16 15.5a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9Zm-8 11.5c0-4.4 3.6-8 8-8s8 3.6 8 8',
  heart: 'M16 27C15.6 26.75 14.7 26.1 12.5 24 9 20.8 6 17.6 6 14a6 6 0 0 1 10-4.47A6 6 0 0 1 26 14c0 3.6-3 6.8-6.5 10-2.2 2.1-3.1 2.75-3.5 3Z',
  comment: 'M8 9.5h16v11H13l-5 4v-4Z',
  send: 'M8 16L24 8l-4 8 4 8L8 16Zm10-1.5-6.5 3',
  bookmark: 'M9.5 8h13v17l-6.5-4-6.5 4V8Z',
};

export const KennelMartLogo = ({ compact = false, showTagline = true, className = '' }: KennelMartLogoProps) => {
  const gradientId = useId();
  const shimId = useId();

  return (
    <div className={`kennelmart-logo ${compact ? 'compact' : ''} ${className}`.trim()}>
      <span className="kennelmart-logo-mark" aria-hidden="true">
        <svg viewBox="0 0 64 64" role="presentation">
          <defs>
            <linearGradient id={gradientId} x1="6" y1="4" x2="58" y2="60" gradientUnits="userSpaceOnUse">
              <stop offset="0%" stopColor="#0f2f6b" />
              <stop offset="45%" stopColor="#1d4ed8" />
              <stop offset="100%" stopColor="#d4a62a" />
            </linearGradient>
            <radialGradient id={shimId} cx="38%" cy="22%" r="62%">
              <stop offset="0%" stopColor="rgba(255,255,255,0.25)" />
              <stop offset="100%" stopColor="rgba(255,255,255,0)" />
            </radialGradient>
          </defs>
          <rect x="2" y="2" width="60" height="60" rx="16" fill={`url(#${gradientId})`} />
          <rect x="2" y="2" width="60" height="60" rx="16" fill={`url(#${shimId})`} />
          {/* 4 paw toe circles sitting ABOVE / ON TOP of the roof */}
          <circle cx="22" cy="9" r="2.4" fill="#fff" opacity="0.92" />
          <circle cx="28.5" cy="6" r="2.4" fill="#fff" opacity="0.92" />
          <circle cx="35.5" cy="6" r="2.4" fill="#fff" opacity="0.92" />
          <circle cx="42" cy="9" r="2.4" fill="#fff" opacity="0.92" />
          {/* Kennel roof (no chimney) */}
          <polygon points="10,30 32,10 54,30" fill="rgba(255,255,255,0.18)" />
          <polyline points="10,30 32,10 54,30" fill="none" stroke="#fff" strokeWidth="2.6" strokeLinejoin="round" strokeLinecap="round" />
          {/* Store body */}
          <rect x="12" y="29" width="40" height="24" rx="3" fill="rgba(255,255,255,0.12)" />
          <rect x="12" y="29" width="40" height="24" rx="3" fill="none" stroke="#fff" strokeWidth="2.4" />
          {/* Door */}
          <rect x="22" y="36" width="20" height="15" rx="2.5" fill="rgba(255,255,255,0.18)" />
          {/* Basket handle arc */}
          <path d="M 27 43 Q 32 37.5 37 43" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" />
          {/* Basket body */}
          <rect x="26.5" y="42.5" width="11" height="7.5" rx="1.8" fill="rgba(255,255,255,0.15)" stroke="#fff" strokeWidth="1.8" />
          {/* Basket weave vertical */}
          <line x1="29.5" y1="42.5" x2="29.5" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
          <line x1="32" y1="42.5" x2="32" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
          <line x1="34.5" y1="42.5" x2="34.5" y2="50" stroke="#fff" strokeWidth="1" strokeOpacity="0.55" />
          {/* Basket weave horizontal */}
          <line x1="26.5" y1="46" x2="37.5" y2="46" stroke="#fff" strokeWidth="0.8" strokeOpacity="0.4" />
        </svg>
      </span>

      {showTagline && (
        <span className="kennelmart-logo-copy">
          <strong>KennelMart</strong>
        </span>
      )}
    </div>
  );
};

export const KennelMartIcon = ({ name, className = '' }: KennelMartIconProps) => {
  const label = name.charAt(0).toUpperCase() + name.slice(1);

  return (
    <svg
      className={`kennelmart-icon ${className}`.trim()}
      viewBox="0 0 32 32"
      role="img"
      aria-label={label}
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      <path d={iconPaths[name]} stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
};