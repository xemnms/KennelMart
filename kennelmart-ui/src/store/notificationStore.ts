import { create } from 'zustand';
import { notificationService } from '../services/notificationService';

interface NotificationState {
  unreadCount: number;
  lastFetchedAt: number;
  isFetchingUnreadCount: boolean;
  fetchUnreadCount: (force?: boolean) => Promise<void>;
  markAllAsRead: () => Promise<void>;
}

export const useNotificationStore = create<NotificationState>((set) => ({
  unreadCount: 0,
  lastFetchedAt: 0,
  isFetchingUnreadCount: false,
  fetchUnreadCount: async (force = false) => {
    const { lastFetchedAt, isFetchingUnreadCount } = useNotificationStore.getState();
    const now = Date.now();

    // Prevent duplicate fetch bursts from multiple mounted components.
    if (isFetchingUnreadCount) return;
    if (!force && now - lastFetchedAt < 3000) return;

    set({ isFetchingUnreadCount: true });
    try {
      const count = await notificationService.getUnreadCount();
      set({ unreadCount: count, lastFetchedAt: Date.now() });
    } catch (error) {
      console.error(error);
    } finally {
      set({ isFetchingUnreadCount: false });
    }
  },
  markAllAsRead: async () => {
    try {
      await notificationService.markAllAsRead();
      set({ unreadCount: 0, lastFetchedAt: Date.now() });
    } catch (error) {
      console.error(error);
    }
  },
}));