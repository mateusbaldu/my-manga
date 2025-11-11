import { Injectable } from '@angular/core';
import { CartItem } from '../models/cart-item.model';
import { MangaResponse } from '../models/manga-response.model';
import { VolumeResponse } from '../models/volume-response.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private items: CartItem[] = [];

  addItem(manga: MangaResponse, volume: VolumeResponse): void {
    const existingItem = this.items.find(item => item.id === volume.id);

    if (existingItem) {
      existingItem.quantity += 1;
      console.log('Quantidade incrementada:', existingItem);
    } else {
      const newItem: CartItem = {
        id: volume.id,
        mangaTitle: manga.title,
        volumeNumber: volume.volumeNumber,
        price: volume.price,
        imageUrl: manga.imageUrl,
        quantity: 1,
        maxQuantity: volume.quantity
      };
      this.items.push(newItem);
      console.log('Novo item adicionado ao carrinho:', newItem);
    }
  }

  getItems(): CartItem[] {
    return this.items;
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
  }

  clearCart(): void {
    this.items = [];
  }

  getCartItemCount(): number {
    return this.items.reduce((total, item) => total + item.quantity, 0);
  }

  getTotal(): number {
    return this.items.reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }
}
