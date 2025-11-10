import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container my-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header"><h3>Crie sua Nova Senha</h3></div>
            <div class="card-body">
              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label for="password" class="form-label">Nova Senha</label>
                  <input type="password" class="form-control" id="password" [(ngModel)]="password" name="password" required>
                </div>
                @if (successMessage) {
                  <div class="alert alert-success">{{ successMessage }}</div>
                }
                @if (errorMessage) {
                  <div class="alert alert-danger">{{ errorMessage }}</div>
                }
                <button type="submit" class="btn btn-primary" [disabled]="loading">
                  {{ loading ? 'Salvando...' : 'Salvar Nova Senha' }}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ResetPassword implements OnInit {
  password = '';
  token = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private authService: Auth,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'];
    if (!this.token) {
      this.errorMessage = 'Token inválido ou ausente.';
    }
  }

  onSubmit(): void {
    if (!this.password) return;
    this.loading = true;
    this.authService.resetPassword(this.token, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Senha alterada com sucesso! Redirecionando para o login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.errorMessage = err.error.errors[0].message;
          } else {
            this.errorMessage = err.error.message;
          }
        } else {
          this.errorMessage = 'Erro ao redefinir senha. O token pode ter expirado.';
        }
      }
    });
  }
}