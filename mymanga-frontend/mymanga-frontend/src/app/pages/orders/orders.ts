import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Order } from '../../services/order';
import { Auth } from '../../services/auth';
import { jwtDecode } from 'jwt-decode';

@Component({
  selector: 'app-orders',
  imports: [
    CommonModule,
    RouterLink
  ],
  templateUrl: './orders.html',
  styleUrl: './orders.scss',
})
export class Orders implements OnInit {
  orders: any[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private orderService: Order,
    private authService: Auth
  ) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    const token = this.authService.getToken();
    if (token) {
      const decoded: any = jwtDecode(token);
      const username = decoded.sub;

      this.loading = true;
      this.errorMessage = '';

      this.orderService.getMyOrders(username).subscribe({
        next: (data: any) => {
          this.orders = data;
          this.loading = false;
          console.log('Pedidos carregados:', this.orders);
        },
        error: (err: any) => {
          console.error('Erro ao carregar pedidos:', err);
          this.errorMessage = 'Erro ao carregar seus pedidos.';
          this.loading = false;
        }
      });
    }
  }

  getStatusColor(status: string): string {
    const statusColors: { [key: string]: string } = {
      'PENDING': 'warn',
      'CONFIRMED': 'primary',
      'SHIPPED': 'accent',
      'DELIVERED': 'primary',
      'CANCELLED': 'warn'
    };
    return statusColors[status] || 'primary';
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  }

  getTotalItems(order: any): number {
    if (!order.items) return 0;
    return order.items.reduce((sum: number, item: any) => sum + (item.quantity || 1), 0);
  }
}
