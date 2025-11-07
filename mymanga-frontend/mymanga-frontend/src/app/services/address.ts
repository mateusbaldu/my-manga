import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Address {
  private readonly apiUrl = 'http://localhost:8080/my-manga/users';

  constructor(private http: HttpClient) {}

  getAddresses(username: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${username}/address/all`);
  }

  addAddress(username: string, data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${username}/address/new`, data);
  }
}
