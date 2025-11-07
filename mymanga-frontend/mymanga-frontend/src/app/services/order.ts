import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Order {
  private readonly apiUrl = 'http://localhost:8080/my-manga/orders';

  constructor(private http: HttpClient) {}

  checkout(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/new`, data);
  }

  getMyOrders(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/${username}`);
  }
}
