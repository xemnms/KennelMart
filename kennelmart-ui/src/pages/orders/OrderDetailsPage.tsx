import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { orderService } from '../../services/orderService';
import { reviewService } from '../../services/reviewService';
import { ReviewModal } from '../../components/ReviewModal';
import type { OrderResponse } from '../../types/order';
import './OrderDetails.css';

export const OrderDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const [order, setOrder] = useState<OrderResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [reviewSubmitted, setReviewSubmitted] = useState(false);

  useEffect(() => {
    if (!id) return;
    orderService.getOrderById(id)
      .then(setOrder)
      .catch((err) => {
        setError('Failed to load order details');
        console.error(err);
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleSubmitReview = async (rating: number, comment: string) => {
    if (!order) return;
    await reviewService.submitReview({
      orderId: order.id,
      rating,
      comment,
    });
    setReviewSubmitted(true);
    alert('Review submitted! It will appear after admin approval.');
  };

  if (loading) return <div className="loading">Loading order...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (!order) return <div className="error-message">Order not found</div>;

  const canReview = order.status === 'DELIVERED' && !reviewSubmitted;
  const statusClass = order.status.toLowerCase();

  return (
    <div className="order-details-container">
      <h1>Order #{order.orderNumber}</h1>
      <div className="order-meta">
        <p><strong>Placed on:</strong> {new Date(order.createdAt).toLocaleString()}</p>
        <p><strong>Payment Method:</strong> {order.paymentMethod}</p>
        <p><strong>Status:</strong> <span className={`order-status ${statusClass}`}>{order.status}</span></p>
        {order.deliveredAt && <p><strong>Delivered on:</strong> {new Date(order.deliveredAt).toLocaleString()}</p>}
      </div>

      <div className="order-seller">
        <h3>Seller: {order.sellerName}</h3>
        <Link to={`/messages/${order.sellerId}`}>Contact Seller</Link>
      </div>

      <div className="order-items">
        <h2>Items</h2>
        <table className="items-table">
          <thead>
            <tr><th>Product</th><th>Quantity</th><th>Price</th><th>Subtotal</th></tr>
          </thead>
          <tbody>
            {order.items.map((item, idx) => (
              <tr key={idx}>
                <td>{item.productTitle}</td>
                <td>{item.quantity}</td>
                <td>₱{item.price}</td>
                <td>₱{item.subtotal}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr><td colSpan={3} className="total-label">Total</td><td className="total-value">₱{order.totalPrice}</td></tr>
          </tfoot>
        </table>
      </div>

      {canReview && (
        <div className="review-action">
          <button onClick={() => setShowReviewModal(true)} className="write-review-btn">
            Write a Review
          </button>
        </div>
      )}

      {reviewSubmitted && (
        <div className="review-submitted-message">
          Thank you! Your review has been submitted and is pending approval.
        </div>
      )}

      <div className="order-actions">
        <Link to="/orders">← Back to My Orders</Link>
      </div>

      <ReviewModal
        isOpen={showReviewModal}
        onClose={() => setShowReviewModal(false)}
        onSubmit={handleSubmitReview}
      />
    </div>
  );
};