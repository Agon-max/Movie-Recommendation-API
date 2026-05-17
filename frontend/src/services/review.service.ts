import api from "@/lib/api";
import type { Review } from "@/types";

export const reviewService = {
  async createReview(review: Omit<Review, "id" | "createdAt" | "updatedAt">): Promise<Review> {
    const response = await api.post<Review>("/reviews", review);
    return response.data;
  },

  async getReviewsByMovie(movieId: number): Promise<Review[]> {
    const response = await api.get<Review[]>(`/reviews/movie/${movieId}`);
    return response.data;
  },

  async getReviewsByUser(userId: number): Promise<Review[]> {
    const response = await api.get<Review[]>(`/reviews/user/${userId}`);
    return response.data;
  },

  async deleteReview(id: number): Promise<void> {
    await api.delete(`/reviews/${id}`);
  },
};
