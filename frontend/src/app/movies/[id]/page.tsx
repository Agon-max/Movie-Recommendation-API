"use client";

import { useState, useEffect, useCallback } from "react";
import { use } from "react";
import Image from "next/image";
import Link from "next/link";
import { movieService } from "@/services/movie.service";
import { reviewService } from "@/services/review.service";
import { userService } from "@/services/user.service";
import { useAuth } from "@/contexts/auth-context";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { StarRating } from "@/components/movies/star-rating";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Star,
  Calendar,
  Globe,
  Play,
  ArrowLeft,
  MessageSquare,
  Trophy,
  Loader2,
  User,
} from "lucide-react";
import type { Movie, Review } from "@/types";

interface MoviePageProps {
  params: Promise<{ id: string }>;
}

export default function MovieDetailPage({ params }: MoviePageProps) {
  const { id } = use(params);
  const { tmdbId } = use(params);
  const { user, isAuthenticated, refreshUser } = useAuth();
  const [movie, setMovie] = useState<Movie | null>(null);
  const [reviews, setReviews] = useState<Review[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isWatching, setIsWatching] = useState(false);
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [reviewTitle, setReviewTitle] = useState("");
  const [reviewBody, setReviewBody] = useState("");
  const [reviewRating, setReviewRating] = useState(0);
  const [isSubmittingReview, setIsSubmittingReview] = useState(false);
  const [pointsEarned, setPointsEarned] = useState<number | null>(null);

 const fetchMovie = useCallback(async () => {
   try {
     const data = await movieService.getMovieByTmdbId(Number(tmdbId));
     setMovie(data);
   } catch (error) {
     console.error("Failed to fetch movie:", error);
   }
 }, [tmdbId]);

  const fetchReviews = useCallback(async () => {
    try {
      const data = await reviewService.getReviewsByMovie(Number(id));
      setReviews(data);
    } catch (error) {
      console.error("Failed to fetch reviews:", error);
      setReviews([]);
    }
  }, [id]);

  useEffect(() => {
    const loadData = async () => {
      setIsLoading(true);
      await Promise.all([fetchMovie(), fetchReviews()]);
      setIsLoading(false);
    };
    loadData();
  }, [fetchMovie, fetchReviews]);

  const handleWatch = async () => {
    if (!isAuthenticated || !movie) return;
    
    setIsWatching(true);
    try {
      await userService.watchMovie(movie.tmdbId, 120);
      setPointsEarned(15);
      await refreshUser();
      setTimeout(() => setPointsEarned(null), 3000);
    } catch (error) {
      console.error("Failed to mark as watched:", error);
    } finally {
      setIsWatching(false);
    }
  };

  const handleSubmitReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!isAuthenticated || !user || !movie || reviewRating === 0) return;

    setIsSubmittingReview(true);
    try {
      const newReview = await reviewService.createReview({
        userId: user.id,
        movieId: movie.tmdbId,
        title: reviewTitle,
        body: reviewBody,
        rating_score: reviewRating,
      });
      setReviews([newReview, ...reviews]);
      setReviewTitle("");
      setReviewBody("");
      setReviewRating(0);
      setShowReviewForm(false);
      setPointsEarned(10);
      await refreshUser();
      setTimeout(() => setPointsEarned(null), 3000);
    } catch (error) {
      console.error("Failed to submit review:", error);
    } finally {
      setIsSubmittingReview(false);
    }
  };

  const posterUrl = movie?.posterPath
    ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
    : "/placeholder-movie.jpg";

  const backdropUrl = movie?.backdropPath
    ? `https://image.tmdb.org/t/p/original${movie.backdropPath}`
    : null;

  if (isLoading) {
    return <MovieDetailSkeleton />;
  }

  if (!movie) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-foreground mb-4">Movie not found</h1>
          <Link href="/movies">
            <Button>Back to Movies</Button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen">
      {/* Points Notification */}
      {pointsEarned && (
        <div className="fixed top-20 right-4 z-50 points-earned">
          <div className="flex items-center gap-2 px-4 py-3 bg-accent text-accent-foreground rounded-lg shadow-lg">
            <Trophy className="h-5 w-5" />
            <span className="font-semibold">+{pointsEarned} points earned!</span>
          </div>
        </div>
      )}

      {/* Backdrop */}
      {backdropUrl && (
        <div className="absolute top-0 left-0 right-0 h-[60vh] overflow-hidden">
          <Image
            src={backdropUrl}
            alt={movie.title}
            fill
            className="object-cover opacity-30"
            priority
          />
          <div className="absolute inset-0 bg-gradient-to-t from-background via-background/80 to-transparent" />
        </div>
      )}

      {/* Content */}
      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Back button */}
        <Link href="/movies" className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground mb-8 transition-colors">
          <ArrowLeft className="h-4 w-4" />
          Back to Movies
        </Link>

        <div className="grid lg:grid-cols-[300px_1fr] gap-8">
          {/* Poster */}
          <div className="flex-shrink-0">
            <div className="aspect-[2/3] relative rounded-xl overflow-hidden shadow-2xl">
              <Image
                src={posterUrl}
                alt={movie.title}
                fill
                className="object-cover"
                priority
              />
            </div>
          </div>

          {/* Details */}
          <div className="space-y-6">
            <div>
              <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold text-foreground mb-4 text-balance">
                {movie.title}
              </h1>

              <div className="flex flex-wrap items-center gap-4 mb-6">
                {movie.averageRating > 0 && (
                  <div className="flex items-center gap-2 px-3 py-1.5 bg-accent/10 rounded-full">
                    <Star className="h-5 w-5 fill-accent text-accent" />
                    <span className="font-semibold text-accent">
                      {movie.averageRating.toFixed(1)}
                    </span>
                  </div>
                )}
                
                {movie.releaseDate && (
                  <div className="flex items-center gap-2 text-muted-foreground">
                    <Calendar className="h-4 w-4" />
                    <span>{new Date(movie.releaseDate).getFullYear()}</span>
                  </div>
                )}

                {movie.language && (
                  <div className="flex items-center gap-2 text-muted-foreground">
                    <Globe className="h-4 w-4" />
                    <span className="uppercase">{movie.language}</span>
                  </div>
                )}
              </div>
            </div>

            {/* Overview */}
            {movie.overview && (
              <div>
                <h2 className="text-lg font-semibold text-foreground mb-2">Overview</h2>
                <p className="text-muted-foreground leading-relaxed">{movie.overview}</p>
              </div>
            )}

            {/* Genre badges */}
            {movie.genreIds.length > 0 && (
              <div className="flex flex-wrap gap-2">
                {movie.genreIds.map((genreId) => (
                  <Badge key={genreId} variant="secondary">
                    Genre {genreId}
                  </Badge>
                ))}
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex flex-wrap gap-4 pt-4">
              {isAuthenticated && (
                <>
                  <Button onClick={handleWatch} disabled={isWatching} className="gap-2">
                    {isWatching ? (
                      <Loader2 className="h-4 w-4 animate-spin" />
                    ) : (
                      <Play className="h-4 w-4" />
                    )}
                    Mark as Watched (+15 pts)
                  </Button>
                  <Button
                    variant="outline"
                    onClick={() => setShowReviewForm(!showReviewForm)}
                    className="gap-2"
                  >
                    <MessageSquare className="h-4 w-4" />
                    Write Review (+10 pts)
                  </Button>
                </>
              )}
              {!isAuthenticated && (
                <Link href="/login">
                  <Button className="gap-2">
                    <User className="h-4 w-4" />
                    Sign in to interact
                  </Button>
                </Link>
              )}
            </div>
          </div>
        </div>

        {/* Review Form */}
        {showReviewForm && isAuthenticated && (
          <Card className="mt-8 animate-fade-in">
            <CardHeader>
              <CardTitle>Write a Review</CardTitle>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmitReview} className="space-y-4">
                <div>
                  <label className="text-sm font-medium text-foreground mb-2 block">
                    Your Rating
                  </label>
                  <StarRating
                    rating={reviewRating}
                    size="lg"
                    interactive
                    onRatingChange={setReviewRating}
                  />
                </div>
                
                <div>
                  <label htmlFor="reviewTitle" className="text-sm font-medium text-foreground mb-2 block">
                    Title
                  </label>
                  <Input
                    id="reviewTitle"
                    placeholder="Review title..."
                    value={reviewTitle}
                    onChange={(e) => setReviewTitle(e.target.value)}
                    required
                  />
                </div>

                <div>
                  <label htmlFor="reviewBody" className="text-sm font-medium text-foreground mb-2 block">
                    Review
                  </label>
                  <textarea
                    id="reviewBody"
                    placeholder="Share your thoughts about this movie..."
                    value={reviewBody}
                    onChange={(e) => setReviewBody(e.target.value)}
                    required
                    rows={4}
                    className="flex w-full rounded-md border border-border bg-input px-3 py-2 text-sm text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                  />
                </div>

                <div className="flex gap-4">
                  <Button type="submit" disabled={isSubmittingReview || reviewRating === 0}>
                    {isSubmittingReview ? (
                      <>
                        <Loader2 className="h-4 w-4 animate-spin" />
                        Submitting...
                      </>
                    ) : (
                      "Submit Review"
                    )}
                  </Button>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => setShowReviewForm(false)}
                  >
                    Cancel
                  </Button>
                </div>
              </form>
            </CardContent>
          </Card>
        )}

        {/* Reviews Section */}
        <div className="mt-12">
          <h2 className="text-2xl font-bold text-foreground mb-6 flex items-center gap-2">
            <MessageSquare className="h-6 w-6" />
            Reviews ({reviews.length})
          </h2>

          {reviews.length > 0 ? (
            <div className="space-y-4">
              {reviews.map((review, index) => (
                <Card key={review.id || index}>
                  <CardContent className="pt-6">
                    <div className="flex items-start justify-between mb-3">
                      <div>
                        <h3 className="font-semibold text-foreground">{review.title}</h3>
                        <p className="text-sm text-muted-foreground">
                          by {review.username || `User ${review.userId}`}
                        </p>
                      </div>
                      <StarRating rating={review.rating_score} size="sm" />
                    </div>
                    <p className="text-muted-foreground">{review.body}</p>
                    {review.createdAt && (
                      <p className="text-xs text-muted-foreground mt-3">
                        {new Date(review.createdAt).toLocaleDateString()}
                      </p>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Card>
              <CardContent className="py-12 text-center">
                <MessageSquare className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                <p className="text-muted-foreground">No reviews yet. Be the first to review!</p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}

function MovieDetailSkeleton() {
  return (
    <div className="min-h-screen">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Skeleton className="h-6 w-32 mb-8" />
        <div className="grid lg:grid-cols-[300px_1fr] gap-8">
          <Skeleton className="aspect-[2/3] rounded-xl" />
          <div className="space-y-4">
            <Skeleton className="h-12 w-3/4" />
            <div className="flex gap-4">
              <Skeleton className="h-8 w-24" />
              <Skeleton className="h-8 w-24" />
            </div>
            <Skeleton className="h-32 w-full" />
            <div className="flex gap-2">
              <Skeleton className="h-6 w-20" />
              <Skeleton className="h-6 w-20" />
              <Skeleton className="h-6 w-20" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
