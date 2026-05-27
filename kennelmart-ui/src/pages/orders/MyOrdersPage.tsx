import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import type { OrderResponse } from '../../types/order';
import './MyOrders.css';

export const MyOrdersPage = () => {
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    orderService.getMyOrders(0, 20)
      .then(data => setOrders(data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading your orders...</div>;

  return (
    <div className="my-orders-container">
      <h1>My Orders</h1>
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
    </div>
  );
};