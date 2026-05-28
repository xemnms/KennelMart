export function getImageUrl(path: string | undefined): string {
  if (!path) return '/placeholder.png';
  // If already absolute, return as is (but you probably don't have absolute URLs)
  if (path.startsWith('http')) return path;
  // Return relative path; the Vite proxy will forward /uploads to backend
  return path;
}