/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState, useCallback } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { listingService } from '../../services/listingService';
import { userService } from '../../services/userService';
import { useAuthStore } from '../../store/authStore';
import { useMessageStore } from '../../store/messageStore';
import { useNotificationStore } from '../../store/notificationStore';
import type { ProductListing, ListingFilters } from '../../types/marketplace';
import type { User } from '../../types/auth';
import { getImageUrl } from '../../utils/imageUtils';
import './Marketplace.css';

type SearchMode = 'products' | 'users';

export const MarketplacePage = () => {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const { unreadCount: msgUnreadCount, fetchUnreadCount: fetchMsgUnreadCount } = useMessageStore();
  const { unreadCount: notifUnreadCount, fetchUnreadCount: fetchNotifUnreadCount } = useNotificationStore();
  const navigate = useNavigate();

  // Product search state
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ListingFilters>({ page: 0, size: 12 });
  const [totalPages, setTotalPages] = useState(0);
  
  // User search state
  const [users, setUsers] = useState<User[]>([]);
  const [userTotalPages, setUserTotalPages] = useState(0);
  const [userPage, setUserPage] = useState(0);
  
  // Common state
  const [searchInput, setSearchInput] = useState('');
  const [searchMode, setSearchMode] = useState<SearchMode>('products');

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

  // Fetch products
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

  // Fetch users
  const fetchUsers = useCallback(async () => {
    if (!searchInput.trim()) {
      setUsers([]);
      setUserTotalPages(0);
      setLoading(false);
      return;
    }
    setLoading(true);
    try {
      const data = await userService.searchUsers(searchInput, userPage, 12);
      setUsers(data.content);
      setUserTotalPages(data.totalPages);
    } catch (error) {
      console.error(error);
      setUsers([]);
    } finally {
      setLoading(false);
    }
  }, [searchInput, userPage]);

  // Trigger fetch when mode or filters change
  useEffect(() => {
    if (searchMode === 'products') {
      fetchListings();
    } else {
      fetchUsers();
    }
  }, [searchMode, fetchListings, fetchUsers]);

  // Reset page when mode or search term changes
  useEffect(() => {
    if (searchMode === 'products') {
      setFilters(prev => ({ ...prev, page: 0 }));
    } else {
      setUserPage(0);
    }
  }, [searchMode, searchInput]);

  // Message & notification counts
  useEffect(() => {
    if (user) {
      fetchMsgUnreadCount();
      fetchNotifUnreadCount();
    }
  }, [user, fetchMsgUnreadCount, fetchNotifUnreadCount]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchMode === 'products') {
      setFilters(prev => ({ ...prev, keyword: searchInput, page: 0 }));
    } else {
      setUserPage(0);
      // fetch will be triggered by useEffect due to userPage change
    }
  };

  const handleCategoryClick = (category: string) => {
    setSearchMode('products');
    setFilters(prev => ({
      ...prev,
      category: category === 'All' ? undefined : category,
      page: 0
    }));
  };

  const handleProductPageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const handleUserPageChange = (newPage: number) => {
    setUserPage(newPage);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const renderUserCard = (user: User) => (
    <div key={user.id} className="product-card user-card">
      <Link to={`/user/${user.id}`}>
        <div className="user-avatar">
          {user.profileImage ? (
            <img src={getImageUrl(user.profileImage)} alt={user.name} />
          ) : (
            <div className="avatar-placeholder">👤</div>
          )}
        </div>
        <div className="user-info">
          <h3>{user.name}</h3>
          <p className="user-email">{user.email}</p>
          <p className="user-role">{user.role}</p>
          {user.averageRating && (
            <p className="user-rating">⭐ {user.averageRating.toFixed(1)}</p>
          )}
        </div>
      </Link>
    </div>
  );

  return (
    <div className="marketplace">
      {/* Header */}
      <header className="marketplace-header">
        <div className="logo">
          <Link to="/">KennelMart</Link>
        </div>
        <div className="search-section">
          <div className="search-mode-toggle">
            <button
              className={searchMode === 'products' ? 'active' : ''}
              onClick={() => setSearchMode('products')}
            >
              Products
            </button>
            <button
              className={searchMode === 'users' ? 'active' : ''}
              onClick={() => setSearchMode('users')}
            >
              People
            </button>
          </div>
          <form onSubmit={handleSearch} className="search-bar">
            <input
              type="text"
              placeholder={searchMode === 'products' ? "Search for products..." : "Search for users by name or email..."}
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
            />
            <button type="submit">🔍</button>
          </form>
        </div>
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

      {/* Categories - only show in product mode */}
      {searchMode === 'products' && (
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
      )}

      {/* Results */}
      {loading ? (
        <div className="loading">Loading...</div>
      ) : (
        <>
          {searchMode === 'products' ? (
            <>
              {listings.length === 0 ? (
                <div className="empty-state">
                  <p>No products found. Try a different search or <Link to="/seller/listings/new">sell something</Link>!</p>
                </div>
              ) : (
                <div className="product-grid">
                  {listings.map((listing) => (
                    <div key={listing.id} className="product-card">
                      <Link to={`/listings/${listing.id}`}>
                        <div className="product-image">
                          <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
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
                      onClick={() => handleProductPageChange(i)}
                      className={filters.page === i ? 'active' : ''}
                    >
                      {i + 1}
                    </button>
                  ))}
                </div>
              )}
            </>
          ) : (
            <>
              {users.length === 0 ? (
                <div className="empty-state">
                  <p>No users found. Try a different name or email.</p>
                </div>
              ) : (
                <div className="users-grid">
                  {users.map(renderUserCard)}
                </div>
              )}
              {userTotalPages > 1 && (
                <div className="pagination">
                  {Array.from({ length: userTotalPages }, (_, i) => (
                    <button
                      key={i}
                      onClick={() => handleUserPageChange(i)}
                      className={userPage === i ? 'active' : ''}
                    >
                      {i + 1}
                    </button>
                  ))}
                </div>
              )}
            </>
          )}
        </>
      )}
    </div>
  );
};