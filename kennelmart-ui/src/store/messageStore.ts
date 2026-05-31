import { create } from 'zustand';
import { messageService } from '../services/messageService';

interface MessageState {
  unreadCount: number;
  lastFetchedAt: number;
  isFetchingUnreadCount: boolean;
  fetchUnreadCount: (force?: boolean) => Promise<void>;
}

export const useMessageStore = create<MessageState>((set) => ({
  unreadCount: 0,
  lastFetchedAt: 0,
  isFetchingUnreadCount: false,
  fetchUnreadCount: async (force = false) => {
    const { lastFetchedAt, isFetchingUnreadCount } = useMessageStore.getState();
    const now = Date.now();

    // Prevent duplicate fetch bursts from multiple mounted components.
    if (isFetchingUnreadCount) return;
    if (!force && now - lastFetchedAt < 3000) return;

    set({ isFetchingUnreadCount: true });
    try {
      const count = await messageService.getUnreadCount();
      set({ unreadCount: count, lastFetchedAt: Date.now() });
    } catch (error) {
      console.error(error);
    } finally {
      set({ isFetchingUnreadCount: false });
    }
  },
}));