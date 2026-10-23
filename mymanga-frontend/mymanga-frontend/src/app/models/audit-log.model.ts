export type AuditStatus = 'SUCCESS' | 'FAILURE';

export type AuditAction =
  | 'USER_LOGIN'
  | 'USER_LOGIN_FAILED'
  | 'PASSWORD_RESET_REQUESTED'
  | 'PASSWORD_RESET_COMPLETED'
  | 'USER_CREATED'
  | 'USER_ACTIVATED'
  | 'USER_UPDATED'
  | 'USER_DELETED'
  | 'ADDRESS_CREATED'
  | 'ADDRESS_UPDATED'
  | 'ADDRESS_DELETED'
  | 'MANGA_CREATED'
  | 'MANGA_UPDATED'
  | 'MANGA_DELETED'
  | 'VOLUMES_ADDED'
  | 'VOLUME_UPDATED'
  | 'VOLUME_DELETED'
  | 'ORDER_CREATED'
  | 'ORDER_CONFIRMED'
  | 'ORDER_UPDATED'
  | 'ORDER_CANCELLED';

export interface AuditLogResponse {
  id: number;
  action: AuditAction;
  entityType: string;
  entityId: string | null;
  actorId: number | null;
  actorUsername: string;
  ipAddress: string;
  status: AuditStatus;
  details: string;
  createdAt: string;
}

export interface AuditLogFilter {
  action?: string;
  entityType?: string;
  actorUsername?: string;
  status?: string;
  startDate?: string;
  endDate?: string;
}
