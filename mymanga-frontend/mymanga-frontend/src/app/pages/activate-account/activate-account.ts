import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { User } from '../../services/user';

@Component({
  selector: 'app-activate-account',
  imports: [CommonModule, RouterLink],
  templateUrl: './activate-account.html',
  styleUrl: './activate-account.scss',
})
export class ActivateAccount implements OnInit {
  isActivating = true;
  successMessage = '';
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private userService: User
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      
      if (!token) {
        this.isActivating = false;
        this.errorMessage = 'Token de ativação não encontrado na URL.';
        return;
      }

      this.userService.activateAccount(token).subscribe({
        next: (response) => {
          this.isActivating = false;
          this.successMessage = 'Conta ativada com sucesso! Você já pode fazer login.';
        },
        error: (err) => {
          this.isActivating = false;
          if (err.error && err.error.message) {
            if (err.error.errors && Array.isArray(err.error.errors)) {
              this.errorMessage = err.error.errors[0].message;
            } else {
              this.errorMessage = err.error.message;
            }
          } else {
            this.errorMessage = 'Erro ao ativar conta. O token pode estar expirado ou inválido.';
          }
        }
      });
    });
  }
}
