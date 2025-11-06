import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { authApi, userApi } from '../services/api'
import { useAuthStore } from '../store/authStore'
import './Auth.css'

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const setAuth = useAuthStore((state) => state.setAuth)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const loginResponse = await authApi.login({ email, password })
      const token = loginResponse.data.accessToken

      const userEmail = email
      const usernameMatch = userEmail.match(/^(.+)@/)
      const username = usernameMatch ? usernameMatch[1] : 'user'

      let userData
      try {
        const userResponse = await userApi.getByUsername(username)
        userData = userResponse.data
      } catch {
        userData = {
          username: username,
          name: email.split('@')[0],
          roles: [{ id: 2, name: 'BASIC' }]
        }
      }

      setAuth(token, userData)
      navigate('/')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed. Please check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-card">
          <h1 className="auth-title">Welcome Back</h1>
          <p className="auth-subtitle">Login to your account</p>

          {error && <div className="alert alert-error">{error}</div>}

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                placeholder="your@email.com"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="••••••••"
              />
            </div>

            <div className="form-footer">
              <Link to="/forgot-password" className="forgot-link">
                Forgot password?
              </Link>
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? 'Logging in...' : 'Login'}
            </button>
          </form>

          <div className="auth-switch">
            Don't have an account?{' '}
            <Link to="/register" className="auth-link">
              Register here
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
