import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Address {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAddresses(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${username}/address/all`);
  }

  addAddress(username: string, data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${username}/address/new`, data);
  }

  deleteAddress(username: string, addressId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${username}/address/${addressId}`);
  }

  updateAddress(username: string, addressId: number, data: any): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${username}/address/${addressId}`, data);
  }
}
