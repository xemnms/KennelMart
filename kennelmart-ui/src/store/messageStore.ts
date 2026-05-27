import { create } from 'zustand';
import { messageService } from '../services/messageService';

interface MessageState {
  unreadCount: number;
  fetchUnreadCount: () => Promise<void>;
}

export const useMessageStore = create<MessageState>((set) => ({
  unreadCount: 0,
  fetchUnreadCount: async () => {
    try {
      const count = await messageService.getUnreadCount();
      set({ unreadCount: count });
    } catch (error) {
      console.error(error);
    }
  },
}));