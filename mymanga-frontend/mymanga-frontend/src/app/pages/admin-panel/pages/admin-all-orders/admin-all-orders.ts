import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Order } from '../../../../services/order';
import { OrderResponse } from '../../../../models/order-response.model';
import { Page } from '../../../../models/page.model';

@Component({
  selector: 'app-admin-all-orders',
  imports: [
    CommonModule
  ],
  templateUrl: './admin-all-orders.html',
  styleUrl: './admin-all-orders.scss',
})
export class AdminAllOrders implements OnInit {
  pedidos: OrderResponse[] = [];
  loading = false;
  errorMessage = '';
  page = 0;
  totalPages = 0;
  totalPedidos = 0;
  pageSize = 10;

  constructor(private orderService: Order) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.errorMessage = '';

    this.orderService.getAllOrders(this.page, this.pageSize).subscribe({
      next: (response: Page<OrderResponse>) => {
        this.pedidos = response.content;
        this.totalPages = response.totalPages;
        this.totalPedidos = response.totalElements;
        this.loading = false;
        console.log('Pedidos carregados:', this.pedidos);
      },
      error: (err: any) => {
        console.error('Erro ao carregar pedidos:', err);
        this.errorMessage = err.error?.message || 'Erro ao carregar pedidos.';
        this.loading = false;
      }
    });
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadOrders();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadOrders();
    }
  }
}
