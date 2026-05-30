import { useEffect } from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import { useNotificationStore } from '../store/notificationStore';
import { getImageUrl } from '../utils/imageUtils';
import { KennelMartIcon, KennelMartLogo, type KennelMartIconName } from './KennelMartBrand';
import './AppShell.css';

const navItems: Array<{ to: string; label: string; icon: KennelMartIconName; end?: boolean }> = [
  { to: '/', label: 'Home', icon: 'home', end: true },
  { to: '/messages/inbox', label: 'Messages', icon: 'messages' },
  { to: '/notifications', label: 'Notifications', icon: 'notifications' },
  { to: '/seller/listings/new', label: 'Create', icon: 'create' },
  { to: '/cart', label: 'Cart', icon: 'cart' },
  { to: '/orders', label: 'Orders', icon: 'orders' },
  { to: '/profile', label: 'Profile', icon: 'profile' },
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
          <KennelMartLogo className="shell-brand-lockup" />
        </div>

        <nav className="shell-nav">
          {navItems.map((item) => (
            <NavLink key={item.to} to={item.to} end={item.end} className="shell-nav-item">
              <KennelMartIcon name={item.icon} className="shell-icon" />
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