export function getImageUrl(path: string | undefined): string {
  if (!path) return '/placeholder.png';
  if (path.startsWith('http')) return path;

  // For Codespaces: replace '-5173' with '-8080' in the hostname
  const backendHostname = window.location.hostname.replace('-5173', '-8080');
  const backendOrigin = `${window.location.protocol}//${backendHostname}`;
  const relativePath = path.startsWith('/') ? path : `/${path}`;
  return `${backendOrigin}${relativePath}`;
}