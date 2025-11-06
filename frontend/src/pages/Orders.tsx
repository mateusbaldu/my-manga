import { useState, useEffect } from 'react'
import { orderApi, Order } from '../services/api'
import { useAuthStore } from '../store/authStore'
import './Orders.css'

export default function Orders() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const { user } = useAuthStore()

  useEffect(() => {
    loadOrders()
  }, [])

  const loadOrders = async () => {
    try {
      setLoading(true)
      const response = await orderApi.getByUsername(user!.username, 0, 20)
      setOrders(response.data.content)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load orders')
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return 'success'
      case 'DELIVERED':
        return 'success'
      case 'SHIPPED':
        return 'info'
      case 'WAITING_CONFIRMATION':
        return 'warning'
      case 'CANCELLED':
        return 'danger'
      default:
        return 'info'
    }
  }

  const formatStatus = (status: string) => {
    return status.replace(/_/g, ' ')
  }

  if (loading) {
    return (
      <div className="loading">
        <div className="spinner" />
      </div>
    )
  }

  return (
    <div className="orders-page">
      <div className="container">
        <h1 className="page-title">My Orders</h1>

        {error && <div className="alert alert-error">{error}</div>}

        {orders.length === 0 ? (
          <div className="empty-state">
            <p>You haven't placed any orders yet</p>
          </div>
        ) : (
          <div className="orders-list">
            {orders.map((order) => (
              <div key={order.id} className="order-card">
                <div className="order-header">
                  <div>
                    <h3 className="order-id">Order #{order.id}</h3>
                    <p className="order-date">
                      {new Date(order.createdAt).toLocaleDateString()} at{' '}
                      {new Date(order.createdAt).toLocaleTimeString()}
                    </p>
                  </div>
                  <span className={`badge badge-${getStatusColor(order.status)}`}>
                    {formatStatus(order.status)}
                  </span>
                </div>

                <div className="order-items">
                  {order.items.map((item, index) => (
                    <div key={index} className="order-item">
                      <span className="item-name">
                        {item.mangaTitle} - Vol. {item.volumeNumber}
                      </span>
                      <span className="item-quantity">x{item.quantity}</span>
                      <span className="item-price">R$ {item.unitPrice.toFixed(2)}</span>
                    </div>
                  ))}
                </div>

                <div className="order-footer">
                  <div className="order-payment">
                    <strong>Payment:</strong> {order.paymentMethod}
                  </div>
                  <div className="order-total">
                    <strong>Total:</strong> R$ {order.finalPrice.toFixed(2)}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
