import { create } from 'zustand';
import api from '../api/axios';

export interface CartItem {
  id: string;
  listingId: string;
  title: string;
  price: number;
  quantity: number;
  subtotal: number;
}

interface CartState {
  items: CartItem[];
  totalPrice: number;
  isLoading: boolean;
  fetchCart: () => Promise<void>;
  addItem: (listingId: string, quantity: number) => Promise<void>;
  updateQuantity: (cartItemId: string, quantity: number) => Promise<void>;
  removeItem: (cartItemId: string) => Promise<void>;
  clearCart: () => void;
}

export const useCartStore = create<CartState>((set, get) => ({
  items: [],
  totalPrice: 0,
  isLoading: false,

  fetchCart: async () => {
    set({ isLoading: true });
    try {
      const response = await api.get('/api/cart');
      const cart = response.data;
      set({ items: cart.items || [], totalPrice: cart.totalPrice || 0 });
    } catch (error) {
      console.error('Failed to fetch cart', error);
    } finally {
      set({ isLoading: false });
    }
  },

    addItem: async (listingId: string, quantity: number) => {
    try {
        await api.post('/api/cart/items', { listingId, quantity });
        await get().fetchCart();
    } catch (error) {
        console.error('Failed to add item', error);
        throw error;   // rethrow so the caller can catch it
    }
    },

  updateQuantity: async (cartItemId, quantity) => {
    try {
      await api.put(`/api/cart/items/${cartItemId}?quantity=${quantity}`);
      await get().fetchCart();
    } catch (error) {
      console.error('Failed to update quantity', error);
      throw error;
    }
  },

  removeItem: async (cartItemId) => {
    try {
      await api.delete(`/api/cart/items/${cartItemId}`);
      await get().fetchCart();
    } catch (error) {
      console.error('Failed to remove item', error);
      throw error;
    }
  },

  clearCart: () => {
    set({ items: [], totalPrice: 0 });
  },
}));