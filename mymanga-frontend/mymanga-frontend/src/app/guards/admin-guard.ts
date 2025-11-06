import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import { Auth } from '../services/auth';

interface JwtPayload {
  scope?: string;
  exp?: number;
  sub?: string;
}

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const token = authService.getToken();
  
  if (!token) {
    router.navigate(['/login']);
    return false;
  }

  try {
    const decoded: JwtPayload = jwtDecode(token);
    
    if (decoded.scope && decoded.scope.includes('ADMIN')) {
      return true;
    } else {
      console.warn('Acesso negado: usuário não é administrador');
      router.navigate(['/']);
      return false;
    }
  } catch (error) {
    console.error('Erro ao decodificar token:', error);
    router.navigate(['/login']);
    return false;
  }
};
