import { VolumeResponse } from './volume-response.model';

export interface MangaResponse {
  id: number;
  title: string;
  author: string;
  description: string;
  rating: number;
  keywords: string;
  status: string;
  genres: string;
  imageUrl: string;
  volumes: VolumeResponse[];
}
