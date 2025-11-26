import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart-service';
import { Order } from '../../services/order';
import { CartItem } from '../../models/cart-item.model';
import { OrderResponse } from '../../models/order-response.model';

@Component({
  selector: 'app-cart',
  imports: [
    CommonModule,
    FormsModule,
    RouterLink
  ],
  templateUrl: './cart.html',
  styleUrl: './cart.scss',
})
export class Cart implements OnInit {
  cartItems: CartItem[] = [];
  paymentMethod: string = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private cartService: CartService,
    private orderService: Order,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartItems = this.cartService.getItems();
  }

  getTotal(): number {
    return this.cartService.getTotal();
  }

  removeItem(index: number): void {
    this.cartService.removeItem(index);
    this.cartItems = this.cartService.getItems();
  }

  diminuir(item: CartItem): void {
    this.cartService.decreaseItem(item.id);
    this.cartItems = this.cartService.getItems();
  }

  aumentar(item: CartItem): void {
    this.cartService.incrementItem(item.id);
    this.cartItems = this.cartService.getItems();
  }

  finalizarCompra(): void {
    if (this.cartItems.length === 0) {
      this.errorMessage = 'Seu carrinho está vazio!';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const orderData = {
      paymentMethod: this.paymentMethod,
      items: this.cartItems.map(item => ({
        volumeId: item.id,
        quantity: item.quantity
      }))
    };

    console.log('Dados do pedido:', orderData);

    this.orderService.checkout(orderData).subscribe({
      next: (response: OrderResponse) => {
        console.log('Pedido criado com sucesso:', response);
        this.successMessage = 'Pedido realizado com sucesso!';
        this.loading = false;
        this.cartService.clearCart();
        this.cartItems = [];
        
        setTimeout(() => {
          this.router.navigate(['/orders']);
        }, 2000);
      },
      error: (err: any) => {
        console.error('Erro detalhado:', err);
        if (err.error && err.error.message) {
          if (err.error.errors && Array.isArray(err.error.errors)) {
            this.errorMessage = err.error.errors[0].message;
          } else {
            this.errorMessage = err.error.message;
          }
        } else {
          this.errorMessage = 'Erro ao finalizar compra. Tente novamente.';
        }
        this.loading = false;
      }
    });
  }
}
