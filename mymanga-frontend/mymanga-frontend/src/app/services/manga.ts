import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
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
}