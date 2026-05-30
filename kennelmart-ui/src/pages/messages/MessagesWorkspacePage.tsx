import { useEffect, useMemo, useRef, useState, useCallback } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { messageService } from '../../services/messageService';
import { reportService } from '../../services/reportService';
import { userService } from '../../services/userService';
import { useAuthStore } from '../../store/authStore';
import { ReportModal } from '../../components/ReportModal';
import type { Conversation, Message } from '../../types/message';
import './MessagesWorkspace.css';

export const MessagesWorkspacePage = () => {
  const { userId } = useParams<{ userId?: string }>();
  const navigate = useNavigate();
  const currentUser = useAuthStore((state) => state.user);
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedUserId, setSelectedUserId] = useState<string | null>(userId ?? null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [otherUserName, setOtherUserName] = useState<string>('');
  const [targetUserName, setTargetUserName] = useState<string>('');
  const [newMessage, setNewMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [showReportModal, setShowReportModal] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const selectedConversation = useMemo(
    () => conversations.find((conversation) => conversation.userId === selectedUserId) ?? null,
    [conversations, selectedUserId],
  );

  const fetchConversations = useCallback(async () => {
    try {
      const data = await messageService.getConversations();
      setConversations(data);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchMessages = useCallback(async () => {
    if (!selectedUserId) {
      setMessages([]);
      setOtherUserName('');
      setMessagesLoading(false);
      return;
    }

    setMessagesLoading(true);
    try {
      const data = await messageService.getConversation(selectedUserId, 0, 50);
      const sortedMessages = [...data.content].reverse();
      setMessages(sortedMessages);
      if (sortedMessages.length > 0) {
        const first = sortedMessages[0];
        setOtherUserName(first.senderId === selectedUserId ? first.senderName : first.receiverName);
      } else if (selectedConversation?.name) {
        setOtherUserName(selectedConversation.name);
      } else {
        // New conversation — fetch target user's name from public profile
        try {
          const profile = await userService.getUserPublicProfile(selectedUserId);
          setTargetUserName(profile.name);
          setOtherUserName(profile.name);
        } catch {
          setOtherUserName('User');
        }
      }
      await messageService.markAsRead(selectedUserId);
      await fetchConversations();
    } catch (error) {
      console.error(error);
    } finally {
      setMessagesLoading(false);
    }
  }, [selectedUserId, selectedConversation?.name, fetchConversations]);

  useEffect(() => {
    void fetchConversations();
  }, [fetchConversations]);

  useEffect(() => {
    void fetchMessages();
    const interval = selectedUserId ? setInterval(fetchMessages, 5000) : null;
    return () => {
      if (interval) {
        clearInterval(interval);
      }
    };
  }, [fetchMessages, selectedUserId]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    setSelectedUserId(userId ?? null);
  }, [userId]);

  const handleSelectConversation = (conversationId: string) => {
    setSelectedUserId(conversationId);
    navigate(`/messages/${conversationId}`);
  };

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim() || !selectedUserId) return;
    setSending(true);
    try {
      await messageService.sendMessage(selectedUserId, newMessage);
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
    if (!selectedUserId) return;
    await reportService.submitReport({
      targetType: 'USER',
      targetId: selectedUserId,
      reason,
    });
    alert('User reported. Thank you for helping keep the community safe.');
  };

  if (loading) return <div className="loading">Loading conversations...</div>;

  return (
    <div className="messages-workspace">
      <aside className="messages-panel inbox-panel">
        <div className="messages-panel-header">
          <div>
            <p className="eyebrow">Direct messages</p>
            <h1>Inbox</h1>
          </div>
          <Link to="/" className="panel-ghost-link">Back to feed</Link>
        </div>

        <div className="conversations-list workspace-list">
          {conversations.length === 0 ? (
            <div className="empty-inbox compact">No conversations yet.</div>
          ) : (
            conversations.map((conversation) => (
              <button
                type="button"
                key={conversation.userId}
                className={`conversation-item workspace-item ${selectedUserId === conversation.userId ? 'active' : ''}`}
                onClick={() => handleSelectConversation(conversation.userId)}
              >
                <div className="conversation-avatar">{conversation.name.charAt(0)}</div>
                <div className="conversation-details">
                  <div className="conversation-name">{conversation.name}</div>
                  <div className="conversation-last-message">{conversation.lastMessage}</div>
                </div>
                {conversation.unreadCount > 0 && <div className="unread-badge">{conversation.unreadCount}</div>}
              </button>
            ))
          )}
        </div>
      </aside>

      <section className="messages-panel chat-panel">
        {selectedUserId ? (
          <>
            <div className="chat-topbar">
              <div className="chat-recipient">
                <div className="conversation-avatar compact">{(otherUserName || targetUserName || 'U').charAt(0)}</div>
                <div>
                  <h2>{otherUserName || targetUserName || selectedConversation?.name || 'Conversation'}</h2>
                  <span>{selectedConversation ? 'Active conversation' : 'New conversation'}</span>
                </div>
              </div>
              <button type="button" className="report-btn compact" onClick={() => setShowReportModal(true)}>Report</button>
            </div>

            <div className="chat-messages workspace-messages">
              {messagesLoading ? (
                <div className="loading inline">Loading messages...</div>
              ) : messages.length === 0 ? (
                <p className="no-messages">No messages yet. Start the conversation.</p>
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

            <form onSubmit={handleSend} className="chat-input-form workspace-input">
              <input
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="Send a message"
                disabled={sending}
              />
              <button type="submit" disabled={sending}>
                Send
              </button>
            </form>
          </>
        ) : (
          <div className="chat-empty-state">
            <p>Select a conversation to chat inline without leaving the inbox.</p>
          </div>
        )}
      </section>

      <aside className="messages-panel profile-panel">
        {selectedUserId ? (
          <>
            <div className="profile-panel-header">
              <div className="conversation-avatar profile-large">{(otherUserName || targetUserName || 'U').charAt(0)}</div>
              <div>
                <p className="eyebrow">Profile</p>
                <h2>{otherUserName || targetUserName || selectedConversation?.name || 'User'}</h2>
                <span>{selectedConversation ? `${selectedConversation.unreadCount} unread` : 'New conversation'}</span>
              </div>
            </div>

            <div className="profile-card-block">
              <div>
                <strong>Last message</strong>
                <p>{selectedConversation?.lastMessage || 'No messages yet. Say hello!'}</p>
              </div>
              <div className="profile-meta-row">
                <span>User ID</span>
                <strong>{selectedUserId}</strong>
              </div>
            </div>

            <div className="profile-panel-actions">
              <Link to={`/user/${selectedUserId}`} className="profile-action-link">Open profile</Link>
              <button type="button" className="profile-action-link secondary" onClick={() => setShowReportModal(true)}>
                Report user
              </button>
            </div>
          </>
        ) : (
          <div className="chat-empty-state compact">
            <p>Pick a conversation to see the profile details here.</p>
          </div>
        )}
      </aside>

      <ReportModal
        isOpen={showReportModal}
        onClose={() => setShowReportModal(false)}
        onSubmit={handleReportUser}
        targetType="USER"
      />
    </div>
  );
};