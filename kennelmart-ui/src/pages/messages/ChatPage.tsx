import { useEffect, useState, useRef, useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
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
  const [menuOpen, setMenuOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const menuRef = useRef<HTMLDivElement>(null);

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

  // Close menu when clicking outside
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

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

  const initial = (otherUserName || '?').charAt(0).toUpperCase();

  return (
    <div className="chat-container">
      <div className="chat-header">
        <Link to="/messages/inbox" className="chat-back-btn" aria-label="Back to inbox">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
            <path d="M19 12H5M12 5l-7 7 7 7"/>
          </svg>
        </Link>

        {/* Tapping avatar or name → profile */}
        <Link to={`/user/${userId}`} className="chat-header-link">
          <div className="chat-header-avatar">{initial}</div>
          <div className="chat-header-info">
            <h2>{otherUserName || 'Chat'}</h2>
          </div>
        </Link>

        {/* 3-dot menu */}
        <div className="chat-header-menu" ref={menuRef}>
          <button
            className="report-btn"
            aria-label="More options"
            onClick={() => setMenuOpen((o) => !o)}
          >
            <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
              <circle cx="5" cy="12" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="19" cy="12" r="1.5"/>
            </svg>
          </button>
          {menuOpen && (
            <div className="chat-menu-dropdown">
              <Link
                to={`/user/${userId}`}
                className="chat-menu-item"
                onClick={() => setMenuOpen(false)}
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                View profile
              </Link>
              <button
                className="chat-menu-item danger"
                onClick={() => { setMenuOpen(false); setShowReportModal(true); }}
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M4 15s1-1 4-1 5 2 8 2 4-1 4-1V3s-1 1-4 1-5-2-8-2-4 1-4 1z"/><line x1="4" y1="22" x2="4" y2="15"/>
                </svg>
                Report user
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="chat-messages">
        {messages.length === 0 ? (
          <p className="no-messages">No messages yet. Start the conversation!</p>
        ) : (
          messages.map((msg, i) => {
            const isOwn = msg.senderId === currentUser?.id;
            const prevSame = i > 0 && messages[i - 1].senderId === msg.senderId;
            const nextSame = i < messages.length - 1 && messages[i + 1].senderId === msg.senderId;
            let groupClass = '';
            if (!prevSame && nextSame) groupClass = 'group-first';
            else if (prevSame && nextSame) groupClass = 'group-middle';
            else if (prevSame && !nextSame) groupClass = 'group-last';
            return (
              <div
                key={msg.id}
                className={`message ${isOwn ? 'outgoing' : 'incoming'} ${groupClass}`}
              >
                {!isOwn && (
                  <Link
                    to={`/user/${userId}`}
                    className={`msg-avatar${nextSame ? ' hidden' : ''}`}
                    tabIndex={nextSame ? -1 : 0}
                  >
                    {initial}
                  </Link>
                )}
                <div className="message-bubble">
                  <p>{msg.content}</p>
                </div>
              </div>
            );
          })
        )}
        <div ref={messagesEndRef} />
      </div>

      <form onSubmit={handleSend} className="chat-input-form">
        <input
          type="text"
          value={newMessage}
          onChange={(e) => setNewMessage(e.target.value)}
          placeholder="Message..."
          disabled={sending}
        />
        <button type="submit" disabled={sending || !newMessage.trim()} aria-label="Send">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/>
          </svg>
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