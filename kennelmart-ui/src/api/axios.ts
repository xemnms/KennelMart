import axios from 'axios';

const api = axios.create({
  baseURL: '',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Only treat as an auth failure if the request actually sent a token.
    // Requests without a token returning 403 are expected (public endpoints)
    // and should NOT trigger a redirect or reload loop.
    const hadToken = !!(error.config?.headers?.Authorization);
    if (hadToken && (error.response?.status === 401 || error.response?.status === 403)) {
      localStorage.removeItem('accessToken');
      // Clear Zustand persisted auth state so user is not considered logged in after reload
      try {
        const raw = localStorage.getItem('auth-storage');
        if (raw) {
          const parsed = JSON.parse(raw) as { state?: { user?: unknown; token?: unknown } };
          if (parsed?.state) {
            parsed.state.user = null;
            parsed.state.token = null;
            localStorage.setItem('auth-storage', JSON.stringify(parsed));
          }
        }
      } catch {
        localStorage.removeItem('auth-storage');
      }
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;