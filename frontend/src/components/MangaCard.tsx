import { Link } from 'react-router-dom'
import { Manga } from '../services/api'
import './MangaCard.css'

interface MangaCardProps {
  manga: Manga
}

export default function MangaCard({ manga }: MangaCardProps) {
  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'success'
      case 'RELEASING':
        return 'info'
      case 'PAUSED':
      case 'HIATUS':
        return 'warning'
      case 'CANCELLED':
        return 'danger'
      default:
        return 'info'
    }
  }

  const formatGenre = (genre: string) => {
    return genre.replace(/_/g, ' ')
  }

  return (
    <Link to={`/manga/${manga.id}`} className="manga-card">
      <div className="manga-card-header">
        <div className="manga-placeholder">
          <span className="manga-icon">📖</span>
        </div>
        <div className={`manga-status badge badge-${getStatusColor(manga.status)}`}>
          {manga.status}
        </div>
      </div>
      <div className="manga-card-body">
        <h3 className="manga-title">{manga.title}</h3>
        <p className="manga-author">by {manga.author}</p>
        <p className="manga-description">
          {manga.description?.substring(0, 100)}
          {manga.description?.length > 100 ? '...' : ''}
        </p>
        <div className="manga-meta">
          <span className="manga-genre">{formatGenre(manga.genres)}</span>
          <span className="manga-rating">⭐ {manga.rating?.toFixed(1) || 'N/A'}</span>
        </div>
        <div className="manga-volumes">
          {manga.volumes?.length || 0} volumes available
        </div>
      </div>
    </Link>
  )
}
