import api from '../api/axios';
import type { User, UserPublicProfile } from '../types/auth';

export interface UserSearchResponse {
  content: User[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export const userService = {
  /**
   * Search for users by name or email (case-insensitive, partial match).
   * @param keyword search term
   * @param page page number (zero-based)
   * @param size number of results per page
   * @returns paginated user search results
   */
  async searchUsers(keyword: string, page = 0, size = 12): Promise<UserSearchResponse> {
    const response = await api.get<UserSearchResponse>('/api/users/search', {
      params: { keyword, page, size },
    });
    return response.data;
  },

  async getUserPublicProfile(userId: string): Promise<UserPublicProfile> {
    const response = await api.get<UserPublicProfile>(`/api/users/${userId}/public`);
    return response.data;
  }
};