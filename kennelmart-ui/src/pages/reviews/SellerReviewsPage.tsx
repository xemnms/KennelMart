import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { reviewService } from '../../services/reviewService';
import type { Review } from '../../types/review';
import './SellerReviews.css';

export const SellerReviewsPage = () => {
  const { sellerId } = useParams<{ sellerId: string }>();
  const [reviews, setReviews] = useState<Review[]>([]);
  const [averageRating, setAverageRating] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const fetchReviews = async () => {
    if (!sellerId) return;
    setLoading(true);
    try {
      const [reviewsData, avg] = await Promise.all([
        reviewService.getSellerReviews(sellerId, page, 10),
        reviewService.getSellerAverageRating(sellerId)
      ]);
      setReviews(reviewsData.content);
      setTotalPages(reviewsData.totalPages);
      setAverageRating(avg);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchReviews();
  }, [sellerId, page]);

  if (loading) return <div className="loading">Loading reviews...</div>;

  return (
    <div className="seller-reviews-container">
      <div className="seller-reviews-header">
        <h1>Seller Reviews</h1>
        <div className="average-rating">
          Average Rating: ⭐ {averageRating.toFixed(1)} / 5
        </div>
      </div>
      {reviews.length === 0 ? (
        <div className="empty-reviews">No reviews yet.</div>
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
      {totalPages > 1 && (
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, i) => (
            <button key={i} onClick={() => setPage(i)} className={page === i ? 'active' : ''}>
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
};