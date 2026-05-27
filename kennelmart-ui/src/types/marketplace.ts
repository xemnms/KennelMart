export interface ProductListing {
  id: string;
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
  status: 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' | 'PENDING_APPROVAL' | 'DELETED';
  category: string;
  sellerId: string;
  sellerName: string;
  imageUrls: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ListingFilters {
  keyword?: string;
  category?: string;
  page?: number;
  size?: number;
  sort?: string;
}
export interface CreateListingRequest {
  title: string;
  description: string;
  price: number;
  stockQuantity: number;
  category: string;
  imageUrls: string[];
}