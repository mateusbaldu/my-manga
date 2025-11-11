import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  if (authService.hasRole('ADMIN')) {
    return true;
  } else {
    console.warn('Acesso negado: usuário não é administrador');
    router.navigate(['/']);
    return false;
  }
};
