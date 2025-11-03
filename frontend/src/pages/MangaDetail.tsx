import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { mangaApi, Manga, Volume } from '../services/api'
import { useCartStore } from '../store/cartStore'
import './MangaDetail.css'

export default function MangaDetail() {
  const { id } = useParams<{ id: string }>()
  const [manga, setManga] = useState<Manga | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { addItem } = useCartStore()

  useEffect(() => {
    loadManga()
  }, [id])

  const loadManga = async () => {
    try {
      setLoading(true)
      const response = await mangaApi.getById(Number(id))
      setManga(response.data)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load manga details')
    } finally {
      setLoading(false)
    }
  }

  const handleAddToCart = (volume: Volume) => {
    if (volume.quantity === 0) {
      setError('This volume is out of stock')
      return
    }

    addItem({
      volumeId: volume.id,
      mangaTitle: manga!.title,
      volumeNumber: volume.volumeNumber,
      price: volume.price,
      quantity: 1,
      maxQuantity: volume.quantity,
    })
    setSuccess(`Volume ${volume.volumeNumber} added to cart!`)
    setTimeout(() => setSuccess(''), 3000)
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
      </div>
    )
  }

  if (error || !manga) {
    return (
      <div className="container" style={{ padding: '40px 20px' }}>
        <div className="alert alert-error">{error || 'Manga not found'}</div>
      </div>
    )
  }

  return (
    <div className="manga-detail-page">
      <div className="container">
        {success && <div className="alert alert-success">{success}</div>}

        <div className="manga-detail-header">
          <div className="manga-detail-image">
            <div className="manga-detail-placeholder">
              <span className="manga-icon">📖</span>
            </div>
          </div>

          <div className="manga-detail-info">
            <h1 className="manga-detail-title">{manga.title}</h1>
            <p className="manga-detail-author">by {manga.author}</p>

            <div className="manga-detail-meta">
              <span className={`badge badge-${manga.status === 'COMPLETED' ? 'success' : 'info'}`}>
                {manga.status}
              </span>
              <span className="manga-detail-genre">{manga.genres?.replace(/_/g, ' ')}</span>
              <span className="manga-detail-rating">⭐ {manga.rating?.toFixed(1) || 'N/A'}</span>
            </div>

            <p className="manga-detail-description">{manga.description}</p>

            {manga.keywords && (
              <div className="manga-keywords">
                <strong>Keywords:</strong> {manga.keywords}
              </div>
            )}
          </div>
        </div>

        <div className="volumes-section">
          <h2 className="section-title">Available Volumes</h2>

          {!manga.volumes || manga.volumes.length === 0 ? (
            <p className="empty-state">No volumes available yet</p>
          ) : (
            <div className="volumes-grid">
              {manga.volumes.map((volume) => (
                <div key={volume.id} className="volume-card">
                  <div className="volume-header">
                    <h3 className="volume-number">Volume {volume.volumeNumber}</h3>
                    <span className="volume-price">R$ {volume.price.toFixed(2)}</span>
                  </div>

                  <div className="volume-info">
                    <p>
                      <strong>Chapters:</strong> {volume.chapters}
                    </p>
                    <p>
                      <strong>Release:</strong>{' '}
                      {new Date(volume.releaseDate).toLocaleDateString()}
                    </p>
                    <p>
                      <strong>In Stock:</strong> {volume.quantity}
                    </p>
                  </div>

                  <button
                    onClick={() => handleAddToCart(volume)}
                    disabled={volume.quantity === 0}
                    className={`btn ${volume.quantity === 0 ? 'btn-outline' : 'btn-primary'} btn-block`}
                  >
                    {volume.quantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
