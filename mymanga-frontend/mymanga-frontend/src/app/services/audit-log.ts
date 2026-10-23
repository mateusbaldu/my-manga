import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLogFilter, AuditLogResponse } from '../models/audit-log.model';
import { Page } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuditLog {
  private readonly apiUrl = `${environment.apiUrl}/audit-logs`;

  constructor(private http: HttpClient) {}

  getAuditLogs(
    page: number,
    size: number,
    filter?: AuditLogFilter
  ): Observable<Page<AuditLogResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'createdAt,desc');

    if (filter) {
      if (filter.action) {
        params = params.set('action', filter.action);
      }
      if (filter.entityType) {
        params = params.set('entityType', filter.entityType);
      }
      if (filter.actorUsername && filter.actorUsername.trim() !== '') {
        params = params.set('actorUsername', filter.actorUsername.trim());
      }
      if (filter.status) {
        params = params.set('status', filter.status);
      }
      if (filter.startDate) {
        params = params.set('startDate', filter.startDate);
      }
      if (filter.endDate) {
        params = params.set('endDate', filter.endDate);
      }
    }

    return this.http.get<Page<AuditLogResponse>>(this.apiUrl, { params });
  }

  getAuditLogById(id: number): Observable<AuditLogResponse> {
    return this.http.get<AuditLogResponse>(`${this.apiUrl}/${id}`);
  }
}
