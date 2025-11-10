import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="container my-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header"><h3>Redefinir Senha</h3></div>
            <div class="card-body">
              <p class="text-muted">Digite seu email e enviaremos instruções para redefinir sua senha.</p>
              <form (ngSubmit)="onSubmit()">
                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input type="email" class="form-control" id="email" [(ngModel)]="email" name="email" required>
                </div>
                @if (successMessage) {
                  <div class="alert alert-success">{{ successMessage }}</div>
                }
                @if (errorMessage) {
                  <div class="alert alert-danger">{{ errorMessage }}</div>
                }
                <button type="submit" class="btn btn-primary" [disabled]="loading">
                  {{ loading ? 'Enviando...' : 'Enviar' }}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class ForgotPassword {
  email = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: Auth) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.forgotPassword(this.email).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Se o email estiver válido, as instruções enviadas para seu email!';
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
          this.errorMessage = 'Erro ao enviar. Verifique o email digitado.';
        }
      }
    });
  }
}