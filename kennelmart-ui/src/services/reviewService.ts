import api from '../api/axios';
import { Review, ReviewRequest } from '../types/review';

export const reviewService = {
  async submitReview(data: ReviewRequest): Promise<Review> {
    const response = await api.post('/api/reviews', data);
    return response.data;
  },
  async getSellerReviews(sellerId: string, page: number = 0, size: number = 10): Promise<{
    content: Review[];
    totalPages: number;
    totalElements: number;
  }> {
    const response = await api.get(`/api/reviews/sellers/${sellerId}?page=${page}&size=${size}`);
    return response.data;
  },
  async getSellerAverageRating(sellerId: string): Promise<number> {
    const response = await api.get(`/api/reviews/sellers/${sellerId}/average`);
    return response.data;
  },
};