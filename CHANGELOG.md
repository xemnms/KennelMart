# KennelMart Changelog

## [Frontend UI — Complete] - 2026-05-31

### Added

#### Design System
- Global CSS variables: color palette (`--blue-vivid`, `--gold`, `--gold-lt`), spacing, border-radius, easing, dark background tokens
- Mobile-first layout: `AppShell` with sticky top bar, bottom tab navigation, and page card content area
- `KennelMartBrand` component: SVG logo + icon set (home, cart, create, orders, profile, search, messages, notifications, etc.)
- Story-ring avatar border style (blue-to-gold gradient) used on profile and marketplace cards
- Consistent status badges across all pages using the blue/gold palette

#### Pages
- **Splash** (`/welcome`) — animated brand intro with sign-in / sign-up CTAs
- **Login** (`/login`) — JWT login form with error handling
- **Register** (`/register`) — registration form with NU email and student ID fields
- **Marketplace** (`/`) — feed-style listing grid, category filter chips, product/people search toggle, quick-add to cart, DM shortcut, user search results, notification bell, unread badge
- **Listing Detail** (`/listings/:id`) — full listing view, seller info, add-to-cart, message seller, report listing
- **Cart / Pawket** (`/cart`) — cart items, quantity controls, remove, totals, checkout CTA
- **Checkout** (`/checkout`) — payment method selector (COD, GCash, Campus Meetup), order confirmation
- **My Orders** (`/orders`) — buyer order history with per-order status badges and pagination
- **Order Details** (`/orders/:id`) — full order summary, status timeline, leave review CTA
- **Seller Orders** (`/seller/orders`) — incoming orders dashboard with status update actions
- **Create Listing** (`/seller/listings/new`) — multi-image upload form, category selector, price/stock fields
- **Edit Listing** (`/seller/listings/edit/:id`) — pre-filled edit form for existing listings
- **My Listings** (`/my-listings`) — seller's listing grid with status badges, edit and delete
- **Inbox** (`/messages/inbox`) — conversation list with unread counts and relative timestamps
- **Chat** (`/messages/:userId`) — per-conversation message thread, send form, report user button
- **Notifications** (`/notifications`) — paginated notification feed, mark-all-read
- **Profile** (`/profile`) — own profile: avatar upload (camera overlay), name/ID edit, password change, verification badge, logout
- **User Profile** (`/user/:userId`) — public seller profile: avatar, stats, active listings grid, message and review links
- **Seller Reviews** (`/reviews/seller/:sellerId`) — paginated review list with star ratings
- **Admin Dashboard** (`/admin`) — user list with suspend/activate/verify/reject, listing moderation (approve/reject/delete), report queue with actions (review, resolve, dismiss, suspend user, delete listing)

#### Routing & Auth Guards
- `PrivateRoute` — redirects unauthenticated users to `/welcome`
- `AdminRoute` — restricts `/admin` to users with `ADMIN` role
- `AppShell` — shared layout wrapping all authenticated routes; conditionally renders marketplace search bar on home route only

#### State Management (Zustand)
- `authStore` — JWT token, current user, login/logout
- `cartStore` — cart items, add/update/remove, total price
- `messageStore` — unread message count, fetch
- `notificationStore` — unread notification count, fetch

#### API Services (Axios)
- `authService` — register, login
- `listingService` — browse, get by ID, create, update, delete, my listings, user listings
- `cartStore` / `orderService` — cart CRUD, checkout, order history, order details, status update
- `messageService` — send message, get conversation, get conversations, unread count, mark read
- `notificationService` — get notifications, unread count, mark all read
- `reviewService` — add review, get seller reviews, average rating
- `reportService` — submit report
- `userService` — update profile, change password, upload avatar, search users, public profile
- `uploadService` — upload product image
- `adminService` — user list, suspend/activate/verify/reject, listing list/approve/reject/delete, report list/resolve/dismiss/review/suspend-user/delete-listing

#### Components
- `ReportModal` — shared report dialog for listings and users
- `ReviewModal` — shared review dialog triggered from order details

#### Types
- `auth.ts` — `User`, `AuthState`, `LoginRequest`, `RegisterRequest`, `AuthResponse`
- `marketplace.ts` — `ProductListing`, `ListingFilters`, `ListingCategory`, `ProductStatus`, `ProductListingResponse`
- `order.ts` — `Order`, `OrderItem`, `OrderStatus`, `PaymentMethod`, `CheckoutRequest`, `OrderResponse`
- `message.ts` — `Message`, `Conversation`, `MessageRequest`
- `notification.ts` — `Notification`, `NotificationResponse`
- `review.ts` — `Review`, `ReviewRequest`, `ReviewResponse`
- `report.ts` — `Report`, `ReportRequest`

#### Utilities
- `imageUtils.ts` — `getImageUrl()`: resolves relative `/uploads/...` paths to full backend URL with fallback placeholder

---

## [Backend — Complete] - 2026-05-29

### Added

#### Entities (new since Phase 1)
- `ProductListing` — listing entity with title, description, price, stockQuantity, category, status, seller (ManyToOne), images (OneToMany composition)
- `ProductImage` — image entity with imageUrl, displayOrder, listing (ManyToOne)
- `Cart` — one cart per user (OneToOne), with a list of `CartItem`
- `CartItem` — listing, quantity, priceSnapshot, titleSnapshot
- `Order` — orderNumber, buyer, seller, status, totalPrice, paymentMethod, deliveredAt, items (OneToMany)
- `OrderItem` — productTitle, quantity, priceSnapshot, listing reference
- `Review` — seller, buyer, order, rating (1–5), comment; triggers seller average rating update on save
- `Message` — sender, receiver, content, read flag; indexed on conversation pair and read state
- `Notification` — user, title, message, read flag, type, referenceId
- `Report` — reporter, targetType (`LISTING`/`USER`), targetId, reason, status

#### Repositories (new since Phase 1)
- `ProductListingRepository` — by status, by category+status, keyword search, by seller, by seller+statusNot (paginated), by ID+status
- `ProductImageRepository` — by listing ordered by displayOrder
- `CartRepository` — by user (one cart per user)
- `CartItemRepository` — base CRUD
- `OrderRepository` — by buyer (paginated), by seller (paginated + list for analytics)
- `OrderItemRepository` — base CRUD
- `ReviewRepository` — by seller (paginated), existsByOrderId, average rating JPQL query
- `MessageRepository` — conversation query, list by sender or receiver, count unread, mark-as-read `@Modifying`
- `NotificationRepository` — by user ordered by date, count unread, mark-all-read `@Modifying`
- `ReportRepository` — by status (paginated)

#### Services & Implementations (new since Phase 1)
- `UserService` / `UserServiceImpl` — updateProfile, changePassword, updateProfileImage
- `ProductListingService` / `ProductListingServiceImpl` — createListing (requires VERIFIED account), updateListing, deleteListing (soft-delete status DELETED), getListingById, getMyListings, getActiveListings (keyword + category filter), getActiveListingsBySellerId
- `CartService` / `CartServiceImpl` — addItemToCart (stock validation, auto-create cart, merge duplicates), updateCartItemQuantity, removeCartItem, getCart, clearCart
- `OrderService` / `OrderServiceImpl` — checkout (groups cart items by seller into separate orders, reduces stock, clears cart, sends order-received notification), getOrderById, getMyOrdersAsBuyer, getMyOrdersAsSeller, updateOrderStatus (triggers notifications on ACCEPTED / SHIPPED / DELIVERED)
- `ReviewService` / `ReviewServiceImpl` — addReview (only on DELIVERED orders the buyer owns, one review per order), getReviewsForSeller, getAverageRatingForSeller
- `MessageService` / `MessageServiceImpl` — sendMessage (sends notification to receiver), getConversation, getConversations (inbox list with last message + unread count), getUnreadCount, markMessagesAsRead
- `NotificationService` / `NotificationServiceImpl` — sendNotification, getUserNotifications, getUnreadCount, markAllAsRead
- `ReportService` / `ReportServiceImpl` — submitReport, getReports, resolveReport, dismissReport, markAsReviewing, suspendReportedUser, deleteReportedListing (sets status DELETED)
- `FileUploadService` / `FileUploadServiceImpl` — uploadImage (to `uploads/products/`), uploadProfileImage (to `uploads/avatars/`); validates MIME type, generates UUID filename, serves via `ImageController`
- `AnalyticsService` / `AnalyticsServiceImpl` — getSellerAnalytics: totalOrders, totalItemsSold, totalRevenue (delivered only), averageOrderValue, totalListings, activeListings, uniqueBuyers

#### Controllers (new since Phase 1)
- `UserController` — `PUT /api/users/me`, `PUT /api/users/me/password`, `POST /api/users/me/profile-image`, `GET /api/users/search`, `GET /api/users/{userId}/public`
- `ProductListingController` — `POST /api/listings`, `PUT /api/listings/{id}`, `DELETE /api/listings/{id}`, `GET /api/listings/{id}`, `GET /api/listings/my-listings`, `GET /api/listings` (public browse), `GET /api/listings/users/{userId}/listings`
- `CartController` — `POST /api/cart/items`, `PUT /api/cart/items/{cartItemId}`, `DELETE /api/cart/items/{cartItemId}`, `GET /api/cart`, `DELETE /api/cart`
- `OrderController` — `POST /api/orders`, `GET /api/orders/my-orders`, `GET /api/orders/my-sales`, `GET /api/orders/{orderId}`, `PUT /api/orders/{orderId}/status`
- `ReviewController` — `POST /api/reviews`, `GET /api/reviews/sellers/{sellerId}`, `GET /api/reviews/sellers/{sellerId}/average`
- `MessageController` — `POST /api/messages`, `GET /api/messages/conversation/{userId}`, `GET /api/messages/conversations`, `GET /api/messages/unread-count`, `PUT /api/messages/read/{userId}`
- `NotificationController` — `GET /api/notifications`, `GET /api/notifications/unread-count`, `PUT /api/notifications/read-all`
- `ReportController` — `POST /api/reports`
- `AnalyticsController` — `GET /api/analytics/seller`
- `VerificationController` — `POST /api/verification/submit`, `GET /api/verification/status`
- `FileUploadController` — `POST /api/uploads/image`
- `ImageController` — `GET /uploads/{subdir}/{filename}` (static file serving)
- `AdminUserController` — `GET /api/admin/users`, `PUT /api/admin/users/{id}/suspend`, `/activate`, `/verify`, `/reject-verification`
- `AdminListingController` — `GET /api/admin/listings`, `PUT /api/admin/listings/{id}/approve`, `/reject`, `DELETE /api/admin/listings/{id}`
- `AdminVerificationController` — `GET /api/admin/verification/pending`, `PUT /api/admin/verification/{id}/approve`, `/reject` (with notification dispatch)
- `AdminReportController` — `GET /api/admin/reports`, `PUT /api/admin/reports/{id}/resolve`, `/dismiss`, `/review`, `/suspend-user/{userId}`, `/delete-listing/{listingId}`

#### New DTOs (since Phase 1)
`UpdateProfileRequest`, `ChangePasswordRequest`, `UserSearchResponse`, `UserPublicProfileResponse`, `CreateListingRequest`, `UpdateListingRequest`, `ProductListingResponse`, `CartItemRequest`, `CartItemResponse`, `CartResponse`, `CheckoutRequest`, `OrderResponse`, `OrderItemResponse`, `ReviewRequest`, `ReviewResponse`, `MessageRequest`, `MessageResponse`, `ConversationDTO`, `NotificationResponse`, `ReportRequest`, `ReportResponse`, `VerificationRequest`, `SellerAnalyticsResponse`

---

## [Phase 1: Authentication Module] - 2026-05-25

### Added

#### Dependencies
- JJWT (JSON Web Token) library v0.12.3 for JWT authentication
- JUnit 4.13.2 and Spring Boot Test for unit testing

#### Enumerations
- `UserRole` - ADMIN, USER roles (simplified: all users can buy and sell)
- `VerificationStatus` - PENDING, VERIFIED, REJECTED for identity verification
- `AccountStatus` - ACTIVE, SUSPENDED for account management
- `OrderStatus` - ORDER_RECEIVED, ACCEPTED, PREPARING, SHIPPED, DELIVERED, CANCELLED for order lifecycle
- `PaymentMethod` - COD, GCASH, CAMPUS_MEETUP for payment options
- `ProductStatus` - ACTIVE, INACTIVE, SOLD_OUT, PENDING_APPROVAL for product listings
- `ReportStatus` - PENDING, REVIEWING, RESOLVED, DISMISSED for moderation
- `ListingCategory` - ELECTRONICS, BOOKS, CLOTHING, FURNITURE, SERVICES, FOOD_BEVERAGE, SPORTS_RECREATION, STATIONERY, OTHERS

#### Entities
- `BaseEntity` - Abstract base class with UUID id, createdAt, updatedAt timestamps
- `User` - Core user entity with fields: firstName, lastName, email, password, studentOrFacultyId, profileImage, role, verificationStatus, accountStatus
  - Demonstrates OOP: Encapsulation, Inheritance, helper methods (getFullName, isVerified, isActive)
  - Database indexes on email (unique) and studentOrFacultyId

#### Repositories
- `UserRepository` - JPA repository for User persistence
  - Methods: findByEmail(), existsByEmail(), findByStudentOrFacultyId()

#### DTOs (Data Transfer Objects)
- `RegisterRequest` - User registration input with validation
- `LoginRequest` - User login credentials input with validation
- `AuthResponse` - Authentication response with JWT token and user details

#### Services
- `AuthService` (interface) - Authentication service contract
- `AuthServiceImpl` - Implementation of authentication service
  - register() - Register new user with email validation, password hashing, JWT token generation
  - login() - Authenticate user, validate account status, generate JWT token
  - Includes validation: NU Laguna email check (@students.nu-laguna.edu.ph), password matching, account status verification

#### Security Components
- `JwtProvider` - JWT token generation, validation, and claims extraction
  - Uses HS512 algorithm with BCrypt compatible secret keys
  - Configurable expiration time (default: 24 hours)
  - Methods: generateToken(), validateToken(), getUsernameFromToken(), getClaimsFromToken(), isTokenExpired()

- `JwtAuthenticationFilter` - Spring Security filter for JWT authentication
  - Extracts JWT from Authorization header (Bearer scheme)
  - Validates token and loads user details
  - Sets SecurityContext for authenticated requests

#### Configuration
- `SecurityConfig` - Spring Security configuration
  - BCryptPasswordEncoder with strength 12
  - JWT authentication filter integration
  - CORS configuration for frontend (http://localhost:5173, http://localhost:3000)
  - HTTP security policies: stateless session management, public/protected endpoint rules
  - Authorization rules: public auth endpoints, protected user/seller endpoints, admin-only endpoints

- `UserDetailsConfig` - User details service configuration
  - Loads users from database for Spring Security authentication

#### Controllers
- `AuthController` - REST API endpoints for authentication
  - POST /api/auth/register - Register new user (201 Created)
  - POST /api/auth/login - Login with credentials (200 OK)
  - GET /api/auth/me - Get current authenticated user info (200 OK)
  - Full endpoint documentation and validation rules

#### Tests
- `AuthServiceImplTest` - Unit tests for authentication service
  - Tests: registration success, duplicate email, password validation, email validation, login success, suspended account, invalid credentials
  - Uses Mockito for dependency mocking

- `JwtProviderTest` - Unit tests for JWT provider
  - Tests: token generation, username extraction, token validation, token expiration, expiration time retrieval, claims extraction

#### Configuration Files
- `application.properties` - Application configuration
  - PostgreSQL database: jdbc:postgresql://localhost:5432/kennelmart_db
  - JPA/Hibernate: PostgreSQL dialect, DDL auto update, batch processing
  - JWT: configurable secret and expiration time
  - Logging: DEBUG level for application, Spring Security, and Hibernate
  - CORS: configured for local development
  - Server: context path, port 8080, error details

### Architecture Highlights

#### Layered Architecture
```
Controller (AuthController)
    ↓
Service (AuthService/AuthServiceImpl)
    ↓
Repository (UserRepository)
    ↓
Database (PostgreSQL)
```

#### OOP Principles Demonstrated
1. **Encapsulation** - Private fields with getters/setters in User entity
2. **Inheritance** - User extends BaseEntity for common fields
3. **Abstraction** - Interface-based services, abstract BaseEntity class
4. **Single Responsibility** - Each class has one clear purpose
5. **Composition** - User has Role and VerificationStatus enums
6. **Dependency Injection** - Constructor-based injection with @RequiredArgsConstructor
7. **Polymorphism** - Strategy pattern with Spring Security UserDetailsService

#### Security Features
- **Password Security** - BCrypt hashing with strength 12
- **Token Security** - JWT with HS512 algorithm and strong secret keys
- **Email Validation** - NU Laguna email requirement (@students.nu-laguna.edu.ph)
- **Account Status** - Active/Suspended account management
- **Role-Based Access Control** - ADMIN (full access), USER (marketplace access)
- **Stateless Authentication** - JWT-based, no session state

#### Validation
- Backend validation on all inputs
- DTO-based validation with Jakarta validation annotations
- Email uniqueness check
- Password strength requirements (minimum 8 characters)
- Account status verification during login
- NU email domain validation

### Next Steps
- Phase 2: Product Listing Management
- Phase 3: Marketplace Browsing
- Phase 4: Cart and Order System
- Phase 5: Admin Moderation
