import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Manga } from '../../services/manga';
import { FormsModule } from '@angular/forms'; // Importe o FormsModule

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    RouterLink,
    FormsModule // Adicione o FormsModule aqui
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  mangas: any[] = [];
  loading = false;
  error = '';
  searchTerm: string = '';
  isSearching: boolean = false;

  // Propriedades para paginação
  page: number = 0;
  totalPages: number = 0;
  private pageSize: number = 12; // O tamanho que definimos no passo anterior

  constructor(private mangaService: Manga) {}

  ngOnInit(): void {
    this.carregarMangas();
  }

  // Método 'carregarMangas' MODIFICADO
  carregarMangas(): void {
    this.loading = true;
    this.error = '';
    // Agora usa this.page e this.pageSize
    this.mangaService.getMangas(this.page, this.pageSize).subscribe({
      next: (response: any) => {
        this.mangas = response.content;
        this.totalPages = response.totalPages; // Salva o total de páginas da API
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

  // Método 'searchMangas' MODIFICADO
  searchMangas(): void {
    // Reseta a página para 0 toda vez que uma NOVA busca é feita (botão Buscar)
    if (this.isSearching === false) {
        this.page = 0;
    }

    if (this.searchTerm.trim() === '') {
      this.isSearching = false;
      this.page = 0; // Reseta a página
      this.carregarMangas(); // Volta para a lista normal
      return;
    }

    this.loading = true;
    this.isSearching = true;
    this.error = '';
    
    // Agora usa this.page e this.pageSize na busca
    this.mangaService.searchMangas(this.searchTerm, this.page, this.pageSize).subscribe({
      next: (response: any) => {
        this.mangas = response.content;
        this.totalPages = response.totalPages; // Salva o total de páginas da API
        this.loading = false;
      },
      error: (err: any) => {
        this.error = 'Erro ao buscar mangás';
        this.loading = false;
      }
    });
  }

  // NOVO MÉTODO para o botão "Anterior"
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

  // NOVO MÉTODO para o botão "Próxima"
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

  trackByMangaId(index: number, manga: any): number {
    return manga.id;
  }
}