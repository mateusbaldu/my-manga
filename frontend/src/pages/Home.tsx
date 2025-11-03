import { useState, useEffect } from 'react'
import { mangaApi, Manga } from '../services/api'
import MangaCard from '../components/MangaCard'
import './Home.css'

export default function Home() {
  const [mangas, setMangas] = useState<Manga[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    loadMangas()
  }, [page])

  const loadMangas = async () => {
    try {
      setLoading(true)
      const response = await mangaApi.getAll(page, 12)
      setMangas(response.data.content)
      setTotalPages(response.data.totalPages)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load mangas')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!search.trim()) {
      loadMangas()
      return
    }

    try {
      setLoading(true)
      const response = await mangaApi.search(search, 0, 12)
      setMangas(response.data.content)
      setTotalPages(response.data.totalPages)
      setPage(0)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Search failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="home-page">
      <section className="hero">
        <div className="container">
          <h1 className="hero-title">Welcome to My Mangá</h1>
          <p className="hero-subtitle">Discover and collect your favorite manga volumes</p>

          <form onSubmit={handleSearch} className="search-form">
            <input
              type="text"
              placeholder="Search for manga..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="search-input"
            />
            <button type="submit" className="btn btn-primary search-button">
              Search
            </button>
          </form>
        </div>
      </section>

      <section className="mangas-section">
        <div className="container">
          {error && <div className="alert alert-error">{error}</div>}

          {loading ? (
            <div className="loading">
              <div className="spinner" />
            </div>
          ) : mangas.length === 0 ? (
            <div className="empty-state">
              <p>No manga found</p>
            </div>
          ) : (
            <>
              <div className="grid grid-3">
                {mangas.map((manga) => (
                  <MangaCard key={manga.id} manga={manga} />
                ))}
              </div>

              {totalPages > 1 && (
                <div className="pagination">
                  <button
                    onClick={() => setPage(page - 1)}
                    disabled={page === 0}
                    className="btn btn-outline"
                  >
                    Previous
                  </button>
                  <span className="page-info">
                    Page {page + 1} of {totalPages}
                  </span>
                  <button
                    onClick={() => setPage(page + 1)}
                    disabled={page >= totalPages - 1}
                    className="btn btn-outline"
                  >
                    Next
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>
    </div>
  )
}
