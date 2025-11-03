import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/authStore'
import Layout from './components/Layout'
import Home from './pages/Home'
import MangaDetail from './pages/MangaDetail'
import Login from './pages/Login'
import Register from './pages/Register'
import Cart from './pages/Cart'
import Profile from './pages/Profile'
import Orders from './pages/Orders'
import AdminDashboard from './pages/AdminDashboard'
import ProtectedRoute from './components/ProtectedRoute'
import ForgotPassword from './pages/ForgotPassword'

function App() {
  const { isAuthenticated, user } = useAuthStore()

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="manga/:id" element={<MangaDetail />} />
          <Route path="login" element={!isAuthenticated ? <Login /> : <Navigate to="/" />} />
          <Route path="register" element={!isAuthenticated ? <Register /> : <Navigate to="/" />} />
          <Route path="forgot-password" element={!isAuthenticated ? <ForgotPassword /> : <Navigate to="/" />} />
          <Route path="cart" element={<Cart />} />
          <Route
            path="profile"
            element={
              <ProtectedRoute>
                <Profile />
              </ProtectedRoute>
            }
          />
          <Route
            path="orders"
            element={
              <ProtectedRoute>
                <Orders />
              </ProtectedRoute>
            }
          />
          <Route
            path="admin"
            element={
              <ProtectedRoute requireAdmin>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
        </Route>
      </Routes>
    </Router>
  )
}

export default App
