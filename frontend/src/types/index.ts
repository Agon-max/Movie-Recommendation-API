// User types
export interface User {
  id: number;
  username: string;
  email: string;
  totalPoints: number;
  role?: "ADMIN" | "USER";
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

// Movie types
export interface Movie {
  id?: number;
  tmdbId: number;
  title: string;
  overview: string;
  language: string;
  releaseDate: string;
  averageRating: number;
  posterPath?: string;
  backdropPath?: string;
  directorIds: number[];
  genreIds: number[];
  actorIds: number[];
}

export interface Genre {
  id: number;
  name: string;
}

export interface Actor {
  id: number;
  name: string;
  profilePath?: string;
}

export interface Director {
  id: number;
  name: string;
  profilePath?: string;
}

// Review types
export interface Review {
  id?: number;
  userId: number;
  movieId: number;
  title: string;
  body: string;
  rating_score: number;
  createdAt?: string;
  updatedAt?: string;
  username?: string;
}

// Reward types
export type PointEventType =
  | "WATCH_MOVIE"
  | "WRITE_REVIEW"
  | "FIRST_LOGIN"
  | "REWARD_REDEMPTION_BONUS";

export interface PointEvent {
  id: number;
  eventType: PointEventType;
  pointsAwarded: number;
  description: string;
  active: boolean;
}

export interface PointHistory {
  id: number;
  userId: number;
  eventType: PointEventType;
  pointsReceived: number;
  createdAt: string;
}

export interface Reward {
  id: number;
  name: string;
  description: string;
  pointsCost: number;
  rewardType: "DISCOUNT" | "FREE_RENTAL" | "BADGE" | "PREMIUM_ACCESS";
  active: boolean;
}

// Survey types
export interface UserSurvey {
  favoriteGenres: number[];
  favoriteActors: number[];
  favoriteDirectors: number[];
  dislikes: string[];
}

export interface SurveyResponse {
  completed: boolean;
  survey: UserSurvey | null;
  message: string | null;
}

// Pagination
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Leaderboard
export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  totalPoints: number;
}
