import { useState } from 'react';
import { User, Star, LogOut, CreditCard as Edit2, Save, X, Mail, AtSign } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { useNavigate, Link } from 'react-router-dom';
import styles from './ProfilePage.module.css';

export default function ProfilePage() {
  const { user, logout, updateUser } = useAuthStore();
  const navigate = useNavigate();
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({ username: user?.username ?? '', email: user?.email ?? '' });
  const [saved, setSaved] = useState(false);

  if (!user) {
    navigate('/login');
    return null;
  }

  const handleSave = () => {
    updateUser({ ...user, username: form.username, email: form.email });
    setEditing(false);
    setSaved(true);
    setTimeout(() => setSaved(false), 2500);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const pointTiers = [
    { name: 'Bronze', min: 0, max: 299, color: '#cd7f32' },
    { name: 'Silver', min: 300, max: 999, color: '#c0c0c0' },
    { name: 'Gold', min: 1000, max: 4999, color: '#f5c518' },
    { name: 'Platinum', min: 5000, max: Infinity, color: '#e5e4e2' },
  ];

  const tier = pointTiers.find((t) => user.totalPoints >= t.min && user.totalPoints <= t.max) ?? pointTiers[0];
  const nextTier = pointTiers.find((t) => t.min > user.totalPoints);
  const tierPct = nextTier
    ? Math.min(((user.totalPoints - tier.min) / (nextTier.min - tier.min)) * 100, 100)
    : 100;

  return (
    <div className={`page-enter ${styles.page}`}>
      <div className={`container ${styles.container}`}>
        <div className={styles.header}>
          <h1 className={styles.title}>My Profile</h1>
        </div>

        <div className={styles.grid}>
          <div className={styles.profileCard}>
            <div className={styles.avatar}>
              <User size={36} />
            </div>
            <h2 className={styles.username}>{user.username}</h2>
            <p className={styles.email}>{user.email}</p>

            <div className={styles.tierBadge} style={{ borderColor: tier.color, color: tier.color }}>
              <Star size={13} fill="currentColor" />
              {tier.name} Member
            </div>

            <div className={styles.tierProgress}>
              <div className={styles.tierProgressRow}>
                <span className={styles.tierLabel}>{user.totalPoints} pts</span>
                {nextTier && <span className={styles.tierLabel}>{nextTier.min} pts for {nextTier.name}</span>}
              </div>
              <div className={styles.progressBar}>
                <div className={styles.progressFill} style={{ width: `${tierPct}%`, background: tier.color }} />
              </div>
            </div>

            <div className={styles.statsRow}>
              <div className={styles.stat}>
                <span className={styles.statValue}>{user.totalPoints.toLocaleString()}</span>
                <span className={styles.statLabel}>Points</span>
              </div>
            </div>

            <div className={styles.profileActions}>
              <Link to="/rewards" className={styles.rewardsLink}>
                <Star size={15} />
                View Rewards
              </Link>
              <button onClick={handleLogout} className={styles.logoutBtn}>
                <LogOut size={15} />
                Sign Out
              </button>
            </div>
          </div>

          <div className={styles.settingsCard}>
            <div className={styles.settingsHeader}>
              <h3>Account Settings</h3>
              {!editing ? (
                <button onClick={() => setEditing(true)} className={styles.editBtn}>
                  <Edit2 size={14} /> Edit
                </button>
              ) : (
                <button onClick={() => setEditing(false)} className={styles.cancelBtn}>
                  <X size={14} /> Cancel
                </button>
              )}
            </div>

            {saved && (
              <div className={styles.savedMsg}>Profile updated successfully.</div>
            )}

            <div className={styles.fields}>
              <div className={styles.field}>
                <label>
                  <AtSign size={14} />
                  Username
                </label>
                {editing ? (
                  <input
                    value={form.username}
                    onChange={(e) => setForm({ ...form, username: e.target.value })}
                    className={styles.input}
                  />
                ) : (
                  <p className={styles.fieldValue}>{user.username}</p>
                )}
              </div>

              <div className={styles.field}>
                <label>
                  <Mail size={14} />
                  Email
                </label>
                {editing ? (
                  <input
                    value={form.email}
                    onChange={(e) => setForm({ ...form, email: e.target.value })}
                    className={styles.input}
                    type="email"
                  />
                ) : (
                  <p className={styles.fieldValue}>{user.email}</p>
                )}
              </div>
            </div>

            {editing && (
              <button onClick={handleSave} className={styles.saveBtn}>
                <Save size={15} /> Save Changes
              </button>
            )}

            <div className={styles.divider} />

            <div className={styles.surveySection}>
              <h4>Movie Preferences</h4>
              <p>Update your tastes and we'll improve your recommendations.</p>
              <Link to="/survey" className={styles.surveyLink}>
                Retake Preference Survey
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
