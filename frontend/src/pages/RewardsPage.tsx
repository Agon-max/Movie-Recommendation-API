import { useState } from 'react';
import { Star, Gift, Zap, Trophy, TrendingUp, Lock } from 'lucide-react';
import { useAuthStore } from '../store/authStore';
import { Link } from 'react-router-dom';
import styles from './RewardsPage.module.css';

const MOCK_REWARDS = [
  { id: 1, name: '$5 Gift Card', description: 'Amazon gift card redeemable online', pointCost: 500, type: 'GIFT_CARD', monetaryValue: 5, active: true },
  { id: 2, name: '$10 Gift Card', description: 'Amazon gift card redeemable online', pointCost: 950, type: 'GIFT_CARD', monetaryValue: 10, active: true },
  { id: 3, name: '$25 Gift Card', description: 'Premium gift card for top members', pointCost: 2200, type: 'GIFT_CARD', monetaryValue: 25, active: true },
  { id: 4, name: '10% Discount', description: 'Discount code for partner services', pointCost: 300, type: 'DISCOUNT_CODE', monetaryValue: null, active: true },
  { id: 5, name: 'Cash Payout $15', description: 'Direct payout to your account', pointCost: 1500, type: 'CASH_PAYOUT', monetaryValue: 15, active: true },
  { id: 6, name: 'VIP Badge', description: 'Exclusive member badge on your profile', pointCost: 800, type: 'DISCOUNT_CODE', monetaryValue: null, active: true },
];

const EARNING_EVENTS = [
  { icon: <Star size={18} />, label: 'Write a Review', pts: '+50 pts', color: '#f5c518' },
  { icon: <Zap size={18} />, label: 'Watch a Movie', pts: '+20 pts', color: '#3b82f6' },
  { icon: <Trophy size={18} />, label: 'First Login', pts: '+100 pts', color: '#22c55e' },
  { icon: <TrendingUp size={18} />, label: 'Referral', pts: '+200 pts', color: '#a855f7' },
];

const ICON_MAP: Record<string, React.ReactNode> = {
  GIFT_CARD: <Gift size={20} />,
  CASH_PAYOUT: <TrendingUp size={20} />,
  DISCOUNT_CODE: <Zap size={20} />,
};

export default function RewardsPage() {
  const { user } = useAuthStore();
  const [redeemed, setRedeemed] = useState<number[]>([]);
  const [msg, setMsg] = useState<string>('');

  const points = user?.totalPoints ?? 0;

  const progressToNext = () => {
    const next = MOCK_REWARDS.find((r) => r.pointCost > points);
    if (!next) return { label: 'All rewards unlocked!', pct: 100 };
    return {
      label: `${next.pointCost - points} pts until "${next.name}"`,
      pct: Math.min((points / next.pointCost) * 100, 100),
    };
  };

  const progress = progressToNext();

  const handleRedeem = (reward: typeof MOCK_REWARDS[0]) => {
    if (points < reward.pointCost) {
      setMsg(`You need ${reward.pointCost - points} more points to redeem this reward.`);
      setTimeout(() => setMsg(''), 3000);
      return;
    }
    setRedeemed((prev) => [...prev, reward.id]);
    setMsg(`"${reward.name}" redemption request submitted!`);
    setTimeout(() => setMsg(''), 4000);
  };

  if (!user) {
    return (
      <div className={styles.page}>
        <div className={`container ${styles.container}`}>
          <div className={styles.unauthState}>
            <Trophy size={48} />
            <h2>Sign in to view rewards</h2>
            <p>Create an account to start earning points and redeeming rewards.</p>
            <Link to="/login" className={styles.signInBtn}>Sign In</Link>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={`page-enter ${styles.page}`}>
      <div className={`container ${styles.container}`}>
        <div className={styles.header}>
          <h1 className={styles.title}>Rewards Center</h1>
          <p className={styles.sub}>Earn points by engaging with movies, then redeem for real rewards.</p>
        </div>

        <div className={styles.topGrid}>
          <div className={styles.pointsCard}>
            <div className={styles.pointsCardTop}>
              <div>
                <div className={styles.pointsLabel}>Your Balance</div>
                <div className={styles.pointsValue}>{points.toLocaleString()}</div>
                <div className={styles.pointsUnit}>points</div>
              </div>
              <div className={styles.pointsIcon}>
                <Star size={32} fill="var(--gold)" color="var(--gold)" />
              </div>
            </div>
            <div className={styles.progressSection}>
              <div className={styles.progressLabel}>{progress.label}</div>
              <div className={styles.progressBar}>
                <div className={styles.progressFill} style={{ width: `${progress.pct}%` }} />
              </div>
            </div>
          </div>

          <div className={styles.earningCard}>
            <h3 className={styles.earningTitle}>How to Earn Points</h3>
            <div className={styles.earningList}>
              {EARNING_EVENTS.map((e) => (
                <div key={e.label} className={styles.earningItem}>
                  <div className={styles.earningIconWrapper} style={{ color: e.color }}>
                    {e.icon}
                  </div>
                  <span className={styles.earningLabel}>{e.label}</span>
                  <span className={styles.earningPts} style={{ color: e.color }}>{e.pts}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {msg && (
          <div className={styles.toast}>
            <Zap size={15} />
            {msg}
          </div>
        )}

        <div className={styles.rewardsSection}>
          <h2 className={styles.rewardsTitle}>Available Rewards</h2>
          <div className={styles.rewardsGrid}>
            {MOCK_REWARDS.map((reward) => {
              const canAfford = points >= reward.pointCost;
              const isRedeemed = redeemed.includes(reward.id);
              return (
                <div key={reward.id} className={`${styles.rewardCard} ${!canAfford ? styles.locked : ''} ${isRedeemed ? styles.redeemed : ''}`}>
                  <div className={styles.rewardIcon} style={{ color: canAfford ? 'var(--gold)' : 'var(--text-muted)' }}>
                    {ICON_MAP[reward.type] ?? <Gift size={20} />}
                  </div>
                  <h3 className={styles.rewardName}>{reward.name}</h3>
                  <p className={styles.rewardDesc}>{reward.description}</p>
                  <div className={styles.rewardFooter}>
                    <span className={`${styles.rewardCost} ${canAfford ? styles.rewardCostAffordable : ''}`}>
                      <Star size={12} />
                      {reward.pointCost.toLocaleString()} pts
                    </span>
                    {isRedeemed ? (
                      <span className={styles.redeemedBadge}>Requested</span>
                    ) : (
                      <button
                        onClick={() => handleRedeem(reward)}
                        className={`${styles.redeemBtn} ${!canAfford ? styles.redeemBtnLocked : ''}`}
                      >
                        {canAfford ? 'Redeem' : <><Lock size={12} /> Locked</>}
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
