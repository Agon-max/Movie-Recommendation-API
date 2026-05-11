import { Link } from 'react-router-dom';
import { Star, Calendar } from 'lucide-react';
import type { Movie, TmdbMovie } from '../types';
import styles from './MovieCard.module.css';

interface Props {
  movie: Movie | TmdbMovie;
  isTmdb?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

const POSTER_PLACEHOLDER = 'https://images.pexels.com/photos/7991579/pexels-photo-7991579.jpeg?auto=compress&cs=tinysrgb&w=300';

export default function MovieCard({ movie, isTmdb = false, size = 'md' }: Props) {
  const tmdb = movie as TmdbMovie;
  const local = movie as Movie;

  const title = isTmdb ? tmdb.title : local.title;
  const rating = isTmdb ? tmdb.vote_average : local.averageRating;
  const releaseDate = isTmdb ? tmdb.release_date : local.releaseDate;
  const poster = isTmdb && tmdb.poster_path
    ? `https://image.tmdb.org/t/p/w300${tmdb.poster_path}`
    : POSTER_PLACEHOLDER;

  const year = releaseDate ? new Date(releaseDate).getFullYear() : null;

  const content = (
    <div className={`${styles.card} ${styles[size]}`}>
      <div className={styles.poster}>
        <img src={poster} alt={title} loading="lazy" />
        <div className={styles.overlay}>
          <div className={styles.rating}>
            <Star size={12} fill="currentColor" />
            <span>{rating ? rating.toFixed(1) : 'N/A'}</span>
          </div>
        </div>
      </div>
      <div className={styles.info}>
        <h3 className={styles.title}>{title}</h3>
        {year && (
          <div className={styles.meta}>
            <Calendar size={11} />
            <span>{year}</span>
          </div>
        )}
      </div>
    </div>
  );

  if (isTmdb || !local.id) return content;

  return <Link to={`/movies/${local.id}`}>{content}</Link>;
}
