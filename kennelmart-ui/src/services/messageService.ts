import api from '../api/axios';
import { Message, Conversation } from '../types/message';

export const messageService = {
  async sendMessage(receiverId: string, content: string): Promise<Message> {
    const response = await api.post('/api/messages', { receiverId, content });
    return response.data;
  },

  async getConversation(userId: string, page: number = 0, size: number = 20): Promise<{
    content: Message[];
    totalPages: number;
  }> {
    const response = await api.get(`/api/messages/conversation/${userId}?page=${page}&size=${size}`);
    return response.data;
  },

  async getUnreadCount(): Promise<number> {
    const response = await api.get('/api/messages/unread-count');
    return response.data;
  },

  async markAsRead(userId: string): Promise<void> {
    await api.put(`/api/messages/read/${userId}`);
  },

  // New: get all conversations for the current user
  async getConversations(): Promise<Conversation[]> {
    const response = await api.get('/api/messages/conversations');
    return response.data;
  },
};