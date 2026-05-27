import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { listingService } from '../../services/listingService';
import type { ProductListing } from '../../types/marketplace';
import './MyListings.css';
import { useAuthStore } from '../../store/authStore';

export const MyListingsPage = () => {
  const [listings, setListings] = useState<ProductListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);
  const navigate = useNavigate();

  const fetchListings = async () => {
    setLoading(true);
    try {
      const data = await listingService.getMyListings(0, 20);
      setListings(data.content);
    } catch {
      setError('Failed to load your listings');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchListings();
  }, []);

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this listing?')) return;
    setDeletingId(id);
    try {
      // Optionally, refresh the token from store to ensure it's valid
      const token = useAuthStore.getState().token;
      if (!token) throw new Error('Not authenticated');
      await listingService.deleteListing(id);
      setListings(listings.filter(l => l.id !== id));
    } catch (err) {
      console.error(err);
      alert('Failed to delete listing');
    } finally {
      setDeletingId(null);
    }
  };

  if (loading) return <div className="loading">Loading your listings...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="my-listings-container">
      <div className="my-listings-header">
        <h1>My Listings</h1>
        <Link to="/seller/listings/new" className="create-btn">Create New Listing</Link>
      </div>
      {listings.length === 0 ? (
        <div className="empty-state">
          <p>You haven't listed any items yet.</p>
          <Link to="/seller/listings/new">Create your first listing</Link>
        </div>
      ) : (
        <div className="listings-table">
          <table>
            <thead>
              <tr>
                <th>Image</th>
                <th>Title</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {listings.map((listing) => (
                <tr key={listing.id}>
                  <td className="image-cell">
                    <img src={listing.imageUrls[0] || 'https://via.placeholder.com/50x50?text=No+Image'} alt={listing.title} />
                  </td>
                  <td>{listing.title}</td>
                  <td>₱{listing.price}</td>
                  <td>{listing.stockQuantity}</td>
                  <td>
                    <span className={`status-badge ${listing.status.toLowerCase()}`}>
                      {listing.status}
                    </span>
                  </td>
                  <td className="actions">
                    <button onClick={() => navigate(`/listings/${listing.id}`)} className="view-btn">View</button>
                    <button onClick={() => navigate(`/seller/listings/edit/${listing.id}`)} className="edit-btn">Edit</button>
                    <button onClick={() => handleDelete(listing.id)} disabled={deletingId === listing.id} className="delete-btn">
                      {deletingId === listing.id ? 'Deleting...' : 'Delete'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};