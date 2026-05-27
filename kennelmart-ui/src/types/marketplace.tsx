export type ListingCategory =
  | 'ELECTRONICS'
  | 'BOOKS'
  | 'CLOTHING'
  | 'FURNITURE'
  | 'SERVICES'
  | 'FOOD_BEVERAGE'
  | 'SPORTS_RECREATION'
  | 'STATIONERY'
  | 'OTHERS'

export type ProductStatus = 'ACTIVE' | 'INACTIVE' | 'SOLD_OUT' | 'PENDING_APPROVAL'

export type ProductListing = {
  id: string
  title: string
  description: string
  price: number
  stockQuantity: number
  status: ProductStatus
  category: ListingCategory
  sellerName: string
  imageUrls: string[]
}

export type PageResponse<T> = {
  content?: T[]
}

export type UiOrder = {
  id: string
  item: string
  buyer: string
  seller: string
  status: 'ORDER_RECEIVED' | 'ACCEPTED' | 'PREPARING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
  totalPrice: number
  paymentMethod: 'COD' | 'GCASH' | 'CAMPUS_MEETUP'
}
