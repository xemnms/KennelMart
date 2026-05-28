import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { listingService } from '../../services/listingService';
import { uploadImage } from '../../services/uploadService';
import type { CreateListingRequest } from '../../types/marketplace';
import './CreateListing.css';

export const CreateListingPage = () => {
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [imageFiles, setImageFiles] = useState<File[]>([]);
  const [imagePreviews, setImagePreviews] = useState<string[]>([]);
  const [uploadingImages, setUploadingImages] = useState(false);

  const { register, handleSubmit, formState: { errors } } = useForm<CreateListingRequest>({
    defaultValues: {
      title: '',
      description: '',
      price: 0,
      stockQuantity: 1,
      category: 'ELECTRONICS',
      imageUrls: [],
    },
  });

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const files = Array.from(e.target.files);
      setImageFiles(prev => [...prev, ...files]);
      
      // Create previews
      files.forEach(file => {
        const reader = new FileReader();
        reader.onload = (event) => {
          setImagePreviews(prev => [...prev, event.target?.result as string]);
        };
        reader.readAsDataURL(file);
      });
    }
  };

  const removeImage = (index: number) => {
    setImageFiles(prev => prev.filter((_, i) => i !== index));
    setImagePreviews(prev => prev.filter((_, i) => i !== index));
  };

  const onSubmit = async (data: CreateListingRequest) => {
    if (imageFiles.length === 0) {
      setError('Please upload at least one image');
      return;
    }

    setIsSubmitting(true);
    setUploadingImages(true);
    setError(null);

    try {
      // Upload all images
      const uploadedUrls = await Promise.all(
        imageFiles.map(file => uploadImage(file))
      );
      
      // Create listing with uploaded image URLs
      data.imageUrls = uploadedUrls;
      await listingService.createListing(data);
      navigate('/my-listings');
    } catch (err: unknown) {
      let errorMessage = 'Failed to create listing';
      if (err && typeof err === 'object' && 'response' in err && err.response && typeof err.response === 'object' && 'data' in err.response) {
        errorMessage = (err.response as { data?: { message?: string } }).data?.message || errorMessage;
      }
      setError(errorMessage);
    } finally {
      setIsSubmitting(false);
      setUploadingImages(false);
    }
  };

  return (
    <div className="create-listing-container">
      <div className="create-listing-card">
        <h1>Create New Listing</h1>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit(onSubmit)}>
          {/* existing form fields remain the same */}
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
              <input
                type="number"
                step="0.01"
                {...register('price', { required: 'Price is required', min: 0.01 })}
              />
              {errors.price && <span className="error">{errors.price.message}</span>}
            </div>
            <div className="form-group">
              <label>Stock Quantity *</label>
              <input
                type="number"
                {...register('stockQuantity', { required: 'Stock is required', min: 0 })}
              />
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
            <label>Product Images * (Max 5 images)</label>
            <input
              type="file"
              accept="image/*"
              multiple
              onChange={handleImageChange}
              disabled={imageFiles.length >= 5}
            />
            <div className="image-previews">
              {imagePreviews.map((preview, index) => (
                <div key={index} className="image-preview">
                  <img src={preview} alt={`Preview ${index + 1}`} />
                  <button type="button" onClick={() => removeImage(index)}>Remove</button>
                </div>
              ))}
            </div>
            {imageFiles.length === 0 && <span className="error">Please upload at least one image</span>}
          </div>

          <div className="form-actions">
            <button type="button" onClick={() => navigate(-1)}>Cancel</button>
            <button type="submit" disabled={isSubmitting || uploadingImages || imageFiles.length === 0}>
              {uploadingImages ? 'Uploading images...' : isSubmitting ? 'Creating...' : 'Create Listing'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};