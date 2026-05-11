import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, TrendingUp, Star } from 'lucide-react';
import HeroBanner from '../components/HeroBanner';
import MovieRow from '../components/MovieRow';
import { moviesApi, recommendationsApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import type { TmdbMovie, Movie } from '../types';
import styles from './HomePage.module.css';

export default function HomePage() {
  const { user } = useAuthStore();
  const navigate = useNavigate();
  const [popular, setPopular] = useState<TmdbMovie[]>([]);
  const [recommendations, setRecommendations] = useState<Movie[]>([]);
  const [hasSurvey, setHasSurvey] = useState<boolean | null>(null);
  const [loadingPop, setLoadingPop] = useState(true);
  const [loadingRec, setLoadingRec] = useState(true);

  useEffect(() => {
    moviesApi.getPopular()
      .then((data) => setPopular(data.results ?? []))
      .catch(() => setPopular([]))
      .finally(() => setLoadingPop(false));
  }, []);

  useEffect(() => {
    if (!user) return;
    recommendationsApi.getSurvey(user.id)
      .then((data) => {
        if (data?.id) {
          setHasSurvey(true);
          return recommendationsApi.get(user.id, 12);
        } else {
          setHasSurvey(false);
          return Promise.resolve([]);
        }
      })
      .then((recs) => setRecommendations(recs))
      .catch(() => { setHasSurvey(false); setRecommendations([]); })
      .finally(() => setLoadingRec(false));
  }, [user]);

  return (
    <div className={styles.page}>
      {!loadingPop && <HeroBanner movies={popular} />}
      {loadingPop && <div className={`skeleton ${styles.heroSkeleton}`} />}

      <div className={`container ${styles.content}`}>
        {hasSurvey === false && user && (
          <div className={styles.surveyBanner}>
            <div className={styles.surveyBannerContent}>
              <Sparkles size={20} />
              <div>
                <h3>Get personalized recommendations</h3>
                <p>Tell us your preferences and we'll find perfect movies for you.</p>
              </div>
            </div>
            <button onClick={() => navigate('/survey')} className={styles.surveyBtn}>
              Take Survey
            </button>
          </div>
        )}

        {user && (hasSurvey || loadingRec) && (
          <MovieRow
            title="Recommended for You"
            movies={recommendations}
            loading={loadingRec}
            badge={<span className="badge badge-accent"><Sparkles size={10} /> AI Picks</span>}
          />
        )}

        <MovieRow
          title="Trending Now"
          movies={popular}
          isTmdb
          loading={loadingPop}
          badge={<span className="badge badge-muted"><TrendingUp size={10} /> Popular</span>}
        />

        {popular.length > 0 && (
          <MovieRow
            title="Top Rated"
            movies={[...popular].sort((a, b) => b.vote_average - a.vote_average)}
            isTmdb
            loading={loadingPop}
            badge={<span className="badge badge-gold"><Star size={10} /> Top Rated</span>}
          />
        )}
      </div>
    </div>
  );
}
