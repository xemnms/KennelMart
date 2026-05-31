import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import type { OrderResponse } from '../../types/order';
import { SellerOrdersPage } from './SellerOrdersPage';
import './MyOrders.css';

export const MyOrdersPage = () => {
  const [tab, setTab] = useState<'orders' | 'sales'>('orders');
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (tab !== 'orders') return;
    setLoading(true);
    orderService.getMyOrders(0, 20)
      .then(data => setOrders(data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [tab]);

  return (
    <div className="my-orders-container">
      {/* ── Segment tabs ── */}
      <div className="orders-tabs">
        <button
          className={`orders-tab${tab === 'orders' ? ' active' : ''}`}
          onClick={() => setTab('orders')}
        >My Orders</button>
        <button
          className={`orders-tab${tab === 'sales' ? ' active' : ''}`}
          onClick={() => setTab('sales')}
        >Sales</button>
      </div>

      {tab === 'sales' ? <SellerOrdersPage /> : null}
      {tab === 'orders' && loading ? <div className="loading">Loading your orders...</div> : null}
      {tab === 'orders' && !loading ? <>

      {orders.length === 0 ? (
        <div className="empty-orders">
          <p>You haven't placed any orders yet.</p>
          <Link to="/" className="shop-link">Start shopping</Link>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map(order => (
            <div key={order.id} className="order-card">
              <Link to={`/orders/${order.id}`}>
                <div className="order-header">
                  <span className="order-number">{order.orderNumber}</span>
                  <span className={`order-status ${order.status.toLowerCase()}`}>{order.status}</span>
                </div>
                <div className="order-info">
                  <p><strong>Total:</strong> ₱{order.totalPrice}</p>
                  <p><strong>Placed:</strong> {new Date(order.createdAt).toLocaleDateString()}</p>
                  <p><strong>Seller:</strong> {order.sellerName}</p>
                </div>
              </Link>
            </div>
          ))}
        </div>
      )}
      </> : null}
    </div>
  );
};