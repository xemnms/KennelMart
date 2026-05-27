import type { PageResponse, ProductListing } from '../types/marketplace.tsx'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export async function fetchMarketplaceListings(): Promise<ProductListing[]> {
  const response = await fetch(`${API_BASE_URL}/api/listings`)

  if (!response.ok) {
    throw new Error('Unable to load marketplace listings')
  }

  const data = (await response.json()) as PageResponse<ProductListing>
  return data.content ?? []
}
