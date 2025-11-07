import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Manga } from '../../services/manga';

@Component({
  selector: 'app-admin-dashboard',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
})
export class AdminDashboard implements OnInit {
  mangaForm!: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';

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

    this.mangaService.createManga(mangaData).subscribe({
      next: (response: any) => {
        console.log('Mangá criado com sucesso:', response);
        this.successMessage = 'Mangá criado com sucesso!';
        this.loading = false;
        this.mangaForm.reset();
        
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (err: any) => {
        console.error('Erro ao criar mangá:', err);
        this.errorMessage = 'Erro ao criar mangá. Tente novamente.';
        this.loading = false;
      }
    });
  }
}
