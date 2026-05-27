import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import type { Conversation } from '../../types/message';
import './Inbox.css';

export const InboxPage = () => {
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    messageService.getConversations()
      .then(setConversations)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Loading conversations...</div>;

  return (
    <div className="inbox-container">
      <h1>Messages</h1>
      {conversations.length === 0 ? (
        <div className="empty-inbox">No conversations yet.</div>
      ) : (
        <div className="conversations-list">
          {conversations.map((conv) => (
            <Link to={`/messages/${conv.userId}`} key={conv.userId} className="conversation-item">
              <div className="conversation-avatar">👤</div>
              <div className="conversation-details">
                <div className="conversation-name">{conv.name}</div>
                <div className="conversation-last-message">{conv.lastMessage}</div>
              </div>
              {conv.unreadCount > 0 && <div className="unread-badge">{conv.unreadCount}</div>}
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};