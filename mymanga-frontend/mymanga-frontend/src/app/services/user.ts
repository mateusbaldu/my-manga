import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class User {
  private readonly apiUrl = 'http://localhost:8080/my-manga/users';

  constructor(private http: HttpClient) {}

  getProfile(username: string): Observable<any> {
    const params = new HttpParams().set('username', username);
    return this.http.get(this.apiUrl, { params });
  }

  activateAccount(token: string): Observable<any> {
    const params = new HttpParams().set('token', token);
    return this.http.get(`${this.apiUrl}/activate`, { params });
  }
}
