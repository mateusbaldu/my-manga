import { useState } from 'react'
import { mangaApi, volumeApi } from '../services/api'
import './AdminDashboard.css'

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState<'manga' | 'volume'>('manga')
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const [mangaForm, setMangaForm] = useState({
    title: '',
    author: '',
    description: '',
    rating: '',
    status: 'RELEASING',
    genres: 'ACTION',
    keywords: '',
  })

  const [volumeForm, setVolumeForm] = useState({
    mangaId: '',
    volumeNumber: '',
    price: '',
    chapters: '',
    releaseDate: '',
    quantity: '',
  })

  const handleMangaSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      await mangaApi.create({
        ...mangaForm,
        rating: parseFloat(mangaForm.rating),
      })
      setSuccess('Manga created successfully!')
      setMangaForm({
        title: '',
        author: '',
        description: '',
        rating: '',
        status: 'RELEASING',
        genres: 'ACTION',
        keywords: '',
      })
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create manga')
    } finally {
      setLoading(false)
    }
  }

  const handleVolumeSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      await volumeApi.create(parseInt(volumeForm.mangaId), [
        {
          volumeNumber: parseInt(volumeForm.volumeNumber),
          price: parseFloat(volumeForm.price),
          chapters: volumeForm.chapters,
          releaseDate: volumeForm.releaseDate,
          quantity: parseInt(volumeForm.quantity),
        },
      ])
      setSuccess('Volume added successfully!')
      setVolumeForm({
        mangaId: '',
        volumeNumber: '',
        price: '',
        chapters: '',
        releaseDate: '',
        quantity: '',
      })
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add volume')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="admin-page">
      <div className="container">
        <h1 className="page-title">Admin Dashboard</h1>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="admin-tabs">
          <button
            onClick={() => setActiveTab('manga')}
            className={`tab-button ${activeTab === 'manga' ? 'active' : ''}`}
          >
            Add Manga
          </button>
          <button
            onClick={() => setActiveTab('volume')}
            className={`tab-button ${activeTab === 'volume' ? 'active' : ''}`}
          >
            Add Volume
          </button>
        </div>

        {activeTab === 'manga' && (
          <div className="admin-section">
            <h2 className="section-title">Create New Manga</h2>
            <form onSubmit={handleMangaSubmit} className="admin-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="title">Title</label>
                  <input
                    id="title"
                    type="text"
                    value={mangaForm.title}
                    onChange={(e) => setMangaForm({ ...mangaForm, title: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="author">Author</label>
                  <input
                    id="author"
                    type="text"
                    value={mangaForm.author}
                    onChange={(e) => setMangaForm({ ...mangaForm, author: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  value={mangaForm.description}
                  onChange={(e) => setMangaForm({ ...mangaForm, description: e.target.value })}
                  rows={4}
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="rating">Rating (0-10)</label>
                  <input
                    id="rating"
                    type="number"
                    step="0.1"
                    min="0"
                    max="10"
                    value={mangaForm.rating}
                    onChange={(e) => setMangaForm({ ...mangaForm, rating: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="status">Status</label>
                  <select
                    id="status"
                    value={mangaForm.status}
                    onChange={(e) => setMangaForm({ ...mangaForm, status: e.target.value })}
                  >
                    <option value="RELEASING">Releasing</option>
                    <option value="COMPLETED">Completed</option>
                    <option value="PAUSED">Paused</option>
                    <option value="HIATUS">Hiatus</option>
                    <option value="CANCELLED">Cancelled</option>
                  </select>
                </div>

                <div className="form-group">
                  <label htmlFor="genres">Genre</label>
                  <select
                    id="genres"
                    value={mangaForm.genres}
                    onChange={(e) => setMangaForm({ ...mangaForm, genres: e.target.value })}
                  >
                    <option value="ACTION">Action</option>
                    <option value="ADVENTURE">Adventure</option>
                    <option value="COMEDY">Comedy</option>
                    <option value="DRAMA">Drama</option>
                    <option value="FANTASY">Fantasy</option>
                    <option value="ROMANCE">Romance</option>
                    <option value="SCI_FI">Sci-Fi</option>
                    <option value="SLICE_OF_LIFE">Slice of Life</option>
                    <option value="SUSPENSE">Suspense</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="keywords">Keywords</label>
                <input
                  id="keywords"
                  type="text"
                  value={mangaForm.keywords}
                  onChange={(e) => setMangaForm({ ...mangaForm, keywords: e.target.value })}
                  placeholder="adventure, fantasy, magic"
                />
              </div>

              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Creating...' : 'Create Manga'}
              </button>
            </form>
          </div>
        )}

        {activeTab === 'volume' && (
          <div className="admin-section">
            <h2 className="section-title">Add Volume to Manga</h2>
            <form onSubmit={handleVolumeSubmit} className="admin-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="mangaId">Manga ID</label>
                  <input
                    id="mangaId"
                    type="number"
                    value={volumeForm.mangaId}
                    onChange={(e) => setVolumeForm({ ...volumeForm, mangaId: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="volumeNumber">Volume Number</label>
                  <input
                    id="volumeNumber"
                    type="number"
                    value={volumeForm.volumeNumber}
                    onChange={(e) =>
                      setVolumeForm({ ...volumeForm, volumeNumber: e.target.value })
                    }
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="price">Price</label>
                  <input
                    id="price"
                    type="number"
                    step="0.01"
                    value={volumeForm.price}
                    onChange={(e) => setVolumeForm({ ...volumeForm, price: e.target.value })}
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="chapters">Chapters</label>
                  <input
                    id="chapters"
                    type="text"
                    value={volumeForm.chapters}
                    onChange={(e) => setVolumeForm({ ...volumeForm, chapters: e.target.value })}
                    placeholder="1-10"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="releaseDate">Release Date</label>
                  <input
                    id="releaseDate"
                    type="date"
                    value={volumeForm.releaseDate}
                    onChange={(e) =>
                      setVolumeForm({ ...volumeForm, releaseDate: e.target.value })
                    }
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="quantity">Quantity</label>
                  <input
                    id="quantity"
                    type="number"
                    value={volumeForm.quantity}
                    onChange={(e) => setVolumeForm({ ...volumeForm, quantity: e.target.value })}
                    required
                  />
                </div>
              </div>

              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Adding...' : 'Add Volume'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  )
}
