import { useState } from 'react';
import './ReviewModal.css';

interface ReviewModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (rating: number, comment: string) => Promise<void>;
}

export const ReviewModal = ({ isOpen, onClose, onSubmit }: ReviewModalProps) => {
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    try {
      await onSubmit(rating, comment);
      onClose();
    } catch (err: unknown) {
      let errorMessage = 'Failed to submit review';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Write a Review</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Rating</label>
            <select value={rating} onChange={(e) => setRating(Number(e.target.value))}>
              <option value={1}>⭐ 1 – Poor</option>
              <option value={2}>⭐⭐ 2 – Fair</option>
              <option value={3}>⭐⭐⭐ 3 – Good</option>
              <option value={4}>⭐⭐⭐⭐ 4 – Very Good</option>
              <option value={5}>⭐⭐⭐⭐⭐ 5 – Excellent</option>
            </select>
          </div>
          <div className="form-group">
            <label>Comment (optional)</label>
            <textarea
              rows={4}
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Share your experience with the seller..."
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <div className="modal-actions">
            <button type="button" onClick={onClose}>Cancel</button>
            <button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Submitting...' : 'Submit Review'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};