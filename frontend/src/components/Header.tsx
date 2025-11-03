import { Link } from 'react-router-dom'
import { useAuthStore } from '../store/authStore'
import { useCartStore } from '../store/cartStore'
import './Header.css'

export default function Header() {
  const { isAuthenticated, user, isAdmin, logout } = useAuthStore()
  const { getTotalItems } = useCartStore()

  return (
    <header className="header">
      <div className="container">
        <div className="header-content">
          <Link to="/" className="logo">
            <span className="logo-icon">📚</span>
            <span className="logo-text">My Mangá</span>
          </Link>

          <nav className="nav">
            <Link to="/" className="nav-link">
              Home
            </Link>
            {isAuthenticated && (
              <>
                <Link to="/orders" className="nav-link">
                  Orders
                </Link>
                <Link to="/profile" className="nav-link">
                  Profile
                </Link>
                {isAdmin && (
                  <Link to="/admin" className="nav-link admin-link">
                    Admin
                  </Link>
                )}
              </>
            )}
          </nav>

          <div className="header-actions">
            <Link to="/cart" className="cart-button">
              <span className="cart-icon">🛒</span>
              {getTotalItems() > 0 && (
                <span className="cart-badge">{getTotalItems()}</span>
              )}
            </Link>

            {isAuthenticated ? (
              <div className="user-menu">
                <span className="user-name">Hello, {user?.name}!</span>
                <button onClick={logout} className="btn btn-outline">
                  Logout
                </button>
              </div>
            ) : (
              <div className="auth-buttons">
                <Link to="/login" className="btn btn-outline">
                  Login
                </Link>
                <Link to="/register" className="btn btn-primary">
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}
