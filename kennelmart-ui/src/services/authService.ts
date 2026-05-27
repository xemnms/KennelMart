import api from '../api/axios';
import { LoginRequest, RegisterRequest, AuthResponse, User } from '../types/auth';
import type { UpdateProfileRequest, ChangePasswordRequest } from '../types/auth';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/login', credentials);
    return response.data;
  },
  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await api.post<AuthResponse>('/api/auth/register', data);
    return response.data;
  },
  async getCurrentUser(): Promise<User> {
    const response = await api.get<User>('/api/auth/me');
    return response.data;
  },
  async updateProfile(data: UpdateProfileRequest): Promise<User> {
    const response = await api.put<User>('/api/users/me', data);
    return response.data;
  },
  async changePassword(data: ChangePasswordRequest): Promise<void> {
    await api.put('/api/users/me/password', data);
  },
};