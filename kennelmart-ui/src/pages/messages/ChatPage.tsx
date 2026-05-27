import { useEffect, useState, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import { reportService } from '../../services/reportService';
import { useAuthStore } from '../../store/authStore';
import { ReportModal } from '../../components/ReportModal';
import { Message } from '../../types/message';
import './Chat.css';

export const ChatPage = () => {
  const { userId } = useParams<{ userId: string }>();
  const currentUser = useAuthStore((state) => state.user);
  const [messages, setMessages] = useState<Message[]>([]);
  const [otherUserName, setOtherUserName] = useState<string>('');
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const fetchMessages = useCallback(async () => {
    if (!userId) return;
    try {
      const data = await messageService.getConversation(userId, 0, 50);
      const sortedMessages = [...data.content].reverse();
      setMessages(sortedMessages);
      if (sortedMessages.length > 0) {
        const first = sortedMessages[0];
        const otherName = first.senderId === userId ? first.senderName : first.receiverName;
        setOtherUserName(otherName);
      }
      await messageService.markAsRead(userId);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchMessages();
    const interval = setInterval(fetchMessages, 5000);
    return () => clearInterval(interval);
  }, [fetchMessages]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !userId) return;
    setSending(true);
    try {
      await messageService.sendMessage(userId, newMessage);
      setNewMessage('');
      await fetchMessages();
    } catch (error) {
      console.error(error);
      alert('Failed to send message');
    } finally {
      setSending(false);
    }
  };

  const handleReportUser = async (reason: string) => {
    if (!userId) return;
    await reportService.submitReport({
      targetType: 'USER',
      targetId: userId,
      reason,
    });
    alert('User reported. Thank you for helping keep the community safe.');
  };

  if (loading) return <div className="loading">Loading messages...</div>;

  return (
    <div className="chat-container">
      <div className="chat-header">
        <h2>{otherUserName || 'Chat'}</h2>
        <button onClick={() => setShowReportModal(true)} className="report-btn">Report User</button>
      </div>
      <div className="chat-messages">
        {messages.length === 0 ? (
          <p className="no-messages">No messages yet. Start the conversation!</p>
        ) : (
          messages.map((msg) => (
            <div
              key={msg.id}
              className={`message ${msg.senderId === currentUser?.id ? 'outgoing' : 'incoming'}`}
            >
              <div className="message-bubble">
                <p>{msg.content}</p>
                <span className="timestamp">{new Date(msg.createdAt).toLocaleTimeString()}</span>
              </div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>
      <form onSubmit={handleSend} className="chat-input-form">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Type a message..."
          disabled={sending}
        />
        <button type="submit" disabled={sending}>
          Send
        </button>
      </form>

      <ReportModal
        isOpen={showReportModal}
        onClose={() => setShowReportModal(false)}
        onSubmit={handleReportUser}
        targetType="USER"
      />
    </div>
  );
};