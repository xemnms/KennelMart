import { useEffect, useMemo, useState } from 'react'
import './App.css'
import { fetchMarketplaceListings } from './services/kennelMartApi.tsx'
import type { ListingCategory, ProductListing, UiOrder } from './types/marketplace.tsx'

const categories: Array<ListingCategory | 'ALL'> = [
  'ALL',
  'BOOKS',
  'FOOD_BEVERAGE',
  'ELECTRONICS',
  'SERVICES',
  'STATIONERY',
]

const previewListings: ProductListing[] = [
  {
    id: 'listing-1',
    title: 'Calculus reviewer bundle',
    description: 'Printed reviewers, solved exercises, and formula cards for finals week.',
    price: 340,
    stockQuantity: 6,
    status: 'ACTIVE',
    category: 'BOOKS',
    sellerName: 'Mika Reyes',
    imageUrls: [],
  },
  {
    id: 'listing-2',
    title: 'Iced coffee campus pack',
    description: 'Pre-order bottled coffee for pickup near the cafeteria.',
    price: 85,
    stockQuantity: 18,
    status: 'ACTIVE',
    category: 'FOOD_BEVERAGE',
    sellerName: 'Brew by Niko',
    imageUrls: [],
  },
  {
    id: 'listing-3',
    title: 'USB-C hub for laptops',
    description: 'Compact 6-in-1 adapter for class presentations and lab work.',
    price: 720,
    stockQuantity: 4,
    status: 'ACTIVE',
    category: 'ELECTRONICS',
    sellerName: 'Tech Desk',
    imageUrls: [],
  },
  {
    id: 'listing-4',
    title: 'Poster layout service',
    description: 'Academic poster and organization announcement layout package.',
    price: 250,
    stockQuantity: 10,
    status: 'PENDING_APPROVAL',
    category: 'SERVICES',
    sellerName: 'Studio NU',
    imageUrls: [],
  },
]

const previewOrders: UiOrder[] = [
  {
    id: 'ORD-1042',
    item: 'Calculus reviewer bundle',
    buyer: 'Jared Tan',
    seller: 'Mika Reyes',
    status: 'PREPARING',
    totalPrice: 340,
    paymentMethod: 'CAMPUS_MEETUP',
  },
  {
    id: 'ORD-1041',
    item: 'Iced coffee campus pack',
    buyer: 'Anne Cruz',
    seller: 'Brew by Niko',
    status: 'ACCEPTED',
    totalPrice: 170,
    paymentMethod: 'GCASH',
  },
  {
    id: 'ORD-1039',
    item: 'USB-C hub for laptops',
    buyer: 'Paolo Lim',
    seller: 'Tech Desk',
    status: 'SHIPPED',
    totalPrice: 720,
    paymentMethod: 'COD',
  },
]

function formatMoney(value: number) {
  return new Intl.NumberFormat('en-PH', {
    style: 'currency',
    currency: 'PHP',
    maximumFractionDigits: 0,
  }).format(value)
}

function formatLabel(value: string) {
  return value
    .toLowerCase()
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}

function App() {
  const [activeCategory, setActiveCategory] = useState<ListingCategory | 'ALL'>('ALL')
  const [searchTerm, setSearchTerm] = useState('')
  const [listings, setListings] = useState<ProductListing[]>(previewListings)
  const [dataMode, setDataMode] = useState<'backend' | 'preview'>('preview')

  useEffect(() => {
    let isMounted = true

    fetchMarketplaceListings()
      .then((backendListings) => {
        if (!isMounted || backendListings.length === 0) {
          return
        }

        setListings(backendListings)
        setDataMode('backend')
      })
      .catch(() => {
        setDataMode('preview')
      })

    return () => {
      isMounted = false
    }
  }, [])

  const visibleListings = useMemo(() => {
    return listings.filter((listing) => {
      const matchesCategory = activeCategory === 'ALL' || listing.category === activeCategory
      const searchable = `${listing.title} ${listing.description} ${listing.sellerName}`.toLowerCase()

      return matchesCategory && searchable.includes(searchTerm.toLowerCase())
    })
  }, [activeCategory, listings, searchTerm])

  const activeListingCount = listings.filter((listing) => listing.status === 'ACTIVE').length
  const pendingListingCount = listings.filter((listing) => listing.status !== 'ACTIVE').length
  const cartPreviewTotal = previewListings.slice(0, 2).reduce((total, listing) => total + listing.price, 0)

  return (
    <main className="app-shell">
      <aside className="sidebar" aria-label="KennelMart navigation">
        <div className="brand-block">
          <span className="brand-mark">KM</span>
          <div>
            <p className="eyebrow">NU Laguna</p>
            <h1>KennelMart</h1>
          </div>
        </div>

        <nav className="nav-list">
          <a className="nav-item active" href="#marketplace">Marketplace</a>
          <a className="nav-item" href="#cart">Cart</a>
          <a className="nav-item" href="#orders">Orders</a>
          <a className="nav-item" href="#seller">Seller</a>
          <a className="nav-item" href="#admin">Admin</a>
        </nav>

        <section className="identity-card" aria-label="Verified identity">
          <p className="eyebrow">Verified Identity</p>
          <strong>Juan Dela Cruz</strong>
          <span>2024-NU-001 / Active account</span>
        </section>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">Campus-only marketplace</p>
            <h2>Browse, sell, checkout, and moderate from one focused interface.</h2>
          </div>
          <div className="topbar-actions">
            <span className={`data-pill ${dataMode}`}>
              {dataMode === 'backend' ? 'Backend data' : 'Preview data'}
            </span>
            <button className="primary-action" type="button">List item</button>
          </div>
        </header>

        <section className="hero-band">
          <div className="hero-copy">
            <p className="eyebrow">MVP workflow</p>
            <h2>Verified NU users can buy and sell with clear order controls.</h2>
            <p>
              The interface follows the Spring Boot modules for auth, identity verification,
              listings, cart, checkout, seller orders, and admin moderation.
            </p>
          </div>
          <div className="stats-grid" aria-label="Marketplace statistics">
            <div>
              <strong>{activeListingCount}</strong>
              <span>active listings</span>
            </div>
            <div>
              <strong>{previewOrders.length}</strong>
              <span>open orders</span>
            </div>
            <div>
              <strong>{pendingListingCount}</strong>
              <span>needs review</span>
            </div>
          </div>
        </section>

        <section id="marketplace" className="content-section">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Marketplace</p>
              <h2>Available listings</h2>
            </div>
            <label className="search-field">
              <span>Search</span>
              <input
                onChange={(event) => setSearchTerm(event.target.value)}
                placeholder="Books, food, gadgets, services..."
                type="search"
                value={searchTerm}
              />
            </label>
          </div>

          <div className="segmented-control" aria-label="Filter listings by category">
            {categories.map((category) => (
              <button
                className={category === activeCategory ? 'selected' : ''}
                key={category}
                onClick={() => setActiveCategory(category)}
                type="button"
              >
                {category === 'ALL' ? 'All' : formatLabel(category)}
              </button>
            ))}
          </div>

          <div className="product-grid">
            {visibleListings.map((listing) => (
              <article className="product-card" key={listing.id}>
                <div className={`product-art ${listing.category.toLowerCase()}`}>
                  <span>{formatLabel(listing.category)}</span>
                </div>
                <div className="product-body">
                  <div className="product-title-row">
                    <h3>{listing.title}</h3>
                    <strong>{formatMoney(listing.price)}</strong>
                  </div>
                  <p>{listing.description}</p>
                  <div className="meta-row">
                    <span>{listing.sellerName}</span>
                    <span>{listing.stockQuantity} in stock</span>
                    <span className={listing.status === 'ACTIVE' ? 'good' : 'watch'}>
                      {formatLabel(listing.status)}
                    </span>
                  </div>
                  <button className="secondary-action" type="button">Add to cart</button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="two-column-flow">
          <div id="cart" className="panel">
            <div className="section-heading compact">
              <div>
                <p className="eyebrow">Buyer cart</p>
                <h2>Checkout preview</h2>
              </div>
              <span className="pill">Campus meetup</span>
            </div>
            <div className="checkout-list">
              {previewListings.slice(0, 2).map((listing) => (
                <div key={listing.id}>
                  <span>{listing.title}</span>
                  <strong>{formatMoney(listing.price)}</strong>
                </div>
              ))}
            </div>
            <div className="checkout-total">
              <span>Total</span>
              <strong>{formatMoney(cartPreviewTotal)}</strong>
            </div>
            <button className="primary-action full-width" type="button">Place order</button>
          </div>

          <div id="orders" className="panel">
            <div className="section-heading compact">
              <div>
                <p className="eyebrow">Seller dashboard</p>
                <h2>Order tracking</h2>
              </div>
              <span className="pill">Live queue</span>
            </div>
            <div className="order-table" role="table" aria-label="Seller order queue">
              {previewOrders.map((order) => (
                <div className="order-row" role="row" key={order.id}>
                  <div>
                    <strong>{order.id}</strong>
                    <span>{order.item}</span>
                  </div>
                  <span>{order.buyer}</span>
                  <span className="status-chip">{formatLabel(order.status)}</span>
                  <strong>{formatMoney(order.totalPrice)}</strong>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section id="seller" className="seller-strip">
          <div>
            <p className="eyebrow">Listing management</p>
            <h2>Create and monitor products without leaving the marketplace.</h2>
          </div>
          <div className="seller-actions">
            <button className="secondary-action" type="button">Edit stock</button>
            <button className="primary-action" type="button">Create listing</button>
          </div>
        </section>

        <section id="admin" className="admin-grid">
          <article className="admin-card">
            <p className="eyebrow">Verification</p>
            <h3>5 pending identities</h3>
            <span>Approve or reject NU student and faculty accounts.</span>
          </article>
          <article className="admin-card">
            <p className="eyebrow">Reports</p>
            <h3>3 open reports</h3>
            <span>Review listing and user reports from the moderation queue.</span>
          </article>
          <article className="admin-card">
            <p className="eyebrow">Listings</p>
            <h3>{pendingListingCount} awaiting approval</h3>
            <span>Check prohibited items, price accuracy, and seller details.</span>
          </article>
        </section>
      </section>
    </main>
  )
}

export default App
