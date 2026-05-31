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

type KennelMartMarkProps = {
  className?: string;
  role?: 'img' | 'presentation';
  ariaLabel?: string;
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

export const KennelMartMark = ({ className = '', role = 'presentation', ariaLabel }: KennelMartMarkProps) => {
  const tileGradientId = useId();
  const orbGradientId = useId();
  const glossId = useId();

  return (
    <svg
      className={className || undefined}
      viewBox="0 0 64 64"
      role={role}
      aria-label={role === 'img' ? (ariaLabel ?? 'KennelMart') : undefined}
    >
      <defs>
        <linearGradient id={tileGradientId} x1="7" y1="6" x2="58" y2="60" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#0b2f8b" />
          <stop offset="100%" stopColor="#06205f" />
        </linearGradient>
        <radialGradient id={orbGradientId} cx="34" cy="31" r="23" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="#f7da6a" />
          <stop offset="55%" stopColor="#d2a629" />
          <stop offset="100%" stopColor="#8e6820" />
        </radialGradient>
        <radialGradient id={glossId} cx="24" cy="18" r="30" gradientUnits="userSpaceOnUse">
          <stop offset="0%" stopColor="rgba(255,255,255,0.35)" />
          <stop offset="100%" stopColor="rgba(255,255,255,0)" />
        </radialGradient>
      </defs>

      <rect x="2" y="2" width="60" height="60" rx="15" fill={`url(#${tileGradientId})`} />
      <circle cx="32" cy="33" r="21" fill={`url(#${orbGradientId})`} />
      <circle cx="32" cy="33" r="21" fill={`url(#${glossId})`} />

      <circle cx="18" cy="24" r="3" fill="#ffffff" opacity="0.95" />
      <circle cx="24.5" cy="18.5" r="3" fill="#ffffff" opacity="0.95" />
      <circle cx="32" cy="16.5" r="3.1" fill="#ffffff" opacity="0.95" />
      <circle cx="39.5" cy="18.5" r="3" fill="#ffffff" opacity="0.95" />
      <circle cx="46" cy="24" r="3" fill="#ffffff" opacity="0.95" />

      <path d="M19 33.4c4.6-5.9 21.4-5.9 26 0" fill="none" stroke="#ffffff" strokeWidth="3.1" strokeLinecap="round" />

      <path d="M27 42.5c1.9-2.8 8.1-2.8 10 0" fill="none" stroke="#ffffff" strokeWidth="2" strokeLinecap="round" />
      <rect x="25.5" y="42.5" width="13" height="7.5" rx="1.8" fill="none" stroke="#ffffff" strokeWidth="2" />
      <line x1="28.5" y1="42.5" x2="28.5" y2="50" stroke="#ffffff" strokeWidth="1" strokeOpacity="0.6" />
      <line x1="32" y1="42.5" x2="32" y2="50" stroke="#ffffff" strokeWidth="1" strokeOpacity="0.6" />
      <line x1="35.5" y1="42.5" x2="35.5" y2="50" stroke="#ffffff" strokeWidth="1" strokeOpacity="0.6" />
      <circle cx="28" cy="52.5" r="2.1" fill="#ffffff" opacity="0.92" />
      <circle cx="36" cy="52.5" r="2.1" fill="#ffffff" opacity="0.92" />
    </svg>
  );
};

export const KennelMartLogo = ({ compact = false, showTagline = true, className = '' }: KennelMartLogoProps) => {
  return (
    <div className={`kennelmart-logo ${compact ? 'compact' : ''} ${className}`.trim()}>
      <span className="kennelmart-logo-mark" aria-hidden="true">
        <KennelMartMark />
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