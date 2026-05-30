import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import type { Conversation } from '../../types/message';
import './Inbox.css';

const relativeTime = (iso: string): string => {
  const diff = Date.now() - new Date(iso).getTime();
  const m = Math.floor(diff / 60000);
  if (m < 1) return 'Just now';
  if (m < 60) return `${m}m`;
  const h = Math.floor(m / 60);
  if (h < 24) return `${h}h`;
  const d = Math.floor(h / 24);
  if (d < 7) return `${d}d`;
  const w = Math.floor(d / 7);
  if (w < 5) return `${w}w`;
  return new Date(iso).toLocaleDateString();
};

export const InboxPage = () => {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    messageService.getConversations()
      .then(setConversations)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="dm-loading">Loading...</div>;

  return (
    <div className="inbox-container">
      <div className="inbox-header">
        <h1>Messages</h1>
        <Link to="/messages/inbox" className="compose-btn" aria-label="New message">
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5Z" />
          </svg>
        </Link>
      </div>

      {conversations.length === 0 ? (
        <div className="empty-inbox">
          <div className="empty-inbox-icon">
            <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
          </div>
          <p>No conversations yet.</p>
        </div>
      ) : (
        <div className="conversations-list">
          {conversations.map((conv) => (
            <Link to={`/messages/${conv.userId}`} key={conv.userId} className={`dm-item${conv.unreadCount > 0 ? ' unread' : ''}`}>
              <div className="dm-avatar">
                <span>{conv.name.charAt(0).toUpperCase()}</span>
                {conv.unreadCount > 0 && <span className="dm-avatar-dot" />}
              </div>
              <div className="dm-info">
                <span className="dm-name">{conv.name}</span>
                <span className="dm-preview">{conv.lastMessage}</span>
              </div>
              <div className="dm-meta">
                {conv.lastMessageTime && <span className="dm-time">{relativeTime(conv.lastMessageTime)}</span>}
                {conv.unreadCount > 0 && <span className="dm-unread-dot" />}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};
