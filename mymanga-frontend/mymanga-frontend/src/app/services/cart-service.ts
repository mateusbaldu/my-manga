import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private items: any[] = [];

  addItem(item: any): void {
    this.items.push(item);
    console.log('Item adicionado ao carrinho:', item);
  }

  getItems(): any[] {
    return this.items;
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
  }

  clearCart(): void {
    this.items = [];
  }

  getCartItemCount(): number {
    return this.items.length;
  }

  getTotal(): number {
    return this.items.reduce((total, item) => total + (item.price || 0), 0);
  }
}
