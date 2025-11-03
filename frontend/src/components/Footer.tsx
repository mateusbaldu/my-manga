import './Footer.css'

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-content">
          <div className="footer-section">
            <h3>My Mangá</h3>
            <p>Your favorite manga store online</p>
          </div>
          <div className="footer-section">
            <h4>Quick Links</h4>
            <ul>
              <li><a href="/">Home</a></li>
              <li><a href="/orders">Orders</a></li>
              <li><a href="/profile">Profile</a></li>
            </ul>
          </div>
          <div className="footer-section">
            <h4>Contact</h4>
            <p>Email: storemymanga@gmail.com</p>
          </div>
        </div>
        <div className="footer-bottom">
          <p>&copy; 2025 My Mangá. All rights reserved.</p>
        </div>
      </div>
    </footer>
  )
}
