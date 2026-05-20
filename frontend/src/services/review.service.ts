import api from "@/lib/api";
import type { Page, Review, ReviewCreate } from "@/types";

export const reviewService = {
  async createReview(review: ReviewCreate): Promise<Review> {
    const response = await api.post<Review>("/reviews", review);
    return response.data;
  },

  async updateReview(id: number, review: Partial<ReviewCreate>): Promise<Review> {
    const response = await api.put<Review>(`/reviews/${id}`, review);
    return response.data;
  },

  async getReviewById(id: number): Promise<Review> {
    const response = await api.get<Review>(`/reviews/${id}`);
    return response.data;
  },

  async getReviewsByMovie(movieId: number): Promise<Review[]> {
    const response = await api.get<Review[]>(`/reviews/movie/${movieId}`);
    return response.data;
  },

  async getAllReviews(page = 0, size = 20): Promise<Page<Review>> {
    const response = await api.get<Page<Review>>("/reviews", {
      params: { page, size },
    });
    return response.data;
  },

  async getReviewsByUser(userId: number): Promise<Review[]> {
    // No dedicated /reviews/user endpoint on the backend yet — filter the
    // paginated all-reviews endpoint client-side, walking pages until done.
    const collected: Review[] = [];
    let page = 0;
    const size = 100;
    while (true) {
      const res = await api.get<Page<Review>>("/reviews", { params: { page, size } });
      collected.push(...res.data.content.filter((r) => r.userId === userId));
      if (res.data.last || page + 1 >= res.data.totalPages) break;
      page += 1;
      if (page > 20) break; // safety cap
    }
    return collected;
  },

  async deleteReview(id: number): Promise<void> {
    await api.delete(`/reviews/${id}`);
  },
};
