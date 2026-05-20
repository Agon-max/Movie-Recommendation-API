import api from "@/lib/api";
import type { User, PointHistory, LeaderboardEntry } from "@/types";

export const userService = {
  async getUserById(id: number): Promise<User> {
    const response = await api.get<User>(`/users/${id}`);
    return response.data;
  },

  async getAllUsers(): Promise<User[]> {
    const response = await api.get<User[]>("/users");
    return response.data;
  },

  async updateUser(id: number, user: Partial<User>): Promise<User> {
    const response = await api.put<User>(`/users/${id}`, user);
    return response.data;
  },

  async deleteUser(id: number): Promise<void> {
    await api.delete(`/users/${id}`);
  },

  async watchMovie(movieId: number, movieMinutes: number): Promise<void> {
    await api.post(`/users/watchMovie/${movieId}`, null, {
      params: { movieId, movieMinutes },
    });
  },

  async getPointHistory(userId: number): Promise<PointHistory[]> {
    try {
      const response = await api.get<PointHistory[]>(`/users/${userId}/points/history`);
      return response.data;
    } catch {
      return [];
    }
  },

  async getLeaderboard(): Promise<LeaderboardEntry[]> {
    const response = await api.get<User[]>("/users");
    const users = response.data;

    return users
      .slice()
      .sort((a, b) => b.totalPoints - a.totalPoints)
      .map((user, index) => ({
        rank: index + 1,
        userId: user.id,
        username: user.username,
        totalPoints: user.totalPoints,
      }));
  },
};
