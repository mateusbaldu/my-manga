import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  accessToken: string;
  expiresIn?: number;
}

interface JwtPayload {
  sub: string;
  scope: string;
  username: string;
}

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private readonly apiUrl = 'http://localhost:8080/my-manga/login';
  private readonly registerUrl = 'http://localhost:8080/my-manga/users/new';
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    const loginData: LoginRequest = { email, password };
    
    return this.http.post<LoginResponse>(this.apiUrl, loginData).pipe(
      tap(response => {
        if (response.accessToken && response.expiresIn) {
          localStorage.setItem(this.TOKEN_KEY, response.accessToken);
          const expiresInMs = response.expiresIn * 1000;
          const expirationTime = new Date().getTime() + expiresInMs;
          localStorage.setItem('token_expires_at', JSON.stringify(expirationTime));
        }
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(this.registerUrl, data);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    
    const expirationTimeItem = localStorage.getItem('token_expires_at');
    if (!expirationTimeItem) {
      this.logout();
      return false;
    }
    
    const expirationTime = JSON.parse(expirationTimeItem);
    const agora = new Date().getTime();
    
    if (agora > expirationTime) {
      console.log('Token expirado, deslogando...');
      this.logout(); 
      return false;
    }
    
    return true;
  }

  hasRole(role: string): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    try {
      const decodedToken: any = jwtDecode(token);
      
      if (decodedToken.scope && decodedToken.scope.includes(role)) {
        return true;
      }
      
      return false;
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return false;
    }
  }

  getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      const decodedToken = jwtDecode<JwtPayload>(token); 
      return decodedToken.username || null;
    } catch (error) {
      console.error('Erro ao decodificar token:', error);
      return null;
    }
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/forgot-password`, { email }, { responseType: 'text' });
  }

  resetPassword(token: string, newPassword: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/reset-password`, { token, newPassword }, { responseType: 'text' });
  }
}
