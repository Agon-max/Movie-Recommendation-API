import api from "@/lib/api";
import type { Movie, Page, Genre } from "@/types";

export const movieService = {
  async searchMovies(title: string, page = 0, size = 10): Promise<Page<Movie>> {
    const response = await api.get<Page<Movie>>("/movies/search", {
      params: { title, page, size },
    });
    return response.data;
  },

  async getMovieById(id: number): Promise<Movie> {
    const response = await api.get<Movie>(`/movies/${id}`);
    return response.data;
  },

  async createMovie(movie: Omit<Movie, "id">): Promise<Movie> {
    const response = await api.post<Movie>("/movies", movie);
    return response.data;
  },

  async updateMovie(id: number, movie: Partial<Movie>): Promise<Movie> {
    const response = await api.put<Movie>(`/movies/${id}`, movie);
    return response.data;
  },

  async deleteMovie(id: number): Promise<boolean> {
    const response = await api.delete<boolean>(`/movies/${id}`);
    return response.data;
  },

  async getAllGenres(): Promise<Genre[]> {
    const response = await api.get<Genre[]>("/genres");
    return response.data;
  },

  async getRecommendations(userId: number, count = 10): Promise<Movie[]> {
    const response = await api.get<Movie[]>(`/users/${userId}/recommendations`, {
      params: { count },
    });
    return response.data;
  },
};
