import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Manga } from '../../../../services/manga';
import { MangaCardResponse } from '../../../../models/manga-card-response.model';
import { MangaResponse } from '../../../../models/manga-response.model';
import { Page } from '../../../../models/page.model';

@Component({
  selector: 'app-admin-manage-mangas',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './admin-manage-mangas.html',
  styleUrl: './admin-manage-mangas.scss',
})
export class AdminManageMangas implements OnInit {
  mangaForm!: FormGroup;
  volumeForm!: FormGroup;
  mangas: MangaCardResponse[] = [];
  editingMangaId: number | null = null;
  loading = false;
  errorMessage = '';
  successMessage = '';
  volumeLoading = false;
  volumeErrorMessage = '';
  volumeSuccessMessage = '';

  statusOptions = ['RELEASING', 'COMPLETED', 'HIATUS', 'CANCELLED'];
  genreOptions = ['ACTION', 'ADVENTURE', 'COMEDY', 'DRAMA', 'FANTASY', 'HORROR', 'MYSTERY', 'ROMANCE', 'SCI_FI', 'SLICE_OF_LIFE', 'SPORTS', 'SUPERNATURAL', 'THRILLER'];

  constructor(
    private fb: FormBuilder,
    private mangaService: Manga,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.mangaForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(1)]],
      author: ['', [Validators.required, Validators.minLength(1)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      rating: [0, [Validators.required, Validators.min(0), Validators.max(10)]],
      status: ['RELEASING', Validators.required],
      genres: ['', Validators.required],
      keywords: [''],
      imageUrl: ['']
    });

    this.volumeForm = this.fb.group({
      mangaId: ['', Validators.required],
      volumeNumber: ['', [Validators.required, Validators.min(1)]],
      price: ['', [Validators.required, Validators.min(0)]],
      chapters: ['', [Validators.required, Validators.min(1)]],
      releaseDate: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(0)]]
    });

    this.loadMangas();
  }

  loadMangas(): void {
    this.mangaService.getMangas(0, 100).subscribe({
      next: (response: Page<MangaCardResponse>) => {
        this.mangas = response.content;
        console.log('Mangás carregados:', this.mangas);
      },
      error: (err: any) => {
        console.error('Erro ao carregar mangás:', err);
      }
    });
  }

  onSubmit(): void {
    if (this.mangaForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios corretamente.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const formData = this.mangaForm.value;
    
    const mangaData = {
      title: formData.title,
      author: formData.author,
      description: formData.description,
      rating: formData.rating,
      status: formData.status,
      genres: formData.genres,
      keywords: formData.keywords || '',
      imageUrl: formData.imageUrl || ''
    };

    console.log('Dados enviados:', mangaData);

    if (this.editingMangaId) {
      this.mangaService.updateManga(this.editingMangaId, mangaData).subscribe({
        next: (response: any) => {
          console.log('Mangá atualizado com sucesso:', response);
          this.successMessage = 'Mangá atualizado com sucesso!';
          this.loading = false;
          this.mangaForm.reset();
          this.editingMangaId = null;
          this.loadMangas();
          
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (err: any) => {
          console.error('Erro:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao atualizar mangá. Tente novamente.';
          }
          this.loading = false;
        }
      });
    } else {
      this.mangaService.createManga(mangaData).subscribe({
        next: (response: any) => {
          console.log('Mangá criado com sucesso:', response);
          this.successMessage = 'Mangá criado com sucesso!';
          this.loading = false;
          this.mangaForm.reset();
          this.editingMangaId = null;
          this.loadMangas();
          
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (err: any) => {
          console.error('Erro:', err);
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao criar mangá. Tente novamente.';
          }
          this.loading = false;
        }
      });
    }
  }

  onSubmitVolume(): void {
    if (this.volumeForm.invalid) {
      this.volumeErrorMessage = 'Por favor, preencha todos os campos obrigatórios corretamente.';
      return;
    }

    this.volumeLoading = true;
    this.volumeErrorMessage = '';
    this.volumeSuccessMessage = '';

    const formData = this.volumeForm.value;
    const mangaId = formData.mangaId;
    
    const volumeData = {
      volumeNumber: formData.volumeNumber,
      price: formData.price,
      chapters: formData.chapters,
      releaseDate: formData.releaseDate,
      quantity: formData.quantity
    };

    console.log('Dados do volume enviados:', volumeData);

    this.mangaService.addVolume(mangaId, volumeData).subscribe({
      next: (response: any) => {
        console.log('Volume adicionado com sucesso:', response);
        this.volumeSuccessMessage = 'Volume adicionado com sucesso!';
        this.volumeLoading = false;
        this.volumeForm.reset();
      },
      error: (err: any) => {
        console.error('Erro ao adicionar volume:', err);
        this.volumeErrorMessage = err.error?.message || 'Erro ao adicionar volume. Verifique se o ID do mangá está correto.';
        this.volumeLoading = false;
      }
    });
  }

  onEdit(manga: MangaCardResponse): void {
    this.loading = true;
    this.errorMessage = '';

    const mangaId = manga.id.toString();

    this.mangaService.getMangaById(mangaId).subscribe({
      next: (fullManga: MangaResponse) => {
        this.mangaForm.patchValue(fullManga);
        this.editingMangaId = fullManga.id;
        window.scrollTo(0, 0);
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Erro ao carregar dados do mangá para edição.';
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('Tem certeza que quer excluir este mangá?')) {
      this.mangaService.deleteManga(id).subscribe({
        next: () => {
          console.log('Mangá excluído com sucesso');
          this.successMessage = 'Mangá excluído com sucesso!';
          this.loadMangas();
          setTimeout(() => {
            this.successMessage = '';
          }, 3000);
        },
        error: (err: any) => {
          console.error('Erro ao excluir mangá:', err);
          this.errorMessage = err.error?.message || 'Erro ao excluir mangá.';
          setTimeout(() => {
            this.errorMessage = '';
          }, 3000);
        }
      });
    }
  }
}
