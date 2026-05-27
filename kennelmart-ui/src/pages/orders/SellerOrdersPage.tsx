import { useEffect, useState } from 'react';
import { orderService } from '../../services/orderService';
import type { OrderResponse } from '../../types/order';
import './SellerOrders.css';

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

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleStatusChange = async (orderId: string, newStatus: string) => {
    setUpdatingId(orderId);
    try {
      await orderService.updateOrderStatus(orderId, newStatus);
      // Refresh the list
      fetchOrders();
    } catch (err) {
      alert('Failed to update status');
      console.error(err);
    } finally {
      setUpdatingId(null);
    }
  };

  if (loading) return <div className="loading">Loading your sales...</div>;

  return (
    <div className="seller-orders-container">
      <h1>Orders Received</h1>
      {orders.length === 0 ? (
        <div className="empty-orders">
          <p>You haven't received any orders yet.</p>
        </div>
      ) : (
        <div className="orders-table-wrapper">
          <table className="orders-table">
            <thead>
              <tr>
                <th>Order #</th>
                <th>Buyer</th>
                <th>Total</th>
                <th>Placed</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {orders.map(order => (
                <tr key={order.id}>
                  <td>{order.orderNumber}</td>
                  <td>{order.buyerName}</td>
                  <td>₱{order.totalPrice}</td>
                  <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                  <td>
                    <span className={`order-status ${order.status.toLowerCase()}`}>
                      {order.status}
                    </span>
                  </td>
                  <td>
                    <select
                      value={order.status}
                      onChange={(e) => handleStatusChange(order.id, e.target.value)}
                      disabled={updatingId === order.id}
                    >
                      <option value="ORDER_RECEIVED">Order Received</option>
                      <option value="ACCEPTED">Accept</option>
                      <option value="PREPARING">Preparing</option>
                      <option value="SHIPPED">Shipped</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELLED">Cancel</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};