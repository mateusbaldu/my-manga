import { Injectable, inject } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { Auth } from './auth';

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private router = inject(Router);
  private authService = inject(Auth);

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          switch (err.status) {
            case 401:
              console.error('Interceptor 401: Não autorizado');
              this.authService.logout();
              this.router.navigate(['/login']);
              break;
            case 403:
              console.error('Interceptor 403: Acesso proibido');
              this.router.navigate(['/']);
              break;
            case 400:
            case 404:
            case 500:
              const errorMessage = err.error?.message || 'Ocorreu um erro inesperado.';
              console.error(`Interceptor ${err.status}: ${errorMessage}`);
              break;
          }
        } else {
          console.error('Erro de rede ou desconhecido', err);
        }
        return throwError(() => err);
      })
    );
  }
}
