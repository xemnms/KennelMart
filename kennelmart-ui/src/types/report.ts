export interface ReportResponse {
  id: string;
  reporterId: string;
  reporterEmail: string;
  targetType: string; // "LISTING" or "USER"
  targetId: string;
  reason: string;
  status: 'PENDING' | 'REVIEWING' | 'RESOLVED' | 'DISMISSED';
  createdAt: string;
}

export interface ReportRequest {
  targetType: string;
  targetId: string;
  reason: string;
}