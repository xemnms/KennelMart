import { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { listingService } from '../../services/listingService';
import { useAuthStore } from '../../store/authStore';
import { useMessageStore } from '../../store/messageStore';
import { useNotificationStore } from '../../store/notificationStore';
import type { ProductListing, ListingFilters } from '../../types/marketplace';
import './Marketplace.css';

export const MarketplacePage = () => {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const { unreadCount: msgUnreadCount, fetchUnreadCount: fetchMsgUnreadCount } = useMessageStore();
  const { unreadCount: notifUnreadCount, fetchUnreadCount: fetchNotifUnreadCount } = useNotificationStore();
  const navigate = useNavigate();
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ListingFilters>({ page: 0, size: 12 });
  const [totalPages, setTotalPages] = useState(0);
  const [searchInput, setSearchInput] = useState('');

  const categories = [
    'All',
    'ELECTRONICS',
    'BOOKS',
    'CLOTHING',
    'FURNITURE',
    'SERVICES',
    'FOOD_BEVERAGE',
    'SPORTS_RECREATION',
    'STATIONERY',
    'OTHERS'
  ];

  const fetchListings = useCallback(async () => {
    setLoading(true);
    try {
      const data = await listingService.getListings(filters);
      setListings(data.content);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchListings();
  }, [fetchListings]);

  useEffect(() => {
    if (user) {
      fetchMsgUnreadCount();
      fetchNotifUnreadCount();
    }
  }, [user, fetchMsgUnreadCount, fetchNotifUnreadCount]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setFilters(prev => ({ ...prev, keyword: searchInput, page: 0 }));
  };

  const handleCategoryClick = (category: string) => {
    setFilters(prev => ({
      ...prev,
      category: category === 'All' ? undefined : category,
      page: 0
    }));
  };

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="marketplace">
      {/* Header */}
      <header className="marketplace-header">
        <div className="logo">
          <Link to="/">KennelMart</Link>
        </div>
        <form onSubmit={handleSearch} className="search-bar">
          <input
            type="text"
            placeholder="Search for products, services, and more"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
          <button type="submit">🔍</button>
        </form>
        {user ? (
          <div className="user-menu">
            <Link to="/cart" className="cart-link">Cart</Link>
            <Link to="/messages/inbox" className="messages-link">
              Messages
              {msgUnreadCount > 0 && <span className="badge">{msgUnreadCount}</span>}
            </Link>
            <Link to="/notifications" className="notifications-link">
              🔔
              {notifUnreadCount > 0 && <span className="badge">{notifUnreadCount}</span>}
            </Link>
            <Link to="/my-listings" className="my-listings-link">My Listings</Link>
            <Link to="/orders" className="my-orders-link">My Orders</Link>
            <Link to="/seller/orders" className="my-sales-link">My Sales</Link>
            <Link to="/seller/listings/new" className="sell-btn">Sell</Link>
            {user?.role === 'ADMIN' && (
              <Link to="/admin" className="admin-link">Admin Panel</Link>
            )}
            <Link to="/profile" className="avatar">👤</Link>
            <button onClick={handleLogout} className="logout-btn">Logout</button>
          </div>
        ) : (
          <div className="auth-buttons">
            <Link to="/login" className="login-btn">Log in</Link>
            <Link to="/register" className="signup-btn">Sign up</Link>
          </div>
        )}
      </header>

      {/* Categories */}
      <div className="categories">
        {categories.map(cat => (
          <button
            key={cat}
            className={`category-chip ${(filters.category === cat || (cat === 'All' && !filters.category)) ? 'active' : ''}`}
            onClick={() => handleCategoryClick(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Product grid */}
      {loading ? (
        <div className="loading">Loading...</div>
      ) : (
        <>
          {listings.length === 0 ? (
            <div className="empty-state">
              <p>No listings yet. Be the first to <Link to="/seller/listings/new">sell something</Link>!</p>
            </div>
          ) : (
            <div className="product-grid">
              {listings.map((listing) => (
                <div key={listing.id} className="product-card">
                  <Link to={`/listings/${listing.id}`}>
                    <div className="product-image">
                      <img src={listing.imageUrls[0] || '/placeholder.png'} alt={listing.title} />
                    </div>
                    <div className="product-info">
                      <h3>{listing.title}</h3>
                      <p className="price">₱{listing.price}</p>
                      <p className="seller">{listing.sellerName}</p>
                      <span className="category-badge">{listing.category}</span>
                    </div>
                  </Link>
                </div>
              ))}
            </div>
          )}

          {totalPages > 1 && (
            <div className="pagination">
              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  onClick={() => handlePageChange(i)}
                  className={filters.page === i ? 'active' : ''}
                >
                  {i + 1}
                </button>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};