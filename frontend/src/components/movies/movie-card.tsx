"use client";

import Link from "next/link";
import Image from "next/image";
import { Star } from "lucide-react";
import type { Movie } from "@/types";
import { cn } from "@/lib/utils";

interface MovieCardProps {
  movie: Movie;
  className?: string;
}

export function MovieCard({ movie, className }: MovieCardProps) {
  const posterUrl = movie.posterPath
    ? `https://image.tmdb.org/t/p/w500${movie.posterPath}`
    : "/placeholder-movie.jpg";

  return (
    <Link href={`/movies/${movie.tmdbId}`}>
      <div
        className={cn(
          "movie-card group relative rounded-lg overflow-hidden bg-card cursor-pointer",
          className
        )}
      >
        {/* Poster Image */}
        <div className="aspect-[2/3] relative">
          <Image
            src={posterUrl}
            alt={movie.title}
            fill
            className="object-cover"
            sizes="(max-width: 640px) 50vw, (max-width: 1024px) 33vw, 20vw"
          />
          
          {/* Overlay on hover */}
          <div className="absolute inset-0 bg-gradient-to-t from-background via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
        </div>

        {/* Info overlay */}
        <div className="absolute bottom-0 left-0 right-0 p-3 bg-gradient-to-t from-background to-transparent">
          <h3 className="font-semibold text-foreground text-sm line-clamp-2 mb-1">
            {movie.title}
          </h3>
          
          <div className="flex items-center gap-2 text-xs text-muted-foreground">
            {movie.averageRating > 0 && (
              <div className="flex items-center gap-1">
                <Star className="h-3 w-3 fill-accent text-accent" />
                <span>{movie.averageRating.toFixed(1)}</span>
              </div>
            )}
            {movie.releaseDate && (
              <span>{new Date(movie.releaseDate).getFullYear()}</span>
            )}
          </div>
        </div>

        {/* Rating badge */}
        {movie.averageRating > 0 && (
          <div className="absolute top-2 right-2 flex items-center gap-1 bg-background/80 backdrop-blur-sm px-2 py-1 rounded-full">
            <Star className="h-3 w-3 fill-accent text-accent" />
            <span className="text-xs font-semibold">{movie.averageRating.toFixed(1)}</span>
          </div>
        )}
      </div>
    </Link>
  );
}

export function MovieCardSkeleton() {
  return (
    <div className="rounded-lg overflow-hidden bg-card animate-pulse">
      <div className="aspect-[2/3] bg-muted" />
      <div className="p-3 space-y-2">
        <div className="h-4 bg-muted rounded w-3/4" />
        <div className="h-3 bg-muted rounded w-1/2" />
      </div>
    </div>
  );
}
