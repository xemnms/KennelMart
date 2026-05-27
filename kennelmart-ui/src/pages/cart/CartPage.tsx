import { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useCartStore } from '../../store/cartStore';
import './Cart.css';

export const CartPage = () => {
  const { items, totalPrice, isLoading, fetchCart, updateQuantity, removeItem } = useCartStore();

  useEffect(() => {
    fetchCart();
  }, []);

  const handleQuantityChange = (itemId: string, newQuantity: number) => {
    if (newQuantity < 1) {
      removeItem(itemId);
    } else {
      updateQuantity(itemId, newQuantity);
    }
  };

  if (isLoading) return <div className="loading">Loading cart...</div>;

  if (items.length === 0) {
    return (
      <div className="cart-empty">
        <h2>Your cart is empty</h2>
        <Link to="/">Continue shopping</Link>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <h1>Shopping Cart</h1>
      <div className="cart-items">
        {items.map((item) => (
          <div key={item.id} className="cart-item">
            <div className="item-details">
              <h3>{item.title}</h3>
              <p>₱{item.price}</p>
            </div>
            <div className="item-quantity">
              <button onClick={() => handleQuantityChange(item.id, item.quantity - 1)}>-</button>
              <span>{item.quantity}</span>
              <button onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>+</button>
            </div>
            <div className="item-subtotal">₱{item.subtotal}</div>
            <button className="remove-btn" onClick={() => removeItem(item.id)}>Remove</button>
          </div>
        ))}
      </div>
      <div className="cart-summary">
        <h2>Total: ₱{totalPrice}</h2>
        <Link to="/checkout">
          <button>Proceed to Checkout</button>
        </Link>
      </div>
    </div>
  );
};