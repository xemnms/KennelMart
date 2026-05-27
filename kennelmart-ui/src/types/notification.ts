export interface Notification {
  id: string;
  title: string;
  message: string;
  read: boolean;
  type: string;
  referenceId: string | null;
  createdAt: string;
}