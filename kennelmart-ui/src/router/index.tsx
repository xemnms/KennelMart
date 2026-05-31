import { createBrowserRouter } from 'react-router-dom';
import { LoginPage } from '../pages/auth/LoginPage';
import { RegisterPage } from '../pages/auth/RegisterPage';
import { SplashPage } from '../pages/auth/SplashPage';
import { MarketplacePage } from '../pages/marketplace/MarketplacePage';
import { ListingDetailPage } from '../pages/marketplace/ListingDetailPage';
import { PrivateRoute } from '../components/PrivateRoute';
import { CreateListingPage } from '../pages/seller/CreateListingPage';
import { MyListingsPage } from '../pages/seller/MyListingsPage';
import { OrderDetailsPage } from '../pages/orders/OrderDetailsPage';
import { MyOrdersPage } from '../pages/orders/MyOrdersPage';
import { SellerOrdersPage } from '../pages/orders/SellerOrdersPage';
import { ProfilePage } from '../pages/profile/ProfilePage';
import { AdminDashboard } from '../pages/admin/AdminDashboard';
import { AdminRoute } from '../components/AdminRoute';
import { CartPage } from '../pages/cart/CartPage';
import { CheckoutPage } from '../pages/checkout/CheckoutPage';
import { InboxPage } from '../pages/messages/InboxPage';
import { ChatPage } from '../pages/messages/ChatPage';
import { EditListingPage } from '../pages/seller/EditListingPage';
import { NotificationsPage } from '../pages/notifications/NotificationsPage';
import { SellerReviewsPage } from '../pages/reviews/SellerReviewsPage';
import { UserProfilePage } from '../pages/user/UserProfilePage';
import { AppShell } from '../components/AppShell';

export const router = createBrowserRouter([
  {
    path: '/welcome',
    element: <SplashPage />,
  },
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/register',
    element: <RegisterPage />,
  },
  {
    element: <AppShell />,
    children: [
      {
        index: true,
        element: (
          <PrivateRoute>
            <MarketplacePage />
          </PrivateRoute>
        ),
      },
      {
        path: '/listings/:id',
        element: (
          <PrivateRoute>
            <ListingDetailPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/seller/listings/new',
        element: (
          <PrivateRoute>
            <CreateListingPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/my-listings',
        element: (
          <PrivateRoute>
            <MyListingsPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/orders/:id',
        element: (
          <PrivateRoute>
            <OrderDetailsPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/orders',
        element: (
          <PrivateRoute>
            <MyOrdersPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/seller/orders',
        element: (
          <PrivateRoute>
            <SellerOrdersPage />
          </PrivateRoute>
        ),
      },
      {
        path: '/profile',
        element: (
          <PrivateRoute>
            <ProfilePage />
          </PrivateRoute>
        ),
      },
      {
        path: '/admin',
        element: (
          <AdminRoute>
            <AdminDashboard />
          </AdminRoute>
        ),
      },
      {
        path: '/cart',
        element: <PrivateRoute><CartPage /></PrivateRoute>,
      },
      {
        path: '/checkout',
        element: <PrivateRoute><CheckoutPage /></PrivateRoute>,
      },
      {
        path: '/messages/inbox',
        element: <PrivateRoute><InboxPage /></PrivateRoute>,
      },
      {
        path: '/messages/:userId',
        element: <PrivateRoute><ChatPage /></PrivateRoute>,
      },
      {
        path: '/seller/listings/edit/:id',
        element: <PrivateRoute><EditListingPage /></PrivateRoute>,
      },
      {
        path: '/notifications',
        element: <PrivateRoute><NotificationsPage /></PrivateRoute>,
      },
      {
        path: '/reviews/seller/:sellerId',
        element: <SellerReviewsPage />,
      },
      {
        path: '/user/:userId',
        element: <PrivateRoute><UserProfilePage /></PrivateRoute>,
      },
    ],
  }
]);