export interface User {
  id: number;
  username: string;
  email: string;
  totalPoints: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  username: string;
  userId: number;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface Movie {
  id: number;
  tmdbId?: number;
  title: string;
  overview?: string;
  language?: string;
  directorIds?: number[];
  genreIds?: number[];
  actorIds?: number[];
  releaseDate?: string;
  averageRating?: number;
}

export interface TmdbMovie {
  id: number;
  title: string;
  overview: string;
  original_language: string;
  release_date: string;
  vote_average: number;
  genre_ids: number[];
  poster_path?: string;
  backdrop_path?: string;
}

export interface TmdbMovieResponse {
  page: number;
  results: TmdbMovie[];
  total_pages: number;
  total_results: number;
}

export interface Genre {
  id: number;
  tmdbId?: number;
  title: string;
}

export interface Actor {
  id: number;
  tmdbId?: number;
  name: string;
}

export interface Director {
  id: number;
  tmdbId?: number;
  name: string;
}

export interface Review {
  id?: number;
  userId: number;
  movieId: number;
  title?: string;
  body?: string;
  rating_score: number;
  createdAt?: string;
  updatedAt?: string;
  pointsAwarded?: boolean;
}

export interface UserSurvey {
  id?: number;
  userId?: number;
  favoriteGenres: string[];
  favoriteActors: string[];
  favoriteDirectors: string[];
  dislikes?: string;
  completedAt?: string;
}

export interface Reward {
  id: number;
  name: string;
  description: string;
  pointCost: number;
  type: 'GIFT_CARD' | 'CASH_PAYOUT' | 'DISCOUNT_CODE';
  monetaryValue: number;
  active: boolean;
}

export interface Redemption {
  id: number;
  userId: number;
  rewardId: number;
  rewardName: string;
  pointsSpent: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'FULFILLED';
  redeemedAt: string;
  fulfilledAt?: string;
}

export interface PointTransaction {
  id: number;
  userId: number;
  points: number;
  type: 'EARNED' | 'REDEEMED' | 'EXPIRED' | 'BONUS';
  sourceEventType?: string;
  sourceId?: number;
  description?: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
