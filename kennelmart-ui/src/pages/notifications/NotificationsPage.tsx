import { useEffect, useState } from 'react';
import { notificationService } from '../../services/notificationService';
import { useNotificationStore } from '../../store/notificationStore';
import type { Notification } from '../../types/notification';
import './Notifications.css';

export const NotificationsPage = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const { unreadCount, fetchUnreadCount, markAllAsRead } = useNotificationStore();

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const data = await notificationService.getNotifications(0, 50);
      setNotifications(data.content);
      await fetchUnreadCount();
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchNotifications();
  }, []);

  const handleMarkAllRead = async () => {
    await markAllAsRead();
    setNotifications(prev => prev.map(n => ({ ...n, read: true })));
  };

  if (loading) return <div className="loading">Loading notifications...</div>;

  return (
    <div className="notifications-container">
      <div className="notifications-header">
        <h1>Notifications</h1>
        {unreadCount > 0 && (
          <button onClick={handleMarkAllRead} className="mark-all-btn">
            Mark all as read
          </button>
        )}
      </div>
      {notifications.length === 0 ? (
        <div className="empty-notifications">No notifications yet.</div>
      ) : (
        <div className="notifications-list">
          {notifications.map(notif => (
            <div key={notif.id} className={`notification-item ${!notif.read ? 'unread' : ''}`}>
              <div className="notification-icon">
                {notif.type === 'ORDER' ? '🛒' : notif.type === 'VERIFICATION' ? '✅' : notif.type === 'MESSAGE' ? '💬' : '🔔'}
              </div>
              <div className="notification-content">
                <div className="notification-title">{notif.title}</div>
                <div className="notification-message">{notif.message}</div>
                <div className="notification-time">{new Date(notif.createdAt).toLocaleString()}</div>
              </div>
              {!notif.read && <div className="unread-dot"></div>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};