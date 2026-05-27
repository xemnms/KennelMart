import api from '../api/axios';
import { ReportRequest, ReportResponse } from '../types/report';

export const reportService = {
  async submitReport(data: ReportRequest): Promise<void> {
    await api.post('/api/reports', data);
  },
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
  async markReviewing(reportId: string): Promise<void> {
    await api.put(`/api/admin/reports/${reportId}/review`);
  },
  async suspendUserFromReport(reportId: string, userId: string): Promise<void> {
    await api.put(`/api/admin/reports/${reportId}/suspend-user/${userId}`);
  },
  async deleteListingFromReport(reportId: string, listingId: string): Promise<void> {
    await api.put(`/api/admin/reports/${reportId}/delete-listing/${listingId}`);
  },
};