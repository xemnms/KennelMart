import api from '../api/axios';
import { ProductListing, ListingFilters, CreateListingRequest } from '../types/marketplace';

// Define the response shape for paginated listing results
export interface ListingSearchResponse {
  content: ProductListing[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export const listingService = {
  async getListings(filters: ListingFilters = {}): Promise<ListingSearchResponse> {
    const params = new URLSearchParams();
    if (filters.keyword) params.append('keyword', filters.keyword);
    if (filters.category) params.append('category', filters.category);
    if (filters.page !== undefined) params.append('page', filters.page.toString());
    if (filters.size !== undefined) params.append('size', filters.size.toString());
    if (filters.sort) params.append('sort', filters.sort);
    const response = await api.get(`/api/listings?${params.toString()}`);
    return response.data;
  },

  async getListingById(id: string): Promise<ProductListing> {
    const response = await api.get(`/api/listings/${id}`);
    return response.data;
  },

  async createListing(data: CreateListingRequest): Promise<ProductListing> {
    const response = await api.post<ProductListing>('/api/listings', data);
    return response.data;
  },

  async getMyListings(page: number = 0, size: number = 10): Promise<ListingSearchResponse> {
    const response = await api.get(`/api/listings/my-listings?page=${page}&size=${size}`);
    return response.data;
  },

  async deleteListing(id: string): Promise<void> {
    await api.delete(`/api/listings/${id}`);
  },

  async updateListing(id: string, data: CreateListingRequest): Promise<ProductListing> {
    const response = await api.put<ProductListing>(`/api/listings/${id}`, data);
    return response.data;
  },

  async getListingsBySeller(sellerId: string, page = 0, size = 12): Promise<ListingSearchResponse> {
    const response = await api.get(`/api/listings/users/${sellerId}/listings`, {
      params: { page, size }
    });
    return response.data;
  }
};