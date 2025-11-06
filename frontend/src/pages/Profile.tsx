import { useState, useEffect } from 'react'
import { userApi, addressApi, Address } from '../services/api'
import { useAuthStore } from '../store/authStore'
import './Profile.css'

export default function Profile() {
  const { user } = useAuthStore()
  const [addresses, setAddresses] = useState<Address[]>([])
  const [showAddressForm, setShowAddressForm] = useState(false)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [addressForm, setAddressForm] = useState({
    cep: '',
    number: '',
    complement: '',
  })

  useEffect(() => {
    loadAddresses()
  }, [])

  const loadAddresses = async () => {
    try {
      setLoading(true)
      const response = await addressApi.getAll(user!.username, 0, 10)
      setAddresses(response.data.content)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load addresses')
    } finally {
      setLoading(false)
    }
  }

  const handleAddAddress = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await addressApi.create(user!.username, addressForm)
      setSuccess('Address added successfully!')
      setAddressForm({ cep: '', number: '', complement: '' })
      setShowAddressForm(false)
      loadAddresses()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add address')
    }
  }

  const handleDeleteAddress = async (addressId: number) => {
    if (!confirm('Are you sure you want to delete this address?')) return

    try {
      await addressApi.delete(user!.username, addressId)
      setSuccess('Address deleted successfully!')
      loadAddresses()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete address')
    }
  }

  return (
    <div className="profile-page">
      <div className="container">
        <h1 className="page-title">My Profile</h1>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <div className="profile-layout">
          <div className="profile-section">
            <h2 className="section-title">Account Information</h2>
            <div className="info-card">
              <div className="info-row">
                <strong>Name:</strong>
                <span>{user?.name}</span>
              </div>
              <div className="info-row">
                <strong>Username:</strong>
                <span>{user?.username}</span>
              </div>
              <div className="info-row">
                <strong>Role:</strong>
                <span>{user?.roles.map((r) => r.name).join(', ')}</span>
              </div>
            </div>
          </div>

          <div className="profile-section">
            <div className="section-header">
              <h2 className="section-title">My Addresses</h2>
              <button
                onClick={() => setShowAddressForm(!showAddressForm)}
                className="btn btn-primary"
              >
                {showAddressForm ? 'Cancel' : 'Add Address'}
              </button>
            </div>

            {showAddressForm && (
              <form onSubmit={handleAddAddress} className="address-form">
                <div className="form-group">
                  <label htmlFor="cep">CEP (8 digits)</label>
                  <input
                    id="cep"
                    type="text"
                    maxLength={8}
                    value={addressForm.cep}
                    onChange={(e) =>
                      setAddressForm({ ...addressForm, cep: e.target.value })
                    }
                    required
                    placeholder="12345678"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="number">Number</label>
                  <input
                    id="number"
                    type="text"
                    value={addressForm.number}
                    onChange={(e) =>
                      setAddressForm({ ...addressForm, number: e.target.value })
                    }
                    placeholder="123"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="complement">Complement</label>
                  <input
                    id="complement"
                    type="text"
                    value={addressForm.complement}
                    onChange={(e) =>
                      setAddressForm({ ...addressForm, complement: e.target.value })
                    }
                    placeholder="Apt 456"
                  />
                </div>

                <button type="submit" className="btn btn-primary">
                  Save Address
                </button>
              </form>
            )}

            {loading ? (
              <div className="loading">
                <div className="spinner" />
              </div>
            ) : addresses.length === 0 ? (
              <p className="empty-state">No addresses added yet</p>
            ) : (
              <div className="addresses-list">
                {addresses.map((address) => (
                  <div key={address.id} className="address-card">
                    <div className="address-info">
                      <p>
                        <strong>{address.street}</strong>
                        {address.number && `, ${address.number}`}
                      </p>
                      {address.complement && <p>{address.complement}</p>}
                      <p>
                        {address.locality}, {address.city} - {address.state}
                      </p>
                      <p>CEP: {address.cep}</p>
                    </div>
                    <button
                      onClick={() => handleDeleteAddress(address.id)}
                      className="btn btn-danger"
                    >
                      Delete
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
