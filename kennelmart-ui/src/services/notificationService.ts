import api from '../api/axios';
import { Notification } from '../types/notification';

export const notificationService = {
  async getNotifications(page: number = 0, size: number = 20): Promise<{
    content: Notification[];
    totalPages: number;
    totalElements: number;
  }> {
    const response = await api.get(`/api/notifications?page=${page}&size=${size}`);
    return response.data;
  },
  async getUnreadCount(): Promise<number> {
    const response = await api.get('/api/notifications/unread-count');
    return response.data;
  },
  async markAllAsRead(): Promise<void> {
    await api.put('/api/notifications/read-all');
  },
};