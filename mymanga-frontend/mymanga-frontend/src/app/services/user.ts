import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '../models/user-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class User {
  private readonly apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getProfile(username: string): Observable<UserResponse> {
    const params = new HttpParams().set('username', username);
    return this.http.get<UserResponse>(this.apiUrl, { params });
  }

  activateAccount(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.get(`${this.apiUrl}/activate`, { params });
  }

  updateProfile(username: string, data: any): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${this.apiUrl}/${username}`, data);
  }

  deleteAccount(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
