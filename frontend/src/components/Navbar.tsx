import { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Search, Star, LogOut, User, Menu, X, Film } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import styles from './Navbar.module.css';

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuthStore();
  const [scrolled, setScrolled] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [searchVal, setSearchVal] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 40);
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchVal.trim()) {
      navigate(`/browse?q=${encodeURIComponent(searchVal.trim())}`);
      setSearchVal('');
    }
  };

  return (
    <nav className={`${styles.nav} ${scrolled ? styles.scrolled : ''}`}>
      <div className={styles.inner}>
        <Link to="/" className={styles.logo}>
          <Film size={22} />
          <span>CineMax</span>
        </Link>

        {isAuthenticated && (
          <div className={`${styles.links} hide-mobile`}>
            <Link to="/" className={location.pathname === '/' ? styles.active : ''}>Home</Link>
            <Link to="/browse" className={location.pathname === '/browse' ? styles.active : ''}>Browse</Link>
            <Link to="/rewards" className={location.pathname === '/rewards' ? styles.active : ''}>Rewards</Link>
          </div>
        )}

        <div className={styles.right}>
          {isAuthenticated ? (
            <>
              <form onSubmit={handleSearch} className={`${styles.searchForm} hide-mobile`}>
                <Search size={15} />
                <input
                  value={searchVal}
                  onChange={(e) => setSearchVal(e.target.value)}
                  placeholder="Search movies..."
                  className={styles.searchInput}
                />
              </form>

              <Link to="/rewards" className={`${styles.pointsBadge} hide-mobile`}>
                <Star size={14} />
                <span>{user?.totalPoints?.toLocaleString() ?? 0} pts</span>
              </Link>

              <Link to="/profile" className={styles.avatarBtn}>
                <User size={16} />
              </Link>

              <button onClick={logout} className={`${styles.iconBtn} hide-mobile`} title="Sign out">
                <LogOut size={16} />
              </button>

              <button className={`${styles.iconBtn} show-mobile`} onClick={() => setMobileOpen(!mobileOpen)}>
                {mobileOpen ? <X size={20} /> : <Menu size={20} />}
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={styles.loginBtn}>Sign In</Link>
              <Link to="/register" className={styles.registerBtn}>Get Started</Link>
            </>
          )}
        </div>
      </div>

      {mobileOpen && isAuthenticated && (
        <div className={styles.mobileMenu}>
          <form onSubmit={handleSearch} className={styles.mobileSearch}>
            <Search size={15} />
            <input
              value={searchVal}
              onChange={(e) => setSearchVal(e.target.value)}
              placeholder="Search movies..."
            />
          </form>
          <Link to="/">Home</Link>
          <Link to="/browse">Browse</Link>
          <Link to="/rewards">Rewards</Link>
          <Link to="/profile">Profile</Link>
          <div className={styles.mobilePts}>
            <Star size={14} />
            <span>{user?.totalPoints?.toLocaleString() ?? 0} points</span>
          </div>
          <button onClick={logout} className={styles.mobileLogout}>
            <LogOut size={15} /> Sign Out
          </button>
        </div>
      )}
    </nav>
  );
}
