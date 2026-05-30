export function getImageUrl(path: string | undefined): string {
  if (!path) return '/placeholder.svg';

  if (path.startsWith('http://') || path.startsWith('https://')) {
    try {
      const url = new URL(path);
      return `${url.pathname}${url.search}${url.hash}`;
    } catch {
      return path;
    }
  }

  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '');
  const relativePath = path.startsWith('/') ? path : `/${path}`;

  if (apiBaseUrl) {
    return new URL(relativePath, apiBaseUrl).toString();
  }

  return relativePath;
}