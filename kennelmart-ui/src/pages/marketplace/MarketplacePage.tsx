/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import { listingService } from '../../services/listingService';
import { notificationService } from '../../services/notificationService';
import { userService } from '../../services/userService';
import { useCartStore } from '../../store/cartStore';
import { useAuthStore } from '../../store/authStore';
import { useMessageStore } from '../../store/messageStore';
import { useNotificationStore } from '../../store/notificationStore';
import type { Conversation } from '../../types/message';
import type { Notification } from '../../types/notification';
import type { ProductListing, ListingFilters } from '../../types/marketplace';
import type { User } from '../../types/auth';
import { getImageUrl } from '../../utils/imageUtils';
import './Marketplace.css';

type SearchMode = 'products' | 'users';
type QuickWidget = 'cart' | 'notifications' | 'messages' | 'sell' | null;

export const MarketplacePage = () => {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const { unreadCount: msgUnreadCount, fetchUnreadCount: fetchMsgUnreadCount } = useMessageStore();
  const { unreadCount: notifUnreadCount, fetchUnreadCount: fetchNotifUnreadCount } = useNotificationStore();
  const { items: cartItems, totalPrice: cartTotal, isLoading: cartLoading, fetchCart } = useCartStore();
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
  const [quickWidget, setQuickWidget] = useState<QuickWidget>(null);
  const [widgetNotifications, setWidgetNotifications] = useState<Notification[]>([]);
  const [widgetConversations, setWidgetConversations] = useState<Conversation[]>([]);
  const [widgetLoading, setWidgetLoading] = useState(false);

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

  const selectedCategory = filters.category ?? 'All';
  const storyListings = listings.slice(0, 5);
  const suggestionListings = listings.slice(0, 4);
  const suggestionUsers = users.slice(0, 4);

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

  const handleCategoryChange = (category: string) => {
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

  const openWidget = (widget: Exclude<QuickWidget, null>) => {
    setQuickWidget(widget);
  };

  useEffect(() => {
    if (!quickWidget) {
      return;
    }

    if (quickWidget === 'cart') {
      void fetchCart();
      return;
    }

    if (quickWidget === 'sell') {
      return;
    }

    let isActive = true;
    setWidgetLoading(true);

    const loadWidgetData = async () => {
      try {
        if (quickWidget === 'notifications') {
          const data = await notificationService.getNotifications(0, 6);
          if (isActive) {
            setWidgetNotifications(data.content);
          }
        } else if (quickWidget === 'messages') {
          const conversations = await messageService.getConversations();
          if (isActive) {
            setWidgetConversations(conversations.slice(0, 6));
          }
        }
      } catch (error) {
        console.error(error);
      } finally {
        if (isActive) {
          setWidgetLoading(false);
        }
      }
    };

    void loadWidgetData();

    return () => {
      isActive = false;
    };
  }, [quickWidget, fetchCart]);

  const closeWidget = () => {
    setQuickWidget(null);
  };

  const renderUserCard = (user: User) => (
    <article key={user.id} className="feed-post feed-user-card">
      <div className="feed-post-header">
        <div className="post-user">
          <div className="post-avatar">
            {user.profileImage ? (
              <img src={getImageUrl(user.profileImage)} alt={user.name} />
            ) : (
              <span>{user.name.charAt(0)}</span>
            )}
          </div>
          <div>
            <strong>{user.name}</strong>
            <span>{user.role}</span>
          </div>
        </div>
        <Link to={`/user/${user.id}`} className="ghost-action">View</Link>
      </div>
      <div className="feed-caption compact">
        <p>{user.email}</p>
        {user.averageRating && <span>⭐ {user.averageRating.toFixed(1)} rating</span>}
      </div>
    </article>
  );

  const renderListingCard = (listing: ProductListing) => (
    <article key={listing.id} className="feed-post">
      <div className="feed-post-header">
        <div className="post-user">
          <div className="post-avatar">
            {listing.imageUrls[0] ? (
              <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
            ) : (
              <span>•</span>
            )}
          </div>
          <div>
            <strong>{listing.sellerName}</strong>
            <span>{listing.category}</span>
          </div>
        </div>
        <Link to={`/listings/${listing.id}`} className="ghost-action">View</Link>
      </div>
      <Link to={`/listings/${listing.id}`} className="feed-media">
        <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
      </Link>
      <div className="feed-actions-row">
        <div className="feed-icon-group">
          <button type="button" aria-label="Like">♡</button>
          <button type="button" aria-label="Comment">💬</button>
          <button type="button" aria-label="Share">↗</button>
        </div>
        <button type="button" className="feed-save-btn" aria-label="Save">🔖</button>
      </div>
      <div className="feed-caption">
        <Link to={`/listings/${listing.id}`} className="feed-title">{listing.title}</Link>
        <p>{listing.description || 'No description provided.'}</p>
        <div className="feed-meta">
          <span className="feed-price">₱{listing.price}</span>
          <span className="feed-pill">{listing.stockQuantity} in stock</span>
        </div>
      </div>
    </article>
  );

  return (
    <div className="marketplace instagram-shell">
      <aside className="ig-sidebar">
        <Link to="/" className="ig-brand">
          <span className="ig-brand-mark">K</span>
          <span>KennelMart</span>
        </Link>

        <nav className="ig-nav">
          <button type="button" className={searchMode === 'products' ? 'ig-nav-item active' : 'ig-nav-item'} onClick={() => setSearchMode('products')}>
            <span>⌂</span>
            <span>Home</span>
          </button>
          <button type="button" className={searchMode === 'users' ? 'ig-nav-item active' : 'ig-nav-item'} onClick={() => setSearchMode('users')}>
            <span>⌕</span>
            <span>Search</span>
          </button>
          <button type="button" className="ig-nav-item" onClick={() => openWidget('messages')}>
            <span>✉</span>
            <span>Messages</span>
            {msgUnreadCount > 0 && <em>{msgUnreadCount}</em>}
          </button>
          <button type="button" className="ig-nav-item" onClick={() => openWidget('notifications')}>
            <span>♡</span>
            <span>Notifications</span>
            {notifUnreadCount > 0 && <em>{notifUnreadCount}</em>}
          </button>
          <button type="button" className="ig-nav-item" onClick={() => openWidget('sell')}>
            <span>＋</span>
            <span>Create</span>
          </button>
          <Link to="/cart" className="ig-nav-item">
            <span>◫</span>
            <span>Cart</span>
          </Link>
          <Link to="/profile" className="ig-nav-item">
            <span>◉</span>
            <span>Profile</span>
          </Link>
        </nav>

        <div className="ig-sidebar-footer">
          {user ? (
            <>
              <div className="ig-mini-profile">
                <div className="ig-mini-avatar">
                  {user.profileImage ? <img src={getImageUrl(user.profileImage)} alt={user.name} /> : <span>{user.name.charAt(0)}</span>}
                </div>
                <div>
                  <strong>{user.name}</strong>
                  <span>{user.role}</span>
                </div>
              </div>
              <button type="button" className="ig-logout" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <div className="ig-auth-stack">
              <Link to="/login" className="ig-auth-btn">Log in</Link>
              <Link to="/register" className="ig-auth-btn ghost">Sign up</Link>
            </div>
          )}
        </div>
      </aside>

      <main className="ig-feed-column">
        <section className="ig-topbar">
          <div className="ig-search-shell">
            <div className="search-mode-toggle compact">
              <button className={searchMode === 'products' ? 'active' : ''} onClick={() => setSearchMode('products')}>Products</button>
              <button className={searchMode === 'users' ? 'active' : ''} onClick={() => setSearchMode('users')}>People</button>
            </div>
            <form onSubmit={handleSearch} className="search-bar instagram-search">
              <input
                type="text"
                placeholder={searchMode === 'products' ? 'Search products' : 'Search people'}
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
              />
              <button type="submit">⌕</button>
            </form>
          </div>

          {searchMode === 'products' && (
            <div className="category-filter compact">
              <label htmlFor="category-select">Category</label>
              <select id="category-select" value={selectedCategory} onChange={(e) => handleCategoryChange(e.target.value)}>
                {categories.map((cat) => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>
          )}
        </section>

        <section className="story-rail">
          {storyListings.length === 0 ? (
            <div className="story-empty">Featured listings will appear here.</div>
          ) : (
            storyListings.map((listing) => (
              <Link key={listing.id} to={`/listings/${listing.id}`} className="story-pill">
                <span className="story-ring">
                  {listing.imageUrls[0] ? <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} /> : <span>•</span>}
                </span>
                <strong>{listing.sellerName.split(' ')[0]}</strong>
              </Link>
            ))
          )}
        </section>

        <section className="feed-shell">
          {loading ? (
            <div className="loading insta-loader">Loading...</div>
          ) : searchMode === 'products' ? (
            listings.length === 0 ? (
              <div className="empty-state insta-empty">
                <p>No products found. Try another search or <Link to="/seller/listings/new">create one</Link>.</p>
              </div>
            ) : (
              <div className="feed-list">
                {listings.map(renderListingCard)}
              </div>
            )
          ) : users.length === 0 ? (
            <div className="empty-state insta-empty">
              <p>No people found. Try a different name or email.</p>
            </div>
          ) : (
            <div className="feed-list people-feed">
              {users.map(renderUserCard)}
            </div>
          )}

          <div className="pagination insta-pagination">
            {searchMode === 'products' && totalPages > 1 && Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                onClick={() => handleProductPageChange(i)}
                className={filters.page === i ? 'active' : ''}
              >
                {i + 1}
              </button>
            ))}
            {searchMode === 'users' && userTotalPages > 1 && Array.from({ length: userTotalPages }, (_, i) => (
              <button
                key={i}
                onClick={() => handleUserPageChange(i)}
                className={userPage === i ? 'active' : ''}
              >
                {i + 1}
              </button>
            ))}
          </div>
        </section>
      </main>

      <aside className="ig-right-rail">
        {user ? (
          <div className="right-card profile-summary">
            <div className="profile-summary-head">
              <div className="ig-mini-avatar large">
                {user.profileImage ? <img src={getImageUrl(user.profileImage)} alt={user.name} /> : <span>{user.name.charAt(0)}</span>}
              </div>
              <div>
                <strong>{user.name}</strong>
                <span>{user.role}</span>
              </div>
            </div>
            <div className="summary-stats">
              <div><strong>{msgUnreadCount}</strong><span>Messages</span></div>
              <div><strong>{notifUnreadCount}</strong><span>Alerts</span></div>
              <div><strong>{categories.length - 1}</strong><span>Categories</span></div>
            </div>
          </div>
        ) : (
          <div className="right-card profile-summary">
            <strong>Welcome back</strong>
            <p>Log in to see cart, messages, and seller shortcuts.</p>
            <div className="auth-stack right-auth">
              <Link to="/login" className="ig-auth-btn">Log in</Link>
              <Link to="/register" className="ig-auth-btn ghost">Sign up</Link>
            </div>
          </div>
        )}

        <div className="right-card suggestions-card">
          <div className="card-head">
            <strong>{searchMode === 'users' ? 'People' : 'Suggested for you'}</strong>
            <button type="button" onClick={() => setSearchMode('products')}>See all</button>
          </div>
          <div className="suggestion-list">
            {searchMode === 'users' && suggestionUsers.length > 0
              ? suggestionUsers.map((person) => (
                  <Link key={person.id} to={`/user/${person.id}`} className="suggestion-item">
                    <div className="suggestion-avatar">
                      {person.profileImage ? <img src={getImageUrl(person.profileImage)} alt={person.name} /> : <span>{person.name.charAt(0)}</span>}
                    </div>
                    <div>
                      <strong>{person.name}</strong>
                      <span>{person.email}</span>
                    </div>
                  </Link>
                ))
              : suggestionListings.map((listing) => (
                  <Link key={listing.id} to={`/listings/${listing.id}`} className="suggestion-item">
                    <div className="suggestion-avatar">
                      {listing.imageUrls[0] ? <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} /> : <span>•</span>}
                    </div>
                    <div>
                      <strong>{listing.title}</strong>
                      <span>₱{listing.price} · {listing.sellerName}</span>
                    </div>
                  </Link>
                ))}
          </div>
        </div>

        <div className="right-card quick-actions-card">
          <button type="button" onClick={() => openWidget('cart')}>Open cart</button>
          <button type="button" onClick={() => openWidget('messages')}>Open messages</button>
          <button type="button" onClick={() => openWidget('notifications')}>Open notifications</button>
          <button type="button" onClick={() => openWidget('sell')}>Create listing</button>
        </div>
      </aside>

      {quickWidget && (
        <div className="widget-overlay" onClick={closeWidget}>
          <aside className={`widget-panel ${quickWidget}`} onClick={(e) => e.stopPropagation()}>
            <div className="widget-panel-header">
              <div>
                <p className="eyebrow">Quick panel</p>
                <h3>
                  {quickWidget === 'cart'
                    ? 'Cart'
                    : quickWidget === 'notifications'
                      ? 'Notifications'
                      : quickWidget === 'messages'
                        ? 'Messages'
                        : 'Sell'}
                </h3>
              </div>
              <button type="button" className="close-widget" onClick={closeWidget}>Close</button>
            </div>

            {quickWidget === 'cart' && (
              <div className="widget-panel-body">
                {cartLoading ? (
                  <div className="widget-empty">Loading cart...</div>
                ) : cartItems.length === 0 ? (
                  <div className="widget-empty">
                    <p>Your cart is empty.</p>
                    <Link to="/" onClick={closeWidget}>Continue shopping</Link>
                  </div>
                ) : (
                  <>
                    <div className="mini-list">
                      {cartItems.slice(0, 5).map((item) => (
                        <div key={item.id} className="mini-item">
                          <div>
                            <strong>{item.title}</strong>
                            <span>Qty {item.quantity}</span>
                          </div>
                          <strong>₱{item.subtotal}</strong>
                        </div>
                      ))}
                    </div>
                    <div className="widget-footer">
                      <div>
                        <span>Total</span>
                        <strong>₱{cartTotal}</strong>
                      </div>
                      <div className="widget-actions">
                        <Link to="/cart" className="secondary-action" onClick={closeWidget}>Open cart</Link>
                        <Link to="/checkout" className="primary-action" onClick={closeWidget}>Checkout</Link>
                      </div>
                    </div>
                  </>
                )}
              </div>
            )}

            {quickWidget === 'notifications' && (
              <div className="widget-panel-body">
                {widgetLoading ? (
                  <div className="widget-empty">Loading notifications...</div>
                ) : widgetNotifications.length === 0 ? (
                  <div className="widget-empty">No notifications yet.</div>
                ) : (
                  <div className="mini-list">
                    {widgetNotifications.map((notification) => (
                      <div key={notification.id} className={`mini-item ${notification.read ? '' : 'unread'}`}>
                        <div>
                          <strong>{notification.title}</strong>
                          <span>{notification.message}</span>
                        </div>
                        <span>{new Date(notification.createdAt).toLocaleDateString()}</span>
                      </div>
                    ))}
                  </div>
                )}
                <div className="widget-footer">
                  <Link to="/notifications" className="primary-action" onClick={closeWidget}>Open notifications</Link>
                </div>
              </div>
            )}

            {quickWidget === 'messages' && (
              <div className="widget-panel-body">
                {widgetLoading ? (
                  <div className="widget-empty">Loading messages...</div>
                ) : widgetConversations.length === 0 ? (
                  <div className="widget-empty">No conversations yet.</div>
                ) : (
                  <div className="mini-list">
                    {widgetConversations.map((conversation) => (
                      <Link key={conversation.userId} to={`/messages/${conversation.userId}`} className="mini-item link-item" onClick={closeWidget}>
                        <div>
                          <strong>{conversation.name}</strong>
                          <span>{conversation.lastMessage}</span>
                        </div>
                        {conversation.unreadCount > 0 && <span className="badge">{conversation.unreadCount}</span>}
                      </Link>
                    ))}
                  </div>
                )}
                <div className="widget-footer">
                  <Link to="/messages/inbox" className="primary-action" onClick={closeWidget}>Open inbox</Link>
                </div>
              </div>
            )}

            {quickWidget === 'sell' && (
              <div className="widget-panel-body sell-widget">
                <div className="sell-hero">
                  <p className="eyebrow">Sell faster</p>
                  <h4>Create a listing in a focused, distraction-free panel.</h4>
                  <p>
                    The full listing form still uses the same backend flow. This panel simply gives you a cleaner starting point.
                  </p>
                </div>
                <div className="mini-list checklist">
                  <div className="mini-item">
                    <strong>1</strong>
                    <span>Add photos, price, and stock.</span>
                  </div>
                  <div className="mini-item">
                    <strong>2</strong>
                    <span>Choose a category from the modern selector.</span>
                  </div>
                  <div className="mini-item">
                    <strong>3</strong>
                    <span>Publish and manage it from your listings page.</span>
                  </div>
                </div>
                <div className="widget-footer">
                  <Link to="/seller/listings/new" className="primary-action" onClick={closeWidget}>Open listing creator</Link>
                  <Link to="/my-listings" className="secondary-action" onClick={closeWidget}>Manage listings</Link>
                </div>
              </div>
            )}
          </aside>
        </div>
      )}
    </div>
  );
};