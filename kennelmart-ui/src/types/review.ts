export interface ReviewRequest {
  orderId: string;
  rating: number;
  comment: string;
}

export interface Review {
  id: string;
  sellerId: string;
  sellerName: string;
  buyerId: string;
  buyerName: string;
  rating: number;
  comment: string;
  createdAt: string;
}