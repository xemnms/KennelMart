/* eslint-disable react-hooks/set-state-in-effect */
import { useEffect, useState, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { userService } from '../../services/userService';
import { listingService } from '../../services/listingService';
import type { UserPublicProfile } from '../../types/auth';
import type { ProductListing } from '../../types/marketplace';
import './UserProfile.css';

export const UserProfilePage = () => {
  const { userId } = useParams<{ userId: string }>();
  const [profile, setProfile] = useState<UserPublicProfile | null>(null);
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [listingsLoading, setListingsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchProfile = useCallback(async () => {
    if (!userId) return;
    try {
      const data = await userService.getUserPublicProfile(userId);
      setProfile(data);
    } catch (err) {
      console.error('Error fetching profile:', err);
      setError('User not found or inactive');
    } finally {
      setLoading(false);
    }
  }, [userId]);

  const fetchListings = useCallback(async () => {
    if (!userId) return;
    try {
      const data = await listingService.getListingsBySeller(userId, 0, 12);
      setListings(data.content);
    } catch (err) {
      console.error('Error fetching listings:', err);
    } finally {
      setListingsLoading(false);
    }
  }, [userId]);

  useEffect(() => {
    if (userId) {
      fetchProfile();
      fetchListings();
    }
  }, [userId, fetchProfile, fetchListings]);

  if (loading) return <div className="loading">Loading profile...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!profile) return null;

  return (
    <div className="user-profile">
      <div className="profile-header">
        <div className="profile-avatar">
          {profile.profileImage ? (
            <img src={profile.profileImage} alt={profile.name} />
          ) : (
            <div className="avatar-placeholder-large">👤</div>
          )}
        </div>
        <div className="profile-info">
          <h1>{profile.name}</h1>
          <p className="role">{profile.role}</p>
          {profile.averageRating && (
            <p className="rating">⭐ {profile.averageRating.toFixed(1)}</p>
          )}
          <p className="joined">Member since {new Date(profile.joinedAt).toLocaleDateString()}</p>
        </div>
      </div>

      <div className="profile-listings">
        <h2>Listings by {profile.name}</h2>
        {listingsLoading ? (
          <div className="loading">Loading listings...</div>
        ) : listings.length === 0 ? (
          <div className="empty-state">This user has no active listings.</div>
        ) : (
          <div className="product-grid">
            {listings.map(listing => (
              <div key={listing.id} className="product-card">
                <Link to={`/listings/${listing.id}`}>
                  <div className="product-image">
                    <img src={listing.imageUrls[0] || '/placeholder.png'} alt={listing.title} />
                  </div>
                  <div className="product-info">
                    <h3>{listing.title}</h3>
                    <p className="price">₱{listing.price}</p>
                    <span className="category-badge">{listing.category}</span>
                  </div>
                </Link>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};