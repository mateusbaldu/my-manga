import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Manga } from '../../services/manga';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule
  ],
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
    this.mangaService.getMangas(0, 12).subscribe({
      next: (response: any) => {
        this.mangas = response.content;
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
