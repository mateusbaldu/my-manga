import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login implements OnInit {
  private authService = inject(Auth);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Por favor, preencha os campos corretamente.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const email = this.loginForm.value.email;
    const password = this.loginForm.value.password;

    this.authService.login(email, password).subscribe({
      next: (response: any) => {
        console.log('Login bem-sucedido!', response);
        this.loading = false;
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        this.errorMessage = err.error?.message || 'Email ou senha inválidos';
        this.loading = false;
      }
    });
  }
}
