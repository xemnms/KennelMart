export interface OrderItemResponse {
  productTitle: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface OrderResponse {
  id: string;
  orderNumber: string;
  buyerId: string;
  buyerName: string;
  sellerId: string;
  sellerName: string;
  status: 'ORDER_RECEIVED' | 'ACCEPTED' | 'PREPARING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';
  totalPrice: number;
  paymentMethod: 'COD' | 'GCASH' | 'CAMPUS_MEETUP';
  items: OrderItemResponse[];
  createdAt: string;
  deliveredAt: string | null;
}