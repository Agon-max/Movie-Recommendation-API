// ============================================================================
// User & Auth
// ============================================================================
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

// ============================================================================
// Movies / catalog
// ============================================================================
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

// ============================================================================
// Reviews
// ============================================================================
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

export interface ReviewCreate {
  movieId: number;
  title?: string;
  body?: string;
  rating_score: number;
}

// ============================================================================
// Points
// ============================================================================
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

// ============================================================================
// Rewards & Redemptions
// ============================================================================
export type RewardType = "GIFT_CARD" | "CASH_PAYOUT" | "DISCOUNT_CODE";

export interface Reward {
  id: number;
  name: string;
  description: string;
  pointCost: number;
  type: RewardType;
  monetaryValue: number;
  stock: number;
  active: boolean;
}

export interface RewardCreate {
  name: string;
  description?: string;
  pointCost: number;
  type: RewardType;
  monetaryValue?: number;
  stock?: number;
  active?: boolean;
}

export type RedemptionStatus = "PENDING" | "APPROVED" | "FULFILLED" | "REJECTED";

export interface Redemption {
  id: number;
  userId?: number;
  rewardId?: number;
  rewardName?: string;
  pointsSpent?: number;
  status?: RedemptionStatus;
  createdAt?: string;
  fulfilledAt?: string;
}

// ============================================================================
// Survey
// ============================================================================
export interface UserSurveyDto {
  id?: number;
  userId?: number;
  favoriteGenres: string[];
  favoriteActors: string[];
  favoriteDirectors: string[];
  dislikes?: string;
  completedAt?: string;
}

export interface SurveyResponse {
  exists: boolean;
  survey: UserSurveyDto | null;
  message: string | null;
}

export interface SurveyRequest {
  favoriteGenres: string[];
  favoriteActors: string[];
  favoriteDirectors: string[];
  dislikes?: string;
}

// ============================================================================
// Pagination & Leaderboard
// ============================================================================
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  totalPoints: number;
}
