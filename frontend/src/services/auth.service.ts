import api from "@/lib/api";
import type { LoginRequest, LoginResponse, RegisterRequest, User } from "@/types";

export const authService = {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>("/auth/login", credentials);
    const data = response.data;
    
    if (typeof window !== "undefined") {
      localStorage.setItem("token", data.token);
      localStorage.setItem("user", JSON.stringify({
        id: data.userId,
        username: data.username,
      }));
    }
    
    return data;
  },

  async register(data: RegisterRequest): Promise<User> {
    const response = await api.post<User>("/auth/register", data);
    return response.data;
  },

  logout(): void {
    if (typeof window !== "undefined") {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
    }
  },

  getToken(): string | null {
    if (typeof window !== "undefined") {
      return localStorage.getItem("token");
    }
    return null;
  },

  getCurrentUser(): { id: number; username: string } | null {
    if (typeof window !== "undefined") {
      const user = localStorage.getItem("user");
      if (user) {
        try {
          return JSON.parse(user);
        } catch {
          return null;
        }
      }
    }
    return null;
  },

  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};
