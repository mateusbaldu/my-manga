import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditLog } from '../../../../services/audit-log';
import {
  AuditAction,
  AuditLogFilter,
  AuditLogResponse,
} from '../../../../models/audit-log.model';
import { Page } from '../../../../models/page.model';

@Component({
  selector: 'app-admin-audit-logs',
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-audit-logs.html',
  styleUrl: './admin-audit-logs.scss',
})
export class AdminAuditLogs implements OnInit {
  logs: AuditLogResponse[] = [];
  loading = false;
  errorMessage = '';
  page = 0;
  totalPages = 0;
  totalLogs = 0;
  pageSize = 15;

  filter: AuditLogFilter = {
    action: '',
    entityType: '',
    actorUsername: '',
    status: '',
  };

  readonly actionOptions: AuditAction[] = [
    'USER_LOGIN',
    'USER_LOGIN_FAILED',
    'PASSWORD_RESET_REQUESTED',
    'PASSWORD_RESET_COMPLETED',
    'USER_CREATED',
    'USER_ACTIVATED',
    'USER_UPDATED',
    'USER_DELETED',
    'ADDRESS_CREATED',
    'ADDRESS_UPDATED',
    'ADDRESS_DELETED',
    'MANGA_CREATED',
    'MANGA_UPDATED',
    'MANGA_DELETED',
    'VOLUMES_ADDED',
    'VOLUME_UPDATED',
    'VOLUME_DELETED',
    'ORDER_CREATED',
    'ORDER_CONFIRMED',
    'ORDER_UPDATED',
    'ORDER_CANCELLED',
  ];

  readonly entityTypeOptions = [
    'Auth',
    'Users',
    'Address',
    'Manga',
    'Volume',
    'Order',
  ];

  readonly statusOptions = ['SUCCESS', 'FAILURE'];

  constructor(private auditLogService: AuditLog) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.loading = true;
    this.errorMessage = '';

    this.auditLogService
      .getAuditLogs(this.page, this.pageSize, this.filter)
      .subscribe({
        next: (response: Page<AuditLogResponse>) => {
          this.logs = response.content;
          this.totalPages = response.totalPages;
          this.totalLogs = response.totalElements;
          this.loading = false;
        },
        error: (err: any) => {
          console.error('Erro ao carregar logs de auditoria:', err);
          this.errorMessage =
            err.error?.message || 'Erro ao carregar os logs do sistema.';
          this.loading = false;
        },
      });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadLogs();
  }

  clearFilters(): void {
    this.filter = {
      action: '',
      entityType: '',
      actorUsername: '',
      status: '',
    };
    this.page = 0;
    this.loadLogs();
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadLogs();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadLogs();
    }
  }
}
