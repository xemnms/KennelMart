import { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuthStore } from '../../store/authStore';
import { authService } from '../../services/authService';
import { getImageUrl } from '../../utils/imageUtils';
import type { UpdateProfileRequest, ChangePasswordRequest } from '../../types/auth';
import './Profile.css';

export const ProfilePage = () => {
  const navigate = useNavigate();
  const { user, fetchUser } = useAuthStore();
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
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
      <div className="profile-card">
        <div className="profile-header">
          <div className="avatar-large">
            <img src={getImageUrl(user.profileImage)} alt={user.name} />
            <button
              type="button"
              className="upload-avatar-btn"
              onClick={() => fileInputRef.current?.click()}
              disabled={uploadingAvatar}
            >
              {uploadingAvatar ? 'Uploading...' : '📷'}
            </button>
            <input
              type="file"
              ref={fileInputRef}
              style={{ display: 'none' }}
              accept="image/*"
              onChange={handleAvatarChange}
            />
          </div>
          <h1>{user.name}</h1>
          <p className="email">{user.email}</p>
          <div className="badges">
            <span className={`badge role-${roleClass}`}>{user.role}</span>
            <span className={`badge status-${verificationClass}`}>{user.verificationStatus}</span>
            {user.averageRating !== undefined && user.averageRating > 0 && (
              <span className="badge rating">★ {user.averageRating.toFixed(1)}</span>
            )}
          </div>
        </div>

        {message && <div className={`profile-message ${message.type}`}>{message.text}</div>}

        <div className="profile-section">
          <div className="section-header">
            <h2>Personal Information</h2>
            {!isEditing && (
              <button type="button" onClick={() => setIsEditing(true)} className="edit-btn">
                Edit
              </button>
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
                <button type="button" onClick={() => setIsEditing(false)}>
                  Cancel
                </button>
                <button type="submit">Save Changes</button>
              </div>
            </form>
          ) : (
            <div className="profile-info">
              <p><strong>Name:</strong> {user.name}</p>
              <p><strong>Student/Faculty ID:</strong> {user.studentOrFacultyId}</p>
            </div>
          )}
        </div>

        <div className="profile-section">
          <div className="section-header">
            <h2>Security</h2>
            {!isChangingPassword && (
              <button type="button" onClick={() => setIsChangingPassword(true)} className="edit-btn">
                Change Password
              </button>
            )}
          </div>
          {isChangingPassword && (
            <form onSubmit={handlePasswordSubmit(onPasswordChange)} className="profile-form">
              <div className="form-group">
                <label>Current Password</label>
                <input type="password" {...registerPassword('currentPassword', { required: 'Current password required' })} />
                {passwordErrors.currentPassword && <span className="error">{passwordErrors.currentPassword.message}</span>}
              </div>
              <div className="form-group">
                <label>New Password</label>
                <input type="password" {...registerPassword('newPassword', { required: 'New password required', minLength: 8 })} />
                {passwordErrors.newPassword && <span className="error">{passwordErrors.newPassword.message}</span>}
              </div>
              <div className="form-group">
                <label>Confirm New Password</label>
                <input type="password" {...registerPassword('confirmPassword', { required: 'Please confirm password' })} />
              </div>
              <div className="form-actions">
                <button type="button" onClick={() => setIsChangingPassword(false)}>
                  Cancel
                </button>
                <button type="submit">Update Password</button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};