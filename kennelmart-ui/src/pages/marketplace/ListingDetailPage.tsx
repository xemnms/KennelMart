import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { listingService } from '../../services/listingService';
import { reviewService } from '../../services/reviewService';
import { reportService } from '../../services/reportService';
import { useCartStore } from '../../store/cartStore';
import { ReportModal } from '../../components/ReportModal';
import type { ProductListing } from '../../types/marketplace';
import type { Review } from '../../types/review';
import { getImageUrl } from '../../utils/imageUtils';
import './ListingDetail.css';

export const ListingDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const [listing, setListing] = useState<ProductListing | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [averageRating, setAverageRating] = useState<number>(0);
  const [reviewsLoading, setReviewsLoading] = useState(true);
  const [showReportListingModal, setShowReportListingModal] = useState(false);
  const [showReportSellerModal, setShowReportSellerModal] = useState(false);
  const addItem = useCartStore((state) => state.addItem);

  useEffect(() => {
    if (!id) return;
    listingService.getListingById(id)
      .then((listingData) => {
        setListing(listingData);
        if (listingData.sellerId) {
          return Promise.all([
            reviewService.getSellerReviews(listingData.sellerId, 0, 5),
            reviewService.getSellerAverageRating(listingData.sellerId)
          ]);
        } else {
          return Promise.all([{ content: [], totalPages: 0, totalElements: 0 }, 0]);
        }
      })
      .then(([reviewsData, avg]) => {
        setReviews(reviewsData.content);
        setAverageRating(avg);
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load listing');
      })
      .finally(() => {
        setLoading(false);
        setReviewsLoading(false);
      });
  }, [id]);

  const handleAddToCart = async () => {
    if (!listing) return;
    try {
      await addItem(listing.id, 1);
      alert('Added to cart!');
    } catch (err: unknown) {
      console.error('Add to cart error:', err);
      let errorMessage = 'Failed to add to cart';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      alert(errorMessage);
    }
  };

  const handleReportListing = async (reason: string) => {
    if (!listing) return;
    await reportService.submitReport({
      targetType: 'LISTING',
      targetId: listing.id,
      reason,
    });
    alert('Listing reported. Thank you for helping keep the community safe.');
  };

  const handleReportSeller = async (reason: string) => {
    if (!listing) return;
    await reportService.submitReport({
      targetType: 'USER',
      targetId: listing.sellerId,
      reason,
    });
    alert('Seller reported. Thank you for helping keep the community safe.');
  };

  if (loading) return <div className="loading">Loading...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!listing) return <div className="error">Listing not found</div>;

  return (
    <div className="detail-container">
      <div className="detail-images">
        <img src={getImageUrl(listing.imageUrls[0])} alt={listing.title} />
      </div>
      <div className="detail-info">
        <h1>{listing.title}</h1>
        <p className="price">₱{listing.price}</p>
        <p className="stock">Stock: {listing.stockQuantity}</p>
        <p className="seller">Sold by: {listing.sellerName}</p>
        <div className="seller-rating">
          {averageRating > 0 ? (
            <span>⭐ {averageRating.toFixed(1)} / 5</span>
          ) : (
            <span>No reviews yet</span>
          )}
          <Link to={`/reviews/seller/${listing.sellerId}`}>See all reviews</Link>
        </div>
        <p className="description">{listing.description}</p>
        <div className="action-buttons">
          <button onClick={handleAddToCart}>Add to Cart</button>
          <Link to={`/messages/${listing.sellerId}`} className="contact-btn">Contact Seller</Link>
          <button onClick={() => setShowReportListingModal(true)} className="report-btn">Report Listing</button>
          <button onClick={() => setShowReportSellerModal(true)} className="report-btn">Report Seller</button>
        </div>
      </div>

      {/* Reviews Section */}
      <div className="reviews-section">
        <h2>Customer Reviews</h2>
        {reviewsLoading ? (
          <div>Loading reviews...</div>
        ) : reviews.length === 0 ? (
          <p>No reviews yet.</p>
        ) : (
          <div className="reviews-list">
            {reviews.map(review => (
              <div key={review.id} className="review-card">
                <div className="review-header">
                  <strong>{review.buyerName}</strong>
                  <span className="rating">⭐ {review.rating}</span>
                </div>
                <p className="review-comment">{review.comment}</p>
                <div className="review-date">{new Date(review.createdAt).toLocaleDateString()}</div>
              </div>
            ))}
          </div>
        )}
      </div>

      <ReportModal
        isOpen={showReportListingModal}
        onClose={() => setShowReportListingModal(false)}
        onSubmit={handleReportListing}
        targetType="LISTING"
      />
      <ReportModal
        isOpen={showReportSellerModal}
        onClose={() => setShowReportSellerModal(false)}
        onSubmit={handleReportSeller}
        targetType="USER"
      />
    </div>
  );
};