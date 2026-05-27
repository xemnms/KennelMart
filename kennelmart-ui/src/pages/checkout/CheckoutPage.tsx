import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCartStore } from '../../store/cartStore';
import api from '../../api/axios';
import './Checkout.css';

export const CheckoutPage = () => {
  const { items, totalPrice, clearCart, fetchCart } = useCartStore();
  const [paymentMethod, setPaymentMethod] = useState('COD');
  const [isProcessing, setIsProcessing] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (items.length === 0) {
      fetchCart();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handlePlaceOrder = async () => {
    setIsProcessing(true);
    try {
      await api.post('/api/orders', { paymentMethod });
      clearCart();
      navigate('/orders');
    } catch (error) {
      console.error('Order failed', error);
      alert('Failed to place order. Please try again.');
    } finally {
      setIsProcessing(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="checkout-empty">
        <h2>Your cart is empty</h2>
        <button onClick={() => navigate('/')}>Continue Shopping</button>
      </div>
    );
  }

  return (
    <div className="checkout-container">
      <h1>Checkout</h1>
      <div className="checkout-summary">
        <h3>Order Summary</h3>
        {items.map((item) => (
          <div key={item.id} className="checkout-item">
            <span>{item.title} x {item.quantity}</span>
            <span>₱{item.subtotal}</span>
          </div>
        ))}
        <div className="total">
          <strong>Total: ₱{totalPrice}</strong>
        </div>
      </div>
      <div className="payment-method">
        <h3>Payment Method</h3>
        <label>
          <input
            type="radio"
            value="COD"
            checked={paymentMethod === 'COD'}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />
          Cash on Delivery (COD)
        </label>
        <label>
          <input
            type="radio"
            value="GCASH"
            checked={paymentMethod === 'GCASH'}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />
          GCash
        </label>
        <label>
          <input
            type="radio"
            value="CAMPUS_MEETUP"
            checked={paymentMethod === 'CAMPUS_MEETUP'}
            onChange={(e) => setPaymentMethod(e.target.value)}
          />
          Campus Meetup
        </label>
      </div>
      <button onClick={handlePlaceOrder} disabled={isProcessing}>
        {isProcessing ? 'Processing...' : 'Place Order'}
      </button>
    </div>
  );
};