import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Star, Calendar, Globe, ChevronLeft, Send } from 'lucide-react';
import { moviesApi, reviewsApi, actorsApi, directorsApi } from '../lib/api';
import { useAuthStore } from '../store/authStore';
import type { Movie, Actor, Director } from '../types';
import styles from './MovieDetailPage.module.css';

const POSTER_PLACEHOLDER = 'https://images.pexels.com/photos/7991579/pexels-photo-7991579.jpeg?auto=compress&cs=tinysrgb&w=400';

export default function MovieDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [movie, setMovie] = useState<Movie | null>(null);
  const [actors, setActors] = useState<Actor[]>([]);
  const [directors, setDirectors] = useState<Director[]>([]);
  const [loading, setLoading] = useState(true);
  const [review, setReview] = useState({ title: '', body: '', rating_score: 0 });
  const [hoverStar, setHoverStar] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [reviewed, setReviewed] = useState(false);
  const [reviewMsg, setReviewMsg] = useState('');

  useEffect(() => {
    if (!id) return;
    const movieId = Number(id);
    setLoading(true);
    Promise.all([
      moviesApi.getById(movieId),
      actorsApi.getByMovie(movieId).catch(() => []),
      directorsApi.getByMovie(movieId).catch(() => []),
    ]).then(([m, a, d]) => {
      setMovie(m);
      setActors(a);
      setDirectors(d);
    }).catch(() => navigate('/browse'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const submitReview = async () => {
    if (!user || !movie || review.rating_score === 0) return;
    setSubmitting(true);
    try {
      await reviewsApi.create({
        userId: user.id,
        movieId: movie.id,
        title: review.title,
        body: review.body,
        rating_score: review.rating_score,
      });
      setReviewed(true);
      setReviewMsg('Review submitted! You earned points.');
    } catch {
      setReviewMsg('Could not submit review. You may have already reviewed this movie.');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className={styles.page}>
        <div className={`skeleton ${styles.backdropSkeleton}`} />
        <div className={`container ${styles.container}`}>
          <div className={styles.info}>
            <div className={`skeleton ${styles.posterSkeleton}`} />
            <div className={styles.details}>
              <div className={`skeleton ${styles.titleSkeleton}`} />
              <div className={`skeleton ${styles.overviewSkeleton}`} />
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!movie) return null;

  const stars = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  return (
    <div className={`page-enter ${styles.page}`}>
      <div className={styles.backdrop}>
        <img
          src={`https://images.pexels.com/photos/7991164/pexels-photo-7991164.jpeg?auto=compress&cs=tinysrgb&w=1920`}
          alt=""
        />
        <div className={styles.backdropGrad} />
      </div>

      <div className={`container ${styles.container}`}>
        <button onClick={() => navigate(-1)} className={styles.backBtn}>
          <ChevronLeft size={18} />
          Back
        </button>

        <div className={styles.info}>
          <div className={styles.posterWrapper}>
            <img
              src={POSTER_PLACEHOLDER}
              alt={movie.title}
              className={styles.poster}
            />
          </div>

          <div className={styles.details}>
            <h1 className={styles.title}>{movie.title}</h1>

            <div className={styles.metaRow}>
              {movie.averageRating != null && (
                <span className={styles.rating}>
                  <Star size={15} fill="var(--gold)" color="var(--gold)" />
                  {movie.averageRating.toFixed(1)}
                  <span className={styles.ratingMax}>/10</span>
                </span>
              )}
              {movie.releaseDate && (
                <span className={styles.metaItem}>
                  <Calendar size={13} />
                  {new Date(movie.releaseDate).getFullYear()}
                </span>
              )}
              {movie.language && (
                <span className={styles.metaItem}>
                  <Globe size={13} />
                  {movie.language.toUpperCase()}
                </span>
              )}
            </div>

            {movie.overview && (
              <p className={styles.overview}>{movie.overview}</p>
            )}

            {directors.length > 0 && (
              <div className={styles.creditSection}>
                <h4 className={styles.creditLabel}>Director{directors.length > 1 ? 's' : ''}</h4>
                <div className={styles.creditList}>
                  {directors.map((d) => (
                    <span key={d.id} className={styles.creditChip}>{d.name}</span>
                  ))}
                </div>
              </div>
            )}

            {actors.length > 0 && (
              <div className={styles.creditSection}>
                <h4 className={styles.creditLabel}>Cast</h4>
                <div className={styles.creditList}>
                  {actors.slice(0, 8).map((a) => (
                    <span key={a.id} className={styles.creditChip}>{a.name}</span>
                  ))}
                  {actors.length > 8 && (
                    <span className={styles.moreChip}>+{actors.length - 8} more</span>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>

        {user && !reviewed && (
          <div className={styles.reviewSection}>
            <h2 className={styles.reviewTitle}>Write a Review</h2>
            <p className={styles.reviewSub}>Earn points for sharing your thoughts</p>

            <div className={styles.starRow}>
              {stars.map((s) => (
                <button
                  key={s}
                  className={`${styles.starBtn} ${s <= (hoverStar || review.rating_score) ? styles.starFilled : ''}`}
                  onMouseEnter={() => setHoverStar(s)}
                  onMouseLeave={() => setHoverStar(0)}
                  onClick={() => setReview({ ...review, rating_score: s })}
                  aria-label={`Rate ${s}`}
                >
                  ★
                </button>
              ))}
              {review.rating_score > 0 && (
                <span className={styles.ratingSelected}>{review.rating_score}/10</span>
              )}
            </div>

            <div className={styles.reviewFields}>
              <input
                className={styles.reviewInput}
                placeholder="Review title (optional)"
                value={review.title}
                onChange={(e) => setReview({ ...review, title: e.target.value })}
              />
              <textarea
                className={styles.reviewTextarea}
                placeholder="What did you think? (optional)"
                value={review.body}
                onChange={(e) => setReview({ ...review, body: e.target.value })}
                rows={4}
              />
            </div>

            <button
              className={styles.submitReviewBtn}
              onClick={submitReview}
              disabled={submitting || review.rating_score === 0}
            >
              {submitting ? <span className={styles.spinner} /> : <><Send size={15} /> Submit Review</>}
            </button>

            {reviewMsg && <p className={styles.reviewMsg}>{reviewMsg}</p>}
          </div>
        )}

        {reviewed && (
          <div className={styles.reviewDone}>
            <Star size={20} fill="var(--gold)" color="var(--gold)" />
            <div>
              <h3>Review submitted!</h3>
              <p>{reviewMsg}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
