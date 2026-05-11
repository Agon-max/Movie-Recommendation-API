import { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Search, SlidersHorizontal, X } from 'lucide-react';
import MovieCard from '../components/MovieCard';
import { moviesApi } from '../lib/api';
import type { Movie } from '../types';
import styles from './BrowsePage.module.css';

export default function BrowsePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [query, setQuery] = useState(searchParams.get('q') || '');
  const [inputVal, setInputVal] = useState(searchParams.get('q') || '');
  const [movies, setMovies] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchMovies = useCallback(async (q: string, p: number) => {
    if (!q.trim()) {
      setMovies([]);
      setTotalElements(0);
      return;
    }
    setLoading(true);
    try {
      const data = await moviesApi.search(q, p, 24);
      if (p === 0) {
        setMovies(data.content ?? []);
      } else {
        setMovies((prev) => [...prev, ...(data.content ?? [])]);
      }
      setTotalPages(data.totalPages ?? 0);
      setTotalElements(data.totalElements ?? 0);
    } catch {
      setMovies([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const q = searchParams.get('q') || '';
    setQuery(q);
    setInputVal(q);
    setPage(0);
    fetchMovies(q, 0);
  }, [searchParams, fetchMovies]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPage(0);
    setSearchParams(inputVal ? { q: inputVal } : {});
  };

  const loadMore = () => {
    const next = page + 1;
    setPage(next);
    fetchMovies(query, next);
  };

  const clear = () => {
    setInputVal('');
    setSearchParams({});
  };

  return (
    <div className={styles.page}>
      <div className={`container ${styles.inner}`}>
        <div className={styles.header}>
          <h1 className={styles.title}>Browse Movies</h1>
          <p className={styles.sub}>Search our catalog for your next watch</p>
        </div>

        <form onSubmit={handleSearch} className={styles.searchBar}>
          <Search size={18} className={styles.searchIcon} />
          <input
            value={inputVal}
            onChange={(e) => setInputVal(e.target.value)}
            placeholder="Search by title..."
            className={styles.searchInput}
            autoFocus
          />
          {inputVal && (
            <button type="button" onClick={clear} className={styles.clearBtn}>
              <X size={16} />
            </button>
          )}
          <button type="submit" className={styles.searchBtn}>Search</button>
        </form>

        {query && !loading && (
          <div className={styles.resultsMeta}>
            <span>{totalElements.toLocaleString()} result{totalElements !== 1 ? 's' : ''} for "<strong>{query}</strong>"</span>
          </div>
        )}

        {!query && !loading && (
          <div className={styles.emptyState}>
            <Search size={48} />
            <h3>Search for movies</h3>
            <p>Enter a movie title to browse the catalog</p>
          </div>
        )}

        {loading && page === 0 ? (
          <div className={styles.grid}>
            {Array.from({ length: 12 }).map((_, i) => (
              <div key={i} className={`skeleton ${styles.cardSkeleton}`} />
            ))}
          </div>
        ) : movies.length > 0 ? (
          <>
            <div className={styles.grid}>
              {movies.map((m) => (
                <MovieCard key={m.id} movie={m} size="lg" />
              ))}
              {loading && Array.from({ length: 6 }).map((_, i) => (
                <div key={`load-${i}`} className={`skeleton ${styles.cardSkeleton}`} />
              ))}
            </div>

            {page < totalPages - 1 && (
              <div className={styles.loadMoreWrapper}>
                <button onClick={loadMore} className={styles.loadMoreBtn} disabled={loading}>
                  {loading ? 'Loading…' : 'Load More'}
                </button>
              </div>
            )}
          </>
        ) : query && !loading ? (
          <div className={styles.emptyState}>
            <SlidersHorizontal size={48} />
            <h3>No movies found</h3>
            <p>Try a different search term</p>
          </div>
        ) : null}
      </div>
    </div>
  );
}
