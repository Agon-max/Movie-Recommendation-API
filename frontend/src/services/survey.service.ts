import api from "@/lib/api";
import type { SurveyRequest, SurveyResponse, UserSurveyDto } from "@/types";

export const surveyService = {
  async getSurvey(userId: number): Promise<SurveyResponse> {
    const response = await api.get<SurveyResponse>(`/users/${userId}/survey`);
    return response.data;
  },

  async submitSurvey(userId: number, request: SurveyRequest): Promise<UserSurveyDto> {
    const response = await api.post<UserSurveyDto>(`/users/${userId}/survey`, request);
    return response.data;
  },
};
