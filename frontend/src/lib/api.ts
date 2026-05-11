import axios from 'axios';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090';

export const api = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(err);
  }
);

// Auth
export const authApi = {
  login: (data: { username: string; password: string }) =>
    api.post('/api/auth/login', data).then((r) => r.data),
  register: (data: { username: string; email: string; password: string }) =>
    api.post('/api/auth/register', data).then((r) => r.data),
};

// Movies
export const moviesApi = {
  getPopular: () => api.get('/api/movies/popular').then((r) => r.data),
  search: (title: string, page = 0, size = 12) =>
    api.get('/api/movies/search', { params: { title, page, size } }).then((r) => r.data),
  getById: (id: number) => api.get(`/api/movies/${id}`).then((r) => r.data),
  create: (data: object) => api.post('/api/movies', data).then((r) => r.data),
};

// Recommendations
export const recommendationsApi = {
  get: (userId: number, count = 10) =>
    api.get(`/api/recommendations/${userId}`, { params: { count } }).then((r) => r.data),
  getSurvey: (userId: number) =>
    api.get(`/api/recommendations/survey/${userId}`).then((r) => r.data),
  submitSurvey: (userId: number, data: object) =>
    api.post(`/api/recommendations/survey/${userId}`, data).then((r) => r.data),
};

// Genres
export const genresApi = {
  getAll: () => api.get('/api/genres').then((r) => r.data),
  importFromTmdb: () => api.post('/api/genres/import').then((r) => r.data),
};

// Actors
export const actorsApi = {
  getByMovie: (movieId?: number, movieTitle?: string) =>
    api.get('/api/actors', { params: { movieId, movieTitle } }).then((r) => r.data),
};

// Directors
export const directorsApi = {
  getByMovie: (movieId?: number, movieTitle?: string) =>
    api.get('/api/directors', { params: { movieId, movieTitle } }).then((r) => r.data),
};

// Reviews
export const reviewsApi = {
  create: (data: object) => api.post('/api/reviews', data).then((r) => r.data),
};

// Users
export const usersApi = {
  getById: (id: number) => api.get(`/api/users/${id}`).then((r) => r.data),
  update: (id: number, data: object) => api.put(`/api/users/${id}`, data).then((r) => r.data),
};

// TMDB image helper
export const tmdbImage = (path: string | undefined, size = 'w500') =>
  path ? `https://image.tmdb.org/t/p/${size}${path}` : null;
