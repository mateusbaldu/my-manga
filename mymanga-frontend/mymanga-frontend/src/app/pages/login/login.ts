import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  email = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.email || !this.password) {
      this.errorMessage = 'Por favor, preencha todos os campos';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).subscribe({
      next: (response: any) => {
        console.log('Login bem-sucedido!', response);
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        console.error('Erro no login:', err);
        this.errorMessage = 'Email ou senha inválidos';
        this.loading = false;
      }
    });
  }
}
