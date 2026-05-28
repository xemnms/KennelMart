import api from '../api/axios';
import type { User } from '../types/auth';
import type { ProductListing } from '../types/marketplace';
import type { ReportResponse } from '../types/report';

export const adminService = {
  // Users
  async getUsers(params?: { verificationStatus?: string; accountStatus?: string; page?: number; size?: number }): Promise<{
    content: User[];
    totalPages: number;
    totalElements: number;
  }> {
    const response = await api.get('/api/admin/users', { params });
    return response.data;
  },
  async suspendUser(userId: string): Promise<void> {
    await api.put(`/api/admin/users/${userId}/suspend`);
  },
  async activateUser(userId: string): Promise<void> {
    await api.put(`/api/admin/users/${userId}/activate`);
  },
  // New methods for verification management
  async verifyUser(userId: string): Promise<void> {
    await api.put(`/api/admin/users/${userId}/verify`);
  },
  async rejectUserVerification(userId: string): Promise<void> {
    await api.put(`/api/admin/users/${userId}/reject-verification`);
  },

  // Listings
  async getListings(params?: { status?: string; page?: number; size?: number }): Promise<{
    content: ProductListing[];
    totalPages: number;
    totalElements: number;
  }> {
    const response = await api.get('/api/admin/listings', { params });
    return response.data;
  },
  async approveListing(listingId: string): Promise<void> {
    await api.put(`/api/admin/listings/${listingId}/approve`);
  },
  async rejectListing(listingId: string): Promise<void> {
    await api.put(`/api/admin/listings/${listingId}/reject`);
  },
  async deleteListing(listingId: string): Promise<void> {
    await api.delete(`/api/admin/listings/${listingId}`);
  },

  // Reports
  async getReports(params?: { status?: string; page?: number; size?: number }): Promise<{
    content: ReportResponse[];
    totalPages: number;
    totalElements: number;
  }> {
    const response = await api.get('/api/admin/reports', { params });
    return response.data;
  },
  async resolveReport(reportId: string): Promise<void> {
    await api.put(`/api/admin/reports/${reportId}/resolve`);
  },
  async dismissReport(reportId: string): Promise<void> {
    await api.put(`/api/admin/reports/${reportId}/dismiss`);
  },
};