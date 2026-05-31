import { useEffect, useState } from 'react';
import { orderService } from '../../services/orderService';
import type { OrderResponse } from '../../types/order';
import './SellerOrders.css';

const STATUS_OPTIONS = [
  { value: 'ORDER_RECEIVED', label: 'Order Received' },
  { value: 'ACCEPTED',       label: 'Accept'         },
  { value: 'PREPARING',      label: 'Preparing'      },
  { value: 'SHIPPED',        label: 'Shipped'        },
  { value: 'DELIVERED',      label: 'Delivered'      },
  { value: 'CANCELLED',      label: 'Cancel'         },
];

const relativeTime = (iso: string): string => {
  const diff = Date.now() - new Date(iso).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 1) return 'Just now';
  if (m < 60) return `${m}m ago`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h ago`;
  const d = Math.floor(h / 24);
  if (d < 7) return `${d}d ago`;
  return new Date(iso).toLocaleDateString();
};

export const SellerOrdersPage = () => {
  const [orders, setOrders] = useState<OrderResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [updatingId, setUpdatingId] = useState<string | null>(null);

  const fetchOrders = () => {
    orderService.getMySales(0, 50)
      .then(data => setOrders(data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchOrders(); }, []);

  const handleStatusChange = async (orderId: string, newStatus: string) => {
    setUpdatingId(orderId);
    try {
      await orderService.updateOrderStatus(orderId, newStatus);
      fetchOrders();
    } catch (err) {
      alert('Failed to update status');
      console.error(err);
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading) return <div className="so-loading">Loading sales...</div>;

  return (
    <div className="so-page">
      <div className="so-header">
        <h1 className="so-title">Sales</h1>
        <span className="so-count">{orders.length} order{orders.length !== 1 ? 's' : ''}</span>
      </div>
      {orders.length === 0 ? (
        <div className="so-empty">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.4" strokeLinecap="round" strokeLinejoin="round">
            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
            <line x1="3" y1="6" x2="21" y2="6"/>
            <path d="M16 10a4 4 0 0 1-8 0"/>
          </svg>
          <p>No orders received yet.</p>
        </div>
      ) : (
        <div className="so-list">
          {orders.map(order => (
            <div key={order.id} className={`so-card${updatingId === order.id ? ' updating' : ''}`}>

              <div className="so-card-top">
                <div className="so-buyer">
                  <div className="so-avatar">{order.buyerName?.charAt(0).toUpperCase() ?? '?'}</div>
                  <div className="so-buyer-info">
                    <span className="so-buyer-name">{order.buyerName}</span>
                    <span className="so-order-num">#{order.orderNumber}</span>
                  </div>
                </div>
                <span className="so-time">{relativeTime(order.createdAt)}</span>
              </div>

              <div className="so-card-mid">
                <span className="so-price">₱{order.totalPrice}</span>
                <span className={`so-badge so-badge--${order.status.toLowerCase()}`}>
                  {order.status.replace(/_/g, ' ')}
                </span>
              </div>

              <div className="so-card-foot">
                <label className="so-select-label">Update status</label>
                <div className="so-select-row">
                  <select
                    className="so-select"
                    value={order.status}
                    onChange={e => handleStatusChange(order.id, e.target.value)}
                    disabled={updatingId === order.id}
                  >
                    {STATUS_OPTIONS.map(opt => (
                      <option key={opt.value} value={opt.value}>{opt.label}</option>
                    ))}
                  </select>
                  {updatingId === order.id && <span className="so-spinner" />}
                </div>
              </div>

            </div>
          ))}
        </div>
      )}
    </div>
  );
};