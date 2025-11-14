import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CartService } from '../../services/cart-service';
import { Auth } from '../../services/auth';
import { Navigation } from '../../services/navigation';

@Component({
  selector: 'app-header',
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  constructor(
    private cartService: CartService,
    private authService: Auth,
    private router: Router,
    private navigationService: Navigation
  ) {}

  getQuantidadeItens(): number {
    return this.cartService.getCartItemCount();
  }

  public get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  public get isAdmin(): boolean {
    return this.authService.hasRole('ADMIN');
  }

  goHome(): void {
    this.navigationService.notifyHomeClicked();
    this.router.navigate(['/']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
