import { create } from 'zustand';
import { notificationService } from '../services/notificationService';

interface NotificationState {
  unreadCount: number;
  fetchUnreadCount: () => Promise<void>;
  markAllAsRead: () => Promise<void>;
}

export const useNotificationStore = create<NotificationState>((set) => ({
  unreadCount: 0,
  fetchUnreadCount: async () => {
    try {
      const count = await notificationService.getUnreadCount();
      set({ unreadCount: count });
    } catch (error) {
      console.error(error);
    }
  },
  markAllAsRead: async () => {
    try {
      await notificationService.markAllAsRead();
      set({ unreadCount: 0 });
    } catch (error) {
      console.error(error);
    }
  },
}));