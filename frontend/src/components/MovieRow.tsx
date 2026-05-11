import { useRef } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import MovieCard from './MovieCard';
import type { Movie, TmdbMovie } from '../types';
import styles from './MovieRow.module.css';

interface Props {
  title: string;
  movies: (Movie | TmdbMovie)[];
  isTmdb?: boolean;
  badge?: React.ReactNode;
  loading?: boolean;
}

export default function MovieRow({ title, movies, isTmdb = false, badge, loading = false }: Props) {
  const rowRef = useRef<HTMLDivElement>(null);

  const scroll = (dir: 'left' | 'right') => {
    if (!rowRef.current) return;
    rowRef.current.scrollBy({ left: dir === 'right' ? 600 : -600, behavior: 'smooth' });
  };

  return (
    <section className={styles.section}>
      <div className={styles.header}>
        <div className={styles.titleRow}>
          <h2 className={styles.title}>{title}</h2>
          {badge}
        </div>
        <div className={styles.controls}>
          <button onClick={() => scroll('left')} className={styles.scrollBtn} aria-label="Scroll left">
            <ChevronLeft size={18} />
          </button>
          <button onClick={() => scroll('right')} className={styles.scrollBtn} aria-label="Scroll right">
            <ChevronRight size={18} />
          </button>
        </div>
      </div>

      <div className={`scroll-row ${styles.row}`} ref={rowRef}>
        {loading
          ? Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className={`skeleton ${styles.skeleton}`} />
            ))
          : movies.map((m, i) => (
              <MovieCard key={(m as TmdbMovie).id ?? (m as Movie).id ?? i} movie={m} isTmdb={isTmdb} />
            ))}
      </div>
    </section>
  );
}
