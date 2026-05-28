export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  studentOrFacultyId: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface AuthResponse {
  userId: string;
  name: string;
  idnumber: string;
  email: string;
  role: 'ADMIN' | 'USER';
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  createdAt: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'ADMIN' | 'USER';
  verificationStatus: 'PENDING' | 'VERIFIED' | 'REJECTED';
  accountStatus: 'ACTIVE' | 'SUSPENDED';
  studentOrFacultyId: string;
  profileImage?: string;
  averageRating?: number;
}

export interface UpdateProfileRequest {
  name?: string;
  studentOrFacultyId?: string;
  profileImage?: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface UserPublicProfile {
  id: string;
  name: string;
  profileImage?: string;
  averageRating?: number;
  role: 'ADMIN' | 'USER';
  joinedAt: string;
}