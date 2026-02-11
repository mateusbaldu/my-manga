import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderResponse } from '../models/order-response.model';
import { Page } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Order {
  private readonly apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) {}

  checkout(data: any): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.apiUrl}/new`, data);
  }

  getMyOrders(): Observable<Page<OrderResponse>> {
    return this.http.get<Page<OrderResponse>>(`${this.apiUrl}/my-orders`);
  }

  getAllOrders(page: number, size: number): Observable<Page<OrderResponse>> {
    return this.http.get<Page<OrderResponse>>(`${this.apiUrl}/all?page=${page}&size=${size}`);
  }

  cancelOrder(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/cancel`, null);
  }
}
