import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Manga {
  private readonly apiUrl = 'http://localhost:8080/my-manga/mangas';

  constructor(private http: HttpClient) {}

  getAllMangas(): Observable<any> {
    return this.http.get(`${this.apiUrl}/all`);
  }

  getMangas(page: number = 0, size: number = 12): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get(`${this.apiUrl}/all`, { params });
  }
}