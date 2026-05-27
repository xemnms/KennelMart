export interface Message {
  id: string;
  senderId: string;
  senderName: string;
  receiverId: string;
  receiverName: string;
  content: string;
  read: boolean;
  createdAt: string;
}

export interface Conversation {
  userId: string;
  name: string;
  lastMessage: string;
  lastMessageTime: string;
  unreadCount: number;
}