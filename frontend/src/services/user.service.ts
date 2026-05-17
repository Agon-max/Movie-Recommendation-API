import api from "@/lib/api";
import type { User, PointHistory, Reward, LeaderboardEntry } from "@/types";

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
      params: { movieMinutes },
    });
  },

  async getPointHistory(userId: number): Promise<PointHistory[]> {
    const response = await api.get<PointHistory[]>(`/users/${userId}/points/history`);
    return response.data;
  },

  async getLeaderboard(): Promise<LeaderboardEntry[]> {
    const response = await api.get<User[]>("/users");
    const users = response.data;
    
    // Sort by points and create leaderboard
    const sorted = users
      .sort((a, b) => b.totalPoints - a.totalPoints)
      .map((user, index) => ({
        rank: index + 1,
        userId: user.id,
        username: user.username,
        totalPoints: user.totalPoints,
      }));
    
    return sorted;
  },
};

export const rewardService = {
  async getAllRewards(): Promise<Reward[]> {
    const response = await api.get<Reward[]>("/rewards");
    return response.data;
  },

  async getActiveRewards(): Promise<Reward[]> {
    const response = await api.get<Reward[]>("/rewards/active");
    return response.data;
  },

  async getRewardById(id: number): Promise<Reward> {
    const response = await api.get<Reward>(`/rewards/${id}`);
    return response.data;
  },

  async createReward(reward: Omit<Reward, "id">): Promise<Reward> {
    const response = await api.post<Reward>("/rewards", reward);
    return response.data;
  },

  async updateReward(id: number, reward: Partial<Reward>): Promise<Reward> {
    const response = await api.put<Reward>(`/rewards/${id}`, reward);
    return response.data;
  },

  async deleteReward(id: number): Promise<void> {
    await api.delete(`/rewards/${id}`);
  },
};
