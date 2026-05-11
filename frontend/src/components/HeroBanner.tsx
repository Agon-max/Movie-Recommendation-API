import { useState, useEffect } from 'react';
import { Play, Info, Star, Calendar } from 'lucide-react';
import type { TmdbMovie } from '../types';
import styles from './HeroBanner.module.css';

interface Props {
  movies: TmdbMovie[];
}

const BACKDROP_BASE = 'https://image.tmdb.org/t/p/original';
const FALLBACK_BG = 'https://images.pexels.com/photos/7991164/pexels-photo-7991164.jpeg?auto=compress&cs=tinysrgb&w=1920';

export default function HeroBanner({ movies }: Props) {
  const [current, setCurrent] = useState(0);

  useEffect(() => {
    if (movies.length < 2) return;
    const timer = setInterval(() => setCurrent((c) => (c + 1) % Math.min(movies.length, 5)), 6000);
    return () => clearInterval(timer);
  }, [movies.length]);

  if (!movies.length) {
    return <div className={styles.skeleton} />;
  }

  const movie = movies[current];
  const backdrop = movie.backdrop_path
    ? `${BACKDROP_BASE}${movie.backdrop_path}`
    : FALLBACK_BG;
  const year = movie.release_date ? new Date(movie.release_date).getFullYear() : '';

  return (
    <div className={styles.hero}>
      <div className={styles.backdrop}>
        <img src={backdrop} alt={movie.title} key={backdrop} className={styles.backdropImg} />
        <div className={styles.backdropGrad} />
      </div>

      <div className={`container ${styles.content}`}>
        <div className={styles.inner}>
          <div className={styles.meta}>
            {movie.vote_average > 0 && (
              <span className={`badge badge-gold ${styles.ratingBadge}`}>
                <Star size={11} fill="currentColor" />
                {movie.vote_average.toFixed(1)} / 10
              </span>
            )}
            {year && (
              <span className={styles.year}>
                <Calendar size={12} />
                {year}
              </span>
            )}
          </div>

          <h1 className={styles.title}>{movie.title}</h1>

          {movie.overview && (
            <p className={styles.overview}>
              {movie.overview.length > 200 ? `${movie.overview.slice(0, 200)}…` : movie.overview}
            </p>
          )}

          <div className={styles.actions}>
            <button className={styles.playBtn}>
              <Play size={18} fill="currentColor" />
              Watch Now
            </button>
            <button className={styles.infoBtn}>
              <Info size={18} />
              More Info
            </button>
          </div>
        </div>
      </div>

      {movies.length > 1 && (
        <div className={styles.dots}>
          {movies.slice(0, 5).map((_, i) => (
            <button
              key={i}
              className={`${styles.dot} ${i === current ? styles.dotActive : ''}`}
              onClick={() => setCurrent(i)}
              aria-label={`Slide ${i + 1}`}
            />
          ))}
        </div>
      )}
    </div>
  );
}
