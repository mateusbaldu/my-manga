import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private items: any[] = [];

  addItem(manga: any, volume: any): void {
    const existingItem = this.items.find(item => item.id === volume.id);

    if (existingItem) {
      existingItem.quantity += 1;
      console.log('Quantidade incrementada:', existingItem);
    } else {
      const newItem = {
        id: volume.id,
        mangaTitle: manga.title,
        volumeNumber: volume.volumeNumber,
        price: volume.price,
        imageUrl: manga.imageUrl,
        quantity: 1
      };
      this.items.push(newItem);
      console.log('Novo item adicionado ao carrinho:', newItem);
    }
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
    return this.items.reduce((total, item) => total + (item.quantity || 1), 0);
  }

  getTotal(): number {
    return this.items.reduce((total, item) => {
      const price = item.price || 0;
      const quantity = item.quantity || 1;
      return total + (price * quantity);
    }, 0);
  }
}
