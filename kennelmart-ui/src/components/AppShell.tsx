import { useEffect } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import { useNotificationStore } from '../store/notificationStore';
import { getImageUrl } from '../utils/imageUtils';
import './AppShell.css';

const navItems = [
  { to: '/', label: 'Home', icon: '⌂', end: true },
  { to: '/messages/inbox', label: 'Messages', icon: '✉' },
  { to: '/notifications', label: 'Notifications', icon: '♡' },
  { to: '/seller/listings/new', label: 'Create', icon: '＋' },
  { to: '/cart', label: 'Cart', icon: '◫' },
  { to: '/orders', label: 'Orders', icon: '☰' },
  { to: '/profile', label: 'Profile', icon: '◉' },
];

export const AppShell = () => {
  const user = useAuthStore((state) => state.user);
  const { unreadCount: messageUnreadCount, fetchUnreadCount: fetchMessageUnreadCount } = useMessageStore();
  const { unreadCount: notificationUnreadCount, fetchUnreadCount: fetchNotificationUnreadCount } = useNotificationStore();
  const location = useLocation();

  useEffect(() => {
    if (user) {
      void fetchMessageUnreadCount();
      void fetchNotificationUnreadCount();
    }
  }, [user, fetchMessageUnreadCount, fetchNotificationUnreadCount, location.pathname]);

  return (
    <div className="app-shell instagram-shell-shell">
      <aside className="shell-rail">
        <div className="shell-brand">
          <span className="shell-mark">K</span>
          <span className="shell-brand-label">KennelMart</span>
        </div>

        <nav className="shell-nav">
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} className="shell-nav-item">
              <span className="shell-icon">{item.icon}</span>
              <span className="shell-label">{item.label}</span>
              {item.label === 'Messages' && messageUnreadCount > 0 && <em>{messageUnreadCount}</em>}
              {item.label === 'Notifications' && notificationUnreadCount > 0 && <em>{notificationUnreadCount}</em>}
            </NavLink>
          ))}
        </nav>

        <div className="shell-footer">
          {user ? (
            <div className="shell-profile">
              <div className="shell-avatar">
                {user.profileImage ? <img src={getImageUrl(user.profileImage)} alt={user.name} /> : <span>{user.name.charAt(0)}</span>}
              </div>
              <div className="shell-profile-meta">
                <strong>{user.name}</strong>
                <span>{user.role}</span>
              </div>
            </div>
          ) : (
            <div className="shell-profile shell-guest">
              <strong>Guest mode</strong>
              <span>Log in for messages, cart, and seller tools.</span>
            </div>
          )}
        </div>
      </aside>

      <main className="shell-content">
        <Outlet />
      </main>
    </div>
  );
};