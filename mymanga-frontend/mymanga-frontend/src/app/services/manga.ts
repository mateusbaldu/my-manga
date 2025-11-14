import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MangaCardResponse } from '../models/manga-card-response.model';
import { MangaResponse } from '../models/manga-response.model';
import { VolumeResponse } from '../models/volume-response.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root',
})
export class Manga {
  private readonly apiUrl = 'http://localhost:8080/my-manga/mangas';

  constructor(private http: HttpClient) {}

  getAllMangas(): Observable<Page<MangaCardResponse>> {
    return this.http.get<Page<MangaCardResponse>>(`${this.apiUrl}/all`);
  }

  getMangas(page: number = 0, size: number = 10): Observable<Page<MangaCardResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<Page<MangaCardResponse>>(`${this.apiUrl}/all`, { params });
  }

  createManga(data: Partial<MangaResponse>): Observable<MangaResponse> {
    return this.http.post<MangaResponse>(`${this.apiUrl}/new`, data);
  }

  getMangaById(id: string): Observable<MangaResponse> {
    return this.http.get<MangaResponse>(`${this.apiUrl}/${id}`);
  }

  addVolume(mangaId: string, volumeData: Partial<VolumeResponse>): Observable<VolumeResponse> {
    return this.http.post<VolumeResponse>(`${this.apiUrl}/${mangaId}/volumes/new`, volumeData);
  }

  searchMangas(keyword: string, page: number = 0, size: number = 10): Observable<Page<MangaCardResponse>> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<MangaCardResponse>>(`${this.apiUrl}/search`, { params });
  }
}