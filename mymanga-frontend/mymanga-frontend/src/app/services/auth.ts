import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';

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
  private readonly apiUrl = `${environment.apiUrl}/login`;
  private readonly registerUrl = `${environment.apiUrl}/users/new`;
  private token: string | null = null;
  private tokenExpiresAt: number | null = null;

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    const loginData: LoginRequest = { email, password };
    
    return this.http.post<LoginResponse>(this.apiUrl, loginData).pipe(
      tap(response => {
        if (response.accessToken) {
          this.token = response.accessToken;
          this.tokenExpiresAt =
            typeof response.expiresIn === 'number'
              ? new Date().getTime() + response.expiresIn * 1000
              : null;
        }
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(this.registerUrl, data);
  }

  logout(): void {
    this.token = null;
    this.tokenExpiresAt = null;
  }

  getToken(): string | null {
    return this.token;
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }
    
    if (this.tokenExpiresAt !== null && new Date().getTime() > this.tokenExpiresAt) {
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
