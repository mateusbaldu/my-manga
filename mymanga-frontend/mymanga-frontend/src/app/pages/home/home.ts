import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Manga } from '../../services/manga';
import { FormsModule } from '@angular/forms';
import { MangaCardResponse } from '../../models/manga-card-response.model';
import { Page } from '../../models/page.model';
import { Navigation } from '../../services/navigation';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    RouterLink,
    FormsModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit, OnDestroy {
  mangas: MangaCardResponse[] = [];
  loading = false;
  error = '';
  searchTerm: string = '';
  isSearching: boolean = false;

  page: number = 0;
  totalPages: number = 0;
  private pageSize: number = 12;
  private destroy$ = new Subject<void>();

  constructor(
    private mangaService: Manga,
    private navigationService: Navigation
  ) {}

  ngOnInit(): void {
    this.carregarMangas();

    this.navigationService.homeClicked$
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.clearSearchAndReload();
      });
  }

  carregarMangas(): void {
    this.loading = true;
    this.error = '';
    this.mangaService.getMangas(this.page, this.pageSize).subscribe({
      next: (response: Page<MangaCardResponse>) => {
        this.mangas = response.content;
        this.totalPages = response.totalPages;
        this.loading = false;
        console.log('Mangás carregados:', this.mangas);
      },
      error: (err: any) => {
        console.error('Erro detalhado:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.error = err.error.errors[0].message;
          } else {
            this.error = err.error.message;
          }
        } else {
          this.error = 'Erro ao carregar mangás';
        }
        this.loading = false;
      }
    });
  }

  searchMangas(): void {
    if (this.isSearching === false) {
        this.page = 0;
    }

    if (this.searchTerm.trim() === '') {
      this.isSearching = false;
      this.page = 0;
      this.carregarMangas();
      return;
    }

    this.loading = true;
    this.isSearching = true;
    this.error = '';
    
    this.mangaService.searchMangas(this.searchTerm, this.page, this.pageSize).subscribe({
      next: (response: Page<MangaCardResponse>) => {
        this.mangas = response.content;
        this.totalPages = response.totalPages;
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Erro detalhado:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.error = err.error.errors[0].message;
          } else {
            this.error = err.error.message;
          }
        } else {
          this.error = 'Erro ao buscar mangás';
        }
        this.loading = false;
      }
    });
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      if (this.isSearching) {
        this.searchMangas();
      } else {
        this.carregarMangas();
      }
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      if (this.isSearching) {
        this.searchMangas();
      } else {
        this.carregarMangas();
      }
    }
  }

  trackByMangaId(index: number, manga: MangaCardResponse): number {
    return manga.id;
  }

  clearSearchAndReload(): void {
    this.searchTerm = '';
    this.isSearching = false;
    this.page = 0;
    this.carregarMangas();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}