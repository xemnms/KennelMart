/* eslint-disable react-hooks/set-state-in-effect */
import { useCallback, useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import { listingService } from '../../services/listingService';
import { notificationService } from '../../services/notificationService';
import { userService } from '../../services/userService';
import { useCartStore } from '../../store/cartStore';
import { useAuthStore } from '../../store/authStore';
import { useMessageStore } from '../../store/messageStore';
import { useNotificationStore } from '../../store/notificationStore';
import { KennelMartIcon, KennelMartLogo } from '../../components/KennelMartBrand';
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
  const [searchParams, setSearchParams] = useSearchParams();

  // Product search state
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState<ListingFilters>({ page: 0, size: 12, sort: 'createdAt,desc' });
  const [totalPages, setTotalPages] = useState(0);
  
  // User search state
  const [users, setUsers] = useState<User[]>([]);
  const [userTotalPages, setUserTotalPages] = useState(0);
  const [userPage, setUserPage] = useState(0);
  
  // Common state
  const [quickWidget, setQuickWidget] = useState<QuickWidget>(null);
  const [widgetNotifications, setWidgetNotifications] = useState<Notification[]>([]);
  const [widgetConversations, setWidgetConversations] = useState<Conversation[]>([]);
  const [widgetLoading, setWidgetLoading] = useState(false);
  const [sellerProfiles, setSellerProfiles] = useState<Record<string, { name: string; profileImage?: string }>>({});
  const [likedListings, setLikedListings] = useState<Set<number>>(new Set());
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
  const suggestionListings = listings.slice(0, 4);
  const suggestionUsers = users.slice(0, 4);
  const searchInput = searchParams.get('q') ?? '';
  const searchMode = searchParams.get('mode') === 'users' ? 'users' : 'products';

  const updateSearchParams = (nextMode: SearchMode, nextQuery: string) => {
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

  useEffect(() => {
    const sellerIds = Array.from(new Set(listings.map((listing) => listing.sellerId)));
    const missingSellerIds = sellerIds.filter((sellerId) => !sellerProfiles[sellerId]);

    if (missingSellerIds.length === 0) {
      return;
    }

    let isActive = true;

    void Promise.all(
      missingSellerIds.map(async (sellerId) => {
        try {
          const profile = await userService.getUserPublicProfile(sellerId);
          return { sellerId, profile };
        } catch (error) {
          console.error(error);
          return null;
        }
      }),
    ).then((results) => {
      if (!isActive) return;

      setSellerProfiles((current) => {
        const next = { ...current };
        results.forEach((result) => {
          if (result) {
            next[result.sellerId] = {
              name: result.profile.name,
              profileImage: result.profile.profileImage,
            };
          }
        });
        return next;
      });
    });

    return () => {
      isActive = false;
    };
  }, [listings, sellerProfiles]);

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
      setFilters(prev => ({ ...prev, keyword: searchInput || undefined, page: 0 }));
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

  const handleCategoryChange = (category: string) => {
    updateSearchParams('products', searchInput);
    setFilters(prev => ({
      ...prev,
      category: category === 'All' ? undefined : category,
      page: 0,
      sort: 'createdAt,desc'
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

  const toggleLike = (id: number) => {
    setLikedListings((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const renderUserCard = (user: User) => (
    <article key={user.id} className="feed-post feed-user-card user-card-link">
      <Link to={`/user/${user.id}`} className="user-card-main">
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
        </div>
        <div className="feed-caption compact">
          <p>{user.email}</p>
          {user.averageRating && <span>⭐ {user.averageRating.toFixed(1)} rating</span>}
        </div>
      </Link>
      <div className="user-card-actions">
        <Link to={`/user/${user.id}`} className="user-card-action ghost">View profile</Link>
        <Link to={`/messages/${user.id}`} className="user-card-action primary">Message</Link>
      </div>
    </article>
  );

  const renderListingCard = (listing: ProductListing) => {
    const sellerProfile = sellerProfiles[listing.sellerId];
    const isLiked = likedListings.has(listing.id);

    return (
      <article key={listing.id} className="ig-post">
        {/* ── Post header ── */}
        <div className="ig-post-head">
          <Link to={`/user/${listing.sellerId}`} className="ig-post-user">
            <div className="ig-story-ring">
              <div className="ig-post-avatar">
                {sellerProfile?.profileImage ? (
                  <img src={getImageUrl(sellerProfile.profileImage)} alt={sellerProfile.name || listing.sellerName} />
                ) : (
                  <span>{listing.sellerName.charAt(0)}</span>
                )}
              </div>
            </div>
            <div className="ig-post-user-info">
              <strong>{listing.sellerName}</strong>
              <span>{listing.category.split('_').map((w) => w.charAt(0) + w.slice(1).toLowerCase()).join(' ')}</span>
            </div>
          </Link>
          <div className="ig-post-head-end">
            <Link to={`/listings/${listing.id}`} className="ig-follow-btn">View</Link>
            <button type="button" className="ig-more-btn" aria-label="More options">⋯</button>
          </div>
        </div>

        {/* ── Full-bleed image ── */}
        <Link to={`/listings/${listing.id}`} className="ig-post-media">
          {listing.imageUrls[0] ? (
            <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
          ) : (
            <div className="ig-post-media-placeholder">
              <span>{listing.title.charAt(0)}</span>
            </div>
          )}
        </Link>

        {/* ── Action bar ── */}
        <div className="ig-post-actions">
          <div className="ig-post-actions-left">
            <button
              type="button"
              className={`ig-action-btn${isLiked ? ' liked' : ''}`}
              aria-label="Like"
              onClick={() => toggleLike(listing.id)}
            >
              <KennelMartIcon name="heart" className="ig-action-icon" />
            </button>
            <Link to={`/messages/${listing.sellerId}`} className="ig-action-btn" aria-label="Message seller">
              <KennelMartIcon name="comment" className="ig-action-icon" />
            </Link>
            <Link to={`/listings/${listing.id}`} className="ig-action-btn" aria-label="View listing">
              <KennelMartIcon name="send" className="ig-action-icon" />
            </Link>
          </div>
          <button type="button" className="ig-action-btn ig-save-btn" aria-label="Save">
            <KennelMartIcon name="bookmark" className="ig-action-icon" />
          </button>
        </div>

        {/* ── Caption ── */}
        <div className="ig-post-caption">
          <div className="ig-post-price">₱{listing.price}</div>
          <p><strong>{listing.sellerName}</strong> {listing.title}</p>
          {listing.description && <p className="ig-caption-desc">{listing.description}</p>}
          <span className="ig-caption-meta">{listing.stockQuantity} in stock · {listing.category.split('_').map((w) => w.charAt(0) + w.slice(1).toLowerCase()).join(' ')}</span>
        </div>
      </article>
    );
  };

  return (
    <div className="marketplace instagram-shell">
      <aside className="ig-sidebar">
        <Link to="/" className="ig-brand">
          <KennelMartLogo />
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
        {searchMode === 'products' && (
          <div className="ig-category-strip">
            {categories.map((cat) => (
              <button
                key={cat}
                type="button"
                className={`ig-cat-pill${selectedCategory === cat ? ' active' : ''}`}
                onClick={() => handleCategoryChange(cat)}
              >
                {cat === 'All' ? 'All' : cat.split('_').map((w) => w.charAt(0) + w.slice(1).toLowerCase()).join(' ')}
              </button>
            ))}
          </div>
        )}

        <section className="feed-shell">
          {loading ? (
            <div className="loading insta-loader">Loading...</div>
          ) : searchMode === 'products' ? (
            listings.length === 0 ? (
              <div className="empty-state insta-empty">
                <p>No products found. Try another search or <Link to="/seller/listings/new">create one</Link>.</p>
              </div>
            ) : (
              <div className="feed-list product-grid-feed">
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

      {quickWidget && createPortal(
        <div className={`widget-overlay ${quickWidget === 'notifications' ? 'top-right' : 'bottom-right'}`} onClick={closeWidget}>
          <aside className={`widget-panel ${quickWidget}`} onClick={(e) => e.stopPropagation()}>
            <div className="widget-panel-header">
              <div>
                <p className="eyebrow">Quick panel</p>
                <h3 className="panel-title">
                  <KennelMartIcon
                    name={quickWidget === 'cart' ? 'cart' : quickWidget === 'notifications' ? 'notifications' : quickWidget === 'messages' ? 'messages' : 'create'}
                    className="panel-icon"
                  />
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
                    <KennelMartIcon name="cart" className="widget-empty-icon" />
                    <p>Your cart is empty.</p>
                    <Link to="/" onClick={closeWidget}>Continue shopping</Link>
                  </div>
                ) : (
                  <>
                    <div className="mini-list">
                      {cartItems.slice(0, 5).map((item) => (
                        <div key={item.id} className="mini-item">
                          <span className="mini-item-icon"><KennelMartIcon name="cart" className="panel-icon" /></span>
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
                  <div className="widget-empty">
                    <KennelMartIcon name="notifications" className="widget-empty-icon" />
                    <p>No notifications yet.</p>
                  </div>
                ) : (
                  <div className="mini-list">
                    {widgetNotifications.map((notification) => (
                      <div key={notification.id} className={`mini-item ${notification.read ? '' : 'unread'}`}>
                        <span className="mini-item-icon">
                          <KennelMartIcon
                            name={notification.type === 'ORDER' ? 'cart' : notification.type === 'MESSAGE' ? 'messages' : 'notifications'}
                            className="panel-icon"
                          />
                        </span>
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
                  <div className="widget-empty">
                    <KennelMartIcon name="messages" className="widget-empty-icon" />
                    <p>No conversations yet.</p>
                  </div>
                ) : (
                  <div className="mini-list">
                    {widgetConversations.map((conversation) => (
                      <Link key={conversation.userId} to={`/messages/${conversation.userId}`} className="mini-item link-item" onClick={closeWidget}>
                        <span className="mini-item-icon"><KennelMartIcon name="messages" className="panel-icon" /></span>
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
                  <KennelMartIcon name="create" className="widget-hero-icon" />
                  <p className="eyebrow">Sell faster</p>
                  <h4>Create a listing in a focused, distraction-free panel.</h4>
                  <p>
                    The full listing form still uses the same backend flow. This panel simply gives you a cleaner starting point.
                  </p>
                </div>
                <div className="mini-list checklist">
                  <div className="mini-item">
                    <span className="mini-item-icon"><KennelMartIcon name="create" className="panel-icon" /></span>
                    <strong>1</strong>
                    <span>Add photos, price, and stock.</span>
                  </div>
                  <div className="mini-item">
                    <span className="mini-item-icon"><KennelMartIcon name="search" className="panel-icon" /></span>
                    <strong>2</strong>
                    <span>Choose a category from the modern selector.</span>
                  </div>
                  <div className="mini-item">
                    <span className="mini-item-icon"><KennelMartIcon name="orders" className="panel-icon" /></span>
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
        </div>,
        document.body,
      )}
    </div>
  );
};