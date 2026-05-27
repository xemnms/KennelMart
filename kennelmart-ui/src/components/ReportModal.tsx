import { useState } from 'react';
import './ReportModal.css';

interface ReportModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (reason: string) => Promise<void>;
  targetType: string;
}

export const ReportModal = ({ isOpen, onClose, onSubmit, targetType }: ReportModalProps) => {
  const [reason, setReason] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!reason.trim()) {
      setError('Please provide a reason');
      return;
    }
    setIsSubmitting(true);
    setError(null);
    try {
      await onSubmit(reason);
      onClose();
    } catch (err: unknown) {
      let errorMessage = 'Failed to submit report';
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
        <h2>Report {targetType.toLowerCase()}</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Reason for reporting</label>
            <textarea
              rows={5}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Please describe the issue..."
              required
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <div className="modal-actions">
            <button type="button" onClick={onClose}>Cancel</button>
            <button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Submitting...' : 'Submit Report'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};