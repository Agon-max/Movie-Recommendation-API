"use client";

import { useState, useEffect, useCallback, useMemo } from "react";
import { useSearchParams } from "next/navigation";
import { movieService } from "@/services/movie.service";
import { MovieCard, MovieCardSkeleton } from "@/components/movies/movie-card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { SurveyPromptModal } from "@/components/rewards/survey-prompt-modal";
import { Search, Filter, X, ChevronLeft, ChevronRight } from "lucide-react";
import type { Movie, Genre, Page } from "@/types";

const SEARCH_DEBOUNCE_MS = 300;

export default function MoviesPage() {
  const searchParams = useSearchParams();

  const [movies, setMovies] = useState<Movie[]>([]);
  const [genres, setGenres] = useState<Genre[]>([]);

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedGenre, setSelectedGenre] = useState<number | null>(null);
  const [selectedYear, setSelectedYear] = useState<number | null>(null);

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [showSearch, setShowSearch] = useState(searchParams.get("search") === "true");

  // Year options: current year + 2 (upcoming) down to 1950
  const yearOptions = useMemo(() => {
    const currentYear = new Date().getFullYear();
    const years: number[] = [];
    for (let y = currentYear + 2; y >= 1950; y--) years.push(y);
    return years;
  }, []);

  const fetchMovies = useCallback(
    async (
      filters: { title: string; genreId: number | null; releaseYear: number | null },
      page: number
    ) => {
      setIsLoading(true);
      try {
        const response: Page<Movie> = await movieService.searchMovies(
          {
            title: filters.title,
            genreId: filters.genreId,
            releaseYear: filters.releaseYear,
          },
          page,
          20
        );
        setMovies(response.content);
        setTotalPages(response.totalPages);
        setCurrentPage(response.number);
      } catch (error) {
        console.error("Failed to fetch movies:", error);
        setMovies([]);
        setTotalPages(0);
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  // Load genres once on mount.
  useEffect(() => {
    movieService
      .getAllGenres()
      .then(setGenres)
      .catch((error) => console.error("Failed to fetch genres:", error));
  }, []);

  // Reset to first page whenever any filter changes so the user sees the
  // top of the new result set, not page 5 of the previous query.
  useEffect(() => {
    setCurrentPage(0);
  }, [searchQuery, selectedGenre, selectedYear]);

  // Debounced fetch on every filter / page change. The cleanup clears the
  // pending timeout so rapid typing only fires one request after the user
  // pauses for SEARCH_DEBOUNCE_MS.
  useEffect(() => {
    const handle = setTimeout(() => {
      fetchMovies(
        { title: searchQuery, genreId: selectedGenre, releaseYear: selectedYear },
        currentPage
      );
    }, SEARCH_DEBOUNCE_MS);
    return () => clearTimeout(handle);
  }, [searchQuery, selectedGenre, selectedYear, currentPage, fetchMovies]);

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  const clearFilters = () => {
    setSearchQuery("");
    setSelectedGenre(null);
    setSelectedYear(null);
  };

  const hasActiveFilters = searchQuery.length > 0 || selectedGenre !== null || selectedYear !== null;

  return (
    <div className="min-h-screen">
      {/* Header */}
      <div className="bg-gradient-to-b from-primary/10 to-transparent">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
            <div>
              <h1 className="text-3xl sm:text-4xl font-bold text-foreground mb-2">
                Browse Movies
              </h1>
              <p className="text-muted-foreground">
                Discover amazing films from our collection
              </p>
            </div>

            <Button
              variant="outline"
              className="gap-2 w-fit"
              onClick={() => setShowSearch(!showSearch)}
            >
              {showSearch ? <X className="h-4 w-4" /> : <Search className="h-4 w-4" />}
              {showSearch ? "Close Search" : "Search Movies"}
            </Button>
          </div>

          {/* Search Bar */}
          {showSearch && (
            <div className="mt-6 animate-fade-in">
              <div className="flex gap-2 max-w-xl">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                  <Input
                    type="text"
                    placeholder="Search by movie title..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-10"
                  />
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Filters */}
        <div className="mb-8 space-y-4">
          {/* Year + clear */}
          <div className="flex flex-wrap items-center gap-3">
            <div className="flex items-center gap-2">
              <Filter className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium text-muted-foreground">
                Release year
              </span>
            </div>
            <select
              value={selectedYear ?? ""}
              onChange={(e) =>
                setSelectedYear(e.target.value ? Number(e.target.value) : null)
              }
              className="h-10 rounded-md border border-border bg-input px-3 py-2 text-sm text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
            >
              <option value="">Any year</option>
              {yearOptions.map((y) => (
                <option key={y} value={y}>
                  {y}
                </option>
              ))}
            </select>

            {hasActiveFilters && (
              <Button
                variant="ghost"
                size="sm"
                onClick={clearFilters}
                className="gap-2 ml-auto"
              >
                <X className="h-4 w-4" />
                Clear filters
              </Button>
            )}
          </div>

          {/* Genre */}
          {genres.length > 0 && (
            <div>
              <div className="flex items-center gap-2 mb-3">
                <Filter className="h-4 w-4 text-muted-foreground" />
                <span className="text-sm font-medium text-muted-foreground">
                  Filter by genre
                </span>
              </div>
              <div className="flex flex-wrap gap-2">
                <Badge
                  variant={selectedGenre === null ? "default" : "secondary"}
                  className="cursor-pointer"
                  onClick={() => setSelectedGenre(null)}
                >
                  All
                </Badge>
                {genres.map((genre) => (
                  <Badge
                    key={genre.id}
                    variant={selectedGenre === genre.id ? "default" : "secondary"}
                    className="cursor-pointer"
                    onClick={() => setSelectedGenre(genre.id)}
                  >
                    {genre.name}
                  </Badge>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Movies Grid */}
        {isLoading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
            {Array.from({ length: 20 }).map((_, i) => (
              <MovieCardSkeleton key={i} />
            ))}
          </div>
        ) : movies.length > 0 ? (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
              {movies.map((movie) => (
                <MovieCard key={movie.id ?? movie.tmdbId} movie={movie} />
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-center gap-4 mt-12">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                >
                  <ChevronLeft className="h-4 w-4" />
                  Previous
                </Button>
                <span className="text-sm text-muted-foreground">
                  Page {currentPage + 1} of {totalPages}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage === totalPages - 1}
                >
                  Next
                  <ChevronRight className="h-4 w-4" />
                </Button>
              </div>
            )}
          </>
        ) : (
          <div className="text-center py-20">
            <Search className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-foreground mb-2">No movies found</h3>
            <p className="text-muted-foreground">
              Try adjusting your search or filter criteria
            </p>
          </div>
        )}
      </div>

      <SurveyPromptModal />
    </div>
  );
}
