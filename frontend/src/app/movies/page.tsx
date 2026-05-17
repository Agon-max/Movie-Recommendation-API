"use client";

import { useState, useEffect, useCallback } from "react";
import { useSearchParams } from "next/navigation";
import { movieService } from "@/services/movie.service";
import { MovieCard, MovieCardSkeleton } from "@/components/movies/movie-card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Search, Filter, X, ChevronLeft, ChevronRight } from "lucide-react";
import type { Movie, Genre, Page } from "@/types";

export default function MoviesPage() {
  const searchParams = useSearchParams();
  const [movies, setMovies] = useState<Movie[]>([]);
  const [genres, setGenres] = useState<Genre[]>([]);
  const [selectedGenre, setSelectedGenre] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showSearch, setShowSearch] = useState(searchParams.get("search") === "true");

  const fetchMovies = useCallback(async (query: string, page: number) => {
    setIsLoading(true);
    try {
      const searchTerm = query.trim() || "a"; // Default search to get all movies
      const response: Page<Movie> = await movieService.searchMovies(searchTerm, page, 20);
      setMovies(response.content);
      setTotalPages(response.totalPages);
      setCurrentPage(response.number);
    } catch (error) {
      console.error("Failed to fetch movies:", error);
      setMovies([]);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const fetchGenres = useCallback(async () => {
    try {
      const genreList = await movieService.getAllGenres();
      setGenres(genreList);
    } catch (error) {
      console.error("Failed to fetch genres:", error);
    }
  }, []);

  useEffect(() => {
    fetchGenres();
    fetchMovies("", 0);
  }, [fetchGenres, fetchMovies]);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchMovies(searchQuery, 0);
  };

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < totalPages) {
      fetchMovies(searchQuery, newPage);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  };

  const filteredMovies = selectedGenre
    ? movies.filter((movie) => movie.genreIds.includes(selectedGenre))
    : movies;

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
            <form onSubmit={handleSearch} className="mt-6 animate-fade-in">
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
                <Button type="submit">Search</Button>
              </div>
            </form>
          )}
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Genre Filters */}
        {genres.length > 0 && (
          <div className="mb-8">
            <div className="flex items-center gap-2 mb-4">
              <Filter className="h-4 w-4 text-muted-foreground" />
              <span className="text-sm font-medium text-muted-foreground">Filter by genre</span>
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

        {/* Movies Grid */}
        {isLoading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
            {Array.from({ length: 20 }).map((_, i) => (
              <MovieCardSkeleton key={i} />
            ))}
          </div>
        ) : filteredMovies.length > 0 ? (
          <>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4 sm:gap-6">
              {filteredMovies.map((movie) => (
                <MovieCard key={movie.tmdbId} movie={movie} />
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
    </div>
  );
}
