import api from "@/lib/api";
import type { Redemption, Reward, RewardCreate } from "@/types";

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

  async createReward(reward: RewardCreate): Promise<Reward> {
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

export const redemptionService = {
  async redeem(userId: number, rewardId: number): Promise<Redemption> {
    const response = await api.post<Redemption>("/redemptions", null, {
      params: { userId, rewardId },
    });
    return response.data;
  },

  async getUserRedemptions(userId: number): Promise<Redemption[]> {
    const response = await api.get<Redemption[]>(`/redemptions/${userId}`);
    return response.data;
  },
};
