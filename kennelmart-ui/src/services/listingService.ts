import api from '../api/axios';
import { ProductListing, ListingFilters, CreateListingRequest } from '../types/marketplace';
export const listingService = {
  async getListings(filters: ListingFilters = {}): Promise<{
    content: ProductListing[];
    totalPages: number;
    totalElements: number;
  }> {
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
  async getMyListings(page: number = 0, size: number = 10): Promise<{
  content: ProductListing[];
  totalPages: number;
  totalElements: number;
  }> {
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
};
