import { useEffect, useState } from 'react';
import { NavLink, Outlet, useLocation, useSearchParams } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';
import { useMessageStore } from '../store/messageStore';
import { useNotificationStore } from '../store/notificationStore';
import { getImageUrl } from '../utils/imageUtils';
import { KennelMartIcon, KennelMartLogo, type KennelMartIconName } from './KennelMartBrand';
import './AppShell.css';

const navItems: Array<{ to: string; label: string; icon: KennelMartIconName; end?: boolean; center?: boolean }> = [
  { to: '/', label: 'Home', icon: 'home', end: true },
  { to: '/cart', label: 'Pawket', icon: 'cart' },
  { to: '/seller/listings/new', label: 'Create', icon: 'create', center: true },
  { to: '/orders', label: 'Orders', icon: 'orders' },
  { to: '/profile', label: 'Pawfile', icon: 'profile' },
];

export const AppShell = () => {
  const user = useAuthStore((state) => state.user);
  const messageUnreadCount = useMessageStore((state) => state.unreadCount);
  const fetchMessageUnreadCount = useMessageStore((state) => state.fetchUnreadCount);
  const notificationUnreadCount = useNotificationStore((state) => state.unreadCount);
  const fetchNotificationUnreadCount = useNotificationStore((state) => state.fetchUnreadCount);
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [sidebarSearch, setSidebarSearch] = useState(searchParams.get('q') ?? '');
  const sidebarMode = searchParams.get('mode') === 'users' ? 'users' : 'products';
  const showSidebarSearch = location.pathname === '/';

  useEffect(() => {
    setSidebarSearch(searchParams.get('q') ?? '');
  }, [location.pathname, searchParams]);

  useEffect(() => {
    if (user) {
      void fetchMessageUnreadCount(true);
      void fetchNotificationUnreadCount(true);

      const intervalId = window.setInterval(() => {
        void fetchMessageUnreadCount();
        void fetchNotificationUnreadCount();
      }, 20000);

      return () => window.clearInterval(intervalId);
    }
  }, [user, fetchMessageUnreadCount, fetchNotificationUnreadCount]);

  const updateMarketplaceSearch = (nextMode: 'products' | 'users', nextQuery: string) => {
    const trimmedQuery = nextQuery.trim();

    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);

      if (trimmedQuery) {
        next.set('q', trimmedQuery);
      } else {
        next.delete('q');
      }

      next.set('mode', nextMode);
      next.delete('page');
      next.delete('userPage');
      return next;
    });
  };

  const handleSidebarSearchSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    updateMarketplaceSearch(sidebarMode, sidebarSearch);
  };

  return (
    <div className="app-shell mobile-app-shell">

      {/* ── Top status bar ── */}
      <header className="mobile-topbar">
        <NavLink to="/seller/listings/new" className="mobile-topbar-icon-btn" aria-label="Create listing">
          <KennelMartIcon name="create" className="shell-icon" />
        </NavLink>
        <KennelMartLogo className="mobile-brand-lockup" />
        <div className="mobile-topbar-actions">
          {user ? (
            <>
              <NavLink to="/notifications" className="mobile-topbar-icon-btn" aria-label="Notifications">
                <KennelMartIcon name="notifications" className="shell-icon" />
                {notificationUnreadCount > 0 && <em className="topbar-badge">{notificationUnreadCount}</em>}
              </NavLink>
              <NavLink to="/messages/inbox" className="mobile-topbar-icon-btn" aria-label="Messages">
                <KennelMartIcon name="messages" className="shell-icon" />
                {messageUnreadCount > 0 && <em className="topbar-badge">{messageUnreadCount}</em>}
              </NavLink>
            </>
          ) : (
            <NavLink to="/login" className="mobile-topbar-login">Log in</NavLink>
          )}
        </div>
      </header>

      {/* ── Search bar — marketplace page only ── */}
      {showSidebarSearch && (
        <div className="mobile-search-section">
          <div className="shell-search-toggle">
            <button type="button" className={sidebarMode === 'products' ? 'active' : ''} onClick={() => updateMarketplaceSearch('products', sidebarSearch)}>Products</button>
            <button type="button" className={sidebarMode === 'users' ? 'active' : ''} onClick={() => updateMarketplaceSearch('users', sidebarSearch)}>People</button>
          </div>
          <form className="mobile-search-form" onSubmit={handleSidebarSearchSubmit}>
            <label className="shell-search-field">
              <KennelMartIcon name="search" className="shell-search-icon" />
              <input
                type="text"
                value={sidebarSearch}
                onChange={(event) => setSidebarSearch(event.target.value)}
                placeholder={sidebarMode === 'users' ? 'Search people' : 'Search products'}
              />
            </label>
          </form>
        </div>
      )}

      {/* ── Page content ── */}
      <main className="shell-content">
        <div className="shell-stage">
          <div className="shell-page-card">
            <Outlet />
          </div>
        </div>
      </main>

      {/* ── Bottom tab navigation ── */}
      <nav className="mobile-tabbar">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={`mobile-tab-item${item.center ? ' mobile-tab-create' : ''}`}
          >
            <div className="mobile-tab-icon-wrap">
              <KennelMartIcon name={item.icon} className="shell-icon" />
            </div>
            {!item.center && <span className="mobile-tab-label">{item.label}</span>}
          </NavLink>
        ))}
      </nav>

    </div>
  );
};