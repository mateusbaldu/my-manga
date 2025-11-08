import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Manga } from '../../services/manga';
import { CartService } from '../../services/cart-service';

@Component({
  selector: 'app-manga-detail',
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './manga-detail.html',
  styleUrl: './manga-detail.scss',
})
export class MangaDetail implements OnInit {
  manga: any;
  selectedVolume: any;
  loading = false;
  error = '';
  successMessage = '';

  constructor(
    private route: ActivatedRoute,
    private mangaService: Manga,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    
    if (id) {
      this.loading = true;
      this.mangaService.getMangaById(id).subscribe({
        next: (data: any) => {
          this.manga = data;
          this.loading = false;
          console.log('Mangá carregado:', this.manga);
        },
        error: (err: any) => {
          this.error = 'Erro ao carregar detalhes do mangá';
          this.loading = false;
          console.error('Erro:', err);
        }
      });
    }
  }

  adicionarAoCarrinho(): void {
    if (this.selectedVolume) {
      this.cartService.addItem({
        ...this.selectedVolume,
        mangaTitle: this.manga.title,
        mangaId: this.manga.id
      });
      this.successMessage = `Volume ${this.selectedVolume.volumeNumber} adicionado ao carrinho!`;
      this.selectedVolume = null; // Limpa a seleção

      // Limpa a mensagem após 3 segundos
      setTimeout(() => {
        this.successMessage = '';
      }, 3000);
    }
  }
}
