import api from '../api/axios';

const BACKEND_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await api.post<{ url: string }>('/api/uploads/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  
  // Return absolute URL to avoid proxy issues
  return `${BACKEND_URL}${response.data.url}`;
}