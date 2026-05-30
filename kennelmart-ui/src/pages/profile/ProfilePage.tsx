import { useEffect, useState, useRef } from 'react';
import { createPortal } from 'react-dom';
import { useNavigate, Link } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuthStore } from '../../store/authStore';
import { authService } from '../../services/authService';
import { listingService } from '../../services/listingService';
import { getImageUrl } from '../../utils/imageUtils';
import type { UpdateProfileRequest, ChangePasswordRequest } from '../../types/auth';
import type { ProductListing } from '../../types/marketplace';
import './Profile.css';

export const ProfilePage = () => {
  const navigate = useNavigate();
  const { user, fetchUser, logout } = useAuthStore();
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const [showSettings, setShowSettings] = useState(false);
  const [listings, setListings] = useState<ProductListing[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const {
    register: registerProfile,
    handleSubmit: handleProfileSubmit,
    reset: resetProfile,
    formState: { errors: profileErrors },
  } = useForm<UpdateProfileRequest>();

  const {
    register: registerPassword,
    handleSubmit: handlePasswordSubmit,
    reset: resetPassword,
    formState: { errors: passwordErrors },
  } = useForm<ChangePasswordRequest>();

  useEffect(() => {
    if (!user) {
      navigate('/login');
    }
  }, [user, navigate]);

  useEffect(() => {
    if (user) {
      fetchUser();
    }
  }, [user, fetchUser]);

  useEffect(() => {
    if (user) {
      resetProfile({
        name: user.name,
        studentOrFacultyId: user.studentOrFacultyId,
        profileImage: user.profileImage || '',
      });
    }
  }, [user, resetProfile]);

  useEffect(() => {
    if (!user?.id) return;
    listingService.getListingsBySeller(user.id, 0, 24)
      .then(data => setListings(data.content))
      .catch(console.error);
  }, [user?.id]);

  const handleLogout = () => {
    logout();
    navigate('/auth/login');
  };

  const onProfileUpdate = async (data: UpdateProfileRequest) => {
    setMessage(null);
    try {
      const updated = await authService.updateProfile(data);
      useAuthStore.setState({ user: updated });
      setMessage({ type: 'success', text: 'Profile updated successfully' });
      setIsEditing(false);
    } catch (err) {
      let errorMessage = 'Update failed';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setMessage({ type: 'error', text: errorMessage });
    }
  };

  const onPasswordChange = async (data: ChangePasswordRequest) => {
    setMessage(null);
    if (data.newPassword !== data.confirmPassword) {
      setMessage({ type: 'error', text: 'New passwords do not match' });
      return;
    }
    try {
      await authService.changePassword(data);
      setMessage({ type: 'success', text: 'Password changed successfully' });
      setIsChangingPassword(false);
      resetPassword();
    } catch (err) {
      let errorMessage = 'Password change failed';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setMessage({ type: 'error', text: errorMessage });
    }
  };

  const handleAvatarChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploadingAvatar(true);
    setMessage(null);
    try {
      const imageUrl = await authService.uploadProfileImage(file);
      if (user) {
        const updatedUser = { ...user, profileImage: imageUrl };
        useAuthStore.setState({ user: updatedUser });
        setMessage({ type: 'success', text: 'Profile picture updated' });
      }
      await fetchUser();
    } catch (error) {
      console.error('Avatar upload error:', error);
      setMessage({ type: 'error', text: 'Failed to upload profile picture' });
    } finally {
      setUploadingAvatar(false);
    }
  };

  if (!user) {
    return <div className="loading">Redirecting...</div>;
  }

  const roleClass = user.role ? user.role.toLowerCase() : '';
  const verificationClass = user.verificationStatus ? user.verificationStatus.toLowerCase() : '';

  return (
    <div className="profile-container">

      {/* ── IG-style top row: username + hamburger ── */}
      <div className="own-profile-toprow">
        <span className="own-username">{user.name}</span>
        <button
          type="button"
          className="profile-menu-btn"
          aria-label="Settings"
          onClick={() => setShowSettings(true)}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round">
            <line x1="3" y1="6" x2="21" y2="6" />
            <line x1="3" y1="12" x2="21" y2="12" />
            <line x1="3" y1="18" x2="21" y2="18" />
          </svg>
        </button>
      </div>

      {/* ── Profile info ── */}
      <div className="own-profile-info-row">
        <div className="avatar-large">
          <img src={getImageUrl(user.profileImage)} alt={user.name} />
          <button
            type="button"
            className="upload-avatar-btn"
            onClick={() => fileInputRef.current?.click()}
            disabled={uploadingAvatar}
            aria-label="Change photo"
          >
            {uploadingAvatar ? (
              <span style={{ fontSize: '0.55rem' }}>…</span>
            ) : (
              <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>
              </svg>
            )}
          </button>
          <input type="file" ref={fileInputRef} style={{ display: 'none' }} accept="image/*" onChange={handleAvatarChange} />
        </div>
        <div className="own-profile-meta">
          <div className="own-profile-stat">
            <strong>{listings.length}</strong>
            <span>posts</span>
          </div>
          <div className="badges">
            <span className={`badge role-${roleClass}`}>{user.role}</span>
            <span className={`badge status-${verificationClass}`}>{user.verificationStatus}</span>
            {user.averageRating !== undefined && user.averageRating > 0 && (
              <span className="badge rating">★ {user.averageRating.toFixed(1)}</span>
            )}
          </div>
        </div>
      </div>

      {/* ── Posts grid ── */}
      <div className="profile-posts-section">
        <div className="profile-posts-divider">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/>
          </svg>
          <span>Posts</span>
        </div>
        {listings.length === 0 ? (
          <div className="profile-no-posts">No posts yet.</div>
        ) : (
          <div className="profile-posts-grid">
            {listings.map(listing => (
              <Link key={listing.id} to={`/listings/${listing.id}`} className="profile-post-thumb">
                <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
                <div className="profile-post-overlay">
                  <span className="profile-post-price">₱{listing.price}</span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>

      {/* ── Settings drawer — portalled to body so it's never clipped ── */}
      {showSettings && createPortal(
        <>
          <div className="settings-overlay" onClick={() => setShowSettings(false)} />
          <div className="settings-drawer">
            <div className="settings-drawer-handle" />
            <div className="settings-drawer-header">
              <h2>Settings</h2>
              <button type="button" className="settings-close-btn" onClick={() => setShowSettings(false)} aria-label="Close">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round">
                  <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
              </button>
            </div>

            {message && <div className={`profile-message ${message.type}`}>{message.text}</div>}

            {/* Edit profile */}
            <div className="settings-section">
              <div className="section-header">
                <h3>Personal Information</h3>
                {!isEditing && (
                  <button type="button" onClick={() => setIsEditing(true)} className="edit-btn">Edit</button>
                )}
              </div>
              {isEditing ? (
                <form onSubmit={handleProfileSubmit(onProfileUpdate)} className="profile-form">
                  <div className="form-group">
                    <label>Full Name</label>
                    <input {...registerProfile('name', { required: 'Name is required' })} />
                    {profileErrors.name && <span className="error">{profileErrors.name.message}</span>}
                  </div>
                  <div className="form-group">
                    <label>Student/Faculty ID</label>
                    <input {...registerProfile('studentOrFacultyId', { required: 'ID is required' })} />
                    {profileErrors.studentOrFacultyId && <span className="error">{profileErrors.studentOrFacultyId.message}</span>}
                  </div>
                  <div className="form-actions">
                    <button type="button" onClick={() => setIsEditing(false)}>Cancel</button>
                    <button type="submit">Save Changes</button>
                  </div>
                </form>
              ) : (
                <div className="profile-info-rows">
                  <p><strong>Name</strong><span>{user.name}</span></p>
                  <p><strong>ID</strong><span>{user.studentOrFacultyId}</span></p>
                  <p><strong>Email</strong><span>{user.email}</span></p>
                </div>
              )}
            </div>

            {/* Change password */}
            <div className="settings-section">
              <div className="section-header">
                <h3>Security</h3>
                {!isChangingPassword && (
                  <button type="button" onClick={() => setIsChangingPassword(true)} className="edit-btn">Change</button>
                )}
              </div>
              {isChangingPassword && (
                <form onSubmit={handlePasswordSubmit(onPasswordChange)} className="profile-form">
                  <div className="form-group">
                    <label>Current Password</label>
                    <input type="password" {...registerPassword('currentPassword', { required: 'Required' })} />
                    {passwordErrors.currentPassword && <span className="error">{passwordErrors.currentPassword.message}</span>}
                  </div>
                  <div className="form-group">
                    <label>New Password</label>
                    <input type="password" {...registerPassword('newPassword', { required: 'Required', minLength: 8 })} />
                    {passwordErrors.newPassword && <span className="error">{passwordErrors.newPassword.message}</span>}
                  </div>
                  <div className="form-group">
                    <label>Confirm New Password</label>
                    <input type="password" {...registerPassword('confirmPassword', { required: 'Required' })} />
                  </div>
                  <div className="form-actions">
                    <button type="button" onClick={() => { setIsChangingPassword(false); resetPassword(); }}>Cancel</button>
                    <button type="submit">Update Password</button>
                  </div>
                </form>
              )}
            </div>

            {/* Logout */}
            <div className="settings-section settings-section--danger">
              <button type="button" className="logout-btn" onClick={handleLogout}>
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>
                </svg>
                Log Out
              </button>
            </div>
          </div>
        </>,
        document.body
      )}
    </div>
  );
};