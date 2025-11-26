import { Injectable } from '@angular/core';
import { CartItem } from '../models/cart-item.model';
import { MangaResponse } from '../models/manga-response.model';
import { VolumeResponse } from '../models/volume-response.model';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private items: CartItem[] = [];
  private readonly STORAGE_KEY = 'cart_items';

  constructor() {
    this.loadFromStorage();
  }

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
    this.saveToStorage();
  }

  getItems(): CartItem[] {
    return this.items;
  }

  incrementItem(volumeId: number): void {
    const item = this.items.find(item => item.id === volumeId);
    
    if (item && item.quantity < item.maxQuantity) {
      item.quantity++;
      this.saveToStorage();
    }
  }

  decreaseItem(volumeId: number): void {
    const item = this.items.find(item => item.id === volumeId);
    
    if (item) {
      item.quantity--;
      
      if (item.quantity === 0) {
        const index = this.items.indexOf(item);
        this.removeItem(index);
      } else {
        this.saveToStorage();
      }
    }
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
    this.saveToStorage();
  }

  clearCart(): void {
    this.items = [];
    this.saveToStorage();
  }

  getCartItemCount(): number {
    return this.items.reduce((total, item) => total + item.quantity, 0);
  }

  getTotal(): number {
    return this.items.reduce((total, item) => {
      return total + (item.price * item.quantity);
    }, 0);
  }

  private saveToStorage(): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.items));
  }

  private loadFromStorage(): void {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      this.items = JSON.parse(stored);
    }
  }
}
