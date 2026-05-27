import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { listingService } from '../../services/listingService';
import type { CreateListingRequest } from '../../types/marketplace';
import './CreateListing.css';

export const EditListingPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [imageUrls, setImageUrls] = useState<string[]>(['']);

  const { register, handleSubmit, setValue, formState: { errors } } = useForm<CreateListingRequest>({
    defaultValues: {
      title: '',
      description: '',
      price: 0,
      stockQuantity: 1,
      category: 'ELECTRONICS',
      imageUrls: [''],
    },
  });

  useEffect(() => {
    if (!id) return;
    listingService.getListingById(id)
      .then((listing) => {
        setValue('title', listing.title);
        setValue('description', listing.description);
        setValue('price', listing.price);
        setValue('stockQuantity', listing.stockQuantity);
        setValue('category', listing.category);
        const urls = listing.imageUrls.length ? listing.imageUrls : [''];
        setImageUrls(urls);
        setValue('imageUrls', urls);
        setIsLoading(false);
      })
      .catch((err: unknown) => {
        let errorMessage = 'Failed to load listing';
        if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
          errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
        }
        setError(errorMessage);
        console.error(err);
        setIsLoading(false);
      });
  }, [id, setValue]);

  const addImageUrl = () => {
    setImageUrls([...imageUrls, '']);
  };

  const removeImageUrl = (index: number) => {
    const newUrls = imageUrls.filter((_, i) => i !== index);
    setImageUrls(newUrls);
    setValue('imageUrls', newUrls);
  };

  const updateImageUrl = (index: number, value: string) => {
    const newUrls = [...imageUrls];
    newUrls[index] = value;
    setImageUrls(newUrls);
    setValue('imageUrls', newUrls);
  };

  const onSubmit = async (data: CreateListingRequest) => {
    setIsSubmitting(true);
    setError(null);
    data.imageUrls = imageUrls.filter(url => url.trim() !== '');
    try {
      await listingService.updateListing(id!, data);
      navigate('/my-listings');
    } catch (err: unknown) {
      let errorMessage = 'Failed to update listing';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setError(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) return <div className="loading">Loading listing...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="create-listing-container">
      <div className="create-listing-card">
        <h1>Edit Listing</h1>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="form-group">
            <label>Title *</label>
            <input {...register('title', { required: 'Title is required' })} />
            {errors.title && <span className="error">{errors.title.message}</span>}
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea {...register('description')} rows={4} />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Price (₱) *</label>
              <input type="number" step="0.01" {...register('price', { required: 'Price is required', min: 0.01 })} />
              {errors.price && <span className="error">{errors.price.message}</span>}
            </div>
            <div className="form-group">
              <label>Stock Quantity *</label>
              <input type="number" {...register('stockQuantity', { required: 'Stock is required', min: 0 })} />
              {errors.stockQuantity && <span className="error">{errors.stockQuantity.message}</span>}
            </div>
          </div>
          <div className="form-group">
            <label>Category *</label>
            <select {...register('category', { required: 'Category is required' })}>
              <option value="ELECTRONICS">Electronics</option>
              <option value="BOOKS">Books</option>
              <option value="CLOTHING">Clothing</option>
              <option value="FURNITURE">Furniture</option>
              <option value="SERVICES">Services</option>
              <option value="FOOD_BEVERAGE">Food & Beverage</option>
              <option value="SPORTS_RECREATION">Sports & Recreation</option>
              <option value="STATIONERY">Stationery</option>
              <option value="OTHERS">Others</option>
            </select>
          </div>
          <div className="form-group">
            <label>Image URLs (optional)</label>
            {imageUrls.map((url, index) => (
              <div key={index} className="image-url-row">
                <input value={url} onChange={(e) => updateImageUrl(index, e.target.value)} placeholder="https://example.com/image.jpg" />
                <button type="button" onClick={() => removeImageUrl(index)}>Remove</button>
              </div>
            ))}
            <button type="button" onClick={addImageUrl} className="add-url-btn">+ Add another image URL</button>
          </div>
          <div className="form-actions">
            <button type="button" onClick={() => navigate('/my-listings')}>Cancel</button>
            <button type="submit" disabled={isSubmitting}>{isSubmitting ? 'Saving...' : 'Save Changes'}</button>
          </div>
        </form>
      </div>
    </div>
  );
};