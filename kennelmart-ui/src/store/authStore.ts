import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authService } from '../services/authService';
import type { LoginRequest, RegisterRequest, User } from '../types/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  fetchUser: () => Promise<void>;
}
export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      isLoading: false,
      login: async (credentials) => {
        set({ isLoading: true });
        try {
          const response = await authService.login(credentials);
          localStorage.setItem('accessToken', response.accessToken);
          set({
            token: response.accessToken,
            user: {
              id: response.userId,
              name: response.name,
              email: response.email,
              role: response.role,
              verificationStatus: 'PENDING',
              accountStatus: 'ACTIVE',
              studentOrFacultyId: response.idnumber,
            },
          });
          await get().fetchUser();
        } finally {
          set({ isLoading: false });
        }
      },
      register: async (data) => {
        set({ isLoading: true });
        try {
          const response = await authService.register(data);
          localStorage.setItem('accessToken', response.accessToken);
          set({
            token: response.accessToken,
            user: {
              id: response.userId,
              name: response.name,
              email: response.email,
              role: response.role,
              verificationStatus: 'PENDING',
              accountStatus: 'ACTIVE',
              studentOrFacultyId: response.idnumber,
            },
          });
        } finally {
          set({ isLoading: false });
        }
      },
      logout: () => {
        localStorage.removeItem('accessToken');
        set({ user: null, token: null });
      },
      fetchUser: async () => {
        try {
          const user = await authService.getCurrentUser();
          set({ user });
        } catch (error) {
          console.error('Failed to fetch user', error);
        }
      },
    }),
    { name: 'auth-storage', partialize: (state) => ({ user: state.user, token: state.token }) }
  )
);
