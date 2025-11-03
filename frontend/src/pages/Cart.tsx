import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCartStore } from '../store/cartStore'
import { useAuthStore } from '../store/authStore'
import { orderApi } from '../services/api'
import './Cart.css'

export default function Cart() {
  const { items, removeItem, updateQuantity, clearCart, getTotalPrice } = useCartStore()
  const { isAuthenticated } = useAuthStore()
  const navigate = useNavigate()
  const [paymentMethod, setPaymentMethod] = useState('CREDIT')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleCheckout = async () => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    if (items.length === 0) {
      setError('Your cart is empty')
      return
    }

    setLoading(true)
    setError('')

    try {
      const orderData = {
        paymentMethod,
        items: items.map((item) => ({
          volumeId: item.volumeId,
          quantity: item.quantity,
        })),
      }

      await orderApi.create(orderData)
      clearCart()
      alert('Order placed successfully! Check your email to confirm the order.')
      navigate('/orders')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to place order')
    } finally {
      setLoading(false)
    }
  }

  if (items.length === 0) {
    return (
      <div className="cart-page">
        <div className="container">
          <div className="empty-cart">
            <h2>Your cart is empty</h2>
            <p>Add some manga volumes to get started!</p>
            <button onClick={() => navigate('/')} className="btn btn-primary">
              Browse Manga
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="cart-page">
      <div className="container">
        <h1 className="page-title">Shopping Cart</h1>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="cart-layout">
          <div className="cart-items">
            {items.map((item) => (
              <div key={item.volumeId} className="cart-item">
                <div className="cart-item-info">
                  <h3 className="cart-item-title">
                    {item.mangaTitle} - Vol. {item.volumeNumber}
                  </h3>
                  <p className="cart-item-price">R$ {item.price.toFixed(2)} each</p>
                </div>

                <div className="cart-item-actions">
                  <div className="quantity-controls">
                    <button
                      onClick={() => updateQuantity(item.volumeId, item.quantity - 1)}
                      disabled={item.quantity <= 1}
                      className="quantity-btn"
                    >
                      -
                    </button>
                    <span className="quantity-value">{item.quantity}</span>
                    <button
                      onClick={() => updateQuantity(item.volumeId, item.quantity + 1)}
                      disabled={item.quantity >= item.maxQuantity}
                      className="quantity-btn"
                    >
                      +
                    </button>
                  </div>

                  <div className="cart-item-total">
                    R$ {(item.price * item.quantity).toFixed(2)}
                  </div>

                  <button
                    onClick={() => removeItem(item.volumeId)}
                    className="btn btn-danger"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <h2 className="summary-title">Order Summary</h2>

            <div className="form-group">
              <label htmlFor="paymentMethod">Payment Method</label>
              <select
                id="paymentMethod"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
              >
                <option value="CREDIT">Credit Card</option>
                <option value="DEBIT">Debit Card</option>
                <option value="PIX">PIX</option>
                <option value="BOLETO">Boleto</option>
              </select>
            </div>

            <div className="summary-row">
              <span>Subtotal:</span>
              <span>R$ {getTotalPrice().toFixed(2)}</span>
            </div>

            <div className="summary-row summary-total">
              <span>Total:</span>
              <span>R$ {getTotalPrice().toFixed(2)}</span>
            </div>

            <button
              onClick={handleCheckout}
              disabled={loading}
              className="btn btn-primary btn-block"
            >
              {loading ? 'Processing...' : 'Checkout'}
            </button>

            <button onClick={() => navigate('/')} className="btn btn-outline btn-block">
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}
