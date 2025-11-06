import { Component, OnInit } from '@angular/core';
import { Manga } from '../../services/manga';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  mangas: any[] = [];
  loading = false;
  error = '';

  constructor(private mangaService: Manga) {}

  ngOnInit(): void {
    this.carregarMangas();
  }

  carregarMangas(): void {
    this.loading = true;
    this.mangaService.getAllMangas().subscribe({
      next: (data: any) => {
        this.mangas = data.content || data;
        this.loading = false;
        console.log('Mangás carregados:', this.mangas);
      },
      error: (err: any) => {
        this.error = 'Erro ao carregar mangás';
        this.loading = false;
        console.error('Erro:', err);
      }
    });
  }
}
