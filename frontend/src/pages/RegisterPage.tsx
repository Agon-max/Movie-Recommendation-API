import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Film, Eye, EyeOff, CircleAlert as AlertCircle, CircleCheck as CheckCircle } from 'lucide-react';
import { authApi, usersApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import styles from './AuthPage.module.css';

export default function RegisterPage() {
  const [form, setForm] = useState({ username: '', email: '', password: '' });
  const [showPass, setShowPass] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { login } = useAuthStore();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.username || !form.email || !form.password) {
      setError('Please fill in all fields.');
      return;
    }
    if (form.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }
    setLoading(true);
    setError('');
    try {
      await authApi.register(form);
      const loginRes = await authApi.login({ username: form.username, password: form.password });
      const user = await usersApi.getById(loginRes.userId);
      login(loginRes.token, user);
      navigate('/survey');
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })?.response?.data?.message;
      setError(msg || 'Registration failed. Try a different username or email.');
    } finally {
      setLoading(false);
    }
  };

  const passwordStrength = form.password.length === 0 ? 0
    : form.password.length < 6 ? 1
    : form.password.length < 10 ? 2
    : 3;

  return (
    <div className={styles.page}>
      <div className={styles.bg} />

      <div className={styles.card}>
        <Link to="/" className={styles.logo}>
          <Film size={24} />
          <span>CineMax</span>
        </Link>

        <h1 className={styles.heading}>Create account</h1>
        <p className={styles.sub}>Join CineMax and start discovering great movies</p>

        {error && (
          <div className={styles.error}>
            <AlertCircle size={15} />
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Username</label>
            <input
              type="text"
              placeholder="Choose a username"
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
              autoComplete="username"
              autoFocus
            />
          </div>

          <div className={styles.field}>
            <label>Email</label>
            <input
              type="email"
              placeholder="your@email.com"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              autoComplete="email"
            />
          </div>

          <div className={styles.field}>
            <label>Password</label>
            <div className={styles.passWrapper}>
              <input
                type={showPass ? 'text' : 'password'}
                placeholder="Min. 6 characters"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                autoComplete="new-password"
              />
              <button type="button" onClick={() => setShowPass(!showPass)} className={styles.eyeBtn}>
                {showPass ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
            {form.password && (
              <div className={styles.strengthBar}>
                {[1,2,3].map((s) => (
                  <div key={s} className={`${styles.strengthSeg} ${passwordStrength >= s ? styles[`str${s}`] : ''}`} />
                ))}
                <span className={styles.strengthLabel}>
                  {passwordStrength === 1 ? 'Weak' : passwordStrength === 2 ? 'Good' : 'Strong'}
                </span>
              </div>
            )}
          </div>

          <div className={styles.perks}>
            {['Personalized recommendations', 'Earn points for watching', 'Redeem rewards'].map((p) => (
              <div key={p} className={styles.perk}>
                <CheckCircle size={14} />
                <span>{p}</span>
              </div>
            ))}
          </div>

          <button type="submit" className={styles.submitBtn} disabled={loading}>
            {loading ? <span className={styles.spinner} /> : 'Create Account'}
          </button>
        </form>

        <p className={styles.switchText}>
          Already have an account?{' '}
          <Link to="/login">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
