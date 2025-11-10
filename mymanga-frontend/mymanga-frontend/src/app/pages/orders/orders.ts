import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Order } from '../../services/order';

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
  successMessage = '';

  constructor(
    private orderService: Order
  ) {}

  ngOnInit(): void {
  this.orderService.getMyOrders().subscribe({
    next: (response: any) => {
      this.orders = response.content; 
      this.errorMessage = '';
    },
    error: (err: any) => {
      this.orders = [];
      this.errorMessage = err.error?.message || 'Falha ao carregar pedidos.';
    }
  });
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

  cancelarPedido(id: number): void {
    this.orderService.cancelOrder(id).subscribe({
      next: () => {
        this.successMessage = 'Pedido cancelado com sucesso!';
        this.errorMessage = '';
        this.ngOnInit(); // Recarrega a lista
      },
      error: (err: any) => {
        this.errorMessage = err.error?.message || 'Falha ao cancelar o pedido.';
        this.successMessage = '';
      }
    });
  }
}
